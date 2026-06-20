package fr.artcas2.mincraftofflinestatus;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
public class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            int port;

            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid port number: {}", args[0]);
                throw new IllegalArgumentException("Invalid port number: " + args[0]);
            }

            new Server(port).run(args[1], args[2]);
        } else {
            File jarFile = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            LOGGER.info("Usage: java -jar {} <port> <status> <message>",
                    jarFile.isFile() ? jarFile.getName() : "path/to/file.jar");
        }
    }

    public void run(String status, String message) {
        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down server...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }));

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new PacketDecoder());
                        socketChannel.pipeline().addLast(new PacketEncoder());
                        socketChannel.pipeline().addLast(new PacketHandler(status, message));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            LOGGER.info("Waiting for port {} to be available...", port);

            while (true) {
                Channel channel = null;

                while (channel == null) {
                    ChannelFuture channelFuture;

                    channelFuture = serverBootstrap.bind(port).await();

                    if (channelFuture.isSuccess()) {
                        channel = channelFuture.channel();
                    } else {
                        Thread.sleep(3000);
                    }
                }

                LOGGER.info("Server started on *:{}", port);
                waitForMinecraftServer(channel);
                channel.close().sync();
                LOGGER.info("Server stopped");
                LOGGER.info("Waiting for Minecraft server to stop...");

                while (isMinecraftServerRunning()) {
                    Thread.sleep(3000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void waitForMinecraftServer(Channel channel) throws InterruptedException {
        while (channel.isOpen()) {
            if (isMinecraftServerRunning()) {
                return;
            }

            Thread.sleep(1000);
        }
    }

    private boolean isMinecraftServerRunning() {
        List<VirtualMachineDescriptor> descriptors = VirtualMachine.list();

        for (VirtualMachineDescriptor descriptor : descriptors) {
            if (descriptor.id().equals(String.valueOf(ProcessHandle.current().pid()))) {
                continue;
            }

            try {
                VirtualMachine vm = VirtualMachine.attach(descriptor);

                if (Objects.equals(vm.getSystemProperties().getProperty("app.name"), "MinecraftServer")) {
                    vm.detach();
                    return true;
                }

                vm.detach();
            } catch (AttachNotSupportedException | IOException ignored) {}
        }

        return false;
    }
}

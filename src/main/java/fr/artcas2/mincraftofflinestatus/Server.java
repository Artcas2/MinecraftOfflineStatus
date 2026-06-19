package fr.artcas2.mincraftofflinestatus;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Server {
    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            new Server(Integer.parseInt(args[0])).run(args[1], args[2]);
        } else {
            File jarFile = new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            LOGGER.info("Usage: java -jar {} <port> <status> <message>", jarFile.isFile() ? jarFile.getName() : "path/to/file.jar");
        }
    }

    public void run(String status, String message) throws Exception {
        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        try {
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

            LOGGER.info("Starting server on port {}...", port);
            serverBootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            LOGGER.info("Shutting down server...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

package fr.artcas2.mincraftofflinestatus;

import fr.artcas2.mincraftofflinestatus.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private Protocol protocol = Protocol.HANDSHAKE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        in.markReaderIndex();

        if (!in.isReadable()) {
            return;
        }

        int packetLength = VarInt.readVarInt(in);

        if (in.readableBytes() < packetLength) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf buf = in.readBytes(packetLength);
        int packetId = VarInt.readVarInt(buf);
        Class<?> packetClass = protocol.getClassById(packetId);

        if (packetClass == null) {
            System.out.printf("Unknown packet with id 0x%02X received%n", packetId);
            return;
        }

        Packet packet = (Packet) packetClass.getDeclaredConstructor().newInstance();

        packet.read(buf);
        out.add(packet);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
}

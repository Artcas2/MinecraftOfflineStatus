package fr.artcas2.mincraftofflinestatus;

import fr.artcas2.mincraftofflinestatus.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        int packetId = ctx.pipeline().get(PacketDecoder.class).getProtocol().getIdByClass(packet.getClass());
        ByteBuf buf = Unpooled.buffer();

        VarInt.writeVarInt(buf, packetId);
        packet.write(buf);
        VarInt.writeVarInt(out, buf.readableBytes());
        out.writeBytes(buf);
    }
}

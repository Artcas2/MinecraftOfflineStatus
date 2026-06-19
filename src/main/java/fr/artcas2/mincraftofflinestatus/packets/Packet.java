package fr.artcas2.mincraftofflinestatus.packets;

import fr.artcas2.mincraftofflinestatus.VarInt;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public abstract class Packet {
    public Packet() {}

    public abstract void read(ByteBuf buf);

    public abstract void write(ByteBuf buf);

    protected static String readString(ByteBuf buf) {
        int length = VarInt.readVarInt(buf);
        byte[] bytes = new byte[length];

        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected static void writeString(String string, ByteBuf buf) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        VarInt.writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }
}

package fr.artcas2.mincraftofflinestatus;

import io.netty.buffer.ByteBuf;

// https://minecraft.wiki/w/Java_Edition_protocol/Packets#VarInt_and_VarLong
public class VarInt {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public static int readVarInt(ByteBuf buf) {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = buf.readByte();
            value |= (currentByte & SEGMENT_BITS) << position;

            if ((currentByte & CONTINUE_BIT) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                buf.writeByte(value);
                return;
            }

            buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);

            value >>>= 7;
        }
    }
}

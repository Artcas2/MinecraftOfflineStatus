package fr.artcas2.mincraftofflinestatus.packets;

import io.netty.buffer.ByteBuf;

public class Disconnect extends Packet {
    private final String reason;

    public Disconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void read(ByteBuf buf) {
        throw new UnsupportedOperationException("Can't read disconnect packet!");
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(reason, buf);
    }
}

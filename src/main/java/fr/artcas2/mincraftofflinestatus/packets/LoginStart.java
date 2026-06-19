package fr.artcas2.mincraftofflinestatus.packets;

import io.netty.buffer.ByteBuf;

public class LoginStart extends Packet {
    @Override
    public void read(ByteBuf buf) {}

    @Override
    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException("Can't write login start packet!");
    }
}

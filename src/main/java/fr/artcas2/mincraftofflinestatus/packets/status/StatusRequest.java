package fr.artcas2.mincraftofflinestatus.packets.status;

import fr.artcas2.mincraftofflinestatus.packets.Packet;
import io.netty.buffer.ByteBuf;

public class StatusRequest extends Packet {
    @Override
    public void read(ByteBuf buf) {}

    @Override
    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException("Can't write status response packet!");
    }
}

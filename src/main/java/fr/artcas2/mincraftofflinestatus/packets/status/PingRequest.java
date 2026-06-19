package fr.artcas2.mincraftofflinestatus.packets.status;

import fr.artcas2.mincraftofflinestatus.packets.Packet;
import io.netty.buffer.ByteBuf;

public class PingRequest extends Packet {
    private long time;

    public long getTime() {
        return time;
    }

    @Override
    public void read(ByteBuf buf) {
        this.time = buf.readLong();
    }

    @Override
    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException("Can't write ping packet!");
    }
}

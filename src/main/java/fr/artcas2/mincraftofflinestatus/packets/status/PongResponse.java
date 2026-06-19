package fr.artcas2.mincraftofflinestatus.packets.status;

import fr.artcas2.mincraftofflinestatus.packets.Packet;
import io.netty.buffer.ByteBuf;

public class PongResponse extends Packet {
    private final long time;

    public PongResponse(long time) {
        this.time = time;
    }

    @Override
    public void read(ByteBuf buf) {
        throw new UnsupportedOperationException("Can't read pong packet!");
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong(time);
    }
}

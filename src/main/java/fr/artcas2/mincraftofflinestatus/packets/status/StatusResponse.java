package fr.artcas2.mincraftofflinestatus.packets.status;

import fr.artcas2.mincraftofflinestatus.packets.Packet;
import io.netty.buffer.ByteBuf;

public class StatusResponse extends Packet {
    private final String jsonResponse;

    public StatusResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    @Override
    public void read(ByteBuf buf) {
        throw new UnsupportedOperationException("Can't read status response packet!");
    }

    @Override
    public void write(ByteBuf buf) {
        writeString(jsonResponse, buf);
    }
}

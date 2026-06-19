package fr.artcas2.mincraftofflinestatus.packets.handshake;

import fr.artcas2.mincraftofflinestatus.VarInt;
import fr.artcas2.mincraftofflinestatus.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Handshake extends Packet {
    private int protocolVersion;
    private String host;
    private int port;
    private int nextState;

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getNextState() {
        return nextState;
    }

    @Override
    public void read(ByteBuf buf) {
        this.protocolVersion = VarInt.readVarInt(buf);
        this.host = readString(buf);
        this.port = buf.readUnsignedShort();
        this.nextState = VarInt.readVarInt(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        throw new UnsupportedOperationException("Can't write handshake packet!");
    }
}

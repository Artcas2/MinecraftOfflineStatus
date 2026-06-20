package fr.artcas2.mincraftofflinestatus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.artcas2.mincraftofflinestatus.packets.Disconnect;
import fr.artcas2.mincraftofflinestatus.packets.LoginStart;
import fr.artcas2.mincraftofflinestatus.packets.Packet;
import fr.artcas2.mincraftofflinestatus.packets.handshake.Handshake;
import fr.artcas2.mincraftofflinestatus.packets.status.PingRequest;
import fr.artcas2.mincraftofflinestatus.packets.status.PongResponse;
import fr.artcas2.mincraftofflinestatus.packets.status.StatusRequest;
import fr.artcas2.mincraftofflinestatus.packets.status.StatusResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {
    private final TextComponent textComponent;
    private final String status;
    private ProtocolState currentState = ProtocolState.HANDSHAKE;

    public PacketHandler(String status, String message) {
        this.textComponent = new TextComponent(message, "red");
        this.status = status;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        if (packet instanceof Handshake handshake) {
            checkState(ProtocolState.HANDSHAKE);

            switch (handshake.getNextState()) {
                case 1:
                    ctx.pipeline().get(PacketDecoder.class).setProtocol(Protocol.STATUS);
                    this.currentState = ProtocolState.STATUS;
                    break;
                case 2:
                    ctx.pipeline().get(PacketDecoder.class).setProtocol(Protocol.LOGIN);
                    this.currentState = ProtocolState.LOGIN;
                    break;
            }
        } else if (packet instanceof StatusRequest) {
            checkState(ProtocolState.STATUS);
            ctx.writeAndFlush(this.getStatusResponse());
        } else if (packet instanceof PingRequest pingRequest) {
            checkState(ProtocolState.STATUS);
            ctx.writeAndFlush(new PongResponse(pingRequest.getTime()));
        } else if (packet instanceof LoginStart) {
            checkState(ProtocolState.LOGIN);
            ctx.writeAndFlush(new Disconnect(new Gson().toJson(textComponent)));
        }
    }

    private StatusResponse getStatusResponse() {
        JsonObject json = new JsonObject();
        JsonObject version = new JsonObject();
        JsonObject players = new JsonObject();
        JsonObject description = new Gson().toJsonTree(textComponent).getAsJsonObject();

        version.addProperty("name", status);
        version.addProperty("protocol", -1);
        json.add("version", version);
        players.addProperty("max", 0);
        players.addProperty("online", 0);
        json.add("players", players);
        json.add("description", description);
        return new StatusResponse(json.toString());
    }

    private void checkState(ProtocolState expectedState) {
        if (this.currentState != expectedState) {
            throw new IllegalStateException(expectedState.name() + " is expected but currently is " + this.currentState.name());
        }
    }

    private enum ProtocolState {
        HANDSHAKE,
        STATUS,
        LOGIN
    }
}

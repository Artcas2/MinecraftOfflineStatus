package fr.artcas2.mincraftofflinestatus;

import fr.artcas2.mincraftofflinestatus.packets.Disconnect;
import fr.artcas2.mincraftofflinestatus.packets.LoginStart;
import fr.artcas2.mincraftofflinestatus.packets.Packet;
import fr.artcas2.mincraftofflinestatus.packets.handshake.Handshake;
import fr.artcas2.mincraftofflinestatus.packets.status.PingRequest;
import fr.artcas2.mincraftofflinestatus.packets.status.PongResponse;
import fr.artcas2.mincraftofflinestatus.packets.status.StatusRequest;
import fr.artcas2.mincraftofflinestatus.packets.status.StatusResponse;

import java.util.HashMap;
import java.util.Map;

public enum Protocol {
    HANDSHAKE {{
        registerPacket(0x00, Handshake.class, Direction.CLIENT_TO_SERVER);
    }},

    STATUS {{
        registerPacket(0x00, StatusRequest.class, Direction.CLIENT_TO_SERVER);
        registerPacket(0x00, StatusResponse.class, Direction.SERVER_TO_CLIENT);

        registerPacket(0x01, PingRequest.class, Direction.CLIENT_TO_SERVER);
        registerPacket(0x01, PongResponse.class, Direction.SERVER_TO_CLIENT);
    }},

    LOGIN {{
        registerPacket(0x00, LoginStart.class, Direction.CLIENT_TO_SERVER);
        registerPacket(0x00, Disconnect.class, Direction.SERVER_TO_CLIENT);
    }};

    public final ProtocolData data = new ProtocolData();

    public Class<?> getClassById(int id) {
        return data.incoming.get(id);
    }

    public Integer getIdByClass(Class<? extends Packet> packetClass) {
        return data.outgoing.entrySet().stream()
                .filter(entry -> entry.getValue().equals(packetClass))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    protected void registerPacket(int id, Class<? extends Packet> packetClass, Direction direction) {
        if (direction == Direction.CLIENT_TO_SERVER) {
            data.incoming.put(id, packetClass);
        } else {
            data.outgoing.put(id, packetClass);
        }
    }

    public static class ProtocolData {
        final Map<Integer, Class<? extends Packet>> incoming = new HashMap<>();
        final Map<Integer, Class<? extends Packet>> outgoing = new HashMap<>();
    }

    public enum Direction {
        CLIENT_TO_SERVER,
        SERVER_TO_CLIENT
    }
}

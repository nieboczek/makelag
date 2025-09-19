package nieboczek.makelag.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PingS2CPacket(String playerName, int delay) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, PingS2CPacket> CODEC = PacketCodec.tuple(PacketCodecs.STRING, PingS2CPacket::playerName, PacketCodecs.VAR_INT, PingS2CPacket::delay, PingS2CPacket::new);
    public static final CustomPayload.Id<PingS2CPacket> ID = new Id<>(Identifier.of("makelag", "ping"));

    public CustomPayload.Id<PingS2CPacket> getId() {
        return ID;
    }
}

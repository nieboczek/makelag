package nieboczek.makelag.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PingDisplayS2CPacket(boolean displayPing) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, PingDisplayS2CPacket> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, PingDisplayS2CPacket::displayPing, PingDisplayS2CPacket::new);
    public static final CustomPayload.Id<PingDisplayS2CPacket> ID = new Id<>(Identifier.of("makelag", "ping_display"));

    public CustomPayload.Id<PingDisplayS2CPacket> getId() {
        return ID;
    }
}

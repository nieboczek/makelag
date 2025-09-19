package nieboczek.makelag;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import nieboczek.makelag.network.PingDisplayS2CPacket;
import nieboczek.makelag.network.PingS2CPacket;

import java.util.HashMap;
import java.util.Map;

public class MakeLagClient implements ClientModInitializer {
    public static final Map<String, Integer> DELAYS = new HashMap<>();
    public static boolean displayPing = false;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                PingS2CPacket.ID,
                (packet, context) -> DELAYS.put(packet.playerName(), packet.delay())
        );

        ClientPlayNetworking.registerGlobalReceiver(
                PingDisplayS2CPacket.ID,
                (packet, context) -> displayPing = packet.displayPing()
        );
    }
}

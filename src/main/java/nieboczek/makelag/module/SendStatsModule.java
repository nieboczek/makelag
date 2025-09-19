package nieboczek.makelag.module;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

public class SendStatsModule extends Module {
    public static final int STATS_INTERVAL = 1200; // 1 min
    private static final String STATS_STRING = """

§a========== §6MakeLag Stats Report §a==========
§cDropped packets:§6 %s
§cModules ran:§6 %s
§cFake lag spikes:§6 %s
§cLag spikes:§6 %s
""";

    @Override
    public boolean canRun(ServerPlayerEntity player, ModuleState state) {
        return MakeLag.ticksUntilSendStats <= 0;
    }

    @Override
    public void run(ServerPlayerEntity player, ModuleState state) {
        player.sendMessage(Text.literal(STATS_STRING.formatted(MakeLag.droppedPackets, MakeLag.modulesRan, MakeLag.fakeLagSpikes, MakeLag.lagSpikes)));

        MakeLag.ticksUntilSendStats = STATS_INTERVAL;
        MakeLag.droppedPackets = 0;
        MakeLag.modulesRan = -1; // modulesRan will be incremented after this function finishes
        MakeLag.lagSpikes = 0;
    }

    @Override
    public String getName() {
        return "sendStats";
    }
}

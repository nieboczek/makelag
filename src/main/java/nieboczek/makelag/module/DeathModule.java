package nieboczek.makelag.module;

import net.minecraft.server.network.ServerPlayerEntity;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

public class DeathModule extends Module {
    @Override
    public void run(ServerPlayerEntity player, ModuleState state) {
        MakeLag.getState(player, Modules.CHANGE_DEATH_MESSAGE).set(Modules.CHANGE_DEATH_MESSAGE.forceCustomDeathMessage, true);
        player.kill(player.getWorld());
    }

    @Override
    public String getId() {
        return "death";
    }
}

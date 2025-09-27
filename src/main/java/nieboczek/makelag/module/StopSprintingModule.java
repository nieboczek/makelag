package nieboczek.makelag.module;

import net.minecraft.server.network.ServerPlayerEntity;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

public class StopSprintingModule extends Module {
    @Override
    public boolean canRun(ServerPlayerEntity player, ModuleState state) {
        return player.isSprinting();
    }

    @Override
    public void run(ServerPlayerEntity player, ModuleState state) {
        player.setSprinting(false);
    }

    @Override
    public String getId() {
        return "stopSprinting";
    }
}

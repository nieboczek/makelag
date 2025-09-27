package nieboczek.makelag.module;

import net.minecraft.server.network.ServerPlayerEntity;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

public class DismountModule extends Module {
    @Override
    public boolean canRun(ServerPlayerEntity player, ModuleState state) {
        return player.getVehicle() != null;
    }

    @Override
    public void run(ServerPlayerEntity player, ModuleState state) {
        player.dismountVehicle();
    }

    @Override
    public String getId() {
        return "dismount";
    }
}

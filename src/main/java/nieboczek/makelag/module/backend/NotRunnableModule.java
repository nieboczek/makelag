package nieboczek.makelag.module.backend;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class NotRunnableModule extends Module {
    @Override
    public boolean canRun(ServerPlayerEntity player, ModuleState state) {
        return false;
    }

    @Override
    public void run(ServerPlayerEntity player, ModuleState state) {}
}

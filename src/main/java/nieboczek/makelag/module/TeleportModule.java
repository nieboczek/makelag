package nieboczek.makelag.module;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

import java.util.concurrent.TimeUnit;

public class TeleportModule extends Module {
    public Key<Boolean> isTeleporting = key(Key.bool());

    @Override
    public boolean canRun(ServerPlayerEntity player, ModuleState state) {
        return !state.get(isTeleporting) && !MakeLag.positions.isEmpty();
    }

    @Override
    public void run(ServerPlayerEntity player, ModuleState state) {
        Vec3d playerPos = player.getPos();
        float playerYaw = player.getYaw();
        float playerPitch = player.getPitch();

        state.set(isTeleporting, true);
        Vec3d pos = MakeLag.positions.get(MakeLag.random.nextInt(MakeLag.positions.size()));
        player.teleportTo(new TeleportTarget(player.getWorld(), pos, Vec3d.ZERO, playerYaw, playerPitch, TeleportTarget.NO_OP));

        MakeLag.scheduler.schedule(() -> MakeLag.server.executeSync(() -> {
            player.teleportTo(new TeleportTarget(player.getWorld(), playerPos, Vec3d.ZERO, playerYaw, playerPitch, TeleportTarget.NO_OP));
            state.set(isTeleporting, false);
        }), 2, TimeUnit.SECONDS);
    }

    @Override
    public String getName() {
        return "teleport";
    }
}

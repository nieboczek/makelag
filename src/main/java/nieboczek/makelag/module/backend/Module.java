package nieboczek.makelag.module.backend;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Random;

public abstract class Module {
    public static final Key<Float> intensity = Key.zeroToOneFloat("intensity");

    public boolean didRandomChanceSucceed(ModuleState state, Random random) {
        return random.nextFloat() < state.get(intensity);
    }

    public boolean canRun(ServerPlayerEntity player, ModuleState state) {
        return true;
    }

    public abstract void run(ServerPlayerEntity player, ModuleState state);

    public ArrayList<Key<?>> getAllKeys() {
        ArrayList<Key<?>> keys = new ArrayList<>();

        keys.add(intensity);

        return keys;
    }

    public Key<?>[] getConfigKeys() {
        return getAllKeys().stream().filter(key -> key.argumentType() != null).toArray(Key<?>[]::new);
    }

    public abstract String getId();
}

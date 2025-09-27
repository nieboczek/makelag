package nieboczek.makelag.module.backend;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Random;

public abstract class Module {
    public ArrayList<Key<?>> allKeys = new ArrayList<>();
    public ArrayList<Key<?>> configurableKeys = new ArrayList<>();

    public Key<Float> intensity = configKey(Key.zeroToOneFloat("intensity"));

    public boolean didRandomChanceSucceed(ModuleState state, Random random) {
        return random.nextFloat() < state.get(intensity);
    }

    public boolean canRun(ServerPlayerEntity player, ModuleState state) {
        return true;
    }

    public abstract void run(ServerPlayerEntity player, ModuleState state);

    public abstract String getId();

    protected <T> Key<T> key(Key<T> key) {
        allKeys.add(key);
        return key;
    }

    protected <T> Key<T> configKey(Key<T> key) {
        allKeys.add(key);
        configurableKeys.add(key);
        return key;
    }
}

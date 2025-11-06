package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NoIntensityModule;
import nieboczek.makelag.network.DelayedChannelHandler;

import java.util.ArrayList;

public class PacketModule extends NoIntensityModule {
    public static final Key<Integer> delay = Key.notNegativeInt("delay");
    public static final Key<Integer> delayDelta = Key.notNegativeInt("delayDelta");
    public static final Key<Float> dropChance = Key.zeroToOneFloat("dropChance");
    public static final Key<Float> lagSpikeChance = Key.zeroToOneFloat("lagSpikeChance");
    public static final Key<Float> lagSpikeMultiplier = Key.notNegativeFloat("lagSpikeMultiplier");

    public static final Key<DelayedChannelHandler> handler = Key.of();

    @Override
    public ArrayList<Key<?>> getAllKeys() {
        var keys = super.getAllKeys();

        keys.add(delay);
        keys.add(delayDelta);
        keys.add(dropChance);
        keys.add(lagSpikeChance);
        keys.add(lagSpikeMultiplier);
        keys.add(handler);

        return keys;
    }

    @Override
    public String getId() {
        return "packet";
    }
}

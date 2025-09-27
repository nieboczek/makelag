package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NoIntensityModule;
import nieboczek.makelag.network.DelayedChannelHandler;

public class PacketModule extends NoIntensityModule {
    public Key<Integer> delay = configKey(Key.notNegativeInt("delay"));
    public Key<Integer> delayDelta = configKey(Key.notNegativeInt("delayDelta"));
    public Key<Float> dropChance = configKey(Key.zeroToOneFloat("dropChance"));
    public Key<Float> lagSpikeChance = configKey(Key.zeroToOneFloat("lagSpikeChance"));
    public Key<Float> lagSpikeMultiplier = configKey(Key.notNegativeFloat("lagSpikeMultiplier"));

    public Key<DelayedChannelHandler> handler = key(Key.of());

    @Override
    public String getName() {
        return "packet";
    }
}

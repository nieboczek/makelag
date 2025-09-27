package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NoIntensityModule;

public class ConfigModule extends NoIntensityModule {
    public Key<Float> fakeLagSpikeChance = configKey(Key.zeroToOneFloat("fakeLagSpikeChance"));

    @Override
    public String getName() {
        return "config";
    }
}

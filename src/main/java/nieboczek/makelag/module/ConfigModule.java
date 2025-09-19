package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NotRunnableModule;

public class ConfigModule extends NotRunnableModule {
    public Key<Float> fakeLagSpikeChance = configKey(Key.zeroToOneFloat("fakeLagSpikeChance"));

    public ConfigModule() {
        // Remove intensity key
        configurableKeys.removeFirst();
        allKeys.removeFirst();
    }

    @Override
    public String getName() {
        return "config";
    }
}

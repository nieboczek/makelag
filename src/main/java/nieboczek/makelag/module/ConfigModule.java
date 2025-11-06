package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NoIntensityModule;

import java.util.ArrayList;

public class ConfigModule extends NoIntensityModule {
    public static final Key<Float> fakeLagSpikeChance = Key.zeroToOneFloat("fakeLagSpikeChance");

    @Override
    public ArrayList<Key<?>> getAllKeys() {
        var keys = super.getAllKeys();

        keys.add(fakeLagSpikeChance);

        return keys;
    }

    @Override
    public String getId() {
        return "config";
    }
}

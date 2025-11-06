package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NotRunnableModule;

import java.util.ArrayList;

public class ChangeDeathMessageModule extends NotRunnableModule {
    public static final Key<Boolean> forceCustomDeathMessage = Key.bool();

    @Override
    public ArrayList<Key<?>> getAllKeys() {
        var keys = super.getAllKeys();

        keys.add(forceCustomDeathMessage);

        return keys;
    }

    @Override
    public String getId() {
        return "changeDeathMessage";
    }
}

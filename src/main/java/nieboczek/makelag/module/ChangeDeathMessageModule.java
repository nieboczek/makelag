package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NotRunnableModule;

public class ChangeDeathMessageModule extends NotRunnableModule {
    public Key<Boolean> forceCustomDeathMessage = key(Key.bool());

    @Override
    public String getName() {
        return "changeDeathMessage";
    }
}

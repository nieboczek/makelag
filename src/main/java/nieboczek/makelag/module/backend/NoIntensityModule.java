package nieboczek.makelag.module.backend;

import java.util.ArrayList;

public abstract class NoIntensityModule extends NotRunnableModule {
    @Override
    public ArrayList<Key<?>> getAllKeys() {
        return new ArrayList<>();
    }
}

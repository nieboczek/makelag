package nieboczek.makelag.module.backend;

public abstract class NoIntensityModule extends NotRunnableModule {
    public NoIntensityModule() {
        // Remove intensity key
        configurableKeys.removeFirst();
        allKeys.removeFirst();
    }
}

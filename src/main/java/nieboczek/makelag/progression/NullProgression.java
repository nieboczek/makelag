package nieboczek.makelag.progression;

public class NullProgression extends Progression.Provider {
    @Override
    protected void load() {}

    @Override
    public String getId() {
        return "null";
    }
}

package nieboczek.makelag.module.backend;

import java.util.ArrayList;

public class ModuleState {
    private final ArrayList<StoredKey<?>> storedKeys = new ArrayList<>();
    public final Module module;

    public ModuleState(Module module) {
        this.module = module;
        module.allKeys.forEach(key -> storedKeys.add(new StoredKey<>(key)));
    }

    public <T> T get(Key<T> key) {
        return find(key).value;
    }

    public <T> void set(Key<T> key, T value) {
        find(key).value = value;
    }

    @SuppressWarnings("unchecked")
    private <T> StoredKey<T> find(Key<T> key) {
        return (StoredKey<T>) storedKeys.stream().filter(stored -> stored.key == key).findFirst().orElseThrow();
    }

    private static class StoredKey<T> {
        final Key<T> key;
        T value;

        StoredKey(Key<T> key) {
            this.key = key;
            this.value = key.initialValue();
        }
    }
}

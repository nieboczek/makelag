package nieboczek.makelag.config;

import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

import java.util.ArrayList;

public class PlayerConfig {
    public final ArrayList<ModuleState> states = new ArrayList<>();

    public PlayerConfig() {
        Modules.MODULES.forEach(module -> states.add(new ModuleState(module)));
    }

    public ModuleState get(Module module) {
        return states.stream().filter(state -> state.module == module).findFirst().orElseThrow();
    }
}

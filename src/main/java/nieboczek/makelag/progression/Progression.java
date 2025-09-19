package nieboczek.makelag.progression;

import nieboczek.makelag.MakeLag;
import nieboczek.makelag.config.PlayerConfig;
import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

import java.util.*;

public class Progression {
    public static final Provider[] PROVIDERS = {
        new NullProgression(), new DefaultProgression()
    };

    private final Map<Module, List<Keyframe<?>>> timeline = new HashMap<>();
    private String id = "null";

    public void load(Provider provider) {
        provider.apply(this);
    }

    public void add(Keyframe<?> frame) {
        timeline.computeIfAbsent(frame.module(), m -> new ArrayList<>()).add(frame);
        // Keep them ordered by startTick
        timeline.get(frame.module()).sort(Comparator.comparingLong(Keyframe::startTick));
    }

    public void tick(int currentTick) {
        for (Module module : Modules.MODULES) {
            List<ModuleState> states = new ArrayList<>();
            for (PlayerConfig config : MakeLag.playerConfigs.values()) {
                states.add(config.getState(module));
            }
            processFrames(currentTick, states, module);
        }
        processFrames(currentTick, List.of(MakeLag.configState), Modules.CONFIG);
    }

    @SuppressWarnings("unchecked")
    public void resetToDefaults() {
        for (Module module : Modules.MODULES) {
            for (PlayerConfig config : MakeLag.playerConfigs.values()) {
                ModuleState state = config.getState(module);

                for (Key<?> key : module.configurableKeys) {
                    state.set((Key<Object>) key, key.initialValue());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void setTick(int tick) {
        resetToDefaults();

        for (Module module : Modules.MODULES) {
            List<ModuleState> states = new ArrayList<>();
            for (PlayerConfig config : MakeLag.playerConfigs.values()) {
                states.add(config.getState(module));
            }

            for (Map.Entry<Module, List<Keyframe<?>>> entry : timeline.entrySet()) {
                Module key = entry.getKey();
                if (key != module) {
                    continue;
                }

                List<Keyframe<?>> frames = entry.getValue();

                // We already keep them sorted by startTick in add()
                for (Keyframe<?> frame : frames) {
                    Object valueToApply;

                    if (frame.isActive(tick)) {
                        // Somewhere in the frame
                        valueToApply = frame.getValue(tick);
                    } else if (tick > frame.endTick()) {
                        // If a frame has already ended, use its endValue
                        valueToApply = frame.getValue(frame.endTick());
                    } else {
                        // Frame didn't start yet
                        continue;
                    }

                    for (ModuleState state : states) {
                        state.set((Key<Object>) frame.key(), valueToApply);
                    }
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void processFrames(int currentTick, List<ModuleState> states, Module module) {
        for (Map.Entry<Module, List<Keyframe<?>>> entry : timeline.entrySet()) {
            Module key = entry.getKey();
            if (key != module) {
                continue;
            }

            List<Keyframe<?>> frames = entry.getValue();
            for (Keyframe<?> frame : frames) {
                if (!frame.isActive(currentTick)) {
                    continue;
                }

                Object newValue = frame.getValue(currentTick);
                for (ModuleState state : states) {
                    state.set((Key<Object>) frame.key(), newValue);
                }
                break;
            }
        }
    }

    public String getId() {
        return id;
    }

    public abstract static class Provider {
        private Progression progression;

        private void apply(Progression progression) {
            this.progression = progression;
            progression.timeline.clear();
            progression.id = getId();
            load();
        }

        protected <T extends Number> void add(Module module, Key<T> key, int startTick, int endTick, double startValue, double targetValue) {
            progression.add(new Keyframe<>(module, key, startTick, endTick, startValue, targetValue));
        }

        protected abstract void load();

        public abstract String getId();
    }
}

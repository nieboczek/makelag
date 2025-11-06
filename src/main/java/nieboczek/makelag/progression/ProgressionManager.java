package nieboczek.makelag.progression;

import com.google.gson.JsonParseException;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.config.Config;
import nieboczek.makelag.config.PlayerConfig;
import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProgressionManager {
    public static final List<Keyframe<?>> timeline = new ArrayList<>();
    public static int loadedProgressionHash;
    public static String loadedId = "null";

    public static List<String> load(String id) {
        File[] files = Config.getProgressionsDir().listFiles();

        if (files == null) {
            return List.of(
                    "Couldn't load progression:",
                    "Wasn't able to get files in directory config/makelag/progressions"
            );
        }

        for (File file : files) {
            if (!id.equals(FilenameUtils.removeExtension(file.getName()))) {
                continue;
            }

            try (FileReader reader = new FileReader(file)) {
                SerializedProgression progression = Config.gson.fromJson(reader, SerializedProgression.class);
                return deserializeProgression(progression, id, file.hashCode());
            } catch (IOException e) {
                return List.of(
                        "Couldn't load progression:",
                        "An error occurred while trying to read " + file.getName(),
                        e.toString()
                );
            } catch (JsonParseException e) {
                return List.of(
                        "Couldn't load progression:",
                        "An error occurred while trying to parse json from " + file.getName(),
                        e.toString()
                );
            }
        }

        return List.of(
                "Couldn't load progression:",
                "Didn't find a progression with id " + id
        );
    }

    private static List<String> deserializeProgression(SerializedProgression progression, String id, int hash) {
        for (SerializedProgression.SerializedKeyframe serializedFrame : progression.timeline()) {
            Module module = moduleFromStr(serializedFrame.module());
            if (module == null) {
                return List.of(
                        "Couldn't load progression:",
                        "Didn't find a module with id " + serializedFrame.module()
                );
            }

            Key<? extends Number> key = keyFromStr(module, serializedFrame.key());
            if (key == null) {
                return List.of(
                        "Couldn't load progression:",
                        "Didn't find a key with id " + serializedFrame.key() + " in module " + serializedFrame.module()
                );
            }

            timeline.add(new Keyframe<>(
                    module,
                    key,
                    serializedFrame.startTick(),
                    serializedFrame.endTick(),
                    serializedFrame.startValue(),
                    serializedFrame.endValue()
            ));
        }

        timeline.sort(Comparator.comparingLong(Keyframe::startTick));
        loadedProgressionHash = hash;
        loadedId = id;
        return List.of("Loaded progression " + loadedId);
    }

    public static void tick(int currentTick) {
        for (Module module : Modules.MODULES) {
            List<ModuleState> states = new ArrayList<>();
            for (PlayerConfig config : MakeLag.playerConfigs.values()) {
                states.add(config.get(module));
            }
            processFrames(currentTick, states, module);
        }
        processFrames(currentTick, List.of(MakeLag.configState), Modules.CONFIG);
    }

    @SuppressWarnings("unchecked")
    public static void resetToDefaults() {
        for (Module module : Modules.MODULES) {
            for (PlayerConfig config : MakeLag.playerConfigs.values()) {
                ModuleState state = config.get(module);

                for (Key<?> key : module.getConfigKeys()) {
                    state.set((Key<Object>) key, key.initialValue());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void setTick(int tick) {
        resetToDefaults();

        for (Module module : Modules.MODULES) {
            List<ModuleState> states = new ArrayList<>();
            for (PlayerConfig config : MakeLag.playerConfigs.values()) {
                states.add(config.get(module));
            }

            for (Keyframe<?> frame : timeline) {
                if (frame.module() != module) {
                    continue;
                }

                Object valueToApply;
                if (frame.isActive(tick)) {
                    valueToApply = frame.getValue(tick);
                } else if (tick > frame.endTick()) {
                    valueToApply = frame.getValue(frame.endTick());
                } else {
                    continue;
                }

                for (ModuleState state : states) {
                    state.set((Key<Object>) frame.key(), valueToApply);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Key<? extends Number> keyFromStr(Module module, String str) {
        for (Key<?> key : module.getConfigKeys()) {
            if (str.equals(key.id())) {
                return (Key<? extends Number>) key;
            }
        }
        return null;
    }

    private static Module moduleFromStr(String str) {
        if (str.equals("config")) {
            return Modules.CONFIG;
        }

        for (Module module : Modules.MODULES) {
            if (module.getId().equals(str)) {
                return module;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void processFrames(int currentTick, List<ModuleState> states, Module module) {
        for (Keyframe<?> frame : timeline) {
            if (frame.module() != module || !frame.isActive(currentTick)) {
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

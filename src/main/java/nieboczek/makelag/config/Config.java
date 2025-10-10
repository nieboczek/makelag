package nieboczek.makelag.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import nieboczek.makelag.MakeLag;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

public class Config {
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static String[] deathMessages;
    public static final String[] BUILT_IN_PROGRESSIONS = {
            "default", "null"
    };

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void setup() {
        File cfgDir = getCfgDir();
        File deathMessagesFile = new File(cfgDir, "death_messages.json");
        File progressionsDir = new File(cfgDir, "progressions");
        cfgDir.mkdir();
        progressionsDir.mkdir();

        if (!deathMessagesFile.exists()) {
            URL input = MakeLag.class.getResource("/assets/death_messages.json");
            try {
                FileUtils.copyURLToFile(input, deathMessagesFile);
            } catch (IOException e) {
                MakeLag.log.error("[cfg] couldn't copy death_messages.json to config/makelag/death_messages.json");
            }
        }

        for (String builtInProgression : BUILT_IN_PROGRESSIONS) {
            File file = new File(progressionsDir, builtInProgression + ".json");
            if (!file.exists()) {
                URL input = MakeLag.class.getResource("/assets/progressions/" + builtInProgression + ".json");
                try {
                    FileUtils.copyURLToFile(input, file);
                } catch (IOException e) {
                    MakeLag.log.error("[cfg] couldn't copy {}.json to config/makelag/progressions/{}.json", builtInProgression, builtInProgression);
                }
            }
        }
    }

    public static void reload() {
        try {
            File cfgDir = getCfgDir();
            File deathMessagesFile = new File(cfgDir, "death_messages.json");

            try (FileReader reader = new FileReader(deathMessagesFile)) {
                deathMessages = gson.fromJson(reader, String[].class);
            }
            MakeLag.log.info("[cfg] reloaded successfully");
        } catch (IOException e) {
            MakeLag.log.error("[cfg] error while reloading: ", e);
        }
    }

    public static File getCfgDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("makelag").toFile();
    }

    public static File getProgressionsDir() {
        return new File(getCfgDir(), "progressions");
    }
}

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
    public static String[] deathMessages = {
            "{} died from lack of internet",
            "{} should buy better internet",
            "{} realized that they should die",
            "Lag caught up with {}",
            "Server noticed that {} should have zero hearts",
            "Server didn't forget to make {} die",
            "{} tripped over dropped packets",
            "Ping of {} was so high, that the server gave up calculating",
            "Death time of {} was dependent on the network delay",
            "{} dropped the life support packet",
            "{} died fr[PROTOCOL ERROR]",
            "{} forgot to drop the death packet",
            "[Loading the death reason of {}...]",
            "{} alt-tabbed to their death",
            "{} collided with the death packet",
            "{} forgot to live",
            "{} has and will have lags",
            "Router of {} didn't work",
            "{} didn't download more RAM",
            "{} should buy TP-Link Archer C6 as their router and apply the rule of separated connections by buying TP-Link 8-Port 10/100/1000Mbps Desktop Network Switch for only $99.99 with a free delivery to hell",
            "Server doesn't like {}",
            "Brutality of {} offended packets",
            "{} was sending too much packets",
            "{} didn't generate 10000 gems in brawl stars",
            "{} didn't invest in internet",
            "{} dD%D I~ii~ $EE#e Dd)D&D",
            "RIP {} (born today; died today)",
            "{} delayed their whole life",
            "{} peed on their internet",
            "{} drowned in massive ping",
            "{} should buy an ethernet cable",
            "{} died from their ISP"
    };
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

            loadDeathMessages(deathMessagesFile);
            MakeLag.log.info("[cfg] reloaded successfully");
        } catch (IOException e) {
            MakeLag.log.error("[cfg] error while reloading: ", e);
        }
    }

    private static void loadDeathMessages(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            deathMessages = gson.fromJson(reader, String[].class);
        } catch (FileNotFoundException e) {
            MakeLag.log.info("[cfg] didn't find death_messages.json, creating");
            saveDeathMessages(file);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void saveDeathMessages(File file) throws IOException {
        file.createNewFile();

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(deathMessages, writer);
        }
    }

    public static File getCfgDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("makelag").toFile();
    }

    public static File getProgressionsDir() {
        return new File(getCfgDir(), "progressions");
    }
}

package nieboczek.makelag.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import nieboczek.makelag.MakeLag;

import java.io.*;

public class Config {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String DEATH_MESSAGES_FILE = "death_messages.json";
    private static final String PROGRESSIONS_DIR = "progressions";

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

    public static void load() {
        try {
            loadInternal();
            MakeLag.log.info("[cfg] loaded successfully");
        } catch (IOException e) {
            MakeLag.log.error("[cfg] error while loading: ", e);
        }
    }

    public static void save() {
        try {
            saveInternal();
            MakeLag.log.info("[cfg] saved successfully");
        } catch (IOException e) {
            MakeLag.log.error("[cfg] error while saving: ", e);
        }
    }

    private static void loadInternal() throws IOException {
        File cfgDir = getCfgDir();
        File deathMessagesFile = new File(cfgDir, DEATH_MESSAGES_FILE);
        File progressionsDir = new File(cfgDir, PROGRESSIONS_DIR);

        checkState(cfgDir, progressionsDir);

        readDeathMessages(deathMessagesFile);

        // TODO: further logic to load progressions
    }

    private static void saveInternal() throws IOException {
        File cfgDir = getCfgDir();
        File deathMessagesFile = new File(cfgDir, DEATH_MESSAGES_FILE);
        File progressionsDir = new File(cfgDir, PROGRESSIONS_DIR);

        checkState(cfgDir, progressionsDir);

        saveDeathMessages(deathMessagesFile);

        // TODO: further logic to save progressions
    }

    private static void readDeathMessages(File file) throws IOException {
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void checkState(File cfgDir, File progressionsDir) {
        cfgDir.mkdir();

        if (!progressionsDir.exists()) {
            if (!progressionsDir.mkdir()) {
                MakeLag.log.error("[cfg] couldn't create progressions directory");
            }
        }
    }

    private static File getCfgDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("makelag").toFile();
    }
}

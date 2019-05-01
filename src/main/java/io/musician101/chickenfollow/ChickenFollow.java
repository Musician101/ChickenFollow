package io.musician101.chickenfollow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ChickenFollow.MOD_ID, name = ChickenFollow.MOD_NAME, version = ChickenFollow.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ChickenFollow {

    public static final String MOD_ID = "chicken_follow";
    public static final String MOD_NAME = "ChickenFollow";
    public static final String VERSION = "@VERSION@";

    @Instance(MOD_ID)
    public static ChickenFollow INSTANCE;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private final List<String> targets = new ArrayList<>();
    private File configDir;
    private Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory();
        logger = event.getModLog();
        loadConfig();
        MinecraftForge.EVENT_BUS.register(new ChickenListener());
    }

    public void loadConfig() {
        targets.clear();
        File configFile = new File(configDir, "chicken_follow.json");
        if (!configFile.exists()) {
            try {
                configDir.mkdirs();
                configFile.createNewFile();
                FileWriter writer = new FileWriter(configFile);
                JsonArray json = new JsonArray();
                json.add("Musician101");
                json.add("Chefbrando");
                gson.toJson(json, writer);
                writer.close();
            }
            catch (Exception e) {
                logger.error("Failed to create default config!", e);
            }

        }

        try (FileReader reader = new FileReader(configFile)) {
            targets.addAll(gson.fromJson(reader, new TypeToken<List<String>>() {

            }.getType()));
        }
        catch (Exception e) {
            logger.error("Failed to read the config!", e);
        }
    }

    public List<String> getTargets() {
        return targets;
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new ReloadCommand());
    }
}

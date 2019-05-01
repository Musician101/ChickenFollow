package io.musician101.chickenfollow;

import com.google.common.base.Predicates;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChickenFollow.MOD_ID)
public class ChickenFollow {

    public static final String MOD_ID = "chicken_follow";
    public static final String MOD_NAME = "ChickenFollow";
    public static final String VERSION = "@VERSION@";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    private final List<String> targets = new ArrayList<>();
    private File configDir;
    private final Logger logger = LogManager.getLogger(MOD_NAME);

    public static ChickenFollow instance() {
        return ModList.get().getModObjectById(MOD_ID).filter(ChickenFollow.class::isInstance).map(ChickenFollow.class::cast).orElseThrow(() -> new IllegalStateException("ChickenFollow is not enabled."));
    }

    public ChickenFollow() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
    }

    private void preInit(FMLCommonSetupEvent event) {
        configDir = new File("config");
        loadConfig();
        NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID), () -> VERSION, Predicates.alwaysTrue(), Predicates.alwaysTrue());
    }

    private void onSpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityChicken)) {
            return;
        }

        EntityChicken chicken = (EntityChicken) entity;
        chicken.targetTasks.addTask(0, new ChickenFollowPlayer(chicken));
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

    private void onServerStart(FMLServerStartingEvent event) {
        MinecraftForge.EVENT_BUS.addListener(this::onSpawn);
        event.getCommandDispatcher().register(LiteralArgumentBuilder.<CommandSource>literal("cfr").executes(context -> {
            loadConfig();
            context.getSource().sendFeedback(new TextComponentString("ChickenFollow config reloaded."), true);
            return 1;
        }));
    }
}

package dev.astra.visuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.astra.visuals.AstraVisualsClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("astravisuals.json");

    private ConfigManager() {
    }

    public static VisualConfig load() {
        if (!Files.exists(PATH)) {
            VisualConfig config = new VisualConfig();
            save(config);
            return config;
        }

        try (Reader reader = Files.newBufferedReader(PATH)) {
            VisualConfig config = GSON.fromJson(reader, VisualConfig.class);
            if (config == null) {
                config = new VisualConfig();
            }
            config.normalize();
            return config;
        } catch (Exception exception) {
            AstraVisualsClient.LOGGER.warn("Could not read Astra Visuals config; defaults will be used.", exception);
            return new VisualConfig();
        }
    }

    public static void save(VisualConfig config) {
        config.normalize();

        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            AstraVisualsClient.LOGGER.warn("Could not save Astra Visuals config.", exception);
        }
    }
}

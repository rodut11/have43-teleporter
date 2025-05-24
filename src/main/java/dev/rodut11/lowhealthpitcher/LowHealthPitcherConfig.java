package dev.rodut11.lowhealthpitcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LowHealthPitcherConfig {
    public boolean enabled = true;
    public float setYaw = -65.19f;
    public float setPitch = -54.23f;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "lowhealthpitcher.json");

    public void save() {
        try {
            java.nio.file.Files.createDirectories(CONFIG_PATH.getParent());
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LowHealthPitcherConfig load() {
        if (java.nio.file.Files.exists(CONFIG_PATH)) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                return GSON.fromJson(reader, LowHealthPitcherConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new LowHealthPitcherConfig();
    }
}
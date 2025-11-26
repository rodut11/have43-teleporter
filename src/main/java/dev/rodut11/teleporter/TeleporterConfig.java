package dev.rodut11.teleporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TeleporterConfig {
    public boolean enabled = true;
    public float setYaw = -65.19f;
    public float setPitch = -54.23f;
    public float minHp = 8.0f;
    public float lockSeconds = 3.0f;
    public List<AnglePreset> anglePresets = new ArrayList<>();
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
    public static TeleporterConfig load() {
        if (java.nio.file.Files.exists(CONFIG_PATH)) {
            try (java.io.Reader reader = java.nio.file.Files.newBufferedReader(CONFIG_PATH)) {
                return GSON.fromJson(reader, TeleporterConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new TeleporterConfig();
    }
}
package dev.rodut11.lowhealthpitcher;

public class AnglePreset {
    public String name;
    public float yaw;
    public float pitch;

    public AnglePreset() {}

    public AnglePreset(String name, float yaw, float pitch) {
        this.name = name;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
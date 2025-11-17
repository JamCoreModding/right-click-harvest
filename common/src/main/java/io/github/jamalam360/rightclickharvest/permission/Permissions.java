package io.github.jamalam360.rightclickharvest.permission;

public enum Permissions {

    HARVEST("rightclickharvest.radius_harvest");

    private final String permission;

    Permissions(String permission) { this.permission = permission; }

    public String getPermission() { return this.permission; }
}

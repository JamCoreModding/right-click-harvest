package io.github.jamalam360.rightclickharvest.permission;

import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.minecraft.world.entity.player.Player;

public class PermissionManager {

    private static PermissionManager instance;
    private LuckPerms luckPerms;

    private PermissionManager() {
        if (RightClickHarvest.CONFIG.get().enablePermissions) {
            try {
                this.luckPerms = LuckPermsProvider.get();
            } catch (IllegalStateException e) {
                this.luckPerms = null;
                RightClickHarvest.LOGGER.info(
                    "Environment don't have LuckPerms installed, no " +
                    "permissions "
                    + "will be handled");
            }
        }
    }

    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    public boolean hasPermission(Player player, Permissions permission) {
        if (luckPerms == null) {
            return true;
        }
        User user = luckPerms.getUserManager().getUser(player.getUUID());
        return user.getNodes(NodeType.PERMISSION)
            .stream()
            .anyMatch(it
                      -> it.getPermission().equalsIgnoreCase(
                          permission.getPermission()));
    }
}

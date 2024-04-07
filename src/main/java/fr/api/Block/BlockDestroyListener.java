package fr.api.Block;

import fr.api.basededonnees.SQLiteManager;
import fr.api.InventoryManager.InventoryListener;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class BlockDestroyListener implements Listener {
    private final SQLiteManager sqliteManager;
    private static final Logger logger = Logger.getLogger(BlockDestroyListener.class.getName());


    public BlockDestroyListener(SQLiteManager sqliteManager) {
        this.sqliteManager = sqliteManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (isNaturalBlock(blockType)) {
            try {
                String playerName = event.getPlayer().getName();
                saveBlockData(playerName, blockType);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNaturalBlock(Material blockType) {
        Material[] naturalBlocks = {
                Material.OAK_LOG,
                Material.SPRUCE_LOG,
                Material.BIRCH_LOG,
                Material.JUNGLE_LOG,
                Material.ACACIA_LOG,
                Material.DARK_OAK_LOG,
                Material.CRIMSON_STEM,
                Material.WARPED_STEM,
                Material.STRIPPED_OAK_LOG,
                Material.STRIPPED_SPRUCE_LOG,
                Material.STRIPPED_BIRCH_LOG,
                Material.STRIPPED_JUNGLE_LOG,
                Material.STRIPPED_ACACIA_LOG,
                Material.STRIPPED_DARK_OAK_LOG,
                Material.STRIPPED_CRIMSON_STEM,
                Material.STRIPPED_WARPED_STEM,
                Material.OAK_WOOD,
                Material.SPRUCE_WOOD,
                Material.BIRCH_WOOD,
                Material.JUNGLE_WOOD,
                Material.ACACIA_WOOD,
                Material.DARK_OAK_WOOD,
                Material.CRIMSON_HYPHAE,
                Material.WARPED_HYPHAE,
                Material.STRIPPED_OAK_WOOD,
                Material.STRIPPED_SPRUCE_WOOD,
                Material.STRIPPED_BIRCH_WOOD,
                Material.STRIPPED_JUNGLE_WOOD,
                Material.STRIPPED_ACACIA_WOOD,
                Material.STRIPPED_DARK_OAK_WOOD,
                Material.STRIPPED_CRIMSON_HYPHAE,
                Material.STRIPPED_WARPED_HYPHAE,
                Material.GRASS_BLOCK,
                Material.DIRT,
                Material.STONE,
                Material.SAND,
                Material.GRAVEL,
                Material.CLAY,
                Material.RED_SAND,
                Material.MOSSY_COBBLESTONE,
                Material.PODZOL,
                Material.COBBLESTONE,
                Material.SNOW,
                Material.ICE,
                Material.BLUE_ICE,
                Material.PACKED_ICE,
                Material.END_STONE,
                Material.END_STONE_BRICKS,
                Material.END_STONE_BRICK_SLAB,
                Material.END_STONE_BRICK_STAIRS,
                Material.END_STONE_BRICK_WALL,
                Material.END_ROD,
                Material.LAVA,
                Material.WATER,
                Material.WATER_CAULDRON,
                Material.LIGHT_BLUE_WOOL,
                Material.GREEN_WOOL,
                Material.SEAGRASS,
                Material.KELP_PLANT,
                Material.LIGHT,
                Material.NETHERRACK,
                Material.GLOWSTONE,
                Material.SOUL_SAND,
                Material.QUARTZ_BLOCK,
                Material.SOUL_SOIL,
                Material.MAGMA_BLOCK,
                Material.BASALT,
                Material.POLISHED_BASALT,
                Material.CRIMSON_NYLIUM,
                Material.WARPED_NYLIUM,
                Material.CRIMSON_HYPHAE,
                Material.WARPED_HYPHAE,
                Material.SHROOMLIGHT,
                Material.NETHER_WART_BLOCK,
                Material.WARPED_WART_BLOCK,
                Material.COAL_ORE,
                Material.IRON_ORE,
                Material.COPPER_ORE,
                Material.GOLD_ORE,
                Material.REDSTONE_ORE,
                Material.LAPIS_ORE,
                Material.DIAMOND_ORE,
                Material.EMERALD_ORE,
                Material.DEEPSLATE_COAL_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.DEEPSLATE_COPPER_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.DEEPSLATE_DIAMOND_ORE,
                Material.DEEPSLATE_EMERALD_ORE,
                Material.COBBLED_DEEPSLATE,
                Material.DEEPSLATE,
                Material.TUFF,
                Material.DRIPSTONE_BLOCK,
                Material.AMETHYST_BLOCK,
                Material.AMETHYST_CLUSTER,
                Material.CALCITE,
                Material.DRIPSTONE_BLOCK,
                Material.SMOOTH_BASALT,
                Material.AZALEA,
                Material.AZALEA_LEAVES,
                Material.MOSS_CARPET,
                Material.MOSS_BLOCK,
                Material.AZURE_BLUET,
                Material.BIG_DRIPLEAF,
                Material.BIG_DRIPLEAF_STEM,
                Material.CAVE_VINES,
                Material.CAVE_VINES_PLANT,
                Material.CAVE_AIR,
                Material.DEAD_BUSH,
                Material.GLOW_LICHEN,
                Material.GRANITE,
                Material.DIORITE,
                Material.ANDESITE,
                Material.TUFF,
                Material.DEAD_TUBE_CORAL,
                Material.DEAD_BRAIN_CORAL,
                Material.DEAD_BUBBLE_CORAL,
                Material.DEAD_FIRE_CORAL,
                Material.DEAD_HORN_CORAL,
                Material.DEAD_TUBE_CORAL_FAN,
                Material.DEAD_BRAIN_CORAL_FAN,
                Material.DEAD_BUBBLE_CORAL_FAN,
                Material.DEAD_FIRE_CORAL_FAN,
                Material.DEAD_HORN_CORAL_FAN,
                Material.SMOOTH_BASALT,
                Material.LAVA,
                Material.WATER

        };

        for (Material naturalBlock : naturalBlocks) {
            if (blockType == naturalBlock) {
                return true;
            }
        }
        return false;
    }

    private void saveBlockData(String playerName, Material blockType) throws SQLException {
        String selectQuery = "SELECT amount FROM player_destroyblocks WHERE player_name = ? AND block_name = ?";
        try (PreparedStatement selectStatement = sqliteManager.getConnection().prepareStatement(selectQuery)) {
            selectStatement.setString(1, playerName);
            selectStatement.setString(2, blockType.toString());
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                int destroyCount = resultSet.getInt("amount");
                String updateQuery = "UPDATE player_destroyblocks SET amount = ?, block_image = ? WHERE player_name = ? AND block_name = ?";
                try (PreparedStatement updateStatement = sqliteManager.getConnection().prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, destroyCount + 1);
                    updateStatement.setString(2, getImageBase64(blockType.toString())); // Appel de la fonction getImageBase64
                    updateStatement.setString(3, playerName);
                    updateStatement.setString(4, blockType.toString());
                    updateStatement.executeUpdate();
                }
            } else {
                String insertQuery = "INSERT INTO player_destroyblocks (player_name, block_name, amount, block_image) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStatement = sqliteManager.getConnection().prepareStatement(insertQuery)) {
                    insertStatement.setString(1, playerName);
                    insertStatement.setString(2, blockType.toString());
                    insertStatement.setInt(3, 1);
                    insertStatement.setString(4, getImageBase64(blockType.toString())); // Appel de la fonction getImageBase64
                    insertStatement.executeUpdate();
                }
            }
        }
    }
    private String getImageBase64(String nomItem) {
        String dossierImages = "blocks_destroy" + File.separator;

        String imageBase64 = "";

        try {
            Path imagePath = Paths.get(dossierImages + nomItem + ".png");
            byte[] imageBytes = Files.readAllBytes(imagePath);
            imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            logger.severe("Erreur lors de la lecture de l'image : " + e.getMessage());
        }

        return imageBase64;
    }
}
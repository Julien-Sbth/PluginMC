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
                Material.ACACIA_LOG,
                Material.ACACIA_PLANKS,
                Material.AMETHYST_BLOCK,
                Material.AMETHYST_CLUSTER,
                Material.ANCIENT_DEBRIS,
                Material.ANDESITE,
                Material.BAMBOO_BLOCK,
                Material.BAMBOO_PLANKS,
                Material.BASALT,
                Material.BEE_NEST,
                Material.BIRCH_LOG,
                Material.BIRCH_PLANKS,
                Material.BLACKSTONE,
                Material.BLUE_ICE,
                Material.BONE_BLOCK,
                Material.BOOKSHELF,
                Material.BRICK,
                Material.BROWN_MUSHROOM_BLOCK,
                Material.BUDDING_AMETHYST,
                Material.CACTUS,
                Material.CALCITE,
                Material.CARVED_PUMPKIN,
                Material.CHERRY_LOG,
                Material.CHERRY_PLANKS,
                Material.CHISELED_BOOKSHELF,
                Material.CHISELED_DEEPSLATE,
                Material.CHISELED_NETHER_BRICKS,
                Material.CHISELED_POLISHED_BLACKSTONE,
                Material.CHISELED_QUARTZ_BLOCK,
                Material.CHISELED_RED_SANDSTONE,
                Material.SANDSTONE,
                Material.STONE_BRICKS,
                Material.CLAY,
                Material.COAL_ORE,
                Material.COARSE_DIRT,
                Material.COBBLED_DEEPSLATE,
                Material.COBBLESTONE,
                Material.COPPER_BLOCK,
                Material.CRACKED_DEEPSLATE_BRICKS,
                Material.DEEPSLATE_TILES,
                Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                Material.CRACKED_STONE_BRICKS,
                Material.CRYING_OBSIDIAN,
                Material.CUT_COPPER,
                Material.CUT_RED_SANDSTONE,
                Material.CUT_SANDSTONE,
                Material.DARK_OAK_LOG,
                Material.DARK_OAK_PLANKS,
                Material.DARK_PRISMARINE,
                Material.DEEPSLATE,
                Material.DEEPSLATE_BRICKS,
                Material.DEEPSLATE_COAL_ORE,
                Material.DEEPSLATE_COPPER_ORE,
                Material.DEEPSLATE_DIAMOND_ORE,
                Material.DEEPSLATE_EMERALD_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.DEEPSLATE_TILES,
                Material.DIAMOND_ORE,
                Material.DIORITE,
                Material.DIRT,
                Material.DIRT_PATH,
                Material.EMERALD_ORE,
                Material.END_STONE,
                Material.FARMLAND,
                Material.FROSTED_ICE,
                Material.GILDED_BLACKSTONE,
                Material.GLOW_ITEM_FRAME,
                Material.GOLD_ORE,
                Material.GRANITE,
                Material.GRASS_BLOCK,
                Material.GRAVEL,
                Material.HAY_BLOCK,
                Material.HONEYCOMB_BLOCK,
                Material.ICE,
                Material.IRON_ORE,
                Material.ITEM_FRAME,
                Material.JACK_O_LANTERN,
                Material.JUNGLE_LOG,
                Material.JUNGLE_PLANKS,
                Material.LAPIS_ORE,
                Material.MANGROVE_LOG,
                Material.MANGROVE_PLANKS,
                Material.MELON,
                Material.MOSS_BLOCK,
                Material.MUD,
                Material.MUD_BRICKS,
                Material.MYCELIUM,
                Material.NETHER_BRICK,
                Material.NETHER_GOLD_ORE,
                Material.NETHER_QUARTZ_ORE,
                Material.NETHER_WART_BLOCK,
                Material.NETHERRACK,
                Material.OAK_LOG,
                Material.OAK_PLANKS,
                Material.OBSIDIAN,
                Material.OXIDIZED_COPPER,
                Material.OXIDIZED_CUT_COPPER,
                Material.PACKED_ICE,
                Material.PACKED_MUD,
                Material.PRISMARINE_BRICKS,
                Material.PUMPKIN,
                Material.PURPUR_PILLAR,
                Material.PURPUR_BLOCK,
                Material.RAW_COPPER_BLOCK,
                Material.RAW_GOLD_BLOCK,
                Material.RAW_IRON_BLOCK,
                Material.RED_MUSHROOM,
                Material.RED_MUSHROOM_BLOCK,
                Material.RED_NETHER_BRICKS,
                Material.RED_SAND,
                Material.RED_SANDSTONE,
                Material.REDSTONE_ORE,
                Material.SAND,
                Material.SANDSTONE,
                Material.SPRUCE_LOG,
                Material.SPRUCE_PLANKS,
                Material.STONE,
                Material.STONE_BRICKS,
                Material.SUGAR_CANE,
                Material.TUFF,
                Material.WARPED_NYLIUM,
                Material.WARPED_PLANKS,
                Material.WARPED_WART_BLOCK,
                Material.WEATHERED_COPPER,
                Material.CRIMSON_STEM,
                Material.OAK_WOOD,
                Material.SPRUCE_WOOD,
                Material.BIRCH_WOOD,
                Material.JUNGLE_WOOD,
                Material.ACACIA_WOOD,
                Material.DARK_OAK_WOOD,
                Material.CRIMSON_HYPHAE,
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
/*
 * The MIT License.
 *
 *  Copyright (c) 2019 Nekoneko
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package dev.nekoneko.revert;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class RevertCrossBlock extends JavaPlugin implements Listener {

    private final BlockFace[] CROSS_FACES = {
            BlockFace.EAST,
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    private Material magicWandItem = Material.BONE;

    @Override
    public void onEnable() {
        this.loadConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

        new Metrics(this);
    }

    private void loadConfig() {
        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();
        String itemKey = config.getString("wand-item", this.magicWandItem.getKey().getKey());
        if (itemKey == null) {
            throw new IllegalStateException();
        }

        this.magicWandItem = Material.matchMaterial(itemKey);
    }

    @EventHandler(ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.hasBlock()) {
            return;
        }
        if (event.getItem().getType() != this.magicWandItem) {
            return;
        }
        if (!event.getPlayer().hasPermission("revertcrossblock.use")) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        BlockData blockData = clickedBlock.getBlockData();
        if (blockData instanceof Fence || blockData instanceof GlassPane) {
            MultipleFacing facing = (MultipleFacing) blockData;
            for (BlockFace blockFace : CROSS_FACES) {
                facing.setFace(blockFace, true);
            }
            clickedBlock.setBlockData(facing);
            event.setCancelled(true);
        }
    }
}

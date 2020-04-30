package ru.ktrd.waterwater;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WaterWater.MODID)
public class WaterWater
{
    public static final String MODID = "waterwater";

    private static final Logger LOGGER = LogManager.getLogger();

    public WaterWater() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            blockRegistryEvent.getRegistry().registerAll(
                new StoneWaterBlock(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(StoneWaterBlock.REGISTRYNAME + "_wood"),
                new StoneWaterBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)).setRegistryName(StoneWaterBlock.REGISTRYNAME + "_iron"),
                new StoneWaterBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F)).setRegistryName(StoneWaterBlock.REGISTRYNAME),
                new BoxForSource(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName(BoxForSource.REGISTRYNAME + "_wood"),
                new BoxForSource(Block.Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)).setRegistryName(BoxForSource.REGISTRYNAME + "_iron"),
                new BoxForSource(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 6.0F)).setRegistryName(BoxForSource.REGISTRYNAME)
            );
        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent){
            Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS);
            itemRegistryEvent.getRegistry().registerAll(
                    new BlockItem(StoneWaterBlock.STONEWATERBLOCKIRON,properties).setRegistryName(StoneWaterBlock.REGISTRYNAME+"_iron"),
                    new BlockItem(StoneWaterBlock.STONEWATERBLOCKWOOD,properties).setRegistryName(StoneWaterBlock.REGISTRYNAME+"_wood"),
                    new BlockItem(StoneWaterBlock.STONEWATERBLOCK,properties).setRegistryName(StoneWaterBlock.REGISTRYNAME),
                    new BlockItem(BoxForSource.BOXFORSOURCEIRON,properties).setRegistryName(BoxForSource.REGISTRYNAME+"_iron"),
                    new BlockItem(BoxForSource.BOXFORSOURCEWOOD,properties).setRegistryName(BoxForSource.REGISTRYNAME+"_wood"),
                    new BlockItem(BoxForSource.BOXFORSOURCE,properties).setRegistryName(BoxForSource.REGISTRYNAME)
            );
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onClientSetupEvent(FMLClientSetupEvent event) {
            RenderTypeLookup.setRenderLayer(StoneWaterBlock.STONEWATERBLOCK, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(StoneWaterBlock.STONEWATERBLOCKIRON, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(StoneWaterBlock.STONEWATERBLOCKWOOD, RenderType.getTranslucent());
        }
    }
}

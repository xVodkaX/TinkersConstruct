package tconstruct.tools;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Logger;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.CommonProxy;
import tconstruct.TinkerPulse;
import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.modifiers.IModifier;
import tconstruct.library.modifiers.Modifier;
import tconstruct.library.tools.ToolPart;
import tconstruct.tools.block.ToolTableBlock;
import tconstruct.tools.debug.TempToolCrafting;
import tconstruct.tools.debug.TempToolModifying;
import tconstruct.tools.item.ItemTable;
import tconstruct.tools.modifiers.DiamondModifier;
import tconstruct.tools.modifiers.RedstoneModifier;
import tconstruct.tools.modifiers.StoneboundModifier;
import tconstruct.tools.tileentity.TileTable;

@Pulse(id = TinkerTools.PulseId, description = "All the tools and everything related to it.")
public class TinkerTools extends TinkerPulse {

  public static final String PulseId = "TinkerTools";
  static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "tconstruct.tools.ToolClientProxy", serverSide = "tconstruct.CommonProxy")
  public static CommonProxy proxy;

  // Blocks
  public static ToolTableBlock toolTables;

  // Tools
  public static Item pickaxe;

  // Tool Parts
  public static ToolPart pickHead;

  public static ToolPart toolRod;
  public static ToolPart binding;

  // Modifiers
  public static IModifier diamondMod;
  public static IModifier fortifyMod;
  public static IModifier redstoneMod;


  // PRE-INITIALIZATION
  @Handler
  public void preInit(FMLPreInitializationEvent event) {
    TinkerMaterials.registerToolMaterials();

    // register items
    registerToolParts();
    registerTools();
    registerModifiers();

    // register blocks
    toolTables = registerBlock(new ToolTableBlock(), ItemTable.class, "ToolTables");
    GameRegistry.registerTileEntity(TileTable.class, "Table");

    proxy.registerModels();


    // debug things
    // todo: remove. ignore this
    new StoneboundModifier();
    GameRegistry.addRecipe(new TempToolCrafting());
    GameRegistry.addRecipe(new TempToolModifying());

    // register events
    if(event.getSide().isClient()) {
      TinkerMaterials.registerMaterialRendering();
      MinecraftForge.EVENT_BUS.register(new ToolClientEvents());
    }
  }

  private void registerToolParts() {
    pickHead = registerItem(new ToolPart(), "PickHead");

    toolRod = registerItem(new ToolPart(), "ToolRod");
    binding = registerItem(new ToolPart(), "Binding");
  }

  private void registerTools() {
    pickaxe = registerItem(new Pickaxe(), "Pickaxe");
  }

  private void registerModifiers() {
    diamondMod = new DiamondModifier();
    redstoneMod = new RedstoneModifier(50);

    // todo: fix
    fortifyMod = new Modifier("Fortify") {

      @Override
      public void updateNBT(NBTTagCompound modifierTag) {

      }

      @Override
      public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {

      }


      @SideOnly(Side.CLIENT)
      @Override
      public boolean hasTexturePerMaterial() {
        return true;
      }
    };
  }


  // INITIALIZATION
  @Handler
  public void init(FMLInitializationEvent event) {
    registerRecipies();
  }

  private void registerRecipies() {
    // todo: remove debug recipe stuff
    ItemStack pattern = new ItemStack(Items.stick);

    // Crafting Station
    GameRegistry.addRecipe(
        new ShapelessOreRecipe(new ItemStack(toolTables, 1, ToolTableBlock.TableTypes.CraftingStation.meta),
                               "workbench"));
    // Stencil Table
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("plankWood"), toolTables, ToolTableBlock.TableTypes.StencilTable.meta,
                        "P", "B", 'P', pattern, 'B', "plankWood"));
    // Part Builder
    GameRegistry.addRecipe(
        new TableRecipe(OreDictionary.getOres("logWood"), toolTables, ToolTableBlock.TableTypes.PartBuilder.meta, "P",
                        "B", 'P', pattern, 'B', "logWood"));
    // Tool Station
    GameRegistry.addRecipe(
        new ShapedOreRecipe(new ItemStack(toolTables, 1, ToolTableBlock.TableTypes.ToolStation.meta),
                            "P", "B", 'P', pattern, 'B', "workbench"));
    // Tool Forge
    TinkerRegistry.addToolForgeBlock("blockIron");
    TinkerRegistry.addToolForgeBlock("blockGold");
  }

  // called by TinkerRegistry.addToolForgeBlock
  public static void registerToolForgeRecipe(String oredict) {
    // todo: change recipe to brick vs. smeltery-bricks wether smeltery pulse is active
    GameRegistry
        .addRecipe(new TableRecipe(OreDictionary.getOres(oredict), toolTables, ToolTableBlock.TableTypes.ToolForge.meta,
                                   "BBB",
                                   "MTM",
                                   "M M",
                                   'B', Blocks.stonebrick,
                                   'M', oredict,
                                   'T', new ItemStack(toolTables, 1, ToolTableBlock.TableTypes.ToolStation.meta)));
  }

  // POST-INITIALIZATION
  @Handler
  public void postInit(FMLPostInitializationEvent event) {

  }
}

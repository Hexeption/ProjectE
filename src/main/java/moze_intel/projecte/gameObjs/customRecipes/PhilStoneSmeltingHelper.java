package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

public class PhilStoneSmeltingHelper implements IResourceManagerReloadListener
{
    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager)
    {
        if (!ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD)
                .getWorldInfo().getDisabledDataPacks().contains("mod:" + PECore.MODID))
        {
            RecipeManager mgr = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
            for (IRecipe<?> r : mgr.getRecipes(IRecipeType.SMELTING).values())
            {
                if (r.getIngredients().isEmpty()
                        || r.getIngredients().get(0).hasNoMatchingItems()
                        || r.getRecipeOutput().isEmpty())
                {
                    continue;
                }

                Ingredient input = r.getIngredients().get(0);
                ItemStack output = r.getRecipeOutput().copy();
                output.setCount(output.getCount() * 7);

                String inputName = r.getId().toString().replace(':', '_');
                ResourceLocation recipeName = new ResourceLocation(PECore.MODID, "philstone_smelt_" + inputName);

                NonNullList<Ingredient> ingrs = NonNullList.from(Ingredient.EMPTY,
                        Ingredient.fromItems(ObjHandler.philosStone),
                        input, input, input, input, input, input, input,
                        Ingredient.fromItems(Items.COAL, Items.CHARCOAL));
                // todo 1.14 immutable now so can't add to it >.>
                // mgr.getRecipes(IRecipeType.CRAFTING).put(recipeName, new RecipeShapelessHidden(recipeName, "projecte:philstone_smelt", output, ingrs));
            }
        }
    }
}

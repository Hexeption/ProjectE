package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonObject;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;


import javax.annotation.Nonnull;

// todo 1.13 @Optional.Interface(iface = "mezz.jei.api.recipe.IRecipeWrapper", modid = "jei")
public class RecipeShapelessKleinStar implements ICraftingRecipe/*, IRecipeWrapper*/ {
	private final ShapelessRecipe compose;

	public RecipeShapelessKleinStar(ShapelessRecipe compose) {
		this.compose = compose;
	}

	@Override
	public ResourceLocation getId()
	{
		return compose.getId();
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return ObjHandler.KLEIN_RECIPE_SERIALIZER;
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World worldIn) {
		return compose.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
		ItemStack result = compose.getCraftingResult(inv);
		long storedEMC = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof KleinStar) {
				storedEMC += KleinStar.getEmc(stack);
			}
		}

		if (storedEMC != 0 && result.getItem() instanceof KleinStar) {
			KleinStar.setEmc(result, storedEMC);
		}
		return result;
	}

	@Override
	public boolean canFit(int width, int height) {
		return compose.canFit(width, height);
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return compose.getRecipeOutput();
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		return compose.getRemainingItems(inv);
	}

	@Nonnull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return compose.getIngredients();
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Nonnull
	@Override
	public String getGroup() {
		return compose.getGroup();
	}
	/* todo 1.13
	@Override
	@Optional.Method(modid = "jei")
	public void getIngredients(IIngredients ingredients) {
		List<ItemStack> stacks = new ArrayList<>();

		for(Ingredient ing : this.compose.getIngredients()){
			stacks.add(ing.getMatchingStacks()[0]);
		}

		ingredients.setInputs(ItemStack.class, stacks);
		ingredients.setOutput(ItemStack.class, this.compose.getRecipeOutput());
	}
	*/

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeShapelessKleinStar>
	{
		@Override
		public RecipeShapelessKleinStar read(ResourceLocation recipeId, JsonObject json)
		{
			return new RecipeShapelessKleinStar(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, json));
		}

		@Override
		public RecipeShapelessKleinStar read(ResourceLocation recipeId, PacketBuffer buffer)
		{
			return new RecipeShapelessKleinStar(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, buffer));
		}

		@Override
		public void write(PacketBuffer buffer, RecipeShapelessKleinStar recipe)
		{
			IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe.compose);
		}
	}
}
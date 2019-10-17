package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoulStone extends RingToggle implements IPedestalItem
{
	public SoulStone(Properties props)
	{
		super(props);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean held)
	{
		if (world.isRemote || slot > 8 || !(entity instanceof PlayerEntity))
		{
			return;
		}
		
		super.inventoryTick(stack, world, entity, slot, held);
		
		PlayerEntity player = (PlayerEntity) entity;

        if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			if (getEmc(stack) < 64 && !consumeFuel(player, stack, 64, false))
			{
				stack.getTag().putBoolean(TAG_ACTIVE, false);
			}
			else
			{
				player.getCapability(InternalTimers.CAPABILITY, null).ifPresent(timers -> {
					timers.activateHeal();
					if (player.getHealth() < player.getMaxHealth() && timers.canHeal())
					{
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1.0F, 1.0F);
						player.heal(2.0F);
						removeEmc(stack, 64);
					}
				});
			}
		}
	}
	
	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand)
	{
        CompoundNBT tag = stack.getOrCreateTag();
		tag.putBoolean(TAG_ACTIVE, !tag.getBoolean(TAG_ACTIVE));
		return true;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.soul.get() != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			if(!(te instanceof DMPedestalTile))
			{
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0)
			{
				List<ServerPlayerEntity> players = world.getEntitiesWithinAABB(ServerPlayerEntity.class, tile.getEffectBounds());

				for (ServerPlayerEntity player : players)
				{
					if (player.getHealth() < player.getMaxHealth())
					{
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.BLOCKS, 1.0F, 1.0F);
						player.heal(1.0F); // 1/2 heart
					}
				}

				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.soul.get());
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription()
	{
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.soul.get() != -1)
		{
			list.add(new TranslationTextComponent("pe.soul.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.soul.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.soul.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}
}

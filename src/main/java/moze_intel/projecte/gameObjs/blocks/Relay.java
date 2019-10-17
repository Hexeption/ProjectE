package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class Relay extends BlockDirection implements ITileEntityProvider
{
	private final int tier;
	
	public Relay(int tier, Properties props)
	{
		super(props);
		this.tier = tier;
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rtr)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof RelayMK1Tile)
			{
				NetworkHooks.openGui((ServerPlayerEntity) player, (RelayMK1Tile) te, pos);
			}
		}
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader world)
	{
		switch (tier)
		{
			default:
			case 1: return new RelayMK1Tile();
			case 2: return new RelayMK2Tile();
			case 3: return new RelayMK3Tile();
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof RelayMK1Tile)
		{
			RelayMK1Tile relay = ((RelayMK1Tile) te);
			return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
		}
		return 0;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
		{
			te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN)
					.ifPresent(inv -> WorldHelper.dropInventory(inv, world, pos));
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

}

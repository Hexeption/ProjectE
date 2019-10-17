package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class TransmutationStone extends Block
{
	private static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 4, 16);

	public TransmutationStone(Properties props)
	{
		super(props);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		return SHAPE;
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rtr)
	{
		if (!world.isRemote)
		{
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(), b -> {
				b.writeBoolean(false);
			});
		}
		return true;
	}
	
	private static class ContainerProvider implements INamedContainerProvider
	{
		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player)
		{
			return new TransmutationContainer(windowId, playerInventory, Hand.OFF_HAND);
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName()
		{
			return new TranslationTextComponent(ObjHandler.transmuteStone.getTranslationKey());
		}
	}
}

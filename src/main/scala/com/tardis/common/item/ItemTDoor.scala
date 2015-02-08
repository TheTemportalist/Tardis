package com.tardis.common.item

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, ItemBlock}
import net.minecraft.block.{BlockDoor, Block}
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class ItemTDoor(b: Block) extends ItemBlock(b) {
	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World,
			posIn: BlockPos,
			sideIn: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		if (sideIn == EnumFacing.UP) {
			var pos: BlockPos = posIn
			val state: IBlockState = worldIn.getBlockState(pos)
			val block: Block = state.getBlock

			if (!block.isReplaceable(worldIn, pos)) {
				pos = pos.offset(sideIn)
			}

			if (playerIn.canPlayerEdit(pos, sideIn, stack) &&
					this.block.canPlaceBlockAt(worldIn, pos)) {
				this.place(worldIn, pos, EnumFacing.fromAngle(playerIn.rotationYaw.toDouble),
					this.block)
				stack.stackSize -= 1
				return true
			}

		}

		false
	}

	def place(world: World, pos: BlockPos, facing: EnumFacing, block: Block): Unit = {
		val uPos: BlockPos = pos.up
		val state: IBlockState = block.getDefaultState.withProperty(BlockDoor.FACING, facing)
		world.setBlockState(
			pos,
			state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER),
			2
		)
		world.setBlockState(
			uPos,
			state.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER),
			2
		)
		world.notifyNeighborsOfStateChange(pos, block)
		world.notifyNeighborsOfStateChange(uPos, block)
	}

}

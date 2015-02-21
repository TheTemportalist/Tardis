package com.tardis.common.item

import com.tardis.common.TardisManager
import com.temportalist.origin.library.common.lib.vec.V3O
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.{ItemStack, ItemBlock}
import net.minecraft.block.{BlockSnow, Block}
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World

/**
 * TODO move to origin
 *
 * @author TheTemportalist
 */
class ItemConsole(b: Block) extends ItemBlock(b) {

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World,
			posIn: BlockPos,
			sideIn: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		val iblockstate: IBlockState = worldIn.getBlockState(posIn)
		val block: Block = iblockstate.getBlock

		var side: EnumFacing = sideIn
		val pos: V3O = new V3O(posIn)

		if (block == Blocks.snow_layer &&
				iblockstate.getValue(BlockSnow.LAYERS).asInstanceOf[Integer].intValue() < 1) {
			side = EnumFacing.UP
		}
		else if (!block.isReplaceable(worldIn, posIn)) {
			pos += side
		}

		if (stack.stackSize == 0 ||
				!playerIn.canPlayerEdit(pos.toBlockPos(), side, stack) ||
				(pos.y_i() == 255 && this.block.getMaterial.isSolid)) {
			return false
		}
		else if (worldIn.canBlockBePlaced(
			this.block, pos.toBlockPos(), false, side, null.asInstanceOf[Entity], stack
		)) {
			val newMeta: Int = this.getMetadata(stack.getMetadata)
			val state: IBlockState = this.block.onBlockPlaced(
				worldIn, pos.toBlockPos(), side, hitX, hitY, hitZ, newMeta, playerIn
			)
			if (this.placeBlockAt(
				stack, playerIn, worldIn, pos.toBlockPos(), side, hitX, hitY, hitZ, state
			) && this.canPlaceAt(worldIn, pos.toBlockPos(), side, playerIn)) {
				worldIn.playSoundEffect((posIn.getX.toFloat + 0.5F).toDouble,
					(posIn.getY.toFloat + 0.5F).toDouble, (posIn.getZ.toFloat + 0.5F).toDouble,
					this.block.stepSound.getPlaceSound,
					(this.block.stepSound.getVolume + 1.0F) / 2.0F,
					this.block.stepSound.getFrequency * 0.8F)

				stack.stackSize -= 1
			}
			return true
		}
		false
	}

	def canPlaceAt(world: World, pos: BlockPos, side: EnumFacing, player: EntityPlayer): Boolean = {
		!TardisManager.hasConsole(world.provider.getDimensionId)
	}

}

package com.tardis.common.item

import com.tardis.common.init.TardisBlocks
import com.temportalist.origin.api.common.lib.V3O
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
class ItemTDoor(b: Block) extends ItemBlock(b) {

	override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, side: Int,
			subX: Float, subY: Float, subZ: Float): Boolean = {
		if (side == 1) {
			val pos: V3O = new V3O(x, y, z)
			val block: Block = pos.getBlock(world)
			if (!block.isReplaceable(world, pos.x_i(), pos.y_i(), pos.z_i()))
				pos += ForgeDirection.getOrientation(side)
			if (player.canPlayerEdit(pos.x_i(), pos.y_i(), pos.z_i(), side, stack) &&
					this.field_150939_a.canPlaceBlockAt(world, pos.x_i(), pos.y_i(), pos.z_i())) {
				this.place(world, pos, ForgeDirection.NORTH, this.field_150939_a)
				if (!player.capabilities.isCreativeMode)
					stack.stackSize -= 1
				return true
			}

		}
		false
	}

	def place(world: World, pos: V3O, facing: ForgeDirection, block: Block): Unit = {
		val meta: Int = TardisBlocks.tDoor.setFacing(0, facing)
		pos.setBlock(world, TardisBlocks.tDoor, TardisBlocks.tDoor.setHalf(meta, false))
		pos.up().setBlock(world, TardisBlocks.tDoor, TardisBlocks.tDoor.setHalf(meta, true))
	}

}

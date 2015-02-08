package com.tardis.common.block

import com.tardis.common.item.ItemConsole
import com.tardis.common.tile.TEConsole
import com.tardis.common.{TardisManager, Tardis}
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class BlockConsole(name: String)
		extends BlockWrapperTE(
			Material.rock, Tardis.MODID, name,
			classOf[ItemConsole], classOf[TEConsole]
		) {

	override def isOpaqueCube: Boolean = false

	override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState,
			placer: EntityLivingBase, stack: ItemStack): Unit = {
		TardisManager.registerConsole(worldIn, true)
	}

	override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
		super.breakBlock(worldIn, pos, state)
		TardisManager.registerConsole(worldIn, false)
	}

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		side match {
			case EnumFacing.UP =>
				TardisManager.openInterface(playerIn)
				return true
			case _ =>
		}
		false
	}

}

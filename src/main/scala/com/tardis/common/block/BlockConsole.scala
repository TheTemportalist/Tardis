package com.tardis.common.block

import com.tardis.common.dimensions.TardisManager
import com.tardis.common.tile.TEConsole
import com.tardis.common.{EntityTardis, Tardis}
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class BlockConsole(name: String)
		extends BlockWrapperTE(
			Material.rock, Tardis.MODID, name, classOf[TEConsole]
		) {

	override def isOpaqueCube: Boolean = false

	override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		side match {
			case EnumFacing.UP =>
				val tardis: EntityTardis = TardisManager.getTardis(worldIn)
				if (tardis != null) {
					tardis.setPositionAndUpdate(
						tardis.posX + 1, tardis.posY, tardis.posZ
					)
				}
				else {
					println ("null tardis in console activation")
				}
				true
			case _ =>
				false
		}
	}

}

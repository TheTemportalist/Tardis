package com.tardis.common.block

import com.tardis.common.Tardis
import com.tardis.common.tile.TEConsole
import com.temportalist.origin.api.common.block.BlockTile
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class BlockConsole(name: String)
		extends BlockTile(
			Material.rock, Tardis.MODID, name, classOf[TEConsole]
		) {

	override def isOpaqueCube: Boolean = false

	override def onBlockActivated(worldIn: World, x: Int, y: Int, z: Int, player: EntityPlayer,
			side: Int, subX: Float, subY: Float, subZ: Float): Boolean = {

		false
	}

}

package com.temportalist.tardis.common.block

import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import com.temportalist.tardis.common.Tardis
import com.temportalist.tardis.common.tile.TEConsole
import net.minecraft.block.material.Material

/**
 *
 *
 * @author TheTemportalist
 */
class BlockConsole(name: String)
		extends BlockWrapperTE(Material.rock, Tardis.MODID, name, classOf[TEConsole]
		) {

	override def isOpaqueCube: Boolean = false

}

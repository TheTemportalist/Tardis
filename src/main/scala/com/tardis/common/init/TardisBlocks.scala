package com.tardis.common.init

import com.tardis.common.block.{BlockTardisDoor, BlockConsole}
import com.tardis.common.tile.{TEDoor, TEConsole}
import com.temportalist.origin.api.common.register.BlockRegister
import com.temportalist.origin.common.Origin

/**
 *
 *
 * @author TheTemportalist 4/14/15
 */
object TardisBlocks extends BlockRegister {

	var console: BlockConsole = null
	var tDoor: BlockTardisDoor = null

	override def register(): Unit = {

		this.register("console", classOf[TEConsole])
		this.console = new BlockConsole("console")
		Origin.addBlockToTab(this.console)

		this.register("door", classOf[TEDoor])
		this.tDoor = new BlockTardisDoor("tardis_door")
		Origin.addBlockToTab(this.tDoor)

	}

}

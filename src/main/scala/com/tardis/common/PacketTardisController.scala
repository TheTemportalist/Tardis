package com.tardis.common

import com.temportalist.origin.library.common.nethandler.IPacket
import com.temportalist.origin.library.common.utility.WorldHelper
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author TheTemportalist
 */
class PacketTardisController() extends IPacket {

	def this(operation: String, dimID: Int, entityID: Int) {
		this()
		this.add(operation, dimID, entityID)
	}

	override def handle(player: EntityPlayer, isServer: Boolean): Unit = {
		val op: String = this.get[String]
		val dim: Int = this.get[Int]
		val eID: Int = this.get[Int]
		val pt: PlayerTardis = PlayerTardis.get(player)
		op match {
			case "open" =>
				pt.setTardis(dim, eID)
				if (WorldHelper.isClient()) pt.openRender()
			case "close" =>
				if (WorldHelper.isClient()) pt.closeRender()
				pt.setTardis(null)
			case _ =>
		}
	}

}

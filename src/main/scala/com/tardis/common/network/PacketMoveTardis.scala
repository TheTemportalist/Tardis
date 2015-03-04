package com.tardis.common.network

import com.tardis.common.{EntityTardis, PlayerTardis}
import com.temportalist.origin.library.common.nethandler.IPacket
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author TheTemportalist
 */
class PacketMoveTardis() extends IPacket {

	override def handle(player: EntityPlayer, isServer: Boolean): Unit = {
		val pt: PlayerTardis = PlayerTardis.get(player)
		val tardis: EntityTardis = pt.getTardis()
		if (tardis != null) {
			val packetType: String = this.get[String]
			if (packetType.equals("posRot") || packetType.equals("pos")) {
				tardis.setPositionAndUpdate(
					this.get[Double], this.get[Double], this.get[Double]
				)
			}
			if (packetType.equals("posRot") || packetType.equals("rot")) {
				tardis.rotationYaw = this.get[Float]
				tardis.rotationPitch = this.get[Float]
			}
		}
	}

}

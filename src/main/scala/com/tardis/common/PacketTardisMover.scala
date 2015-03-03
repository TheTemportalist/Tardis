package com.tardis.common

import com.temportalist.origin.library.common.nethandler.IPacket
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author TheTemportalist
 */
class PacketTardisMover() extends IPacket {

	def this(forward: Float, strafe: Float) {
		this()
		this.add(strafe, forward)
	}

	override def handle(player: EntityPlayer, isServer: Boolean): Unit = {
		val tardis: EntityTardis = PlayerTardis.get(player).getTardis()
		if (tardis != null) {
			tardis.moveFlying(this.get[Float], this.get[Float], 0.02F)
			tardis.moveEntity(tardis.motionX, tardis.motionY, tardis.motionZ)
		}
	}

}

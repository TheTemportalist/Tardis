package com.tardis.common

import com.temportalist.origin.library.common.nethandler.IPacket
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author TheTemportalist
 */
class PacketTardisMover(var forward: Float, var strafe: Float) extends IPacket {

	def this() {
		this(0, 0)
	}

	override def writeTo(buffer: ByteBuf): Unit = {
		buffer.writeFloat(this.forward)
		buffer.writeFloat(this.strafe)

	}

	override def readFrom(buffer: ByteBuf): Unit = {
		this.forward = buffer.readFloat()
		this.strafe = buffer.readFloat()

	}

	override def handle(player: EntityPlayer): Unit = {
		val tardis: EntityTardis = PlayerTardis.get(player).getTardis()
		if (tardis != null) {
			tardis.moveFlying(this.strafe, this.forward, 0.02F)
			tardis.moveEntity(tardis.motionX, tardis.motionY, tardis.motionZ)
		}
	}

}

package com.temportalist.tardis.common

import com.temportalist.origin.library.common.nethandler.IPacket
import com.temportalist.origin.library.common.utility.WorldHelper
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.network.ByteBufUtils

/**
 *
 *
 * @author TheTemportalist
 */
class PacketTardisController(var operation: String, var dimID: Int, var entityID: Int) extends IPacket {

	def this(oper: String) {
		this(oper, 0, 0)
	}

	def this() {
		this("")
	}

	override def writeTo(buffer: ByteBuf): Unit = {
		ByteBufUtils.writeUTF8String(buffer, this.operation)
		buffer.writeInt(this.dimID)
		buffer.writeInt(this.entityID)
	}

	override def readFrom(buffer: ByteBuf): Unit = {
		this.operation = ByteBufUtils.readUTF8String(buffer)
		this.dimID = buffer.readInt()
		this.entityID = buffer.readInt()
	}

	override def handle(player: EntityPlayer): Unit = {
		val pt: PlayerTardis = PlayerTardis.get(player)
		this.operation match {
			case "open" =>
				pt.setTardis(this.dimID, this.entityID)
				if (WorldHelper.isClient()) pt.openRender()
			case "close" =>
				if (WorldHelper.isClient()) pt.closeRender()
				pt.setTardis(null)
			case _ =>
		}
	}

}

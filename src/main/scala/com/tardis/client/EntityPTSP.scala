package com.tardis.client

import com.tardis.common.network.PacketMoveTardis
import com.tardis.common.{Tardis, EntityTardis}
import com.temportalist.origin.library.common.nethandler.PacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(value = Side.CLIENT)
class EntityPTSP(val player: EntityPlayerSP, tardis: EntityTardis) extends EntityPlayerSP(
	Minecraft.getMinecraft, player.getEntityWorld, player.sendQueue, player.getStatFileWriter
) {

	private var lastReportedPosX: Double = this.posX
	private var lastReportedPosY: Double = this.posY
	private var lastReportedPosZ: Double = this.posZ
	private var lastReportedRotY: Float = this.rotationYaw
	private var lastReportedRotP: Float = this.rotationPitch

	private var posUpdateTicks: Int = 0

	override def onUpdateWalkingPlayer(): Unit = {
		//super.onUpdateWalkingPlayer()

		val difPX: Double = this.posX - this.lastReportedPosX
		val difPY: Double = this.getEntityBoundingBox.minY - this.lastReportedPosY
		val difPZ: Double = this.posZ - this.lastReportedPosZ
		val difRY: Float = this.rotationYaw - this.lastReportedRotY
		val difRP: Float = this.rotationPitch - this.lastReportedRotP

		val shouldUpdatePos = difPX * difPX + difPY * difPY + difPZ * difPZ > 9.0E-4D ||
				this.posUpdateTicks >= 20
		val shouldUpdateRot = difRY != 0d && difRP != 0

		// packety things
		if (shouldUpdatePos && shouldUpdateRot) {
			PacketHandler.sendToServer(Tardis.MODID, new PacketMoveTardis()
					.add("posRot").add(this.posX).add(this.getEntityBoundingBox.minY)
					.add(this.posZ).add(this.rotationYaw).add(this.rotationPitch)
			)
		}
		else if (shouldUpdatePos) {
			PacketHandler.sendToServer(Tardis.MODID, new PacketMoveTardis()
					.add("pos").add(this.posX).add(this.getEntityBoundingBox.minY).add(this.posZ)
			)
		}
		else if (shouldUpdateRot) {
			PacketHandler.sendToServer(Tardis.MODID, new PacketMoveTardis()
					.add("rot").add(this.rotationYaw).add(this.rotationPitch)
			)
		}

		this.posUpdateTicks += 1

		if (shouldUpdatePos) {
			this.lastReportedPosX = this.posX
			this.lastReportedPosY = this.posY
			this.lastReportedPosZ = this.posZ
			this.posUpdateTicks = 0
		}
		if (shouldUpdateRot) {
			this.lastReportedRotY = this.rotationYaw
			this.lastReportedRotP = this.rotationPitch
		}

	}

}

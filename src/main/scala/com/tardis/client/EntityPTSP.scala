package com.tardis.client

import com.tardis.common.EntityTardis
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.MovementInputFromOptions
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

	//this.getW
	//this.setPositionAndUpdate(tardis.posX, tardis.posY, tardis.posZ)

	private var lastReportedPosX: Double = this.posX
	private var lastReportedPosY: Double = this.posY
	private var lastReportedPosZ: Double = this.posZ
	private var lastReportedRotY: Float = this.rotationYaw
	private var lastReportedRotP: Float = this.rotationPitch

	private var posUpdateTicks: Int = 0

	this.movementInput = new MovementInputFromOptions(this.mc.gameSettings) {
		override def updatePlayerMoveState(): Unit = {
			this.moveForward = 0
			this.moveStrafe = 0
		}
	}

	override def onUpdate(): Unit = {}

	override def updateEntityActionState(): Unit = {}

	override def onUpdateWalkingPlayer(): Unit = {}

	override def moveEntityWithHeading(strafe: Float, forward: Float): Unit = {}

	override def moveEntity(x: Double, y: Double, z: Double): Unit = {}



}

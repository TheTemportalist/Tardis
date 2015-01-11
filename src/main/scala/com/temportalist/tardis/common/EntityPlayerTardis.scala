package com.temportalist.tardis.common

import com.temportalist.origin.library.common.nethandler.PacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.GameSettings
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.MovementInputFromOptions
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(value = Side.CLIENT)
class EntityPlayerTardis(player: EntityPlayerSP) extends EntityPlayerSP(
	Minecraft.getMinecraft, player.getEntityWorld, player.sendQueue, player.getStatFileWriter
) {

	var movementY: Double = 0.0D

	def incMoveY(mult: Double): Unit = this.movementY += 0.05D * mult

	this.movementInput = new MovementInputFromOptions(Minecraft.getMinecraft.gameSettings) {

		override def updatePlayerMoveState(): Unit = {
			val tardis: EntityTardis = getTardis()
			if (tardis == null) {
				PlayerTardis.close(getPlayer())
				return
			}
			val gs: GameSettings = Minecraft.getMinecraft.gameSettings

			// todo calculate upwards movement base on pitch

			if (this.moveStrafe != 0) this.moveStrafe = 0
			if (this.moveForward != 0) this.moveForward = 0

			var tardisForward: Float = 0
			var tardisStrafe: Float = 0

			if (gs.keyBindForward.isKeyDown) tardisForward += 1
			if (gs.keyBindBack.isKeyDown) tardisForward -= 1
			if (gs.keyBindLeft.isKeyDown) tardisStrafe += 1
			if (gs.keyBindRight.isKeyDown) tardisStrafe -= 1

			if (tardisForward != 0 || tardisStrafe != 0)
				PacketHandler.sendToServer(Tardis.MODID,
					new PacketTardisMover(tardisForward, tardisStrafe)
				)

		}

	}

	override def setAngles(yaw: Float, pitch: Float): Unit = {
		val tardis: EntityTardis = this.getTardis()
		if (tardis == null) PlayerTardis.close(this.player)
		else tardis.setAngles(yaw, pitch)
	}

	override def isSpectator: Boolean = true

	def getTardis(): EntityTardis = PlayerTardis.get(this.player).getTardis()

	def getPlayer(): EntityPlayerSP = this.player

	override def onLivingUpdate(): Unit = {

		this.movementInput.updatePlayerMoveState()

		if (!this.capabilities.isFlying && !this.onGround) {
			this.capabilities.isFlying = true
			this.sendPlayerAbilities()
		}

		this.motionY += this.movementY * 3.0D

		super.onLivingUpdate()

		if (this.onGround) {
			this.capabilities.isFlying = false
			this.sendPlayerAbilities()
		}

	}

	override def writeToNBT(tagCompund: NBTTagCompound): Unit = {
		super.writeToNBT(tagCompund)
		val playerTag: NBTTagCompound = new NBTTagCompound
		this.player.writeToNBT(playerTag)
		tagCompund.setTag("playerTag", playerTag)
	}

	override def readFromNBT(tagCompund: NBTTagCompound): Unit = {
		super.readFromNBT(tagCompund)
		this.player.readFromNBT(tagCompund.getCompoundTag("playerTag"))
	}

}

@SideOnly(value = Side.CLIENT)
object EntityPlayerTardis {

	def open(): Unit = {
		Minecraft.getMinecraft.thePlayer =
				new EntityPlayerTardis(Minecraft.getMinecraft.thePlayer)
	}

	def close(): Unit = {
		val ept: EntityPlayerTardis =
			Minecraft.getMinecraft.thePlayer.asInstanceOf[EntityPlayerTardis]
		Minecraft.getMinecraft.thePlayer = ept.getPlayer()
		ept.setDead()
	}

}

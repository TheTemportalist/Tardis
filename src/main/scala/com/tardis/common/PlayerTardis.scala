package com.tardis.common

import java.util.UUID

import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.lib.LogHelper
import com.temportalist.origin.library.common.nethandler.{IPacket, PacketHandler}
import com.temportalist.origin.library.common.utility.WorldHelper
import com.temportalist.origin.wrapper.common.extended.{ExtendedEntity, ExtendedEntityHandler}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
class PlayerTardis(p: EntityPlayer) extends ExtendedEntity(p) {

	private var tardisDim: Int = 0
	private var tardisUUID: UUID = null
	private var originalPOV: Int = -1

	override def saveNBTData(tagCom: NBTTagCompound): Unit = {
		//tagCom.setInteger("tardisDim", this.tardisDim)
		//tagCom.setInteger("tardisID", this.tardisID)
	}

	override def loadNBTData(tagCom: NBTTagCompound): Unit = {
		//this.tardisDim = tagCom.getInteger("tardisDim")
		//this.tardisID = tagCom.getInteger("tardisID")
	}

	def setTardis(tDim: Int, tUUID: UUID): Unit = {
		this.tardisDim = tDim
		this.tardisUUID = tUUID
		//this.syncEntity()
	}

	def setTardis(tardis: EntityTardis): Unit = {
		if (tardis == null) this.setTardis(0, null)
		else this.setTardis(tardis.getEntityWorld.provider.getDimensionId, tardis.getUniqueID)
	}

	def isControllingTardis(): Boolean = this.tardisUUID != null

	def getTardis(): EntityTardis = {
		if (this.isControllingTardis()) {
			DimensionManager.getWorld(this.tardisDim).getEntityFromUuid(this.tardisUUID)
					.asInstanceOf[EntityTardis]
		}
		else
			null
	}

	@SideOnly(value = Side.CLIENT)
	def openRender(): Unit = {
		/*
		if (this.isControllingTardis()) {
			val tardis: EntityTardis = this.getTardis()
			if (tardis != null) {

				Minecraft.getMinecraft.setRenderViewEntity(tardis)
				this.originalPOV = Minecraft.getMinecraft.gameSettings.thirdPersonView
				Minecraft.getMinecraft.gameSettings.thirdPersonView = 1

				EntityPlayerTardis.open()

			}
		}
		*/
		this.syncEntity()
	}

	@SideOnly(value = Side.CLIENT)
	def closeRender(): Unit = {
		/*
		if (this.isControllingTardis() &&
				Minecraft.getMinecraft.thePlayer.isInstanceOf[EntityPlayerTardis]) {

			EntityPlayerTardis.close()

			Minecraft.getMinecraft.gameSettings.thirdPersonView = this.originalPOV
			this.originalPOV = -1

		}
		*/
		this.syncEntity()
	}

	override def syncEntity(): Unit = {
		val tagCom: NBTTagCompound = new NBTTagCompound()
		this.saveNBTData(tagCom)
		//val syncMessage: PacketSyncExtendedProperties =
		//	new PacketSyncExtendedProperties(this.getClass, tagCom)
		if (this.player != null)
			if (WorldHelper.isServer()) {
				//PacketHandler.sendToPlayer(Origin.MODID, syncMessage, this.player)
			}
			else {
				//PacketHandler.sendToServer(Origin.MODID, syncMessage)
			}
		else
			LogHelper.info(Origin.MODNAME, "Error: Null player in extended entity")

	}

}

object PlayerTardis {

	def get(player: EntityPlayer): PlayerTardis = {
		ExtendedEntityHandler.getExtended(player, classOf[PlayerTardis]).asInstanceOf[PlayerTardis]
	}

	def open(tardis: EntityTardis, player: EntityPlayer): Unit = {
		/*
		this.send(player, new PacketTardisController("open",
			tardis.getEntityWorld.provider.getDimensionId, tardis.getEntityId
		))
		*/
	}

	def close(player: EntityPlayer): Unit = {
		//this.send(player, new PacketTardisController("close"))
	}

	private def send(player: EntityPlayer, packet: IPacket): Unit = {
		PacketHandler.sendToServer(Tardis.MODID, packet)
		PacketHandler.sendToPlayer(Tardis.MODID, packet, player)
	}

}

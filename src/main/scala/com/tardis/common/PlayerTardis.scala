package com.tardis.common

import java.util.UUID

import com.temportalist.origin.api.common.lib.LogHelper
import com.temportalist.origin.api.common.utility.WorldHelper
import com.temportalist.origin.foundation.common.extended.ExtendedEntity
import com.temportalist.origin.foundation.common.network.PacketExtendedSync
import com.temportalist.origin.internal.common.Origin
import com.temportalist.origin.internal.common.extended.ExtendedEntityHandler
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.DimensionManager

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
		val least = if (this.tardisUUID == null) 0L else this.tardisUUID.getLeastSignificantBits
		val most = if (this.tardisUUID == null) 0L else this.tardisUUID.getMostSignificantBits
		this.syncEntity("tardis", this.tardisDim, least, most)
	}

	def setTardis(tardis: EntityTardis): Unit = {
		if (tardis == null) this.setTardis(0, null)
		else this.setTardis(tardis.worldObj.provider.dimensionId, tardis.getUniqueID)
	}

	def hasTardisToControl: Boolean = this.tardisUUID != null

	def getTardis: EntityTardis = {
		if (this.hasTardisToControl)
			Tardis.getTardisInWorld(
				DimensionManager.getWorld(this.tardisDim),
				this.tardisUUID, this.getTardis.getEntityId
			)
		else null
	}

	override def handleSyncPacketData(uniqueIdentifier: String, packet: PacketExtendedSync,
			side: Side): Unit = {
		uniqueIdentifier match {
			case "tardis" =>
				this.tardisDim = packet.get[Int]
				val least = packet.get[Long]
				val most = packet.get[Long]
				if (least == 0L && most == 0L) this.tardisUUID = null
				else this.tardisUUID = new UUID(most, least)
			case _ =>
		}
	}

}

object PlayerTardis {

	def get(player: EntityPlayer): PlayerTardis = {
		ExtendedEntityHandler.getExtended(player, classOf[PlayerTardis])
	}

}

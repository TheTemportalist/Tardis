package com.tardis.common.dimensions

import java.util.UUID

import com.tardis.common.init.TardisBlocks
import com.tardis.common.{EntityTardis, Tardis}
import com.temportalist.origin.api.common.lib.V3O
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.WorldSavedData
import net.minecraftforge.common.DimensionManager

/**
 *
 *
 * @author TheTemportalist
 */
class InyardData(name: String) extends WorldSavedData(name) {

	private var dimID: Int = 4982752
	private var doorPos: V3O = null
	private var tardisDim: Int = 0
	private var tardisUUID: UUID = null
	private var tardisID: Int = 0
	private var tardis: EntityTardis = null

	def setDim(dimid: Int): Unit = this.dimID = dimid

	def getDim(): Int = this.dimID

	def setDoorPos(vec: V3O): Unit = this.doorPos = vec

	def getDoorPos: V3O = this.doorPos

	def setTardis(tardis: EntityTardis): Unit = {
		this.tardisDim = tardis.worldObj.provider.dimensionId
		this.tardisUUID = tardis.getUniqueID
		this.tardisID = tardis.getEntityId
		this.tardis = tardis
	}

	def getTardis: EntityTardis = {
		//println("Dim@" + this.dimID + " contains tardis stats of Dim@" +
		//		this.tardisDim + " & UUID@" + this.tardisEID)
		println("[InData] " + this.tardisDim)
		println("[InData] " + this.tardisUUID)
		println("[InData] " + this.tardisID)
		Tardis.getTardisInWorld(
			DimensionManager.getWorld(this.tardisDim),
			this.tardisUUID,
			this.tardisID
		)
		this.tardis
	}

	def getSpawnPoint(meta: Int): V3O = {
		(if (TardisBlocks.tDoor.getHalf(meta) > 0) V3O.DOWN else V3O.ZERO) +
				this.doorPos + TardisBlocks.tDoor.getFacing(meta)
	}

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		println("[InData] Writing " + this.dimID)
		// todo write a gen class for handling tag things
		tagCom.setInteger("dimID", this.dimID)
		if (this.doorPos != null) this.doorPos.writeTo(tagCom, "doorPos")
		tagCom.setInteger("tardisDim", this.tardisDim)
		if (this.tardisUUID != null) {
			tagCom.setLong("tardisEID_most", this.tardisUUID.getMostSignificantBits)
			tagCom.setLong("tardisEID_least", this.tardisUUID.getLeastSignificantBits)
		}
		tagCom.setInteger("tardisID", this.tardisID)

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		this.dimID = tagCom.getInteger("dimID")
		println("[InData] Reading " + this.dimID)
		this.doorPos = V3O.readFrom(tagCom, "doorPos")
		this.tardisDim = tagCom.getInteger("tardisDim")
		this.tardisUUID = new UUID(
			tagCom.getLong("tardisEID_most"), tagCom.getLong("tardisEID_least")
		)
		this.tardisID = tagCom.getInteger("tardisID")

	}

}

package com.tardis.common.dimensions

import com.tardis.common.{EntityTardis, TardisManager}
import com.temportalist.origin.library.common.lib.vec.V3O
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.WorldSavedData
import net.minecraftforge.common.DimensionManager

/**
 *
 *
 * @author TheTemportalist
 */
class InyardSavedData(name: String) extends WorldSavedData(name) {

	private var dimID: Int = 4982752

	def setDim(dimid: Int): Unit = this.dimID = dimid

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		System.out.println("Writing dim " + this.dimID)
		tagCom.setInteger("dimID", this.dimID)
		TardisManager.getDoorPos(this.dimID).writeTo(tagCom, "doorPos")
		val tardis: EntityTardis = TardisManager.getTardisForDimension(this.dimID)
		tagCom.setInteger("tardisDimID", tardis.getEntityWorld.provider.getDimensionId)
		tagCom.setInteger("tardisEntityID", tardis.getEntityId)

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		this.dimID = tagCom.getInteger("dimID")
		System.out.println("Reading dim " + this.dimID)
		TardisManager.setDoorPos(this.dimID, V3O.readFrom(tagCom, "doorPos"))
		TardisManager.setTardis(this.dimID, DimensionManager.getWorld(
			tagCom.getInteger("tardisDimID")
		).getEntityByID(tagCom.getInteger("tardisEntityID")).asInstanceOf[EntityTardis])

	}

}

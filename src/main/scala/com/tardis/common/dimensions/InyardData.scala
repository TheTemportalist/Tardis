package com.tardis.common.dimensions

import com.temportalist.origin.library.common.lib.vec.V3O
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.WorldSavedData

/**
 *
 *
 * @author TheTemportalist
 */
class InyardData(name: String) extends WorldSavedData(name) {

	private var dimID: Int = 4982752
	private var spawn: V3O = null

	def setDim(dimid: Int): Unit = this.dimID = dimid

	def getDim(): Int = this.dimID

	def setSpawn(vec: V3O): Unit = this.spawn = vec

	def getSpawn(): V3O = this.spawn

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		tagCom.setInteger("dimID", this.dimID)
		if (this.spawn != null) this.spawn.writeTo(tagCom, "spawn")

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		this.dimID = tagCom.getInteger("dimID")
		this.spawn = V3O.readFrom(tagCom, "spawn")

	}

}

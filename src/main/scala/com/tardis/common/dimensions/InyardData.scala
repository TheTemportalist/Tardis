package com.tardis.common.dimensions

import java.util.UUID

import com.tardis.common.EntityTardis
import com.temportalist.origin.library.common.lib.vec.V3O
import net.minecraft.block.BlockDoor
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{BlockPos, EnumFacing}
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
	private var tardisEID: UUID = null

	def setDim(dimid: Int): Unit = this.dimID = dimid

	def getDim(): Int = this.dimID

	def setDoorPos(vec: V3O): Unit = this.doorPos = vec

	def getDoorPos(): V3O = this.doorPos

	def setTardis(tardis: EntityTardis): Unit = {
		this.tardisDim = tardis.getEntityWorld.provider.getDimensionId
		this.tardisEID = tardis.getUniqueID
	}

	def getTardis(): EntityTardis = {
		//println("Dim@" + this.dimID + " contains tardis stats of Dim@" +
		//		this.tardisDim + " & UUID@" + this.tardisEID)
		DimensionManager.getWorld(this.tardisDim).getEntityFromUuid(this.tardisEID)
				.asInstanceOf[EntityTardis]
	}

	def getSpawnPoint(doorState: IBlockState): BlockPos = {
		((if (doorState.getProperties.containsKey(BlockDoor.FACING))
			doorPos + doorState.getValue(BlockDoor.FACING).asInstanceOf[EnumFacing]
		else
			doorPos + V3O.EAST) + V3O.UP).toBlockPos()
	}

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		println("Writing " + this.dimID)
		// todo write a gen class for handling tag things
		tagCom.setInteger("dimID", this.dimID)
		if (this.doorPos != null) this.doorPos.writeTo(tagCom, "doorPos")
		tagCom.setInteger("tardisDim", this.tardisDim)
		if (this.tardisEID != null) {
			tagCom.setLong("tardisEID_most", this.tardisEID.getMostSignificantBits)
			tagCom.setLong("tardisEID_least", this.tardisEID.getLeastSignificantBits)
		}

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		this.dimID = tagCom.getInteger("dimID")
		println("Reading " + this.dimID)
		this.doorPos = V3O.readFrom(tagCom, "doorPos")
		this.tardisDim = tagCom.getInteger("tardisDim")
		this.tardisEID = new UUID(
			tagCom.getLong("tardisEID_most"), tagCom.getLong("tardisEID_least")
		)

	}

}

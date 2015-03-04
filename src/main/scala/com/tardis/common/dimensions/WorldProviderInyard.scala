package com.tardis.common.dimensions

import net.minecraft.util.BlockPos
import net.minecraft.world.WorldProvider
import net.minecraft.world.chunk.IChunkProvider

/**
 *
 *
 * @author TheTemportalist
 */
class WorldProviderInyard() extends WorldProvider {

	var dimID: Int = 214425
	var data: InyardData = null

	override def getDimensionName: String = TardisManager.getDimName(this.dimID)

	override def getInternalNameSuffix: String = ""

	override def setDimension(dim: Int): Unit = {
		super.setDimension(dim)
		this.dimID = dim
	}

	override def registerWorldChunkManager(): Unit = {
		super.registerWorldChunkManager()
		this.data = TardisManager.getDimData(this.dimID, this.worldObj.isRemote)
	}

	override def createChunkGenerator(): IChunkProvider =
		new ChunkProviderInyard(this.worldObj, this.data)

	override def getSpawnCoordinate: BlockPos = {
		this.data.getSpawn().toBlockPos()
	}

}

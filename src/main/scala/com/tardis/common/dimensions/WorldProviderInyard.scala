package com.tardis.common.dimensions

import net.minecraft.util.ChunkCoordinates
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

	override def setDimension(dim: Int): Unit = {
		super.setDimension(dim)
		this.dimID = dim
	}

	override def registerWorldChunkManager(): Unit = {
		super.registerWorldChunkManager()
		this.data = TardisManager.getDimData(this.dimID, this.worldObj.isRemote)
		// todo spawn
		// todo correct world size
		//this.worldObj.getWorldBorder.setTransition(16)
	}

	override def createChunkGenerator(): IChunkProvider =
		new ChunkProviderInyard(this.worldObj, this.data)

	override def getSpawnPoint: ChunkCoordinates =
		this.data.getSpawnPoint(this.data.getDoorPos().getBlockMeta(this.worldObj)).toChunkCoords()

}

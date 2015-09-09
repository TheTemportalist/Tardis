package com.tardis.common.dimensions

import java.util

import com.tardis.common.init.TardisBlocks
import com.temportalist.origin.api.common.lib.V3O
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.IProgressUpdate
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.world.{ChunkPosition, World}

/**
 *
 *
 * @author TheTemportalist
 */
class ChunkProviderInyard(val world: World, data: InyardData) extends IChunkProvider {

	override def provideChunk(chunkX: Int, chunkZ: Int): Chunk = {
		val chunk: Chunk = new Chunk(this.world, chunkX, chunkZ)
		chunk.generateSkylightMap()
		chunk
	}

	override def chunkExists(x: Int, z: Int): Boolean = true

	override def populate(provider: IChunkProvider, chunkX: Int, chunkZ: Int): Unit = {
		val doorPos: V3O = this.data.getDoorPos
		if ((doorPos.x_i() >> 4) == chunkX && (doorPos.z_i() >> 4) == chunkZ) {
			val radius: Int = 3
			for (xOff <- -radius to radius) for (zOff <- -radius to radius) {
				val pos: V3O = doorPos.plus(xOff, -1, zOff)
				this.world.setBlock(pos.x_i(), pos.y_i(), pos.z_i(), Blocks.stonebrick, 0, 3)
			}

			val meta: Int =
				if (this.data.getTardis.isDoorOpen()) TardisBlocks.tDoor.cycleOpen(0) else 0
			doorPos.setBlock(world, TardisBlocks.tDoor,
				TardisBlocks.tDoor.setHalf(meta, false), 2)
			doorPos.up().setBlock(world, TardisBlocks.tDoor,
				TardisBlocks.tDoor.setHalf(meta, true), 2)

		}
	}

	override def saveChunks(flag: Boolean, progress: IProgressUpdate): Boolean = true

	override def unloadQueuedChunks(): Boolean = false

	override def canSave: Boolean = true

	override def makeString(): String = "RandomLevelSource"

	override def getPossibleCreatures(creatureType: EnumCreatureType, x: Int, y: Int,
			z: Int): util.List[_] = new util.ArrayList[Nothing]()

	override def getLoadedChunkCount: Int = 0

	override def recreateStructures(x: Int, z: Int): Unit = {}

	override def saveExtraData(): Unit = {}

	override def func_147416_a(world: World, str: String, x: Int, y: Int,
			z: Int): ChunkPosition = null

	override def loadChunk(x: Int, z: Int): Chunk = this.provideChunk(x, z)

}

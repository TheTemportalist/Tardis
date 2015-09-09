package com.tardis.common.dimensions

import java.util

import com.tardis.common.init.TardisBlocks
import com.temportalist.origin.api.common.lib.V3O
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.IProgressUpdate
import net.minecraft.world.chunk.{Chunk, IChunkProvider}
import net.minecraft.world.{ChunkPosition, World}
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
class VoidGeneratorProv(world: World) extends IChunkProvider {

	override def chunkExists(x: Int, z: Int): Boolean = true

	override def canSave: Boolean = true

	override def makeString(): String = "Void"

	override def getLoadedChunkCount: Int = 0

	override def saveExtraData(): Unit = {}

	override def saveChunks(bool: Boolean, progress: IProgressUpdate): Boolean = true

	override def getPossibleCreatures(creatureType: EnumCreatureType, x: Int, y: Int,
			z: Int): util.List[_] = new util.ArrayList[Nothing]()

	override def populate(provider: IChunkProvider, chunkX: Int, chunkz: Int): Unit = {
		if (chunkX == 0 && chunkz == 0) {
			val chunkOrigin: V3O = new V3O(chunkX * 16, 0, chunkz * 16)

			for (xOff <- -2 to 2) for (zOff <- -2 to 2) {
				(chunkOrigin + new V3O(xOff, 0, zOff)).setBlock(this.world, Blocks.stone)
			}

			val meta: Int = TardisBlocks.tDoor.setFacing(0, ForgeDirection.NORTH)
			(chunkOrigin + new V3O(0, 1, 0)).setBlock(this.world, TardisBlocks.tDoor,
				meta)
			(chunkOrigin + new V3O(0, 2, 0)).setBlock(this.world, TardisBlocks.tDoor,
				TardisBlocks.tDoor.setHalf(meta, true))
		}
	}

	override def func_147416_a(world: World, str: String, x: Int,
			y: Int, z: Int): ChunkPosition = null

	override def recreateStructures(x: Int, z: Int): Unit = {}

	override def provideChunk(x: Int, z: Int): Chunk = {
		val chunk: Chunk = new Chunk(this.world, x, z)
		chunk.generateSkylightMap()
		chunk
	}

	override def unloadQueuedChunks(): Boolean = false

	override def loadChunk(x: Int, z: Int): Chunk = this.provideChunk(x, z)

}

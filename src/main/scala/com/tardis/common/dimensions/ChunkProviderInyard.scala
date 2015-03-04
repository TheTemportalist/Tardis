package com.tardis.common.dimensions

import java.util

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.{IProgressUpdate, BlockPos}
import net.minecraft.world.World
import net.minecraft.world.chunk.{Chunk, IChunkProvider}

/**
 *
 *
 * @author TheTemportalist
 */
class ChunkProviderInyard(val world: World, data: InyardData) extends IChunkProvider {

	override def provideChunk(pos: BlockPos): Chunk =
		this.provideChunk(pos.getX >> 4, pos.getZ >> 4)

	override def provideChunk(chunkX: Int, chunkZ: Int): Chunk = {
		val chunk: Chunk = new Chunk(this.world, chunkX, chunkZ)
		chunk.generateSkylightMap()
		chunk
	}

	override def chunkExists(x: Int, z: Int): Boolean = true

	override def populate(provider: IChunkProvider, chunkX: Int, chunkZ: Int): Unit = {
		val spawn: BlockPos = new BlockPos(0, 1, 0)
		if (spawn.getX >> 4 == chunkX && spawn.getY >> 4 == chunkZ) {
			val state: IBlockState = Blocks.stone.getDefaultState
			for (xOff <- -2 to 2) for (zOff <- -2 to 2) {
				this.world.setBlockState(spawn.add(xOff, -1, zOff), state, 3)
			}
		}
	}

	override def saveChunks(flag: Boolean, progress: IProgressUpdate): Boolean = true

	override def unloadQueuedChunks(): Boolean = false

	override def func_177460_a(
			provider: IChunkProvider, chunk: Chunk, a: Int, b: Int): Boolean = false

	override def canSave: Boolean = true

	override def makeString(): String = "RandomLevelSource"

	override def getPossibleCreatures(creatureType: EnumCreatureType,
			pos: BlockPos): util.List[_] = new util.ArrayList[Nothing]()

	override def getStrongholdGen(worldIn: World, structureName: String,
			position: BlockPos): BlockPos = null

	override def getLoadedChunkCount: Int = 0

	override def recreateStructures(p_180514_1_ : Chunk, p_180514_2_ : Int,
			p_180514_3_ : Int): Unit = {}

	override def saveExtraData(): Unit = {}

}

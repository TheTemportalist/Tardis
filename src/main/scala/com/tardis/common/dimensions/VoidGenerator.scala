package com.tardis.common.dimensions

import java.util

import com.temportalist.origin.library.common.lib.vec.V3O
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.{IProgressUpdate, BlockPos}
import net.minecraft.world.World
import net.minecraft.world.chunk.{ChunkPrimer, Chunk, IChunkProvider}

/**
 *
 *
 * @author TheTemportalist
 */
class VoidGenerator(world: World) extends IChunkProvider {

	override def chunkExists(x: Int, z: Int): Boolean = true

	override def canSave: Boolean = true

	override def makeString(): String = "Void"

	override def getLoadedChunkCount: Int = 0

	override def saveExtraData(): Unit = {}

	override def saveChunks(bool: Boolean, progress: IProgressUpdate): Boolean = true

	override def func_177460_a(p_177460_1_ : IChunkProvider, p_177460_2_ : Chunk, p_177460_3_ : Int,
			p_177460_4_ : Int): Boolean = false

	override def getPossibleCreatures(creatureType: EnumCreatureType,
			pos: BlockPos): util.List[_] = new util.ArrayList[Nothing]()

	override def populate(provider: IChunkProvider, chunkX: Int, chunkY: Int): Unit = {
		if (chunkX == 0 && chunkY == 0) {
			val chunkOrigin: V3O = new V3O(chunkX * 16, 0, chunkY * 16)
			this.world.setBlockState(chunkOrigin.toBlockPos(), Blocks.stone.getDefaultState, 2)
		}
	}

	override def recreateStructures(chunk: Chunk, chunkX: Int, chunkY: Int): Unit = {}

	override def getStrongholdGen(worldIn: World, structureName: String,
			position: BlockPos): BlockPos = null

	override def provideChunk(x: Int, z: Int): Chunk = {
		val chunkprimer: ChunkPrimer = new ChunkPrimer
		val chunk: Chunk = new Chunk(this.world, chunkprimer, x, z)
		chunk.generateSkylightMap
		chunk
	}

	override def provideChunk(blockPosIn: BlockPos): Chunk =
		this.provideChunk(blockPosIn.getX >> 4, blockPosIn.getZ >> 4)

	override def unloadQueuedChunks(): Boolean = false
}

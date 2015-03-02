package com.tardis.common.dimensions

import net.minecraft.world.WorldProvider
import net.minecraft.world.chunk.IChunkProvider

/**
 *
 *
 * @author TheTemportalist
 */
class InyardProvider() extends WorldProvider {

	override def getDimensionName: String = "Tardis"

	override def getInternalNameSuffix: String = ""

	override def createChunkGenerator(): IChunkProvider =
		new VoidGenerator(this.worldObj)

}

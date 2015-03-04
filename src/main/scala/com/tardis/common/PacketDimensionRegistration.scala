package com.tardis.common

import java.util

import com.tardis.common.dimensions.TardisManager
import com.temportalist.origin.library.common.nethandler.IPacket
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.DimensionManager

/**
 *
 *
 * @author TheTemportalist
 */
class PacketDimensionRegistration() extends IPacket {

	def this(dimID: Int) {
		this()
		this.add(dimID)
	}

	override def handle(player: EntityPlayer, isServer: Boolean): Unit = {
		if (TardisManager.registeredDims == null)
			TardisManager.registeredDims = new util.ArrayList[Int]()
		val dimID: Int = this.get[Int]
		TardisManager.registeredDims.add(dimID)
		DimensionManager.registerDimension(dimID, TardisManager.providerID)
	}

}

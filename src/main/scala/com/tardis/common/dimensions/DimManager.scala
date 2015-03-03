package com.tardis.common.dimensions

import java.util

import com.tardis.common.EntityTardis
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent

/**
 *
 *
 * @author TheTemportalist
 */
@Deprecated
object DimManager {

	var registeredDims: util.List[Int] = null



	// TODO what is this for???
	var providerID: Int = 1210950780

	def removeDim(tardis: EntityTardis): Unit = {
		if (this.registeredDims == null) return
		this.registeredDims.remove(tardis.getInteriorDimension())
		DimensionManager.unregisterDimension(tardis.getInteriorDimension())
	}

	def registerDimensions(isRegistering: Boolean): Unit = {
		if (this.registeredDims == null) return
		for (i <- 0 until this.registeredDims.size()) {
			val dimid: Int = this.registeredDims.get(i)
			if (isRegistering) DimensionManager.registerDimension(dimid, this.providerID)
			else DimensionManager.unregisterDimension(dimid)
		}
		this.registeredDims = null
	}

	def registerTardisAndDim(tardis: EntityTardis): Unit = {
		if (FMLCommonHandler.instance().getMinecraftServerInstance == null)
			throw new RuntimeException("Cannot create dimension client-side.")
		val dimid: Int = DimensionManager.getNextFreeDimId
		this.registerDim(dimid)
		tardis.setInteriorDimension(dimid)
	}

	def registerDim(dimid: Int): Unit = {
		if (!this.registeredDims.contains(dimid)) {
			DimensionManager.registerDimension(dimid, this.providerID)
			this.registeredDims.add(dimid)
		}
	}

	@SubscribeEvent
	def serverConnection(event: ServerConnectionFromClientEvent): Unit = {
		//event.manager.sendPacket(new PacketMCDimension())

	}

}

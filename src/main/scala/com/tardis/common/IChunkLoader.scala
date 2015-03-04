package com.tardis.common

import com.temportalist.origin.library.common.lib.vec.V3O
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.{ChunkCoordIntPair, World}
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeChunkManager.Ticket

/**
 *
 *
 * @author TheTemportalist
 */
trait IChunkLoader {

	private var currentTicket: Ticket = null

	def getUniqueLoaderID(): String

	def getLoaderWorld(): World

	def getType(): ForgeChunkManager.Type

	def getChunkPos(): V3O

	def writeOtherData(ticket: Ticket): Unit = {}

	def isTicketValid(ticketData: NBTTagCompound): Boolean = {
		V3O.readFrom(ticketData, "pos").equals(this.getChunkPos())
	}

	def onInitialized(instance: AnyRef): Unit = {
		ChunkLoaderHandler.register(instance, this)
	}

	def onUnInitialized(instance: AnyRef): Unit = {
		ChunkLoaderHandler.unregister(instance, this)
		ForgeChunkManager.releaseTicket(this.currentTicket)
	}

	final def checkTicket(instance: AnyRef): Unit = {
		val pos: V3O = this.getChunkPos()
		if (this.currentTicket == null && pos != null) {
			val world: World = this.getLoaderWorld()
			val ticketType: ForgeChunkManager.Type = this.getType()
			if (world != null && ticketType != null) {
				this.currentTicket = ForgeChunkManager.requestTicket(instance, world, ticketType)
				if (this.currentTicket != null) {
					this.currentTicket.getModData.setString("id", this.getUniqueLoaderID())
					pos.writeTo(this.currentTicket.getModData, "pos")
					this.writeOtherData(this.currentTicket)
					this.forceChunk(this.currentTicket, pos.toChunkPair())
				}
			}
		}
	}

	final def forceChunk(ticket: Ticket, chunk: ChunkCoordIntPair): Unit = {
		ForgeChunkManager.forceChunk(ticket, chunk)
	}

}

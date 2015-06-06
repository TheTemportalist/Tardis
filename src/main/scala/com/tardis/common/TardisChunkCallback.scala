package com.tardis.common

import java.util
import java.util.UUID

import com.temportalist.origin.api.common.lib.vec.V3O
import com.temportalist.origin.api.common.utility.Scala
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.{ForgeChunkManager, DimensionManager}
import net.minecraftforge.common.ForgeChunkManager.{LoadingCallback, OrderedLoadingCallback, Ticket}

/**
 *
 *
 * @author TheTemportalist
 */
object TardisChunkCallback extends LoadingCallback with OrderedLoadingCallback {

	override def ticketsLoaded(tickets: util.List[Ticket], world: World,
			maxTicketCount: Int): util.List[Ticket] = {
		val retList: util.List[Ticket] = new util.ArrayList[Ticket]()
		Scala.foreach(tickets, (index: Int, ticket: Ticket) => {
			if (ticket.getModData.getString("id").equals("Tardis")) {
				val tardis: EntityTardis = this.getTardis(ticket.getModData)
				println (tardis)
				if (tardis != null) {
					val tardisPos: V3O = new V3O(tardis)
					val tardisChunk: V3O = new V3O(tardisPos.x_i(), 0, tardisPos.z_i())
					val chunkPos: V3O = this.getChunkPos(ticket.getModData)
					if (tardisChunk == chunkPos) {
						val a = retList.add(ticket)
					}
				}
			}
		})
		retList
	}

	override def ticketsLoaded(tickets: util.List[Ticket], world: World): Unit = {
		Scala.foreach(tickets, (index: Int, ticket: Ticket) => {
			if (ticket.getModData.getString("id").equals("Tardis")) {
				val tardis: EntityTardis = this.getTardis(ticket.getModData)
				if (ticket.getEntity == null) ticket.bindEntity(tardis)
				if (tardis != null) {
					val tardisPos: V3O = new V3O(tardis)
					val tardisChunk: V3O = new V3O(tardisPos.x_i(), 0, tardisPos.z_i())
					val chunkPos: V3O = this.getChunkPos(ticket.getModData)
					if (tardisChunk == chunkPos) {
						ForgeChunkManager.forceChunk(ticket, chunkPos.toChunkPair())
					}
				}
			}
		})
	}

	def getChunkPos(nbt: NBTTagCompound): V3O = {
		V3O.readFrom(nbt, "chunkPos")
	}

	def getTardis(nbt: NBTTagCompound): EntityTardis = {
		Tardis.getTardisInWorld(
			DimensionManager.getWorld(nbt.getInteger("tardisDim")),
			new UUID(nbt.getLong("tardisIDmax"), nbt.getLong("tardisIDmin")),
			nbt.getInteger("tardisID")
		)
		/*
		DimensionManager.getWorld(nbt.getInteger("tardisDim")).getEntityFromUuid(

		).asInstanceOf[EntityTardis]
		*/
	}

}

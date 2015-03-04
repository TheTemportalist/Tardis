package com.tardis.common

import java.util

import scala.collection.mutable

import com.temportalist.origin.library.common.utility.Scala
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeChunkManager.{LoadingCallback, OrderedLoadingCallback, Ticket}

/**
 *
 *
 * @author TheTemportalist
 */
class ChunkLoaderHandler extends LoadingCallback with OrderedLoadingCallback {

	val loaders: util.List[IChunkLoader] = new util.ArrayList[IChunkLoader]()

	def register(loader: IChunkLoader): Boolean = {
		this.loaders.add(loader)
	}

	def unregister(loader: IChunkLoader): Boolean = {
		this.loaders.remove(loader)
	}

	override def ticketsLoaded(tickets: util.List[Ticket], world: World,
			maxTicketCount: Int): util.List[Ticket] = {
		val retList: util.List[Ticket] = new util.ArrayList[Ticket]()
		Scala.foreach(tickets, (index: Int, ticket: Ticket) => {
			Scala.foreach(this.loaders, (loaderIndex: Int, loader: IChunkLoader) => {
				if (ticket.getModData.getString("id").equals(loader.getUniqueLoaderID())) {
					if (loader.isTicketValid(ticket.getModData)) {
						val ret = retList.add(ticket)
					}
				}
			})
		})
		retList
	}

	override def ticketsLoaded(tickets: util.List[Ticket], world: World): Unit = {
		Scala.foreach(tickets, (index: Int, ticket: Ticket) => {
			Scala.foreach(this.loaders, (loaderIndex: Int, loader: IChunkLoader) => {
				if (ticket.getModData.getString("id").equals(loader.getUniqueLoaderID())) {
					if (loader.isTicketValid(ticket.getModData)) {
						loader.forceChunk(ticket, loader.getChunkPos().toChunkPair())
					}
				}
			})
		})
	}

}

object ChunkLoaderHandler {

	private final val managers: mutable.Map[AnyRef, ChunkLoaderHandler] =
		mutable.Map[AnyRef, ChunkLoaderHandler]()

	def preInit(instance: AnyRef): Unit = {
		val manager: ChunkLoaderHandler = new ChunkLoaderHandler
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, manager)
		this.managers.put(instance, manager)
	}

	def register(instance: AnyRef, loader: IChunkLoader): Boolean = {
		this.managers.get(instance).get.register(loader)
	}

	def unregister(instance: AnyRef, loader: IChunkLoader): Boolean = {
		this.managers.get(instance).get.unregister(loader)
	}

}

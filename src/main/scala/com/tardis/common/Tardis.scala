package com.tardis.common

import java.util
import java.util.UUID

import com.tardis.common.dimensions.TardisManager
import com.tardis.common.init.{TardisBlocks, TardisEnts, TardisItems}
import com.tardis.common.network.PacketDimensionRegistration
import com.tardis.lookingglass.LookingGlass
import com.tardis.server.CommandTardis
import com.temportalist.origin.api.common.proxy.IProxy
import com.temportalist.origin.api.common.resource.{IModDetails, IModResource}
import com.temportalist.origin.api.common.utility.Scala
import com.temportalist.origin.foundation.common.IMod
import com.temportalist.origin.internal.common.handlers.RegisterHelper
import cpw.mods.fml.common.event._
import cpw.mods.fml.common.{Mod, SidedProxy}
import net.minecraft.entity.Entity
import net.minecraft.world.WorldServer
import net.minecraftforge.common.ForgeChunkManager

/**
 *
 *
 * @author TheTemportalist
 */
@Mod(modid = Tardis.MODID, name = Tardis.NAME, version = Tardis.VERSION,
	guiFactory = Tardis.clientProxy, modLanguage = "scala",
	dependencies = ""//required-after:origin@[6,);required-after:LookingGlass@[0.2,);"
)
object Tardis extends IMod with IModResource {

	final val MODID = "tardis"
	final val NAME = "Tardis"
	final val VERSION = "@MOD_VERSION@"
	final val clientProxy = "com.tardis.client.ProxyClient"
	final val serverProxy = "com.tardis.server.ProxyServer"

	override def getDetails: IModDetails = this

	override def getModVersion: String = this.VERSION

	override def getModName: String = this.NAME

	override def getModid: String = this.MODID

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: IProxy = null

	/*
	Click on console
	Close tardis door
	Lock tardis (you cannot interact with door, in/out/move through)
	Set player abilities so that:
	- Flying
	- Invulnerability
	Teleport player to tardis location
	-> Render player as tardis
	Spawn placeholder entity which renders as the player controlling the tardis
	- Immovable
	- When hurt, player comes back
	 */
	/*
	To exit manually, ESC to menu and press "Exit Controls" (where the "Save and Quit" is)
	save player vec
	save placeholder vec
	Kill placeholder ent
	Teleport player to placeholder vec
	-> Render player as normal
	Spawn tardis at player vec
	unlock tardis
	 */

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this, event, this.proxy, null, TardisEnts, TardisBlocks, TardisItems)

		RegisterHelper.registerExtendedPlayer(
			"tardis", classOf[PlayerTardis], deathPersistance = false
		)

		this.registerPackets(classOf[PacketDimensionRegistration])

		TardisManager.registerProviderType()

		ForgeChunkManager.setForcedChunkLoadingCallback(this, TardisChunkCallback)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)
		FMLInterModComms.sendMessage(LookingGlass.getModid, "API",
			"com.tardis.lookingglass.LookingGlass.register")
	}

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
	}

	@Mod.EventHandler
	def serverStart(event: FMLServerStartingEvent): Unit = {
		event.registerServerCommand(new CommandTardis())
		TardisManager.registerDimsFromDir(event.getServer.worldServerForDimension(0).
				getSaveHandler.getMapFileFromName("dummy").getParentFile
		)
	}

	@Mod.EventHandler
	def serverStop(event: FMLServerStoppedEvent): Unit = {
		dimensions.TardisManager.unregisterDims()
	}

	def getTardisInWorld(world: WorldServer, uuid: UUID, id: Int): EntityTardis = {
		Scala.foreach(world.loadedEntityList.asInstanceOf[util.List[Entity]],
			(index: Int, entity: Entity) => {
				entity match {
					case tardis: EntityTardis =>
						if (tardis.getUniqueID.equals(uuid)) return tardis
					case _ =>
				}
			}: Unit
		)
		/*
		val entity: Entity = world.getEntityByID(id)
		if (entity != null && entity.getUniqueID.equals(uuid) && entity.isInstanceOf[EntityTardis])
			entity.asInstanceOf[EntityTardis]
		else null
		*/
		null
	}

}

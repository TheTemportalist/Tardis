package com.tardis.lookingglass

import com.tardis.common.EntityTardis
import com.tardis.common.dimensions.TardisManager
import com.temportalist.origin.api.common.lib.{V3O, Crash}
import com.temportalist.origin.api.common.resource.IModResource
import com.xcompwiz.lookingglass.api.hook.WorldViewAPI2
import com.xcompwiz.lookingglass.api.{APIInstanceProvider, APIUndefined, APIVersionRemoved, APIVersionUndefined}
import net.minecraft.crash.CrashReport
import net.minecraft.util.ChunkCoordinates

/**
 * Created by TheTemportalist on 9/6/2015.
 */
object LookingGlass extends IModResource {

	private var api: AnyRef = null

	def getModid: String = "LookingGlass"

	override def getModName: String = "LookingGlass"

	override def getModVersion: String = "view-2"

	def register(provider: APIInstanceProvider): Unit = {
		try {
			this.api = provider.getAPIInstance(this.getModVersion)
		}
		catch {
			case e: APIUndefined =>
				// The API we requested doesn't exist.  Give up with a nice log message.
				Crash.throwCrashReport("The LookingGlass API of version " + this.getModVersion
						+ " does not exist. /me gives up...", new CrashReport("", e))
			case e: APIVersionUndefined =>
				// The API we requested exists, but the version we wanted is missing in the local
				// environment. We can try falling back to an older version.
				Crash.throwCrashReport("LookingGlass API version " + this.getModVersion
						+ " is not present. We cannot persist with loading. Sorry."
						+ " Try installing LookingGlass version " + this.getModVersion,
					new CrashReport("", e))
			case e: APIVersionRemoved =>
				// The API we requested exists, but the version we wanted has been removed and is no
				// longer supported. Better update.
				Crash.throwCrashReport("LookingGlass API version " + this.getModVersion
						+ " is no longer supported. Please submit a github issue complaining to "
						+ "the developer in charge that they need to update the LookingGlass API. "
						+ "Because people are silly.", new CrashReport("", e))
		}
	}

	def addWindow(tardis: EntityTardis): Unit = {
		if (!tardis.worldObj.isRemote) return
		this.api match {
			case wvAPI: WorldViewAPI2 =>
				val dim = tardis.getInteriorDimension()
				//val data = TardisManager.getDimData(dim, isServer = false)
				/*
				val wv = wvAPI.createWorldView(dim, new ChunkCoordinates(0, 0, 0), 256, 512)
				wv.getCamera.setLocation(0.5, 0, -0.3)
				wv.getCamera.setPitch(-10)
				wv.getCamera.setYaw(180)
				*/
				val wv = wvAPI.createWorldView(0, new ChunkCoordinates(3, 7, 4), 256, 512)
				wv.getCamera.setLocation(3, 7, 4)
				wv.getCamera.addRotations(0, 0)
				if (wv != null) tardis.setWorldView(wv)
			case _ =>
		}
	}

}

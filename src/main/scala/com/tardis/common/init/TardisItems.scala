package com.tardis.common.init

import com.tardis.common.dimensions.TardisManager
import com.tardis.common.{EntityTardis, Tardis}
import com.tardis.lookingglass.LookingGlass
import com.temportalist.origin.api.common.item.{ItemBase, ItemPlacer}
import com.temportalist.origin.foundation.common.register.ItemRegister
import com.temportalist.origin.internal.common.Origin
import net.minecraft.entity.Entity

/**
 *
 *
 * @author TheTemportalist 4/14/15
 */
object TardisItems extends ItemRegister {

	var tardis: ItemBase = null

	override def register(): Unit = {

		this.tardis = new ItemPlacer(Tardis.MODID, "tardis", classOf[EntityTardis]) {
			override def preSpawn(entity: Entity): Unit = {
				// todo this causes lag when spawning a tardis
				//new Thread(new Runnable {
				//	override def run(): Unit = {
				entity match {
					case tardis: EntityTardis =>
						TardisManager.registerTardis(tardis)
						tardis.setLocationAndAngles(
							tardis.posX, tardis.posY, tardis.posZ, 0F, 0F
						)
						if (tardis.worldObj.isRemote)
							LookingGlass.addWindow(tardis)
					case _ =>
				}


				//	}
				//}).start()
			}

		}
		Origin.addItemToTab(this.tardis)

	}

}

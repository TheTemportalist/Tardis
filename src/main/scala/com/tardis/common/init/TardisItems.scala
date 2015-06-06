package com.tardis.common.init

import com.tardis.common.{Tardis, EntityTardis}
import com.tardis.common.dimensions.TardisManager
import com.temportalist.origin.api.common.item.ItemPlacer
import com.temportalist.origin.api.common.register.ItemRegister
import com.temportalist.origin.common.Origin
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import net.minecraft.entity.Entity

/**
 *
 *
 * @author TheTemportalist 4/14/15
 */
object TardisItems extends ItemRegister {

	var tardis: ItemWrapper = null

	override def register(): Unit = {

		this.tardis = new ItemPlacer(Tardis.MODID, "tardis", classOf[EntityTardis]) {
			override def preSpawn(entity: Entity): Unit = {
				// todo this causes lag when spawning a tardis
				//new Thread(new Runnable {
				//	override def run(): Unit = {
				TardisManager.registerTardis(entity.asInstanceOf[EntityTardis])
				entity.setLocationAndAngles(
					entity.posX, entity.posY, entity.posZ, 0F, 0F
				)
				//	}
				//}).start()
			}

		}
		Origin.addItemToTab(this.tardis)

	}

}

package com.tardis.common.init

import com.tardis.common.{Tardis, EntityTardis}
import com.temportalist.origin.api.common.register.EntityRegister

/**
 *
 *
 * @author TheTemportalist 4/12/15
 */
object TardisEnts extends EntityRegister {

	override def register(): Unit = {
		this.addEntity(classOf[EntityTardis], "tardis", Tardis, 80, 3, true)
	}

}

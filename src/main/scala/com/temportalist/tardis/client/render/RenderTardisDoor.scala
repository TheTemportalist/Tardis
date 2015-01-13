package com.temportalist.tardis.client.render

import com.temportalist.origin.wrapper.client.render.TERenderer
import com.temportalist.tardis.client.model.ModelDoor
import com.temportalist.tardis.common.Tardis
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist
 */
class RenderTardisDoor() extends TERenderer(
	new ResourceLocation(Tardis.MODID, "textures/entity/tardis.png")
) {

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {

		new ModelDoor().render(tileEntity)

	}

}

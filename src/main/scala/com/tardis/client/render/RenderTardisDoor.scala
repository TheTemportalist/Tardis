package com.tardis.client.render

import com.tardis.client.model.ModelDoor
import com.tardis.common.Tardis
import com.temportalist.origin.wrapper.client.render.TERenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist
 */
class RenderTardisDoor() extends TERenderer(
	new ResourceLocation(Tardis.MODID, "textures/blocks/tardis_door2.png")
) {

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {

		new ModelDoor().render(tileEntity)

	}

}

package com.tardis.client.render

import com.tardis.common.Tardis
import com.tardis.common.tile.TEConsole
import com.temportalist.origin.api.client.render.TERenderItem
import com.temportalist.origin.api.client.render.model.ModelWrapper
import com.temportalist.origin.api.common.lib.vec.V3O
import net.minecraft.client.model.ModelRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist 4/13/15
 */
class RenderConsole() extends TERenderItem(new ResourceLocation(
	Tardis.MODID, "textures/model/console.png"
)) {

	val dummy: TEConsole = new TEConsole

	override def getRenderingTileItem(): TileEntity = this.dummy

	val model: ModelWrapper = new ModelWrapper(128, 128) {
		val center: ModelRenderer = this.createModel(
			new V3O(0, -8, 0),
			new V3O(5, 0, 5),
			new V3O(10, 16, 10),
			V3O.ZERO, 20, 38
		)
		val block10x10: ModelRenderer = this.createModel(
			new V3O(0, 16, 0),
			new V3O(10, 0, 10),
			new V3O(20, 1, 20),
			V3O.ZERO, 0, 16
		)
		this.center.addChild(this.block10x10)
		val block20x20: ModelRenderer = this.createModel(
			new V3O(0, 1, 0),
			new V3O(7, 0, 7),
			new V3O(14, 1, 14),
			V3O.ZERO, 12, 0
		)
		this.block10x10.addChild(this.block20x20)
		override def renderModel(f5: Float): Unit = this.center.render(f5)
	}

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {
		this.model.render(tileEntity)
	}

}

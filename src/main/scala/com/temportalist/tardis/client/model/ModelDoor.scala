package com.temportalist.tardis.client.model

import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.origin.wrapper.client.render.model.ModelWrapper
import com.temportalist.tardis.common.Tardis
import com.temportalist.tardis.common.block.BlockTardisDoor
import net.minecraft.block.BlockDoor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.model.ModelRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

/**
 *
 *
 * @author TheTemportalist
 */
class ModelDoor() extends ModelWrapper(128, 128) {

	// Lower Half things
	var base: ModelRenderer = null
	var b_box: ModelRenderer = null
	var b_doorL: ModelRenderer = null
	var b_doorR: ModelRenderer = null

	this.base = this.createModel(
		new V3O(0, 0, 0),
		new V3O(8, 0, 8),
		new V3O(16, 1, 16),
		V3O.ZERO, 0, 0
	)

	this.b_box = this.createModel(
		new V3O(0, 1, 0),
		new V3O(8, 0, 7),
		new V3O(16, 15, 15),
		V3O.ZERO, 0, 0
	)
	this.add(this.base, this.b_box)

	this.b_doorR = this.createModel(
		new V3O(-7, 7, -6),
		new V3O(0, 7, 0),
		new V3O(7, 15, 1),
		V3O.ZERO, 0, 0
	)
	this.add(this.base, this.b_doorR)

	this.b_doorL = this.createModel(
		new V3O(7, 7, -6),
		new V3O(7, 7, 0),
		new V3O(7, 15, 1),
		V3O.ZERO, 0, 0
	)
	this.add(this.base, this.b_doorL)

	// Upper Half things
	var top: ModelRenderer = null
	var t_box: ModelRenderer = null
	var t_doorL: ModelRenderer = null
	var t_doorR: ModelRenderer = null

	this.top = this.createModel(
		new V3O(0, 16, 0),
		new V3O(8, 1, 7),
		new V3O(16, 1, 15),
		V3O.ZERO, 0, 0
	)

	this.t_box = this.createModel(
		new V3O(0, -1, 0),
		new V3O(8, 15, 7),
		new V3O(16, 15, 15),
		V3O.ZERO, 0, 0
	)
	this.add(this.top, this.t_box)

	this.t_doorR = this.createModel(
		new V3O(-7, -7, -6),
		new V3O(0, 7, 0),
		new V3O(7, 15, 1),
		V3O.ZERO, 0, 0
	)
	this.add(this.top, this.t_doorR)

	this.t_doorL = this.createModel(
		new V3O(7, -7, -6),
		new V3O(7, 7, 0),
		new V3O(7, 15, 1),
		V3O.ZERO, 0, 0
	)
	this.add(this.top, this.t_doorL)

	override def render(te: TileEntity): Unit = {
		// todo move to WorldHelper
		val ibs: IBlockState = new V3O(te).getBlockState(te.getWorld)

		if (ibs.getBlock != Tardis.tDoor) return

		te.getBlockType match {
			case _: BlockTardisDoor =>

				val facing: EnumFacing = ibs.getValue(BlockDoor.FACING).asInstanceOf[EnumFacing]
				val yRot: Float = Math.toRadians((facing.getIndex - 2) * 90).toFloat
				val doorRot: Float = Math.toRadians(
					// todo move this to a constant somewhere
					if (ibs.getValue(BlockDoor.OPEN).asInstanceOf[Boolean]) 80
					else 0
				).toFloat

				ibs.getValue(BlockDoor.HALF) match {
					case BlockDoor.EnumDoorHalf.LOWER =>
						this.base.rotateAngleY = yRot
						this.b_doorL.rotateAngleY = -doorRot
						this.b_doorR.rotateAngleY = doorRot
						this.base.render(ModelWrapper.f5)
					case BlockDoor.EnumDoorHalf.UPPER =>
						this.top.rotateAngleY = yRot
						this.t_doorL.rotateAngleY = -doorRot
						this.t_doorL.rotateAngleY = doorRot
						this.top.render(ModelWrapper.f5)
					case _ =>
				}

			case _ =>
		}

	}

}

package com.temportalist.tardis.client.model

import com.temportalist.origin.library.common.lib.vec.Vector3O
import com.temportalist.tardis.common.EntityTardis
import net.minecraft.client.model.{ModelBase, ModelRenderer}
import net.minecraft.entity.Entity

/**
 *
 *
 * @author TheTemportalist
 */
class ModelTardis() extends ModelBase {

	this.textureWidth = 128
	this.textureHeight = 128

	val roof1: ModelRenderer = this.createModel(
		new Vector3O(0, 35, 0),
		new Vector3O(9, 0, 9),
		new Vector3O(18, 1, 18),
		Vector3O.ZERO, 0, 86
	)

	val roof2: ModelRenderer = this.createModel(
		new Vector3O(0, 1, 0),
		new Vector3O(8, 0, 8),
		new Vector3O(16, 1, 16),
		Vector3O.ZERO, 64, 0
	)
	add(this.roof1, this.roof2)
	val light: ModelRenderer = this.createModel(
		new Vector3O(0, 1, 0),
		new Vector3O(1, 0, 1),
		new Vector3O(2, 3, 2),
		Vector3O.ZERO, 0, 0
	)
	add(this.roof2, this.light)

	val bar: ModelRenderer = this.createModel(
		new Vector3O(0, 0, 0),
		new Vector3O(10, 3, 10),
		new Vector3O(20, 3, 20),
		Vector3O.ZERO, 0, 63
	)
	add(this.roof1, this.bar)
	val walls: ModelRenderer = this.createModel(
		new Vector3O(0, -3, 0),
		new Vector3O(9, 31, 9),
		new Vector3O(18, 31, 18),
		Vector3O.ZERO, 0, 14
	)
	add(this.bar, this.walls)

	val postBL: ModelRenderer = this.createModel(
		new Vector3O(8.5, -3, -8.5),
		new Vector3O(1, 31, 1),
		new Vector3O(2, 31, 2),
		Vector3O.ZERO, 96, 31
	)
	add(this.bar, this.postBL)
	val postBR: ModelRenderer = this.createModel(
		new Vector3O(-8.5, -3, -8.5),
		new Vector3O(1, 31, 1),
		new Vector3O(2, 31, 2),
		Vector3O.ZERO, 104, 31
	)
	add(this.bar, this.postBR)
	val postFL: ModelRenderer = this.createModel(
		new Vector3O(8.5, -3, 8.5),
		new Vector3O(1, 31, 1),
		new Vector3O(2, 31, 2),
		Vector3O.ZERO, 112, 31
	)
	add(this.bar, this.postFL)
	val postFR: ModelRenderer = this.createModel(
		new Vector3O(-8.5, -3, 8.5),
		new Vector3O(1, 31, 1),
		new Vector3O(2, 31, 2),
		Vector3O.ZERO, 120, 31
	)
	add(this.bar, this.postFR)

	val base: ModelRenderer = this.createModel(
		new Vector3O(0, -34, 0),
		new Vector3O(11, 1, 11),
		new Vector3O(22, 1, 22),
		Vector3O.ZERO, 0, 105
	)
	add(this.bar, this.base)

	val doorL: ModelRenderer = this.createModel(
		new Vector3O(-0.5, -15.5, 0.5),
		new Vector3O(8, 15.5, 1),
		new Vector3O(8, 31, 1),
		Vector3O.ZERO, 88, 64
	)
	add(this.postFL, this.doorL)
	val doorR: ModelRenderer = this.createModel(
		new Vector3O(0.5, -15.5, 0.5),
		new Vector3O(0, 15.5, 1),
		new Vector3O(8, 31, 1),
		Vector3O.ZERO, 88, 96
	)
	add(this.postFR, this.doorR)

	/*
	val roof1: ModelRenderer = this.createModel(
		new Vector3O(0, 34, 0),
		new Vector3O(9, 0, 9),
		new Vector3O(18, 1, 18),
		Vector3O.ZERO, 0, 0
	)

	val roof2: ModelRenderer = this.createModel(
		new Vector3O(0, 1, 0),
		new Vector3O(8, 0, 8),
		new Vector3O(16, 1, 16),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.roof2)
	val roof3: ModelRenderer = this.createModel(
		new Vector3O(0, 1, 0),
		new Vector3O(7, 0, 7),
		new Vector3O(14, 1, 14),
		Vector3O.ZERO, 0, 0
	)
	this.roof2.addChild(this.roof3)
	val light: ModelRenderer = this.createModel(
		new Vector3O(0, 1, 0),
		new Vector3O(1, 0, 1),
		new Vector3O(2, 3, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof3.addChild(this.light)

	val barTFront: ModelRenderer = this.createModel(
		new Vector3O(0, 0, 8),
		new Vector3O(8, 2, 0),
		new Vector3O(16, 2, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.barTFront)
	val barTBack: ModelRenderer = this.createModel(
		new Vector3O(0, 0, -8),
		new Vector3O(8, 2, 2),
		new Vector3O(16, 2, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.barTBack)
	val barTLeft: ModelRenderer = this.createModel(
		new Vector3O(8, 0, 0),
		new Vector3O(0, 2, 8),
		new Vector3O(2, 2, 16),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.barTLeft)
	val barTRight: ModelRenderer = this.createModel(
		new Vector3O(-8, 0, 0),
		new Vector3O(2, 2, 8),
		new Vector3O(2, 2, 16),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.barTRight)

	val columnFL: ModelRenderer = this.createModel(
		new Vector3O(9, 0, 9),
		new Vector3O(1, 33, 1),
		new Vector3O(2, 33, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.columnFL)
	val columnFR: ModelRenderer = this.createModel(
		new Vector3O(-9, 0, 9),
		new Vector3O(1, 33, 1),
		new Vector3O(2, 33, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.columnFR)
	val columnBL: ModelRenderer = this.createModel(
		new Vector3O(9, 0, -9),
		new Vector3O(1, 33, 1),
		new Vector3O(2, 33, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.columnBL)
	val columnBR: ModelRenderer = this.createModel(
		new Vector3O(-9, 0, -9),
		new Vector3O(1, 33, 1),
		new Vector3O(2, 33, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.columnBR)

	val barBBack: ModelRenderer = this.createModel(
		new Vector3O(0, -33, -8),
		new Vector3O(8, 0, 2),
		new Vector3O(16, 2, 2),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.barBBack)
	val barBLeft: ModelRenderer = this.createModel(
		new Vector3O(8, -33, 0),
		new Vector3O(0, 0, 8),
		new Vector3O(2, 2, 16),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.barBLeft)
	val barBRight: ModelRenderer = this.createModel(
		new Vector3O(-8, -33, 0),
		new Vector3O(2, 0, 8),
		new Vector3O(2, 2, 16),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.barBRight)
	val base: ModelRenderer = this.createModel(
		new Vector3O(0, -33, 0),
		new Vector3O(11, 1, 11),
		new Vector3O(22, 1, 22),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.base)

	val wallBack: ModelRenderer = this.createModel(
		new Vector3O(0, -2, -8),
		new Vector3O(8, 29, 1),
		new Vector3O(16, 29, 1),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.wallBack)
	val wallLeft: ModelRenderer = this.createModel(
		new Vector3O(8, -2, 0),
		new Vector3O(0, 29, 8),
		new Vector3O(1, 29, 16),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.wallLeft)
	val wallRight: ModelRenderer = this.createModel(
		new Vector3O(-8, -2, 0),
		new Vector3O(1, 29, 8),
		new Vector3O(1, 29, 16),
		Vector3O.ZERO, 0, 0
	)
	this.roof1.addChild(this.wallRight)

	val doorLeft: ModelRenderer = this.createModel(
		new Vector3O(8, -17.5, 8),
		new Vector3O(8, 15.5, 0),
		new Vector3O(8, 31, 1),
		new Vector3O(0, -35, 0), 0, 0
	)
	this.roof1.addChild(this.doorLeft)
	val doorRight: ModelRenderer = this.createModel(
		new Vector3O(-8, -17.5, 8),
		new Vector3O(0, 15.5, 0),
		new Vector3O(8, 31, 1),
		new Vector3O(0, 35, 0), 0, 0
	)
	this.roof1.addChild(this.doorRight)
	*/

	private def add(parent: ModelRenderer, child: ModelRenderer): Unit = {
		parent.addChild(child)
	}

	private def createModel(origin: Vector3O, offset: Vector3O,
			bounds: Vector3O, rot: Vector3O, u: Int, v: Int): ModelRenderer = {
		val mr: ModelRenderer = new ModelRenderer(this, u, v)
		mr.setRotationPoint(origin.x_f(), origin.y_f(), origin.z_f())
		mr.addBox(
			-offset.x_f(), -offset.y_f(), -offset.z_f(),
			bounds.x_i(), bounds.y_i(), bounds.z_i()
		)
		mr.setTextureSize(this.textureWidth, this.textureHeight)
		mr.rotateAngleX = Math.toRadians(rot.x).asInstanceOf[Float]
		mr.rotateAngleY = Math.toRadians(rot.y).asInstanceOf[Float]
		mr.rotateAngleZ = Math.toRadians(rot.z).asInstanceOf[Float]
		mr
	}

	override def render(entity: Entity, parTime: Float, parSwingSuppress: Float, unknown1: Float,
			headAngleY: Float, headAngleX: Float, unknown2: Float): Unit = {
		this.setRotationAngles(
			parTime, parSwingSuppress, unknown1, headAngleY, headAngleX, unknown2, entity
		)
		this.roof1.render(unknown2)

	}

	override def setRotationAngles(parTime: Float, parSwingSuppress: Float, unknown1: Float,
			headAngleY: Float, headAngleX: Float, unknown2: Float, entity: Entity): Unit = {
		entity match {
			case tardis: EntityTardis =>
				// todo main rotations when flying

				this.roof1.rotateAngleY = -Math.toRadians(tardis.rotationYaw).toFloat

				// Door rotations
				val rotation: Float = Math.toRadians(
					if (tardis.isDoorOpen()) 80
					else 0
				).toFloat
				this.doorL.rotateAngleY = -rotation
				this.doorR.rotateAngleY = rotation

			case _ =>
		}


	}

}

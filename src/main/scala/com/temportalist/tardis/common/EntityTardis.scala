package com.temportalist.tardis.common

import com.temportalist.origin.library.common.lib.vec.Vector3O
import net.minecraft.entity.{EntityLivingBase, Entity}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util._
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class EntityTardis(w: World) extends Entity(w) {

	this.setSize(1F, 2.5F)

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def entityInit(): Unit = {
		this.dataWatcher.addObject(7, 0)
		this.dataWatcher.addObject(8, 0.toByte)
		this.dataWatcher.addObject(9, 0.toByte)
		this.dataWatcher.addObject(6, 1.0F)

		this.dataWatcher.addObject(10, 0) // door open(1)/shut(0)
		this.dataWatcher.addObject(11, 0) // tardis' interior dimension id

	}

	override def canUseCommand(permissionLevel: Int, command: String): Boolean = false

	override def isEntityInvulnerable(p_180431_1_ : DamageSource): Boolean = true

	/*
		override def applyEntityAttributes(): Unit = {
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(
				200
			)
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed).
					setBaseValue(2D)
		}

		override def addChatComponentMessage(p_146105_1_ : IChatComponent): Unit = {}

		override def addStat(stat: StatBase, amount: Int): Unit = {}

		override def openGui(mod: scala.Any, modGuiId: Int, world: World, x: Int, y: Int,
				z: Int): Unit = {}
		override def canAttackPlayer(other: EntityPlayer): Boolean = false

		override def onDeath(cause: DamageSource): Unit = {}

		override def handleClientSettings(p_147100_1_ : C15PacketClientSettings): Unit = {}

		override def isSpectator: Boolean = true
	*/
	override def onUpdate(): Unit = {}

	override def travelToDimension(dimensionId: Int): Unit = {
		//		this.mcServer.getConfigurationManager().transferPlayerToDimension(this, dimensionId)
		super.travelToDimension(dimensionId)
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def writeEntityToNBT(tagCom: NBTTagCompound): Unit = {

	}

	override def readEntityFromNBT(tagCom: NBTTagCompound): Unit = {

	}

	override def attackEntityFrom(source: DamageSource, amount: Float): Boolean = {
		source.getSourceOfDamage match {
			case player: EntityPlayer =>
				if (player.capabilities.isCreativeMode) {
					this.setDead()
					return true
				}
			case _ =>
		}
		false
	}

	override def canBeCollidedWith: Boolean = !this.isDead

	override def canBePushed: Boolean = false

	override def getCollisionBox(entityIn: Entity): AxisAlignedBB = null

	override def getBoundingBox: AxisAlignedBB = this.getEntityBoundingBox

	override def onCollideWithPlayer(player: EntityPlayer): Unit = {
		// todo collision player things
		println("collision")
	}

	override def interactFirst(playerIn: EntityPlayer): Boolean = {
		if (playerIn.isSneaking) {
			PlayerTardis.open(this, playerIn)
			return true
		}
		if (this.isInFieldOfViewOf(this, playerIn))
			if (this.isDoorOpen()) this.closeDoor() else this.openDoor()
		true
	}

	// TODO move to an entity wrapper
	def canEntityBeSeen(entity: Entity): Boolean = {
		this.worldObj.rayTraceBlocks(
			new Vec3(
				this.posX, this.posY + this.getEyeHeight.asInstanceOf[Double], this.posZ)
			,
			new Vec3(
				entity.posX, entity.posY + entity.getEyeHeight.asInstanceOf[Double], entity.posZ
			)
		) == null
	}

	// todo move to origin
	def isInFieldOfViewOf(entity: EntityTardis, thisEntity: EntityLivingBase): Boolean = {
		val entityLookVec: Vector3O = new Vector3O(entity.getLook(1.0F)) //.normalize()
		val differenceVec: Vector3O = new Vector3O(
				thisEntity.posX - entity.posX,
				thisEntity.posY +
						(thisEntity.height /* / 2.0F */).asInstanceOf[Double] -
						(entity.posY + entity.getEyeHeight().asInstanceOf[Double]),
				thisEntity.posZ - entity.posZ
			)

		val lengthVec: Double = differenceVec.toVec3().lengthVector()

		val differenceVec_normal: Vec3 = differenceVec.toVec3().normalize()

		val d1: Double = entityLookVec.toVec3().dotProduct(differenceVec_normal)

		if (d1 > (1.0D - 0.025D) / lengthVec && thisEntity.canEntityBeSeen(entity)) {
			true
		}
		else {
			false
		}
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def isDoorOpen(): Boolean = this.dataWatcher.getWatchableObjectInt(10) == 1

	def openDoor(): Unit = this.dataWatcher.updateObject(10, 1)

	def closeDoor(): Unit = this.dataWatcher.updateObject(10, 0)

	def getInteriorDimension(): Int = this.dataWatcher.getWatchableObjectInt(11)

	def setInteriorDimension(dimid: Int): Unit =
		this.dataWatcher.updateObject(11, dimid)

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}

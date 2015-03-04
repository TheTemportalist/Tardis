package com.tardis.common

import java.util.UUID

import com.tardis.common.dimensions.TardisManager
import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.origin.library.common.utility.WorldHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util._
import net.minecraft.world.World
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.common.ForgeChunkManager.Ticket

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

	override def travelToDimension(dimensionId: Int): Unit = {
		//		this.mcServer.getConfigurationManager().transferPlayerToDimension(this, dimensionId)
		super.travelToDimension(dimensionId)
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def writeEntityToNBT(tagCom: NBTTagCompound): Unit = {
		tagCom.setInteger("doorState", this.getDoorState())
		tagCom.setInteger("interiorDim", this.getInteriorDimension())
	}

	override def readEntityFromNBT(tagCom: NBTTagCompound): Unit = {
		this.setDoorState(tagCom.getInteger("doorState"))
		this.setInteriorDimension(tagCom.getInteger("interiorDim"))
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
		if (WorldHelper.isInFieldOfView(this, player) && this.isDoorOpen()) {
			if (player.getPositionVector.distanceTo(this.getPositionVector) <= 1.3d) {
				TardisManager.movePlayerIntoTardis(player, this)
			}
		}
	}

	override def interactFirst(playerIn: EntityPlayer): Boolean = {
		if (playerIn.isSneaking) {
			//PlayerTardis.open(this, playerIn)
			return true
		}
		if (WorldHelper.isInFieldOfView(this, playerIn) && this.getInteriorDimension() != 0)
			if (this.isDoorOpen()) this.closeDoor() else this.openDoor()
		true
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getDoorState(): Int = this.dataWatcher.getWatchableObjectInt(10)

	def setDoorState(state: Int): Unit = this.dataWatcher.updateObject(10, state)

	def isDoorOpen(): Boolean = this.getDoorState() == 1

	def openDoor(): Unit = this.setDoorState(1)

	def closeDoor(): Unit = this.setDoorState(0)

	def getInteriorDimension(): Int = this.dataWatcher.getWatchableObjectInt(11)

	def setInteriorDimension(dimid: Int): Unit = this.dataWatcher.updateObject(11, dimid)

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private var chunkTicket: Ticket = null

	override def onEntityUpdate(): Unit = {
		super.onEntityUpdate()
		if (!this.worldObj.isRemote && this.chunkTicket == null) {
			this.chunkTicket = ForgeChunkManager.requestTicket(
				Tardis, this.getEntityWorld, ForgeChunkManager.Type.ENTITY)
			if (this.chunkTicket != null) {
				this.chunkTicket.bindEntity(this)
				this.chunkTicket.getModData.setString("id", "Tardis")
				val chunkPos: V3O = this.getChunkPos()
				chunkPos.writeTo(this.chunkTicket.getModData, "chunkPos")
				// auto write entities by dimid and uuid
				this.chunkTicket.getModData
						.setInteger("tardisDim", this.getEntityWorld.provider.getDimensionId)
				val uuid: UUID = this.getUniqueID
				this.chunkTicket.getModData.setLong("tardisIDmax", uuid.getMostSignificantBits)
				this.chunkTicket.getModData.setLong("tardisIDmin", uuid.getLeastSignificantBits)
				ForgeChunkManager.forceChunk(this.chunkTicket, chunkPos.toChunkPair())
			}
		}
	}

	def getChunkPos(): V3O = {
		val vec: V3O = new V3O(this)
		new V3O(vec.x_i() >> 4, 0, vec.z_i() >> 4)
	}

	override def setDead(): Unit = {
		super.setDead()
		if (!this.worldObj.isRemote)
			ForgeChunkManager.releaseTicket(this.chunkTicket)
	}

}

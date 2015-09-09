package com.tardis.common

import java.util.UUID

import com.tardis.common.dimensions.{InyardData, TardisManager}
import com.tardis.common.init.TardisBlocks
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.WorldHelper
import com.xcompwiz.lookingglass.api.view.IWorldView
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util._
import net.minecraft.world.{World, WorldServer}
import net.minecraftforge.common.ForgeChunkManager.Ticket
import net.minecraftforge.common.{DimensionManager, ForgeChunkManager}

/**
 *
 *
 * @author TheTemportalist
 */
class EntityTardis(w: World) extends Entity(w) {

	this.setSize(1F, 2.5F)

	private var worldView: IWorldView = null

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def entityInit(): Unit = {
		this.dataWatcher.addObject(7, 0)
		this.dataWatcher.addObject(8, 0.toByte)
		this.dataWatcher.addObject(9, 0.toByte)
		this.dataWatcher.addObject(6, 1.0F)

		this.dataWatcher.addObject(10, 0) // door open(1)/shut(0)
		this.dataWatcher.addObject(11, 0) // tardis' interior dimension id

	}

	override def isEntityInvulnerable: Boolean = true

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
		if (!DimensionManager.isDimensionRegistered(this.getInteriorDimension()))
			DimensionManager.registerDimension(
				this.getInteriorDimension(), TardisManager.providerID
			)
		TardisManager.getDimData(this.getInteriorDimension(), !this.worldObj.isRemote)
				.setTardis(this)
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

	override def getBoundingBox: AxisAlignedBB = this.boundingBox

	override def onCollideWithPlayer(player: EntityPlayer): Unit = {
		if (this.isDoorOpen() && WorldHelper.isInFieldOfView(this, player)) {
			if (new V3O(player).distance(new V3O(this)) <= 1.3d) {
				TardisManager.movePlayerIntoTardis(player, this)
			}
		}
	}

	override def interactFirst(playerIn: EntityPlayer): Boolean = {
		if (WorldHelper.isInFieldOfView(this, playerIn) && this.getInteriorDimension() != 0) {
			if (this.isDoorOpen()) this.closeDoor() else this.openDoor()
			val dimData: InyardData = TardisManager.getDimData(
				this.getInteriorDimension(), WorldHelper.isServer(this))
			val world: WorldServer = DimensionManager.getWorld(this.getInteriorDimension())
			if (world != null)
				TardisBlocks.tDoor.cycleOpen_Tall(world, dimData.getDoorPos)
		}
		true
	}

	override def getLookVec: Vec3 = {
		val f1 = MathHelper.cos(-this.rotationYaw * 0.017453292F - Math.PI.toFloat)
		val f2 = MathHelper.sin(-this.rotationYaw * 0.017453292F - Math.PI.toFloat)
		val f3 = -MathHelper.cos(-this.rotationPitch * 0.017453292F)
		val f4 = MathHelper.sin(-this.rotationPitch * 0.017453292F)
		Vec3.createVectorHelper((f2 * f3).toDouble, f4.toDouble, (f1 * f3).toDouble)
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getDoorState(): Int = this.dataWatcher.getWatchableObjectInt(10)

	def setDoorState(state: Int): Unit = this.dataWatcher.updateObject(10, state)

	def setDoorOpen(isOpen: Boolean): Unit =
		if (isOpen) this.setDoorState(1) else this.setDoorState(0)

	def isDoorOpen(): Boolean = this.getDoorState() == 1

	def openDoor(): Unit = this.setDoorState(1)

	def closeDoor(): Unit = this.setDoorState(0)

	def getInteriorDimension(): Int = this.dataWatcher.getWatchableObjectInt(11)

	def setInteriorDimension(dimid: Int): Unit = this.dataWatcher.updateObject(11, dimid)

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private var chunkTicket: Ticket = null
	private var dimTicket: Ticket = null

	override def onEntityUpdate(): Unit = {
		super.onEntityUpdate()
		if (!this.worldObj.isRemote) {
			if (this.chunkTicket == null) {
				this.chunkTicket = ForgeChunkManager.requestTicket(
					Tardis, this.worldObj, ForgeChunkManager.Type.ENTITY)
				if (this.chunkTicket != null) {
					this.chunkTicket.bindEntity(this)
					this.chunkTicket.getModData.setString("id", "Tardis")
					val chunkPos: V3O = this.getChunkPos()
					chunkPos.writeTo(this.chunkTicket.getModData, "chunkPos")
					// auto write entities by dimid and uuid
					this.chunkTicket.getModData.setInteger(
						"tardisDim", this.worldObj.provider.dimensionId
					)
					val uuid: UUID = this.getUniqueID
					this.chunkTicket.getModData.setLong("tardisIDmax", uuid.getMostSignificantBits)
					this.chunkTicket.getModData.setLong("tardisIDmin", uuid.getLeastSignificantBits)
					this.chunkTicket.getModData.setInteger("tardisID", this.getEntityId)
					ForgeChunkManager.forceChunk(this.chunkTicket, chunkPos.toChunkPair)
				}
			}
			/*
			if (this.dimTicket == null) {
				this.dimTicket = ForgeChunkManager.requestTicket(
					Tardis, this.worldObj, ForgeChunkManager.Type.ENTITY)
				if (this.dimTicket != null) {
					this.dimTicket.bindEntity(this)
					this.dimTicket.getModData.setString("id", "TardisDim")
					val doorPos = TardisManager.getDimData(this.getInteriorDimension(), !this.worldObj.isRemote).getDoorPos
					val chunkPos = new V3O(doorPos.x_i() >> 4, 0, doorPos.z_i() >> 4)
					chunkPos.writeTo(this.dimTicket.getModData, "chunkPos")
					ForgeChunkManager.forceChunk(this.dimTicket, chunkPos.toChunkPair)
				}
			}
			*/
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

	def setWorldView(worldView: IWorldView): Unit = {
		this.worldView = worldView
		if (this.worldView != null) {
			this.worldView.markDirty()
		}
	}

	def getWorldView: IWorldView = this.worldView

}

package com.tardis.server

import com.tardis.common.EntityTardis
import com.temportalist.origin.api.common.item.ItemPlacer
import com.temportalist.origin.api.common.lib.vec.V3O
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
class CommandTardis() extends CommandBase {

	override def getCommandName: String = "tardis"

	override def getCommandUsage(sender: ICommandSender): String = {
		"tardis < open <at | new <at | point <x> <y> <z> > | point <x> <y> <z> > | close >"
	}

	override def processCommand(sender: ICommandSender, args: Array[String]): Unit = {
		if (args.length <= 0) return

		var player: EntityPlayer = null
		sender match {
			case p: EntityPlayer => player = p
			case _ =>
		}
		if (player == null) return

		args(0) match {
			case "open" =>
				if (args.length < 2) return
				args(1) match {
					case "at" =>
					case "new" =>
						val dir: Int = MathHelper.floor_double(
							((player.rotationYaw * 4F) / 360F) + 0.5D
						) & 3
						var facing: ForgeDirection = null
						dir match {
							case 0 => facing = ForgeDirection.SOUTH // 3
							case 1 => facing = ForgeDirection.WEST // 4
							case 2 => facing = ForgeDirection.NORTH // 2
							case 3 => facing = ForgeDirection.EAST // 5
							case _ =>
						}

						val world: World = DimensionManager.getWorld(player.dimension)

						val tardis: Entity = ItemPlacer.createEntity(
							classOf[EntityTardis], world,
							new V3O(player) + (new V3O(facing) * 2), dir * 90
						)

					case "point" =>
				}
			case "close" =>

			case _ =>
		}

	}

}

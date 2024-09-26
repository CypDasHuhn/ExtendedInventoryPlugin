package de.cypdashuhn.rooster_demo

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.argument_constructors.CentralArgument
import de.cypdashuhn.rooster.commands.argument_constructors.CentralizedArgumentList
import de.cypdashuhn.rooster.commands.argument_constructors.RootArgument
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import org.bukkit.entity.Player

const val INTERFACE_KEY = "interface"

@RoosterCommand
val testInterfaces = RootArgument(
    startingUnit = {
        it is Player
    },
    label = "testInterface",
    followingArguments = CentralizedArgumentList(
        CentralArgument(
            key = INTERFACE_KEY,
            isValid = { argInfo ->
                Rooster.registeredInterfaces.any { it.interfaceName == argInfo.arg } to {
                    it.sender.tSend(
                        "No such interface ${argInfo.arg}"
                    )
                }
            },
            tabCompletions = { argInfo ->
                Rooster.registeredInterfaces.map { it.interfaceName }
            },
            argumentHandler = { argInfo -> Rooster.registeredInterfaces.first { it.interfaceName == argInfo.arg } },
            errorMissing = { argInfo -> argInfo.sender.tSend("No such interface ${argInfo.arg}") },
            invoke = {
                val targetInterface = it.values[INTERFACE_KEY] as Interface<Context>
                val context = targetInterface.getContext(it.sender as Player)
                targetInterface.openInventory(it.sender, context)
            },
        )
    )
)
package de.cypdashuhn.rooster.commands_new

import de.cypdashuhn.rooster.commands_new.constructors.LiteralArgument
import de.cypdashuhn.rooster.commands_new.constructors.NumberArgument

class Argument {

    companion object {
        val literal = LiteralArgument
        val number = NumberArgument
    }
}

fun test() {
    val s = Argument.number.middle()
    val e = NumberArgument.middle()
}
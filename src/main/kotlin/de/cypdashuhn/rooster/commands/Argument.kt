package de.cypdashuhn.rooster.commands

import de.cypdashuhn.rooster.commands.argument_constructors.RootArgument

/**
 * Using this annotation on a field of the class [RootArgument], makes that
 * field being registered as a Command.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class RoosterCommand
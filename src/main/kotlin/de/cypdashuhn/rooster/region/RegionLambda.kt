package de.cypdashuhn.rooster.region

import de.cypdashuhn.rooster.database.utility_tables.RoosterLambda
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect

internal class RegionLambdaWrapper<T : Any> {
    private var regionLambda: RegionLambda? = null
    private var regionLambdaWithContext: RegionLambdaWithContext<T>? = null

    constructor(regionLambda: RegionLambda) {
        this.regionLambda = regionLambda
    }

    constructor(regionLambdaWithContext: RegionLambdaWithContext<T>) {
        this.regionLambdaWithContext = regionLambdaWithContext
    }

    val eventTargetDTO: EventTargetDTO
        get() {
            if (regionLambda != null) regionLambda!!.eventTargetDTO
            else if (regionLambdaWithContext != null) regionLambdaWithContext!!.eventTargetDTO

            throw IllegalStateException("One of the fields must be initialized")
        }

    val contextClass: KClass<T>?
        get() = regionLambdaWithContext?.contextClass

    fun invokeLambda(event: Event, context: T? = null) {
        if (regionLambda != null) {
            regionLambda!!.actionLambda(event)
        } else if (regionLambdaWithContext != null) {
            requireNotNull(context) { "If the Wrapper is registered with a context-lambda, a context must be given" }
            regionLambdaWithContext!!.actionLambda(event, context)
        } else throw IllegalStateException("One of the fields must be initialized")
    }
}

class RegionLambda private constructor(
    val actionLambda: (Event) -> Unit,
    val eventTargetDTO: EventTargetDTO,
    val lambdaKey: String?
) {
    companion object {
        fun multipleEvents(
            eventTargetDTO: EventTargetDTO,
            actionLambda: (Event) -> Unit
        ): RegionLambda {
            return RegionLambda(
                actionLambda,
                eventTargetDTO,
                extractLambdaKey(actionLambda)
            )
        }

        fun moveEvent(
            eventTarget: List<MoveEvent>,
            actionLambda: (PlayerMoveEvent) -> Unit
        ): RegionLambda {
            return RegionLambda(
                { event ->
                    require(event is PlayerMoveEvent) { "Event is not of PlayerMoveEvent type" }
                    actionLambda(event)
                },
                EventTargetDTO(moveEvents = eventTarget),
                extractLambdaKey(actionLambda)
            )
        }

        fun <E : Event> singleEvent(
            actionLambda: (E) -> Unit,
            eventClass: KClass<E>
        ): RegionLambda {
            return RegionLambda(
                { event ->
                    require(event::class == eventClass) { "Event is not of type ${eventClass.simpleName}" }
                    actionLambda(event as E)
                },
                EventTargetDTO(events = listOf(eventClass as KClass<out Event>)),
                extractLambdaKey(actionLambda)
            )
        }

        @OptIn(ExperimentalReflectionOnLambdas::class)
        private fun <E : Event> extractLambdaKey(lambda: (E) -> Unit): String? {
            val functionRef = lambda.reflect() as? KFunction<*>
            return functionRef?.findAnnotation<RoosterLambda>()?.key
        }
    }
}

class RegionLambdaWithContext<T : Any> private constructor(
    val contextClass: KClass<T>,
    val actionLambda: (Event, T) -> Unit,
    val eventTargetDTO: EventTargetDTO,
    val lambdaKey: String?
) {
    companion object {
        fun <T : Any> multipleEvents(
            eventTargetDTO: EventTargetDTO,
            contextClass: KClass<T>,
            actionLambda: (Event, T) -> Unit
        ): RegionLambdaWithContext<T> {
            return RegionLambdaWithContext(
                contextClass,
                actionLambda,
                eventTargetDTO,
                extractLambdaKey(actionLambda)
            )
        }

        fun <T : Any> moveEvent(
            eventTarget: List<MoveEvent>,
            contextClass: KClass<T>,
            actionLambda: (PlayerMoveEvent, T) -> Unit
        ): RegionLambdaWithContext<T> {
            return RegionLambdaWithContext(
                contextClass,
                { event, context -> actionLambda(event as PlayerMoveEvent, context) },
                EventTargetDTO(moveEvents = eventTarget),
                extractLambdaKey(actionLambda)
            )
        }

        fun <T : Any, E : Event> singleEvent(
            contextClass: KClass<T>,
            actionLambda: (E, T) -> Unit,
            eventClass: KClass<E>
        ): RegionLambdaWithContext<T> {
            return RegionLambdaWithContext(
                contextClass,
                { event, context ->
                    require(event::class == eventClass) { "Event is not of type ${eventClass.simpleName}" }
                    actionLambda(event as E, context)
                },
                EventTargetDTO(events = listOf(eventClass as KClass<out Event>)),
                extractLambdaKey(actionLambda)
            )
        }

        @OptIn(ExperimentalReflectionOnLambdas::class)
        private fun <T : Any, E : Event> extractLambdaKey(lambda: (E, T) -> Unit): String? {
            val functionRef = lambda.reflect() as? KFunction<*>
            return functionRef?.findAnnotation<RoosterLambda>()?.key
        }
    }
}
package de.cypdashuhn.rooster.util

typealias PredicateCombinator<T> = ((T) -> Boolean, (T) -> Boolean) -> ((T) -> Boolean)

/** Returns true if both first and second are true. */
fun <T> andR(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) && second(t) }
}

/** Returns true if either first or second is true. */
fun <T : Any> orR(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) || second(t) }
}

/** Returns the negation (not) of the input. */
fun <T> negateR(condition: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !condition(t) }
}

/** Returns true if first and second are different. */
fun <T> xorR(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) xor second(t) }
}

/** Returns true if first and second are not both true. */
fun <T> nandR(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !(first(t) && second(t)) }
}

/** Returns true if both first and second are false. */
fun <T> norR(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !(first(t) || second(t)) }
}

/** Returns true if first is false or second is true. Only false when first is true and second is false. */
fun <T> impliesR(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> !first(t) || second(t) }
}

/** Returns true if first is true and second is false. */
fun <T> andNotR(first: (T) -> Boolean, second: (T) -> Boolean): (T) -> Boolean {
    return { t: T -> first(t) && !second(t) }
}

/** Returns true if both this and other are true. */
infix fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean {
    return andR(this, other)
}

/** Returns true if either this or other is true. */
infix fun <T : Any> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean {
    return orR(this, other)
}

/** Returns the negation (not) of this. */
fun <T> ((T) -> Boolean).negate(): (T) -> Boolean {
    return negateR(this)
}

/** Returns true if this and other are different. */
infix fun <T> ((T) -> Boolean).xor(other: (T) -> Boolean): (T) -> Boolean {
    return xorR(this, other)
}

/** Returns true if this and other are not both true. */
infix fun <T> ((T) -> Boolean).nand(other: (T) -> Boolean): (T) -> Boolean {
    return nandR(this, other)
}

/** Returns true if both this and other are false. */
infix fun <T> ((T) -> Boolean).nor(other: (T) -> Boolean): (T) -> Boolean {
    return norR(this, other)
}

/** Returns true if this is false or other is true. Only false when this is true and other is false. */
infix fun <T> ((T) -> Boolean).implies(other: (T) -> Boolean): (T) -> Boolean {
    return impliesR(this, other)
}

/** Returns true if this is true and other is false. */
infix fun <T> ((T) -> Boolean).andNot(other: (T) -> Boolean): (T) -> Boolean {
    return andNotR(this, other)
}
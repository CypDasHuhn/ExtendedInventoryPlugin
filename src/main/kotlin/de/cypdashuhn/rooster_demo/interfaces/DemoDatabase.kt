package de.cypdashuhn.rooster_demo.interfaces

abstract class DemoDatabase {
    abstract fun demoPrep()
}

fun List<DemoDatabase>.init() = this.forEach { it.demoPrep() }
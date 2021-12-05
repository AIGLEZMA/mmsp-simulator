package me.aiglez.mmspsimulator.data

abstract class Identifiable {

    abstract val id: Int
    abstract val name: String

    override fun toString(): String {
        return "$id - $name"
    }
}
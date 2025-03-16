package com.github.memory

import kotlin.experimental.and
import kotlin.experimental.or

/**
 * This class is responsible for representing a single 8bit word. It uses data class as a way to
 * reduce boilerplate code.
 * @author rodrigotimoteo
**/
data class Word(
    val content: Byte = 0
) : BitOperations {

    private fun checkInvalidBit(bit: Int) {
        if (bit < 0 || bit > 7) throw IllegalArgumentException("Invalid bit")
    }

    override fun setBit(bit: Int) {
        checkInvalidBit(bit)
        content.or(1.shl(bit).toByte())
    }

    override fun resetBit(bit: Int) {
        checkInvalidBit(bit)
        content.and((1.shl(bit)).inv().toByte())
    }

    override fun testBit(bit: Int): Boolean {
        checkInvalidBit(bit)
        return content.and(1.shl(bit).toByte()) == 1.toByte()
    }
}

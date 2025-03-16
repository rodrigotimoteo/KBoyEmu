package com.github.memory

/**
 * This interface represents the avaliable methods that a user class must implement when they need
 * to do bit operations
 * @author rodrigotimoteo
 **/
interface BitOperations {
    /**
     * Sets the given bit to 1
     */
    fun setBit(bit: Int)

    /**
     * Sets the given bit to 0
     */
    fun resetBit(bit: Int)

    /**
     * Tests the given bit returning bit == 1
     */
    fun testBit(bit: Int): Boolean
}
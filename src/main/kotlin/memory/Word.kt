package memory

import kotlin.experimental.and
import kotlin.experimental.or

/**
 * This class is responsible for representing a single 8bit word. It uses data class as a way to
 * reduce boilerplate code.
 *
 * @author rodrigotimoteo
**/
data class Word(
    var content: Byte = 0
) : BitOperations {

    /**
     * Assigns a new value to a given Word
     *
     * @param value to assign
     */
    fun setValue(value: Int) {
        content = value.toByte()
    }

    /**
     * Gets the value contained in the Word
     *
     * @return value as Int
     */
    fun getValue(): Int {
        return content.toInt()
    }

    /**
     * Checks if the bit given for setBit/resetBit/testBit method fit the required criteria (1 byte = 8 bits)
     *
     * @param bit to check
     */
    @Suppress("MagicNumber")
    private fun checkInvalidBit(bit: Int) {
        require(bit in 0..7)
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

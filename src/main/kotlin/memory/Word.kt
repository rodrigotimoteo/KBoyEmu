package memory

/**
 * This class is responsible for representing a single 8bit word. It uses data class as a way to
 * reduce boilerplate code.
 * @author rodrigotimoteo
 **/
data class Word(
    private var value: UByte = 0x00.toUByte()
) : BitOperations {

    /**
     * Sets the value of a given [Word]
     *
     * @param newValue to assign to this [Word]
     */
    internal fun setValue(newValue: Int) {
        value = newValue.toUByte()
    }

    /**
     * Sets the value of a given [Word]
     *
     * @param newValue to assign to this [Word]
     */
    internal fun setValue(newValue: UByte) {
        value = newValue
    }

    /**
     * Gets the value contained inside this [Word]
     *
     * @return the value inside [Word]
     */
    internal fun getValue(): Int = value.toInt()

    /**
     * Sets a specific bit of a memory word (eg, the value 0010 if we set bit number 0 then value becomes 0011)
     *
     * @param bit position to set
     */
    override fun setBit(bit: Int) {
        require(bit in 0..7) { BIT_ASSERTION_ERROR_MESSAGE }

        value = (value or ((1 shl bit).toUByte()))
    }

    /**
     * Resets a specific bit of a memory word (eg, the value 0010 if we reset bit number 1 then value becomes 0000)
     *
     * @param bit position to reset
     */
    override fun resetBit(bit: Int) {
        require(bit in 0..7) { BIT_ASSERTION_ERROR_MESSAGE }

        value = (value and (1 shl bit).inv().toUByte())
    }

    /**
     * Tests a specific bit of a memory word (eg, returns whether the given bit is set (true) or not (false))
     *
     * @param bit position to test
     */
    override fun testBit(bit: Int): Boolean {
        require(bit in 0..7) { BIT_ASSERTION_ERROR_MESSAGE }

        return (value.toInt() and (1 shl bit)) != 0x00
    }

    companion object {
        const val BIT_ASSERTION_ERROR_MESSAGE = "Bit outside the expected range"
    }
}

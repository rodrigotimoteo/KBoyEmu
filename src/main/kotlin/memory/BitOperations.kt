package memory

/**
 * Defines special behavior regarding bit operations that certain memory classes should implement
 *
 * @author rodrigotimoteo
 **/
interface BitOperations {

    /**
     * Sets a specific bit of a memory word (eg, the value 0010 if we set bit number 0 then value becomes 0011)
     *
     * @param bit position to set
     */
    fun setBit(bit: Int)

    /**
     * Resets a specific bit of a memory word (eg, the value 0010 if we reset bit number 1 then value becomes 0000)
     *
     * @param bit position to reset
     */
    fun resetBit(bit: Int)

    /**
     * Tests a specific bit of a memory word (eg, returns whether the given bit is set (true) or not (false))
     *
     * @param bit position to test
     */
    fun testBit(bit: Int): Boolean
}

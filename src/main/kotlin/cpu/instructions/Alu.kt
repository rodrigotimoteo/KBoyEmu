package cpu.instructions

import memory.Bus

/**
 * Class responsible for handling all things that deal with arithmetic operations
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber")
class Alu(
    private val bus: Bus
) {

    /**
     * Checks if the zero flag should be set or reset
     *
     * @param value to check
     * @return status of zero flag
     */
    private fun checkZero(value: Int): Boolean {
        return (value and 0xFF) == 0x00
    }

    /**
     * Checks if the half carry flag will be set or reset based on the values used in the additions.
     *
     * @param value1 first value used
     * @param value2 second value used
     * @param carry this is either 1 if carry flag was set or 0 otherwise
     * @return status of half carry flag
     */
    private fun checkHalfCarryAdd(
        value1: Int,
        value2: Int,
        carry: Int
    ): Boolean {
        return (((value1 and 0xF) + (value2 and 0xF) + carry) and 0x10) == 0x10
    }

    /**
     * Checks if the carry flag will be set or reset based on the values used in additions.
     *
     * @param value to check
     * @return status of carry flag
     */
    private fun checkCarryAdd(value: Int): Boolean {
        return value > 0xFF
    }

    /**
     * Checks if the half carry flag will be set or reset based on the values used in the subtractions.
     *
     * @param value1 first value used
     * @param value2 second value used
     * @param carry this is either 1 if carry flag was set or 0 otherwise
     * @return status of half carry flag
     */

    private fun checkHalfCarrySub(
        value1: Int,
        value2: Int,
        carry: Int
    ): Boolean {
        return ((value1 and 0xF) - (value2 and 0xF) - carry) < 0
    }

    /**
     * Checks if the carry flag will be set or reset based on the values used in subtractions.
     *
     * @param value to check
     * @return status of carry flag
     */
    private fun checkCarrySub(value: Int): Boolean {
        return value < 0
    }
}

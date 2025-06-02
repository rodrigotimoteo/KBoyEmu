/**
 * Holds the project's constants
 *
 * @author rodrigotimoteo
 **/
object KBConstants {
    const val PROGRAM_COUNTER_INITIAL_VALUE = 0x0100
    const val STACK_POINTER_INITIAL_VALUE = 0xFFFE
    const val AF_INITIAL_VALUE = 0x01B0
    const val BC_INITIAL_VALUE = 0x0013
    const val DE_INITIAL_VALUE = 0x00D8
    const val HL_INITIAL_VALUE = 0x014D

    const val EIGHT_BITS = 8

    const val FILTER_TOP_BITS = 0xFF00
    const val FILTER_LOWER_BITS = 0x00FF
    const val FILTER_4_BYTES = 0xFFFF
    const val FILTER_3_BYTES = 0x0FFF
    const val FILTER_2_BYTES = 0xFF
    const val FILTER_BYTE = 0xF

    const val CARRY_BIT = 4
    const val HALF_CARRY_BIT = 5
    const val SUBTRACT_BIT = 6
    const val ZERO_BIT = 7

    const val TWO_COMPLIMENT = 0x7F
    const val HIGHEST_BIT = 0x80

}

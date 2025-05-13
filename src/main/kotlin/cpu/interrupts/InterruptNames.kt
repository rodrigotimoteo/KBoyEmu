package cpu.interrupts

/**
 * Stores the types of interrupts avaliable and which bit to test in order to check for them
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber")
enum class InterruptNames(
    val testBit: Int
) {
    VBLANK_INT(0),
    STAT_INT(1),
    TIMER_INT(2),
    SERIAL_INT(3),
    JOYPAD_INT(4)
}

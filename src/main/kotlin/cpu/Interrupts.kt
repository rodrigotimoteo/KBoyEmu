package cpu

import memory.Bus

/**
 * Class responsible for handling all the CPU interrupts, these are responsible
 * for servicing hardware timers, such as PPU timers, input and CPU timers
 *
 * @author rodrigotimoteo
 **/
class Interrupts(
    private val bus: Bus
) {
}

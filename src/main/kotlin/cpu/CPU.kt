package cpu

import cpu.instructions.Decoder
import cpu.interrupts.Interrupts
import cpu.registers.Registers
import memory.Bus

/**
 * Represents the Central Processing Unit of an 8-bit 8080-like Sharp CPU
 *
 * @author rodrigotimoteo
 **/
class CPU(
    private val bus: Bus
) {
    internal val registers: Registers = Registers(bus)

    internal val timers: Timers = Timers()

    internal val interrupts: Interrupts = Interrupts(bus)

    private val decoder: Decoder = Decoder(bus)

    private val isCgb = bus.isCGB()

    /**
     * Stores whether the CPU is currently halted
     */
    private var isHalted = false

    /**
     * Stores whether the CPU is currently stopped
     */
    private var isStopped = false

    public fun tick() {
        if (!isStopped) {
            if (!isHalted) {
                fetchOperation()

                val imeChange = interrupts.requestedInterruptChange()
                val interruptChangeCounter = timers.getInterruptChangedCounter()
                val machineCycles = timers.getMachineCycles()

                if (imeChange && interruptChangeCounter < machineCycles) {
                    interrupts.triggerImeChange()
                }
            } else {
                timers.tick()
            }

            interrupts.handleInterrupt()
        }
    }

    private fun fetchOperation() {
        val programCounter = registers.getProgramCounter()

        if (interrupts.isHaltBug()) {
            decoder.decode(programCounter)
            registers.incrementProgramCounter(-1)
            interrupts.disableHaltBug()
        } else {
            decoder.decode(bus.getValue(programCounter))
        }
    }

    /**
     * Getter for the isHalted flag
     *
     * @return true if CPU is halted false otherwise
     */
    fun isHalted(): Boolean = isHalted

    /**
     * Sets the halted state to the provided one
     *
     * @param haltedState should be halted or not (true if should false otherwise)
     */
    fun setHalted(haltedState: Boolean) {
        isHalted = haltedState
    }

    /**
     * Getter for the isStopped flag
     *
     * @return true if CPU is stopped false otherwise
     */
    fun isStopped(): Boolean = isStopped

    /**
     * Sets the stopped state to the provided one
     *
     * @param stoppedState should be stopped or not (true if should false otherwise)
     */
    fun setStopped(stoppedState: Boolean) {
        isStopped = stoppedState
    }
}

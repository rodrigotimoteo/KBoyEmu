package cpu.instructions

import cpu.Flags
import memory.Bus
import memory.BusConstants
import memory.ReservedAddresses

/**
 * @author rodrigotimoteo
 **/
class Control(
    private val bus: Bus
) {
    /**
     * Stores the flags object to be interacted with freely
     */
    private val flags: Flags = bus.getFromCPU(BusConstants.GET_FLAGS, Bus.EMPTY_ARGUMENTS) as Flags

    /**
     * Executes something design to do nothing (quite fun actually)
     */
    fun nop() = bus.executeFromCPU(BusConstants.INCR_PC, 1)

    /**
     * Comlements the carry flag
     */
    fun ccf() {
        flags.setFlags(zero = null, subtract = false, half = false, carry = !flags.getCarryFlag())

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Sets the carry flag
     */
    fun scf() {
        flags.setFlags(zero = null, subtract = false, half = false, carry = true)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Powers down the CPU until the next interrupt occurs. Reduces power consumption
     */
    fun halt() {
        bus.executeFromCPU(BusConstants.HALT, Bus.EMPTY_ARGUMENTS)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Stops the CPU and LCD until a button is pressed
     */
    fun stop() {
        bus.executeFromCPU(BusConstants.STOP, Bus.EMPTY_ARGUMENTS)

        bus.setValue(ReservedAddresses.DIV.memoryAddress, 0)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Disables interrupts after execution
     */
    fun di() {
        bus.executeFromCPU(BusConstants.DISABLE_INT, Bus.EMPTY_ARGUMENTS)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Enales interrupts after execution
     */
    fun ei() {
        bus.executeFromCPU(BusConstants.ENABLE_INT, Bus.EMPTY_ARGUMENTS)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }
}

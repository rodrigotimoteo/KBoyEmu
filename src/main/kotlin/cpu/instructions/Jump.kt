package cpu.instructions

import KBConstants
import cpu.Flags
import memory.Bus
import memory.BusConstants

/**
 * This class is responsible for handling all jump operations, calls and the restart operation
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber", "TooManyFunctions")
class Jump(
    private val bus: Bus
) {
    /**
     * Stores the flags object to be interacted with freely
     */
    private val flags: Flags = bus.getFromCPU(BusConstants.GET_FLAGS, Bus.EMPTY_ARGUMENTS) as Flags

    /**
     * Gets the boolean correspondent to the given condition
     *
     * @param condition which condition to test
     * @return boolean of condition test
     */
    private fun getConditionalValue(condition: JumpConstants): Boolean {
        return when(condition) {
            JumpConstants.NZ -> !flags.getZeroFlag()
            JumpConstants.Z -> flags.getZeroFlag()
            JumpConstants.NC -> !flags.getCarryFlag()
            JumpConstants.C -> flags.getCarryFlag()
        }
    }

    /**
     * Jumps to the address given by the two words after the program counter
     */
    fun jp() {
        val jumpAddress = bus.calculateNN()

        bus.executeFromCPU(BusConstants.SET_PC, jumpAddress)
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
    }

    /**
     * Jumps to the address given by the two words after the program counter if the given condition is statisfied
     *
     * @param condition which condition to test for
     * @see jp()
     */
    fun jpCond(condition: JumpConstants) {
        val conditionalValue = getConditionalValue(condition)

        if (conditionalValue) jp()
        else {
            bus.executeFromCPU(BusConstants.INCR_PC, 3)
            repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }
        }
    }

    /**
     * Jumps to the address contained inside the HL register
     */
    fun jpHL() {
        bus.executeFromCPU(BusConstants.SET_PC, bus.getFromCPU(BusConstants.SET_HL, Bus.EMPTY_ARGUMENTS) as Int)
    }

    /**
     * Adds a given value to the program counter taken from the value after the program counter, this value is signed
     */
    fun jr() {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val programCounter = bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int
        val checkValue = bus.getValue(programCounter + 1)

        if (checkValue shr 7 == 0) {
            bus.executeFromCPU(BusConstants.INCR_PC, checkValue and 0x7F)
        } else {
            bus.executeFromCPU(BusConstants.INCR_PC, (checkValue and 0x7F) - 128)
        }

        bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Adds a given value to the program counter taken from the value after the program counter, if the given condition
     * is satisfied, given value is signed
     *
     * @param condition which condition to test for
     * @see jr()
     */
    fun jrCond(condition: JumpConstants) {
        val conditionalValue = getConditionalValue(condition)

        if (conditionalValue) jr()
        else {
            bus.executeFromCPU(BusConstants.INCR_PC, 3)
            bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        }
    }

    /**
     * Pushes the address of the next instruction onto the stack and then jumps to given NN address
     */
    fun call() {
        repeat(3) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val programCounter = bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int
        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int
        val jumpAddress = bus.calculateNN()

        bus.setValue(stackPointer - 1, ((programCounter + 3) and KBConstants.FILTER_TOP_BITS) shr 8)
        bus.setValue(stackPointer - 2, (programCounter + 3) and KBConstants.FILTER_LOWER_BITS)

        bus.executeFromCPU(BusConstants.SET_PC, jumpAddress)
        bus.executeFromCPU(BusConstants.INCR_PC, -2)
    }

    /**
     * Pushes the address of the next instruction onto the stack and then jumps to given NN address if given condition
     * is satisfied
     *
     * @param condition which condition to test for
     * @see call()
     */
    fun callCond(condition: JumpConstants) {
        val conditionalValue = getConditionalValue(condition)

        if (conditionalValue) call()
        else {
            bus.executeFromCPU(BusConstants.INCR_PC, 3)
            repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }
        }
    }

    /**
     * This operation pops two bytes from the stack and jumps to that address
     */
    fun ret() {
        repeat(3) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int
        val jumpAddress = bus.getValue(stackPointer) + bus.getValue(stackPointer + 1) shl 8

        bus.executeFromCPU(BusConstants.SET_PC, jumpAddress)
        bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * This operation pops two bytes from the stack and jumps to that address
     *
     * @param condition which condition to test for
     * @see ret()
     */
    fun retCond(condition: JumpConstants) {
        val conditionalValue = getConditionalValue(condition)

        if (conditionalValue) ret()
        else bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Pops two bytes from the stack and jumps to that address and enables interrupts
     */
    fun reti() {
        ret()
        bus.executeFromCPU(BusConstants.ENABLE_INT, Bus.EMPTY_ARGUMENTS)
    }

    /**
     * Pushes the present address onto the stack and jump to $0000 plus the given address
     *
     * @param jumpAddress offset of where to jump to
     */
    fun rst(jumpAddress: Int) {
        repeat(3) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val programCounter = bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int
        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int

        bus.setValue(stackPointer - 1, ((programCounter + 1) and KBConstants.FILTER_TOP_BITS) shr 8)
        bus.setValue(stackPointer - 2, (programCounter + 1) and KBConstants.FILTER_LOWER_BITS)

        bus.executeFromCPU(BusConstants.SET_PC, jumpAddress)
        bus.executeFromCPU(BusConstants.INCR_PC, -2)
    }
}

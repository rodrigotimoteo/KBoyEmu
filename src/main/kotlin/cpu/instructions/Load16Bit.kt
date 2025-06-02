package cpu.instructions

import KBConstants
import cpu.Flags
import memory.Bus
import memory.BusConstants

/**
 * Class responsible for handling all things that deal with 16 bit load operations in CPU instruction set
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber")
class Load16Bit(
    private val bus: Bus
) {
    /**
     * Stores the flags object to be interacted with freely
     */
    private val flags: Flags = bus.getFromCPU(BusConstants.GET_FLAGS, Bus.EMPTY_ARGUMENTS) as Flags

    /**
     * Loads a 16bit immediate value after the program counter onto a register set, BC, DE or HL, which is used is
     * defined by the argument
     *
     * @param type selects which register set to use
     */
    fun ld16bit(type: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val value = bus.calculateNN()

        when (type) {
            0 -> bus.executeFromCPU(BusConstants.SET_BC, value)
            1 -> bus.executeFromCPU(BusConstants.SET_DE, value)
            2 -> bus.executeFromCPU(BusConstants.SET_HL, value)
        }

        bus.executeFromCPU(BusConstants.INCR_PC, 3)
    }

    /**
     * Puts the two immediate words after the program counter onto the Stack Pointer
     */
    fun ldSPUU() {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val value = bus.calculateNN()

        bus.executeFromCPU(BusConstants.SET_SP, value)
        bus.executeFromCPU(BusConstants.INCR_PC, 3)
    }

    /**
     * Put the HL 16bit register onto the stack pointer
     */
    fun ldSPHL() {
        val value = bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS)

        bus.executeFromCPU(BusConstants.SET_SP, value)
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Puts the SP value plus the immediate word after the program counter into HL 16 bit register (computes cpu flags)
     */
    fun ldHL() {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int
        val programCounter = bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int

        val value = bus.getWord(programCounter + 1)
        val signedValue = if (value?.testBit(7) == true)
            (value.getValue() and KBConstants.TWO_COMPLIMENT) - KBConstants.HIGHEST_BIT else value?.getValue() ?: 0

        val finalAddress = (stackPointer + signedValue) and KBConstants.FILTER_4_BYTES
        val valueToAssign = bus.getValue(programCounter + 1)

        val halfCarry = ((stackPointer and KBConstants.FILTER_BYTE) + (valueToAssign and KBConstants.FILTER_BYTE)
                and 0x10) == 0x10
        val carry = (((stackPointer and KBConstants.FILTER_2_BYTES) + valueToAssign) and 0x100) == 0x100

        flags.setFlags(zero = false, subtract = false, half = halfCarry, carry = carry)

        bus.executeFromCPU(BusConstants.SET_HL, finalAddress)
        bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Put the stack pointer at 16 bit address immediately after the program counter
     */
    fun ldNNSP() {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val address = bus.calculateNN()
        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int
        bus.setValue(address, stackPointer and KBConstants.FILTER_2_BYTES)
        bus.executeFromCPU(BusConstants.INCR_PC, 3)
    }

    /**
     * Push register pair given onto the stack
     *
     * @param register pair of register to use as input
     */
    fun push(register: Int) {
        repeat(3) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val registerValue = when (register) {
            0 -> bus.getFromCPU(BusConstants.GET_AF, Bus.EMPTY_ARGUMENTS)
            1 -> bus.getFromCPU(BusConstants.GET_BC, Bus.EMPTY_ARGUMENTS)
            2 -> bus.getFromCPU(BusConstants.GET_DE, Bus.EMPTY_ARGUMENTS)
            3 -> bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS)
            else -> return
        } as Int
        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int

        bus.setValue(stackPointer - 1, registerValue and KBConstants.FILTER_TOP_BITS)
        bus.setValue(stackPointer - 2, registerValue and KBConstants.FILTER_LOWER_BITS)

        bus.executeFromCPU(BusConstants.INCR_SP, -2)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Pop two bytes of stack into the given register pair
     *
     * @param register pair of register to use as input
     */
    fun pop(register: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int
        val wordToInsert = bus.getValue(stackPointer + 1) shl 8 + bus.getValue(stackPointer)

        when (register) {
            0 -> bus.executeFromCPU(BusConstants.SET_AF, wordToInsert)
            1 -> bus.executeFromCPU(BusConstants.SET_BC, wordToInsert)
            2 -> bus.executeFromCPU(BusConstants.SET_DE, wordToInsert)
            3 -> bus.executeFromCPU(BusConstants.SET_HL, wordToInsert)
            else -> return
        }

        bus.executeFromCPU(BusConstants.INCR_SP, -2)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }
}

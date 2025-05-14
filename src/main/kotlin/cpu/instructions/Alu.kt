package cpu.instructions

import KBConstants.FILTER_2BYTE_WORD
import KBConstants.FILTER_BYTE
import cpu.Flags
import cpu.registers.RegisterNames
import jdk.jfr.MemoryAddress
import memory.Bus
import memory.BusConstants
import memory.Word

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
     * Stores the flags object to be interacted with freely
     */
    private val flags: Flags = bus.getFromCPU(BusConstants.GET_FLAGS, Bus.EMPTY_ARGUMENTS) as Flags

    /**
     * Checks if the zero flag should be set or reset
     *
     * @param value to check
     * @return status of zero flag
     */
    private fun checkZero(value: Int): Boolean {
        return (value and FILTER_2BYTE_WORD) == 0x00
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
        return (((value1 and FILTER_BYTE) + (value2 and FILTER_BYTE) + carry) and 0x10) == 0x10
    }

    /**
     * Checks if the carry flag will be set or reset based on the values used in additions.
     *
     * @param value to check
     * @return status of carry flag
     */
    private fun checkCarryAdd(value: Int): Boolean {
        return value > FILTER_2BYTE_WORD
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
        return ((value1 and FILTER_BYTE) - (value2 and FILTER_BYTE) - carry) < 0
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

    /**
     * Performs the add operation from any given value stored in a register to register A
     *
     * @param register used to retrieve the add value
     */
    public fun add(register: RegisterNames) {
        val givenRegister = bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word
        val registerA = bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word

        val valueInGivenRegister = givenRegister.getValue()
        val valueInRegisterA = registerA.getValue()
        val finalValue = valueInGivenRegister + valueInRegisterA

        val halfCarry = checkHalfCarryAdd(valueInGivenRegister, valueInRegisterA, 0)
        val carry = checkCarryAdd(finalValue)
        val zero = checkZero(finalValue)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, finalValue))
        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = carry)

        bus.executeFromCPU(BusConstants.INCR_PC, arrayListOf(1))
    }

    /**
     * Performs the add operation from any given value stored in a register to register A
     *
     * @param register used to retrieve the add value
     */
    public fun addSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val registerA = bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word

        val valueInAddress = bus.getValue(memoryAddress)
        val valueInRegisterA = registerA.getValue()
        val finalValue = valueInAddress + valueInRegisterA

        val halfCarry = checkHalfCarryAdd(valueInAddress, valueInRegisterA, 0)
        val carry = checkCarryAdd(finalValue)
        val zero = checkZero(finalValue)


        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, finalValue))
        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = carry)

        if (useHL) bus.executeFromCPU(BusConstants.INCR_PC, arrayListOf(1))
        else bus.executeFromCPU(BusConstants.INCR_PC, arrayListOf(2))
    }
}

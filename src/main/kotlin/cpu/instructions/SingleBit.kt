package cpu.instructions

import cpu.Flags
import cpu.registers.RegisterNames
import memory.Bus
import memory.BusConstants
import memory.Word

/**
 * Class responsible for handling all things that deal with single bit operations in CPU instruction set
 *
 * @author rodrigotimoteo
 **/
class SingleBit(
    private val bus: Bus
) {
    /**
     * Stores the flags object to be interacted with freely
     */
    private val flags: Flags = bus.getFromCPU(BusConstants.GET_FLAGS, Bus.EMPTY_ARGUMENTS) as Flags

    /**
     * Test given bit of a specific register given
     *
     * @param bit to test
     * @param register to test
     */
    fun bit(bit: Int, register: RegisterNames) {
        val givenRegister = bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word

        val testResult = givenRegister.testBit(bit)

        flags.setFlags(zero = !testResult, subtract = false, half = true, carry = null)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Test given bit of a specific memory address contained in (HL)
     *
     * @param bit to test
     * @param memoryAddress retrieve memory to test bit
     */
    fun bitHL(bit: Int, memoryAddress: Int) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val testResult = bus.getWord(memoryAddress)?.testBit(bit) == true

        flags.setFlags(zero = !testResult, subtract = false, half = true, carry = null)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Sets a bit on the given cpu register
     *
     * @param bit to set
     * @param register to change the given bit
     */
    fun set(bit: Int, register: RegisterNames) {
        val givenRegister = bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word

        givenRegister.setBit(bit)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Sets a bit of a specific memory address contained in (HL)
     *
     * @param bit to set
     * @param memoryAddress retrieve memory to test bit
     */
    fun setHL(bit: Int, memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        bus.getWord(memoryAddress)?.setBit(bit)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Resets a bit on the given cpu register
     *
     * @param bit to set
     * @param register to change the given bit
     */
    fun res(bit: Int, register: RegisterNames) {
        val givenRegister = bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word

        givenRegister.resetBit(bit)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Resets a bit of a specific memory address contained in (HL)
     *
     * @param bit to set
     * @param memoryAddress retrieve memory to test bit
     */
    fun resHL(bit: Int, memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        bus.getWord(memoryAddress)?.resetBit(bit)

        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }
}

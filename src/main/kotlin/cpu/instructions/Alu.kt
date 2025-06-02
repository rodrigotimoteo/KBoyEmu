package cpu.instructions

import cpu.Flags
import cpu.registers.RegisterNames
import memory.Bus
import memory.BusConstants
import memory.Word

/**
 * Class responsible for handling all things that deal with arithmetic operations
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber", "TooManyFunctions")
class Alu(
    private val bus: Bus
) {
    /**
     * Stores the flags object to be interacted with freely
     */
    private val flags: Flags = bus.getFromCPU(BusConstants.GET_FLAGS, Bus.EMPTY_ARGUMENTS) as Flags

    /**
     * Checks whether the zero flag should be set or reset
     *
     * @param value to check
     * @return status of zero flag
     */
    private fun checkZero(value: Int): Boolean {
        return (value and KBConstants.FILTER_2_BYTES) == 0x00
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
        return (((value1 and KBConstants.FILTER_BYTE) + (value2 and KBConstants.FILTER_BYTE) + carry) and 0x10) == 0x10
    }

    /**
     * Checks if the carry flag will be set or reset based on the values used in additions.
     *
     * @param value to check
     * @return status of carry flag
     */
    private fun checkCarryAdd(value: Int): Boolean {
        return value > KBConstants.FILTER_2_BYTES
    }

    /**
     * Checks if the half carry flag will be set or reset based on the values used in the subtractions.
     *
     * @param value1 first value used
     * @param value2 second value used
     * @param carry this is either 1 if a carry flag was set or 0 otherwise
     * @return status of half carry flag
     */

    private fun checkHalfCarrySub(
        value1: Int,
        value2: Int,
        carry: Int
    ): Boolean {
        return ((value1 and KBConstants.FILTER_BYTE) - (value2 and KBConstants.FILTER_BYTE) - carry) < 0
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
     * @param register used to retrieve the value to add
     */
    fun add(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val finalValue = valueInGivenRegister + valueInRegisterA

        val halfCarry = checkHalfCarryAdd(valueInGivenRegister, valueInRegisterA, 0)
        val carry = checkCarryAdd(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Performs the add operation from any given value stored in a register to register A
     *
     * @param memoryAddress used to retrieve the value to add
     * @param useHL if should retrieve from HL register
     */
    fun addSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)
        val finalValue = valueInAddress + valueInRegisterA

        val halfCarry = checkHalfCarryAdd(valueInAddress, valueInRegisterA, 0)
        val carry = checkCarryAdd(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        if (useHL) bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Performs the operations of adding any given value (in this case only the ones contained inside registers) to A
     * also adding the carry flag status (1 if true 0 otherwise)
     *
     * @param register used to retrieve the register to add to register A's value
     */
    fun adc(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val carryAsValue = if (flags.getCarryFlag()) 1 else 0
        val finalValue = valueInGivenRegister + valueInRegisterA + carryAsValue

        val halfCarry = checkHalfCarryAdd(valueInGivenRegister, valueInRegisterA, carryAsValue)
        val carry = checkCarryAdd(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Performs the operations of adding any given value (in this case, the value is contained in memory) to A also
     * adding the carry flag status (1 if true 0 otherwise). This can be using register address (1 mCycles) or given by
     * the memory directly (2 mCycles)
     *
     * @param memoryAddress used to retrieve the value to add to register A's value
     * @param useHL      if HL is being used or not
     */
    fun adcSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)

        val carryAsValue = if (flags.getCarryFlag()) 1 else 0
        val halfCarry = checkHalfCarryAdd(valueInAddress, valueInRegisterA, carryAsValue)
        val finalValue = valueInAddress + valueInRegisterA + carryAsValue

        val carry = checkCarryAdd(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        if (useHL) bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Subtracts the given value inside the register from register A's value
     *
     * @param register which register to use
     */
    fun sub(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val halfCarry = checkHalfCarrySub(valueInRegisterA, valueInGivenRegister, 0)
        val finalValue = (valueInRegisterA - valueInGivenRegister)

        val carry = checkCarrySub(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Subtracts the given value from the value inside register A's value, this value can be gathered from the HL
     * register combo if useHL is true if not it will be gathered from the given memory address
     *
     * @param memoryAddress which memory address to use
     * @param useHL if HL is being used or not
     */
    fun subSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)

        val halfCarry = checkHalfCarrySub(valueInRegisterA, valueInAddress, 0)
        val finalValue = (valueInRegisterA - valueInAddress)

        val carry = checkCarrySub(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        if (useHL) bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Subtracts the given register and the carry flag from register A (SBC instruction).
     *
     * Updates flags: Z (zero), N (subtract), H (half-carry), C (carry).
     *
     * @param register The register to subtract from A.
     */
    fun sbc(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val carryAsValue = if (flags.getCarryFlag()) 1 else 0
        val halfCarry = checkHalfCarrySub(valueInRegisterA, valueInGivenRegister, carryAsValue)
        val finalValue = (valueInRegisterA - valueInGivenRegister - carryAsValue)

        val carry = checkCarrySub(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Subtracts the value at the given memory address and the carry flag from register A.
     *
     * Used for SBC (A, HL) or SBC (A, nn) depending on `useHL`.
     * Updates flags: Z, N, H, C.
     *
     * @param memoryAddress The memory address to read the value from.
     * @param useHL If true, increments PC by 1 (HL indirect); otherwise, by 2 (immediate address).
     */
    fun sbcSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)

        val carryAsValue = if (flags.getCarryFlag()) 1 else 0
        val halfCarry = checkHalfCarrySub(valueInRegisterA, valueInAddress, carryAsValue)
        val finalValue = (valueInRegisterA - valueInAddress - carryAsValue)

        val carry = checkCarrySub(finalValue)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        if (useHL)bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Performs bitwise AND between register A and the given register.
     *
     * Stores the result in A and updates flags: Z (zero), N (reset), H (set), C (reset).
     *
     * @param register The register to AND with A.
     */
    fun and(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val finalValue = valueInRegisterA and valueInGivenRegister
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = true, carry = false)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Performs bitwise AND between register A and the value at the given memory address.
     *
     * Used for AND (A, HL) or AND (A, nn) based on `useHL`.
     * Updates flags: Z (zero), N (reset), H (set), C (reset).
     *
     * @param memoryAddress The address to read the value from.
     * @param useHL If true, increments PC by 1 (HL); otherwise by 2 (immediate address).
     */
    fun andSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)
        val finalValue = valueInRegisterA and valueInAddress
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero,  subtract = false, half = true, carry = false)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        if (useHL) bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Performs bitwise OR between register A and the given register.
     *
     * Stores the result in A and updates flags: Z (zero), N (reset), H (reset), C (reset).
     *
     * @param register The register to OR with A.
     */
    fun or(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val finalValue = valueInRegisterA or valueInGivenRegister
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = false, carry = false)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Performs bitwise OR between register A and the value at the given memory address.
     *
     * Used for OR (A, HL) or OR (A, nn) depending on `useHL`.
     * Updates flags: Z (zero), N (reset), H (reset), C (reset).
     *
     * @param memoryAddress The memory address to read the value from.
     * @param useHL If true, increments PC by 1 (HL); otherwise by 2 (immediate address).
     */
    fun orSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)
        val finalValue = valueInRegisterA or valueInAddress
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = false, carry = false)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        if (useHL)bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Performs bitwise XOR between register A and the given register.
     *
     * Stores the result in A and updates flags: Z (zero), N (reset), H (reset), C (reset).
     *
     * @param register The register to XOR with A.
     */
    fun xor(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val finalValue = valueInRegisterA xor valueInGivenRegister
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = false, carry = false)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Performs bitwise XOR between register A and the value at the given memory address.
     *
     * Used for XOR (A, HL) or XOR (A, nn) depending on `useHL`.
     * Updates flags: Z (zero), N (reset), H (reset), C (reset).
     *
     * @param memoryAddress The memory address to read the value from.
     * @param useHL If true, increments PC by 1 (HL); otherwise by 2 (immediate address).
     */
    fun xorSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)
        val finalValue = valueInRegisterA xor valueInAddress
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = false, carry = false)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        if (useHL) bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Compares register A with the given register by performing A - register (without storing result).
     *
     * Updates flags: Z (zero), N (subtract), H (half-carry), C (carry).
     *
     * @param register The register to compare with A.
     */
    fun cp(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val finalValue = valueInGivenRegister - valueInRegisterA

        val zero = checkZero(finalValue)
        val halfCarry = checkHalfCarrySub(valueInRegisterA, valueInGivenRegister, 0)
        val carry = checkCarrySub(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = carry)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Compares register A with the value at the given memory address by performing (value - A) without storing the
     * result.
     *
     * Used for CP (A, HL) or CP (A, nn) depending on `useHL`.
     * Updates flags: Z (zero), N (subtract), H (half-carry), C (carry).
     *
     * @param memoryAddress The memory address to read the value from.
     * @param useHL If true, increments PC by 1 (HL); otherwise by 2 (immediate address).
     */
    fun cpSpecial(memoryAddress: Int, useHL: Boolean) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInAddress = bus.getValue(memoryAddress)
        val finalValue = valueInAddress - valueInRegisterA

        val zero = checkZero(finalValue)
        val halfCarry = checkHalfCarrySub(valueInRegisterA, valueInAddress, 0)
        val carry = checkCarrySub(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = carry)
        if (useHL) bus.executeFromCPU(BusConstants.INCR_PC, 1)
        else bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Increments the value of the specified register by 1.
     *
     * Updates flags: Z (zero), N (reset), H (half-carry). Carry flag is not affected.
     *
     * @param register The register to increment.
     */
    fun inc(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val finalValue = (valueInGivenRegister + 1) and KBConstants.FILTER_2_BYTES

        val halfCarry = checkHalfCarryAdd(valueInGivenRegister, 1, 0)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = null)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Increments the value at the specified memory address by 1.
     *
     * Updates flags: Z (zero), N (reset), H (half-carry). Carry flag is not affected.
     *
     * @param memoryAddress The memory address to increment.
     */
    fun incSpecial(memoryAddress: Int) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInAddress = bus.getValue(memoryAddress)
        val finalValue = (valueInAddress + 1) and KBConstants.FILTER_2_BYTES

        val halfCarry = checkHalfCarryAdd(valueInAddress, 1, 0)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = false, half = halfCarry, carry = null)
        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Decrements the value of the specified register by 1.
     *
     * Updates flags: Z (zero), N (reset), H (half-carry). Carry flag is not affected.
     *
     * @param register The register to decrement.
     */
    fun dec(register: RegisterNames) {
        val valueInGivenRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val finalValue = valueInGivenRegister - 1

        val halfCarry = checkHalfCarrySub(valueInGivenRegister, 1, 0)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = null)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Decrements the value at the specified memory address by 1.
     *
     * Updates flags: Z (zero), N (reset), H (half-carry). Carry flag is not affected.
     *
     * @param memoryAddress The memory address to decrement.
     */
    fun decSpecial(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val valueInAddress = bus.getValue(memoryAddress)
        val finalValue = valueInAddress - 1

        val halfCarry = checkHalfCarrySub(valueInAddress, 1, 0)
        val zero = checkZero(finalValue)

        flags.setFlags(zero = zero, subtract = true, half = halfCarry, carry = null)
        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Adds the value of the given 16-bit register pair to HL.
     *
     * Updates flags: N (reset), H (half-carry), C (carry). Zero flag is not affected.
     *
     * @param register The register pair to add to HL (e.g., BC, DE, HL, SP).
     */
    fun addHL(register: BusConstants) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInHL = bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int
        val givenRegisterPair = bus.getFromCPU(register, Bus.EMPTY_ARGUMENTS) as Int

        val halfCarry = ((valueInHL and KBConstants.FILTER_3_BYTES) +
                (givenRegisterPair and KBConstants.FILTER_3_BYTES) and 0x1000) == 0x1000
        val carry = (valueInHL and KBConstants.FILTER_4_BYTES) +
                (givenRegisterPair and KBConstants.FILTER_4_BYTES) > KBConstants.FILTER_4_BYTES
        val finalValue = ((valueInHL and KBConstants.FILTER_4_BYTES) +
                (givenRegisterPair and KBConstants.FILTER_4_BYTES)) and KBConstants.FILTER_4_BYTES

        flags.setFlags(zero = null, subtract = false, half = halfCarry, carry)
        bus.executeFromCPU(BusConstants.SET_HL, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Adds the Stack Pointer (SP) value to HL.
     *
     * Updates flags: N (reset), H (half-carry), C (carry). Zero flag is not affected.
     */
    fun addHLSP() {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val valueInHL = bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int
        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int

        val halfCarry = ((valueInHL and KBConstants.FILTER_3_BYTES) +
                (stackPointer and KBConstants.FILTER_3_BYTES) and 0x1000) == 0x1000
        val carry = (valueInHL and KBConstants.FILTER_4_BYTES) +
                (stackPointer and KBConstants.FILTER_4_BYTES) > KBConstants.FILTER_4_BYTES
        val finalValue = ((valueInHL and KBConstants.FILTER_4_BYTES) +
                (stackPointer and KBConstants.FILTER_4_BYTES)) and KBConstants.FILTER_4_BYTES

        flags.setFlags(zero = null, subtract = false, half = halfCarry, carry)
        bus.executeFromCPU(BusConstants.SET_HL, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Adds a signed 8-bit immediate value to the Stack Pointer (SP).
     *
     * Updates flags: Z (reset), N (reset), H (half-carry), C (carry).
     *
     * @param memoryAddress The memory address of the signed 8-bit immediate value to add.
     */
    fun addSP(memoryAddress: Int) {
        repeat(3) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val stackPointer = bus.getFromCPU(BusConstants.GET_SP, Bus.EMPTY_ARGUMENTS) as Int
        val valueInAddress = bus.getValue(memoryAddress)
        val valueSigned = if ((valueInAddress and KBConstants.HIGHEST_BIT) shr 7 == 1) valueInAddress
                else (valueInAddress and KBConstants.TWO_COMPLIMENT) - KBConstants.HIGHEST_BIT
        val finalValue = (stackPointer + valueSigned) and KBConstants.FILTER_4_BYTES

        val halfCarry = checkHalfCarryAdd(valueInAddress, stackPointer, 0)
        val carry = (((stackPointer and KBConstants.FILTER_2_BYTES) +
                (valueInAddress and KBConstants.FILTER_2_BYTES)) and 0x100) == 0x100

        flags.setFlags(zero = false, subtract = false, half = halfCarry, carry)
        bus.executeFromCPU(BusConstants.SET_SP, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }
    /**
     * Increments the value of the specified 16-bit register pair by 1.
     *
     * Does not affect any flags.
     *
     * @param registerIn The 16-bit register pair to increment (e.g., BC, DE, HL, SP).
     * @param registerOut The 16-bit register pair where to store the incremented result
     */
    fun incR(registerIn: BusConstants, registerOut: BusConstants) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val givenRegisterPair = bus.getFromCPU(registerIn, Bus.EMPTY_ARGUMENTS) as Int
        val finalValue = (givenRegisterPair + 1) and KBConstants.FILTER_4_BYTES

        bus.executeFromCPU(registerOut, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Decrements the value of the specified 16-bit register pair by 1.
     *
     * Does not affect any flags.
     *
     * @param registerIn The 16-bit register pair to decrement (e.g., BC, DE, HL, SP).
     * @param registerOut The 16-bit register pair where to store the incremented result
     */
    fun decR(registerIn: BusConstants, registerOut: BusConstants) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        val givenRegisterPair = bus.getFromCPU(registerIn, Bus.EMPTY_ARGUMENTS) as Int
        val finalValue = (givenRegisterPair - 1) and KBConstants.FILTER_4_BYTES

        bus.executeFromCPU(registerOut, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Increments the Stack Pointer (SP) by 1.
     *
     * Does not affect any flags.
     */
    fun incSP() {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.INCR_SP, 1)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Decrements the Stack Pointer (SP) by 1.
     *
     * Does not affect any flags.
     */
    fun decSP() {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.INCR_SP, -1)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Performs the Decimal Adjust Accumulator (DAA) operation on register A.
     *
     * Adjusts register A after addition or subtraction to form correct BCD representation.
     * Updates flags: Z (zero), H (reset), C (carry). N (subtract) flag remains unchanged.
     */
    fun daa() {
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        var offset = 0
        var carry = false

        if ((!flags.getSubtractFlag() && (valueInRegisterA and 0x0F) > 0x09) || flags.getHalfCarryFlag())
            offset = offset or 0x06
        if ((!flags.getSubtractFlag() && valueInRegisterA > 0x99) || flags.getCarryFlag()) {
            offset = offset or 0x60
            carry = true
        }

        val finalValue = if (flags.getSubtractFlag()) valueInRegisterA - offset else valueInRegisterA + offset
        val zero = checkZero(finalValue and KBConstants.FILTER_2_BYTES)

        flags.setFlags(zero = zero, subtract = null, half = false, carry)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Complements (bitwise NOT) the value in register A.
     *
     * Sets flags: N (subtract) and H (half-carry) to true. Z (zero) and C (carry) flags are unaffected.
     */
    fun cpl() {
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val finalValue = (valueInRegisterA.inv() and KBConstants.FILTER_2_BYTES)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf<Any>

(RegisterNames.A, finalValue))
        flags.setFlags(zero = null, subtract = true, half = true, carry = null)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }
}

package cpu.instructions

import cpu.Flags
import cpu.registers.RegisterNames
import memory.Bus
import memory.BusConstants
import memory.Word

/**
 * Class responsible for handling all rotate and shift operations
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber", "TooManyFunctions")
class RotateShift(
    private val bus: Bus
) {
    /**
     * Stores the flags object to be interacted with freely
     */
    private val flags: Flags = bus.getFromCPU(BusConstants.GET_FLAGS, Bus.EMPTY_ARGUMENTS) as Flags

    /**
     * Rotates A to the left and the 8th bit is used to set/reset the carry flag
     */
    fun rlca() {
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val carry = (valueInRegisterA and KBConstants.FILTER_2_BYTES) == KBConstants.FILTER_2_BYTES
        val finalValue = ((valueInRegisterA shl 1) and KBConstants.FILTER_2_BYTES) or
                ((valueInRegisterA and KBConstants.FILTER_2_BYTES) shr 7)

        flags.setFlags(zero = false, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates A to the left through the carry flag
     */
    fun rla() {
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val carry = ((valueInRegisterA and KBConstants.FILTER_2_BYTES) == KBConstants.FILTER_2_BYTES)
        val finalValue = ((valueInRegisterA shl 1) and KBConstants.FILTER_2_BYTES) or
                if(flags.getCarryFlag()) 1 else 0

        flags.setFlags(zero = false, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotate A to the right and the 0 bit is used to set or reset the carry flag
     */
    fun rrca() {
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val carry = (valueInRegisterA and 0x01) == 0x01
        val finalValue = ((valueInRegisterA shr 1) and KBConstants.FILTER_2_BYTES) or
                ((valueInRegisterA and 0x01) shl 7)

        flags.setFlags(zero = false, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates A to the right through the carry flag
     */
    fun rra() {
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        val carry = (valueInRegisterA and 0x01) == 0x01
        val finalValue = (((valueInRegisterA shr 1) and KBConstants.FILTER_2_BYTES)
                or ((if (flags.getCarryFlag()) 1 else 0) shl 7)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = false, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates word n to left, and old 7 bit is used to set or reset the carry flag
     *
     * @param register which register to rotate left
     */
    fun rlc(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()

        val carry = (valueInRegister and KBConstants.FILTER_2_BYTES) == KBConstants.FILTER_2_BYTES
        val finalValue = (((valueInRegister shl 1) and KBConstants.FILTER_2_BYTES)
                or ((valueInRegister and KBConstants.FILTER_2_BYTES) shr 7)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }


    /**
     * Rotates given word to the left and uses old 7 bit to set or reset the
     * carry flag
     *
     * @param memoryAddress HL value
     */
    fun rlcHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val carry = (givenValue and KBConstants.FILTER_2_BYTES) == KBConstants.FILTER_2_BYTES
        val finalValue = (((givenValue shl 1) and KBConstants.FILTER_2_BYTES)
                or ((givenValue and KBConstants.FILTER_2_BYTES) shr 7)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotate the given register left through the carry left
     *
     * @param register which register to rotate left
     */
    fun rl(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val carry = (valueInRegister and KBConstants.FILTER_2_BYTES) == KBConstants.FILTER_2_BYTES
        val finalValue = (((valueInRegister shl 1) and KBConstants.FILTER_2_BYTES)
                or ((if (flags.getCarryFlag()) 1 else 0))) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates given word to the left through the carry flag
     *
     * @param memoryAddress HL value
     */
    fun rlHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val carry = (givenValue and KBConstants.FILTER_2_BYTES) == KBConstants.FILTER_2_BYTES
        val finalValue = (((givenValue shl 1) and KBConstants.FILTER_2_BYTES)
                or (if (flags.getCarryFlag()) 1 else 0)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates word n to right, and old 0 bit is used to set or reset the carry
     * flag
     *
     * @param register which register to rotate right
     */
    fun rrc(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val carry = (valueInRegister and 0x01) == 0x01
        val finalValue = (((valueInRegister shr 1) and 0xff) or ((valueInRegister and 0x01) shl 7)) and 0xff

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates given word to the right and uses old 7 bit to set or reset the
     * carry flag
     *
     * @param memoryAddress HL value
     */
    fun rrcHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val carry = (givenValue and 0x01) == 0x01
        val finalValue = (((givenValue shr 1) and KBConstants.FILTER_2_BYTES)
                or ((givenValue and 0x01) shl 7)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates word n to right through the carry flag
     *
     * @param register which register to rotate right
     */
    fun rr(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val carry = (valueInRegister and 0x01) == 0x01
        val finalValue = (((valueInRegister shr 1) and KBConstants.FILTER_2_BYTES)
                or ((if (flags.getCarryFlag()) 1 else 0) shl 7)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Rotates given word to the right through the carry flag
     *
     * @param memoryAddress HL value
     */
    fun rrHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val carry = (givenValue and 0x01) == 0x01
        val finalValue = (((givenValue shr 1) and KBConstants.FILTER_2_BYTES)
                or ((if (flags.getCarryFlag()) 1 else 0) shl 7)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Shifts the register's value left into the carry.
     *
     * @param register which register to shift left
     */
    fun sla(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val carry = (valueInRegister and KBConstants.HIGHEST_BIT) == KBConstants.HIGHEST_BIT
        val finalValue = (valueInRegister shl 1) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Shifts word n to left through the carry flag
     *
     * @param memoryAddress HL value
     */
    fun slaHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val carry = (givenValue and KBConstants.HIGHEST_BIT) == KBConstants.HIGHEST_BIT
        val finalValue = (givenValue shl 1) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Swaps the lower bits with the higher bits of a given register
     *
     * @param register which register to swap
     */
    fun swap(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val finalValue = (((valueInRegister and 0xF0) shr 4) or
                ((valueInRegister and 0x0F) shl 4)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = false)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Swaps the lower bits with the higher bits of memory stored at the address
     * given by the HL value
     *
     * @param memoryAddress HL value
     */
    fun swapHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val finalValue = (((givenValue and 0x0F) shl 4) or
                ((givenValue and 0xF0) shr 4)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = false)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Shifts the value contained inside the given register to the right into
     * the carry. Uses the carry value
     *
     * @param register which register to shift
     */
    fun sra(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val carry = (valueInRegister and 0x01) != 0
        val finalValue = ((valueInRegister shr 1) or
                (valueInRegister and KBConstants.FILTER_2_BYTES))and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Shifts right the value of the memory contained inside the address given
     * by the 16bit register HL. Uses the carry value
     *
     * @param memoryAddress HL value
     */
    fun sraHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val carry = (givenValue and 0x01) != 0
        val finalValue = ((givenValue shr 1) or
                (givenValue and KBConstants.FILTER_2_BYTES)) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Shifts the value contained inside the given register to the right into
     * the carry. Doesn't use the carry value
     *
     * @param register which register to shift
     */
    fun srl(register: RegisterNames) {
        val valueInRegister = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val carry = (valueInRegister and 0x01) == 0x01
        val finalValue = (valueInRegister shr 1) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, finalValue))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Shifts right a word contained in the memory address of 16bit register HL into
     * the carry. Doesn't use the carry value
     *
     * @param memoryAddress HL value
     */
    fun srlHL(memoryAddress: Int) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val givenValue = bus.getValue(memoryAddress)
        val carry = (givenValue and 0x01) == 0x01
        val finalValue = (givenValue shr 1) and KBConstants.FILTER_2_BYTES

        flags.setFlags(zero = finalValue == 0x00, subtract = false, half = false, carry = carry)

        bus.setValue(memoryAddress, finalValue)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }
}

package cpu.instructions

import KBConstants
import cpu.registers.RegisterNames
import memory.Bus
import memory.BusConstants
import memory.Word

/**
 * Class responsible for handling all things that deal with 8 bit load operations in CPU instruction set
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber", "TooManyFunctions")
class Load8Bit(
    private val bus: Bus
) {
    /**
     * Returns one of the 4 types of combined register in the GB CPU, AF, BC, DE and HL
     *
     * @param registerPair which register pair to return
     * @return value of the 16-bit combined register pair
     */
    private fun decodeRegister(registerPair: BusConstants): Int {
        return when (registerPair) {
            BusConstants.GET_AF, BusConstants.GET_BC, BusConstants.GET_DE, BusConstants.GET_HL ->
                bus.getFromCPU(registerPair, Bus.EMPTY_ARGUMENTS) as Int
            else -> { error("Unexpected value received!") }
        }
    }

    /**
     * This operation assigns the value stored in register A to the memory at the location of a 16-bit Register
     * (combination of register)
     *
     * @param registerPair which register pair to use
     */
    fun ldTwoRegisters(registerPair: BusConstants) {
        val memoryAddress = decodeRegister(registerPair)
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.setValue(memoryAddress, valueInRegisterA)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * This operation assigns the value stored in register A to the memory at the location given by the word next to the
     * program counter in memory
     */
    fun ldNN() {
        val memoryAddress = bus.calculateNN()
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.setValue(memoryAddress, valueInRegisterA)
        bus.executeFromCPU(BusConstants.INCR_PC, 3)
    }

    /**
     * Puts the value n (retrieved from the memory address of a 16-bit register value) into A
     *
     * @param registerPair which register pair to use
     */
    fun ldTwoRegistersIntoA(registerPair: BusConstants) {
        val memoryAddress = decodeRegister(registerPair)
        val valueAtAddress = bus.getValue(memoryAddress)

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, valueAtAddress))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Puts the value nn (retrieved by getting the two values next to the program counter and retrieving the value at
     * the given address) into A
     */
    fun ldNNIntoA() {
        val memoryAddress = bus.calculateNN()
        val valueAtAddress = bus.getValue(memoryAddress)

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, valueAtAddress))
        bus.executeFromCPU(BusConstants.INCR_PC, 3)
    }

    /**
     * Stores the value given by the immediate word next to the program counter into the given register
     *
     * @param register where to store immediate word
     */
    fun ldNRegister(register: RegisterNames) {
        val programCounter = bus.getFromCPU(BusConstants.GET_PC, BusConstants.GET_PC) as Int
        val immediateWord = bus.getValue(programCounter + 1)

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, immediateWord))
        bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Stores the value given by the immediate word next to the program counter into the memory address given by the HL
     * register aggregation
     */
    fun ldNHL() {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val programCounter = bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int
        val valueInHL = decodeRegister(BusConstants.GET_HL)
        val value = bus.getValue(programCounter + 1)

        bus.setValue(valueInHL, value)
        bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }

    /**
     * Loads the value in one register into another
     *
     * @param registerIn to receive a new value
     * @param registerOut input register
     */
    fun ld(registerIn: RegisterNames, registerOut: RegisterNames) {
        val value = (bus.getFromCPU(BusConstants.GET_REGISTER, registerOut) as Word).getValue()

        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(registerIn, value))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Assigns a value contained in the memory address given by the HL register
     * to a given register
     *
     * @param register to receive new value
     */
    fun ldHLtoRegister(register: RegisterNames) {
        val valueInHL = decodeRegister(BusConstants.GET_HL)

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(register, valueInHL))
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Assigns a value contained in a register to the memory address given by
     * the HL address
     *
     * @param register to be used as input value
     */
    fun ldRtoHL(register: RegisterNames) {
        val value = (bus.getFromCPU(BusConstants.GET_REGISTER, register) as Word).getValue()
        val valueInHL = decodeRegister(BusConstants.GET_HL)

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.setValue(valueInHL, value)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Assigns a value to A to given word (starting from address 0xFF00 added the value of register C) or vice versa
     *
     * @param aIntoC defines whether the value should be assigned to the A register false or otherwise true
     */
    fun ldAC(aIntoC: Boolean) {
        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val valueInRegisterC = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.C) as Word).getValue()
        val memoryAddress = KBConstants.FILTER_TOP_BITS + valueInRegisterC

        if (aIntoC)
            bus.setValue(memoryAddress, valueInRegisterA)
        else
            bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, bus.getValue(memoryAddress)))

        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)
        bus.executeFromCPU(BusConstants.INCR_PC, 1)
    }

    /**
     * Loads the value from A into the address given by HL or vice versa as well as lowering HL value by one
     *
     * @param aIntoC sets whether register A should be used as input or has the receiver
     */
    fun ldd(aIntoC: Boolean) {
        val valueInHL = decodeRegister(BusConstants.GET_HL)

        if (aIntoC) ldTwoRegisters(BusConstants.GET_HL)
        else ldTwoRegistersIntoA(BusConstants.GET_HL)

        val finalValueInHL = (valueInHL - 1) and KBConstants.FILTER_4_BYTES
        bus.executeFromCPU(BusConstants.SET_HL, finalValueInHL)
    }

    /**
     * Loads the value from A into the address given by HL or vice versa as well as incrementing HL value by one
     *
     * @param aIntoC sets whether register A should be used as input true or as the receiver false
     */
    fun ldi(aIntoC: Boolean) {
        val valueInHL = decodeRegister(BusConstants.GET_HL)

        if (aIntoC) ldTwoRegisters(BusConstants.GET_HL)
        else ldTwoRegistersIntoA(BusConstants.GET_HL)

        val finalValueInHL = (valueInHL + 1) and KBConstants.FILTER_4_BYTES
        bus.executeFromCPU(BusConstants.SET_HL, finalValueInHL)
    }

    /**
     * Puts register A's value into memory address 0xFF00 plus the immediate word after the program counter or vice
     * versa
     *
     * @param isInput sets whether register A should be used as input true or as the receiver false
     */
    fun ldh(isInput: Boolean) {
        repeat(2) { bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS) }

        val valueInRegisterA = (bus.getFromCPU(BusConstants.GET_REGISTER, RegisterNames.A) as Word).getValue()
        val programCounter = bus.getFromCPU(BusConstants.GET_PC, BusConstants.GET_PC) as Int
        val valueN = bus.getValue(programCounter + 1)
        val memoryAddress = KBConstants.FILTER_TOP_BITS + valueN

        if (isInput)
            bus.setValue(memoryAddress, valueInRegisterA)
        else
            bus.executeFromCPU(BusConstants.SET_REGISTER, arrayOf(RegisterNames.A, bus.getValue(memoryAddress)))

        bus.executeFromCPU(BusConstants.INCR_PC, 2)
    }
}

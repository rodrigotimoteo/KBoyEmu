package memory

import KBConstants
import cpu.CPU
import cpu.registers.RegisterNames
import java.util.logging.Level
import java.util.logging.Logger

/**
 * This class is the main vehicle for inter component comunication. It is where PPU/Memory/CPU interact
 *
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber")
class Bus(
    private val isCGB: Boolean,
    private val rom: MemoryModule
) {
    private val memoryManager: MemoryManager = MemoryManager(this, rom)

    private val cpu: CPU = CPU(this)

    //TODO LACKS PPU

    //TODO LACKS DISPLAY

    //TODO LACKS CONTROLLER

    fun isCGB(): Boolean {
        return isCGB
    }

    /**
     * Changes value of specific word based on its memory address
     *
     * @param memoryAddress where to change the value
     * @param value to assign
     */
    fun setValue(memoryAddress: Int, value: Int) {
        memoryManager.setValue(memoryAddress, value)
    }

    /**
     * Gets the value of specific word based on its address
     *
     * @param memoryAddress where to get the value
     * @return value stored in specific address
     */
    fun getValue(memoryAddress: Int): Int {
        return memoryManager.getValue(memoryAddress)
    }

    /**
     * Gets the word contained inside the given memory address (this is nullable because some parts of memory should not
     * be access and therefore return null)
     *
     * @param memoryAddress where to get the value
     * @return object contained in specific address
     */
    fun getWord(memoryAddress: Int): Word? {
        return memoryManager.getWord(memoryAddress)
    }

    /**
     * Stores the program counter in the stack pointer and decreases its pointer by 2
     */
    fun storeProgramCounterInStackPointer() {
        val stackPointer = cpu.registers.getStackPointer()
        val programCounter = cpu.registers.getProgramCounter()

        setValue(stackPointer - 1, (programCounter and KBConstants.FILTER_TOP_BITS) shr 8)
        setValue(stackPointer - 2, programCounter and KBConstants.FILTER_LOWER_BITS)

        cpu.registers.incrementStackPointer(-2)
    }

    /**
     * Executes an action on the CPU of the Game Boy based on predifined options
     *
     * @param action which action to perform
     * @param parameters to use
     */
    @Suppress("CyclomaticComplexMethod")
    fun executeFromCPU(action: BusConstants, parameters: Any) {
        when (action) {
            BusConstants.TICK_TIMERS -> cpu.timers.tick()
            BusConstants.SET_REGISTER -> (parameters as Array<*>).let { 
                cpu.registers.setRegister(it[0] as RegisterNames, it[1] as Int)
            }
            BusConstants.INCR_PC -> cpu.registers.incrementProgramCounter(parameters as Int)
            BusConstants.SET_PC -> cpu.registers.setProgramCounter(parameters as Int)
            BusConstants.INCR_SP -> cpu.registers.incrementStackPointer(parameters as Int)
            BusConstants.SET_SP -> cpu.registers.setStackPointer(parameters as Int)
            BusConstants.SET_AF -> cpu.registers.setAF(parameters as Int)
            BusConstants.SET_BC -> cpu.registers.setBC(parameters as Int)
            BusConstants.SET_DE -> cpu.registers.setDE(parameters as Int)
            BusConstants.SET_HL -> cpu.registers.setHL(parameters as Int)
            BusConstants.DISABLE_INT -> cpu.interrupts.setInterruptChange(false)
            BusConstants.ENABLE_INT -> cpu.interrupts.setInterruptChange(true)
            BusConstants.REQUEST_INT -> cpu.interrupts.requestInterrupt(parameters as Int)
            BusConstants.HALT -> cpu.setHalted(true)
            BusConstants.UNHALT -> cpu.setHalted(false)
            BusConstants.STOP -> cpu.setStopped(true)
            else -> Logger.getGlobal().log(Level.SEVERE, "Executing invalid action! Needs fix!")
        }
    }

    /**
     * Gets values from the CPU from any part of the architecure
     *
     * @param action which value to get
     * @param parameters to use
     */
    fun getFromCPU(action: BusConstants, parameters: Any): Any {
        return when (action) {
            BusConstants.GET_FLAGS -> cpu.registers.flags
            BusConstants.GET_REGISTER -> cpu.registers.getRegister(parameters as RegisterNames)
            BusConstants.GET_AF -> cpu.registers.getAF()
            BusConstants.GET_BC -> cpu.registers.getBC()
            BusConstants.GET_DE -> cpu.registers.getDE()
            BusConstants.GET_HL -> cpu.registers.getHL()
            BusConstants.GET_PC -> cpu.registers.getProgramCounter()
            BusConstants.GET_SP -> cpu.registers.getStackPointer()
            BusConstants.GET_HALTED -> cpu.isHalted()
            BusConstants.GET_STOPPED -> cpu.isStopped()
            BusConstants.GET_MC -> cpu.timers.getMachineCycles()
            BusConstants.GET_HALT_MC -> cpu.timers.getHaltCycleCounter()
            else -> error("Unexpected value! $action")
        }
    }

    /**
     * Calculates a new memory address to fetch memory for specific instructions uses a 16 bit word produced by the sum
     * of lower nibble of PC + 1 and higher nibble of PC + 2
     *
     * @return calculated address
     */
    fun calculateNN(): Int {
        repeat(2) { executeFromCPU(BusConstants.TICK_TIMERS, EMPTY_ARGUMENTS) }

        val programCounter = getFromCPU(BusConstants.GET_PC, EMPTY_ARGUMENTS) as Int

        val lowerAddress = getValue(programCounter + 1)
        val upperAddress = getValue(programCounter + 2) shl 8

        return lowerAddress + upperAddress
    }

    companion object {
        /**
         * Used for actions that do not require any argument instead of passing null
         */
        val EMPTY_ARGUMENTS = emptyArray<String>()
    }
}


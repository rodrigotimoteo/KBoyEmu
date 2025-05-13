package memory

import cpu.CPU
import java.util.StringJoiner

/**
 * This class is the main vehicle for inter component comunication. It is where PPU/Memory/CPU interact
 *
 * @author rodrigotimoteo
 **/
class Bus(
    private val isCGB: Boolean,
    private val rom: MemoryModule
) {
    private val memoryManager: MemoryManager = MemoryManager(this, rom)

    private lateinit var cpu: CPU

    //TODO LACKS PPU

    //TODO LACKS DISPLAY

    //TODO LACKS CONTROLLER

    public fun setCPU(cpu: CPU) {
        this.cpu = cpu
    }

    fun isCGB(): Boolean {
        return isCGB
    }

    /**
     * Changes value of specific word based on its memory address
     *
     * @param memoryAddress where to change the value
     * @param value to assign
     */
    public fun setValue(memoryAddress: Int, value: Int) {
        memoryManager.setValue(memoryAddress, value)
    }

    /**
     * Gets the value of specific word based on its address
     *
     * @param memoryAddress where to get the value
     * @return value stored in specific address
     */
    public fun getValue(memoryAddress: Int): Int {
        return memoryManager.getValue(memoryAddress)
    }

    /**
     * Gets the word contained inside the given memory address (this is nullable because some parts of memory should not
     * be access and therefore return null)
     *
     * @param memoryAddress where to get the value
     * @return object contained in specific address
     */
    public fun getWord(memoryAddress: Int): Word? {
        return memoryManager.getWord(memoryAddress)
    }

    public fun storeProgramCounterInStackPointer() {
//        val stackPointer = cpu.getRegisters()
    }

    public fun executeFromCPU(action: BusConstants, parameters: Any) {

    }

    public fun getFromCPU(action: BusConstants, parameters: Any): Any {
        return Any()
    }

    companion object {
        public val EMPTY_ARGUMENTS = emptyArray<String>()
    }
}


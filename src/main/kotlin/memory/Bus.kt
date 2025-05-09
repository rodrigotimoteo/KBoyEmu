package memory

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
        memoryManager.setValue(memoryAddress, value);
    }

    /**
     * Gets the value of specific word based on its address
     *
     * @param memoryAddress where to change the value
     * @return value stored in specific address
     */
    public fun getValue(memoryAddress: Int): Int {
        return memoryManager.getValue(memoryAddress);
    }

}

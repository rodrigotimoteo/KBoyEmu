package memory

/**
 * @author rodrigotimoteo
 **/
class Bus(
    private val isCGB: Boolean,
    private val rom: MemoryModule
) {
    private val memoryManager: MemoryManager = MemoryManager(this, rom)

    fun isCGB(): Boolean {
        return false
    }

}

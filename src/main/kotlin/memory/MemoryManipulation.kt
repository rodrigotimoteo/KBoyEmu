package memory

/**
 * Base functions implemented by classes that interact with Memory
 *
 * @author rodrigotimoteo
 **/
interface MemoryManipulation {

    /**
     * Assigns a memory address a new value
     *
     * @param memoryAddress memory location where to set value
     * @param value value to put inside address
     */
    fun setValue(memoryAddress: Int, value: Int)

    /**
     * Gets the value inside a memory address
     *
     * @param memoryAddress memory location to fetch content from
     * @return content inside given memory location
     */
    fun getValue(memoryAddress: Int): Int

    /**
     * Get the object contained inside a memory address
     * The nullability of the return type is because some sections of memory should be unreachable by the CPU therefore
     * this was the way I found to actually represent this
     *
     * @param memoryAddress memory location to fetch Word from
     * @return - Word object contained inside Memory Module (if accessible)
     */
    fun getWord(memoryAddress: Int): Word?
}

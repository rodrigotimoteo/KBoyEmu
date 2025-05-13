package memory

/**
 * This interface represents the memory operations that are possible to do in memory modules
 *
 * @author rodrigotimoteo
 **/
interface MemoryOperations {

    /**
     * This method assignes a new value to the given memory address
     */
    fun setValue(memoryAddress: Int, value: Int)

    /**
     * This method returns the value holded by the given memory address
     */
    fun getValue(memoryAddress: Int): Int

    /**
     * This method returns the actual memory object that is on the given address
     */
    fun getWord(memoryAddress: Int): Word
}

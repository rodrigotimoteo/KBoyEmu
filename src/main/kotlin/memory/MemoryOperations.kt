package memory

/**
 * This interface represents the memory operations that are possible to do in memory modules
 *
 * @author rodrigotimoteo
 **/
interface MemoryOperations {

    /**
     * This method assigns a new value to the given memory address
     *
     * @param memoryAddress where to set the value
     * @param value to assign to the address' [Word]
     */
    fun setValue(memoryAddress: Int, value: Int)

    /**
     * This method returns the value held by the given memory address
     *
     * @param memoryAddress where to get the value from
     * @return [Int] contained inside given address' [Word]
     */
    fun getValue(memoryAddress: Int): Int

    /**
     * This method returns the actual memory object that is on the given address
     *
     * @param memoryAddress where to get the word from
     * @return [Word] contained inside given address
     */
    fun getWord(memoryAddress: Int): Word
}

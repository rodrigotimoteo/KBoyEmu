package memory

/**
 * Represents a memory module, the game boy is divided into several modules, therefore this class
 * absorves the main logic those modules
 * @author rodrigotimoteo
 **/
open class MemoryModule(
    private val numberOfBanks: Int = 1,
    private val memoryOffset: Int,
    private val simultaneousBanks: Int,
    private val activeBank: Int = 0,
    private val size: Int,
) : MemoryOperations {
    private val memory: Array<Array<Word>> = Array(numberOfBanks) { Array(size) { Word() } }

    constructor(size: Int, memoryOffset: Int) : this(
        numberOfBanks = 1,
        activeBank = 0,
        simultaneousBanks = 1,
        memoryOffset = memoryOffset,
        size = size
    )

    constructor(size: Int, simultaneousBanks: Int, memoryOffset: Int, numberOfBanks: Int) : this(
        numberOfBanks = numberOfBanks,
        activeBank = if (simultaneousBanks == 2) 1 else 0,
        simultaneousBanks = simultaneousBanks,
        memoryOffset = memoryOffset,
        size = size
    )

    constructor(content: ByteArray, size: Int, simultaneousBanks: Int, memoryOffset: Int, numberOfBanks: Int) : this(
        numberOfBanks = numberOfBanks,
        activeBank = if (simultaneousBanks == 2) 1 else 0,
        simultaneousBanks = simultaneousBanks,
        memoryOffset = memoryOffset,
        size = size
    ) {
        initializeMemory(content)
    }

    private fun initializeMemory(content: ByteArray) {
        for ((i, byte) in content.withIndex()) {
            memory[i / size][i % size].content = byte
        }
    }

    override fun setValue(memoryAddress: Int, value: Int) {
        val realIndex = memoryAddress - memoryOffset

        if (numberOfBanks == 1) {
            memory[activeBank][realIndex].setValue(value)
        } else {
            if (simultaneousBanks == 1) {
                memory[activeBank][realIndex].setValue(value)
            } else {
                val moduleSize = memory[activeBank].size

                if (realIndex < moduleSize) {
                    memory[0][realIndex].setValue(value)
                } else {
                    memory[activeBank][realIndex - moduleSize].setValue(value)
                }
            }
        }
    }

    override fun getValue(memoryAddress: Int): Int {
        val realIndex = memoryAddress - memoryOffset

        return if(numberOfBanks == 1) {
            memory[activeBank][realIndex].getValue()
        } else {
            if(simultaneousBanks == 1)
                memory[activeBank][realIndex].getValue()
            else {
                val moduleSize = memory[activeBank].size

                if(realIndex >= moduleSize) {
                    memory[0][realIndex].getValue()
                } else {
                    memory[activeBank][realIndex - moduleSize].getValue()
                }
            }
        }
    }

    override fun getWord(memoryAddress: Int): Word {
        val realIndex = memoryAddress - memoryOffset

        return if(numberOfBanks == 1) {
            memory[activeBank][realIndex]
        } else {
            if(simultaneousBanks == 1)
                memory[activeBank][realIndex]
            else {
                val moduleSize = memory[activeBank].size

                if(realIndex >= moduleSize) {
                    memory[0][realIndex]
                } else {
                    memory[activeBank][realIndex - moduleSize]
                }
            }
        }
    }

    /**
     * Override the toString method to better reflect the way the information for this class should
     * be read, therefore enabling the prints of this class as a readable output, that enabled easier
     * debugging
     */
    @Suppress("MagicNumber")
    override fun toString(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("0 ")
        for (i in 0 until numberOfBanks) {
            for (j in memory[i].indices) if (i % 16 == 0 && i != 0) {
                stringBuilder.append(" \n")
                stringBuilder.append(Integer.toHexString(i)).append(" ")
                stringBuilder.append(Integer.toHexString(memory[i][j].getValue())).append(" ")
            } else {
                stringBuilder.append(Integer.toHexString(memory[i][j].getValue())).append(" ")
            }
        }

        return stringBuilder.toString()
    }

}

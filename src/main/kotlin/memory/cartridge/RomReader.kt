package memory.cartridge

import memory.MemoryModule
import memory.ReservedAddresses
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.experimental.and

/**
 * Class responsible for reading a file and parsing its rom, creating a MBC for the given rom
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber")
class RomReader {

    /**
     * Holds the number of Rom banks for a given Rom size (coded internally by Game Boy)
     */
    private val romBanksMap: HashMap<Int, Int> = hashMapOf(
        Pair(0, 2),
        Pair(1, 4),
        Pair(2, 8),
        Pair(3, 16),
        Pair(4, 32),
        Pair(5, 64),
        Pair(6, 128),
        Pair(7, 256),
        Pair(8, 512)
    )

    /**
     * Holds the number of Ram banks for a given Ram size (coded internally by Game Boy)
     */
    private val ramBanksMap: HashMap<Int, Int> = hashMapOf(
        Pair(0, 0),
        Pair(1, 1),
        Pair(2, 1),
        Pair(3, 4),
        Pair(4, 16),
        Pair(5, 8)
    )

    /**
     * Holds the content of the rom
     */
    private var romContent: ByteArray = byteArrayOf()

    /**
     * Loads a Rom into rom content
     */
    fun loadRom(romFile: File) {
        try {
            romContent = Files.readAllBytes(romFile.toPath()).toTypedArray().toByteArray()
        } catch (exception: IOException) {
            val newException = IOException("Failed to load file")
            newException.stackTrace = exception.stackTrace

            throw newException
        }
    }

    /**
     * Checks if the Rom is CGB or DMG Rom
     */
    fun isCgb(): Boolean {
        return (romContent[ReservedAddresses.CONSOLE_TYPE.memoryAddress].and(0xFF.toByte()).toInt() == 0x80)
    }

    /**
     * Build and return a new MemoryModule for the given rom
     */
    fun getRomModule(): MemoryModule = when(romContent[ReservedAddresses.CARTRIDGE_TYPE.memoryAddress].toInt()) {
        0x00, 0x08, 0x09 -> MBC0(getRomSize(), getRamSize(), romContent)
        else -> MBC0(getRomSize(), getRamSize(), romContent)
    }

    /**
     * Responsible for translating the hashmap into the number of rom banks the rom needs
     */
    private fun getRomSize(): Int {
        val romSize = romContent[ReservedAddresses.ROM_SIZE.memoryAddress].toInt()

        return romBanksMap[romSize] ?: 0
    }

    /**
     * Responsible for translating the hashmap into the number of ram banks the rom needs
     */
    private fun getRamSize(): Int {
        val ramSize = romContent[ReservedAddresses.RAM_SIZE.memoryAddress].toInt()

        return ramBanksMap[ramSize] ?: 0
    }
}

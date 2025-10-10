package memory.cartridge

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.IOException

/**
 * @author rodrigotimoteo
 **/
class RomReaderTest {

    val file = File("cenasatoa/01-read_timing.gb")
    val victim = RomReader()

    @Test
    fun `loading an invalid file throws an IOException with a predefined message`() {
        val exceptionSetBit = assertThrows<IOException> {
            victim.loadRom(File(""))
        }
        assertEquals(RomReader.LOAD_ROM_IO_EXCEPTION, exceptionSetBit.message)
    }

    @Test
    fun `loading a rom with a valid file does not throw any exception`() {
        victim.loadRom(file)
    }

    @Test
    fun `loading a rom with a valid file (01-read_timing) has the Color Game Boy flag as false`() {
        victim.loadRom(file)

        assertTrue(victim.isCgb())
    }

    @Test
    fun `loading a rom with a valid file (01-read_timing) gives a valid MemoryModule`() {
        victim.loadRom(file)

        victim.getRomModule()
    }
}

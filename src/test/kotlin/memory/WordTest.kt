package memory

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author rodrigotimoteo
 **/
class WordTest {

    @Test
    fun `creating empty word object the default value is 0x00`() {
        val victim = Word()

        assertEquals(victim.getValue(), 0x00)
    }

    @Test
    fun `when creating word object with given value then value is obtained by getValue()`() {
        val victim = Word(0xFF.toUByte())

        assertEquals(victim.getValue(), 0xFF)
    }

    @Test
    fun `creating word object and setting its value returns the value used (0x7F)`() {
        val victim = Word()
        victim.setValue(0x7F)

        assertEquals(victim.getValue(), 0x7F)
    }

    @Test
    fun `creating word object and setting its value returns the value used (0xFF)`() {
        val victim = Word()
        victim.setValue(0xFF)

        assertEquals(victim.getValue(), 0xFF)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7])
    fun `creating word object and setting each bit makes value go from 0 to 1`(bit: Int) {
        val victim = Word()
        victim.setBit(bit)

        assertEquals(victim.getValue(), 0x01 shl bit)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7])
    fun `creating word object and resetting each bit makes value go from 1 to 0`(bit: Int) {
        val victim = Word(0xFF.toUByte())
        victim.resetBit(bit)

        assertEquals(victim.getValue(), 0xFF - (1 shl bit))
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7])
    fun `creating word object (0x00) and testing its bits gives all false`(bit: Int) {
        val victim = Word()
        victim.testBit(bit)

        assertFalse(victim.testBit(bit))
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7])
    fun `creating word object (0xFF) and testing its bits gives all true`(bit: Int) {
        val victim = Word(0xFF.toUByte())
        victim.testBit(bit)

        assertTrue(victim.testBit(bit))
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 9])
    fun `require should throw on invalid bit`(bit: Int) {
        val victim = Word()
        val exceptionSetBit = assertThrows<IllegalArgumentException> {
            victim.setBit(bit)
        }
        assertEquals(Word.BIT_ASSERTION_ERROR_MESSAGE, exceptionSetBit.message)

        val exceptionResetBit = assertThrows<IllegalArgumentException> {
            victim.resetBit(bit)
        }
        assertEquals(Word.BIT_ASSERTION_ERROR_MESSAGE, exceptionResetBit.message)

        val exceptionTestBit = assertThrows<IllegalArgumentException> {
            victim.testBit(bit)
        }
        assertEquals(Word.BIT_ASSERTION_ERROR_MESSAGE, exceptionTestBit.message)
    }
}

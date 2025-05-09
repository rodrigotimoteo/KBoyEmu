package cpu

import KBConstants.AF_INITIAL_VALUE
import KBConstants.BC_INITIAL_VALUE
import KBConstants.DE_INITIAL_VALUE
import KBConstants.EIGHT_BITS
import KBConstants.FILTER_LOWER_BITS
import KBConstants.FILTER_TOP_BITS
import KBConstants.HL_INITIAL_VALUE
import KBConstants.PROGRAM_COUNTER_INITIAL_VALUE
import KBConstants.STACK_POINTER_INITIAL_VALUE
import memory.Bus
import memory.Word
import java.util.*

/**
 * This abstraction holds the registers inside the SHARP CPU as well as the ability to return the Flags from Register F
 *
 * @author rodrigotimoteo
 **/
@Suppress("TooManyFunctions")
class Registers(
    private val bus: Bus
) {
    /**
     * Stores all the registers with their associated name (register names kept as enum due to their limited nature)
     */
    private val registersMap: EnumMap<RegisterNames, Word> = EnumMap(cpu.RegisterNames::class.java)

    /**
     * Stores the CPU flags hold by register F
     */
    private val flags: Flags = Flags(getRegister(RegisterNames.F))

    /**
     * Stores the PC (default value at the end of boot rom is 0x0100)
     */
    private var programCounter: Int = PROGRAM_COUNTER_INITIAL_VALUE

    /**
     * Stores the SP (default value at the end of boot rom is 0xFFFE)
     */
    private var stackPointer: Int = STACK_POINTER_INITIAL_VALUE

    /**
     * Inits the register to their default values
     */
    init {
        setAF(AF_INITIAL_VALUE)
        setBC(BC_INITIAL_VALUE)
        setDE(DE_INITIAL_VALUE)
        setHL(HL_INITIAL_VALUE)
    }

    /**
     * Getter for the flags object
     *
     * @return cpu flags
     */
    public fun getFlags(): Flags = flags

    /**
     * Getter for the PC
     *
     * @return PC value
     */
    public fun getProgramCounter(): Int = programCounter

    /**
     * Increments the program counter by the given value
     *
     * @param value to increase program counter
     */
    public fun incrementProgramCounter(value: Int) {
        programCounter += value
    }

    /**
     * Sets the value of program counter to given one
     *
     * @param value to assign program counter
     */
    public fun setProgramCounter(value: Int) {
        programCounter = value
    }

    /**
     * Getter for the SP
     *
     * @return SP value
     */
    public fun getStackPointer(): Int {
        return stackPointer
    }

    /**
     * Increments the stack pointer by the given value
     *
     * @param value to increase stack pointer
     */
    public fun incrementStackPointer(value: Int) {
        stackPointer += value
    }

    /**
     * Sets the value of stack pointer to given one
     *
     * @param value to assign stack pointer
     */
    public fun setStackPointer(value: Int) {
        stackPointer = value
    }

    /**
     * Register getter based on given name (uses !! due to there not existing a possibility where a register is null)
     *
     * @param register name
     * @return content of given register
     */
    public fun getRegister(register: RegisterNames): Word = registersMap[register]!!

    /**
     * Sets the register content to the given value
     *
     * @param register name
     * @param value to assign to the given register
     */
    public fun setRegister(register: RegisterNames, value: Int) = getRegister(register).setValue(value)

    /**
     * Returns the result of the aggregation of register A (as the left value) and register F (as the right value)
     * creating a 16bit Word
     *
     * @return register A and F together
     */
    public fun getAF(): Int = (getRegister(RegisterNames.A).getValue() shl EIGHT_BITS) +
            getRegister(RegisterNames.F).getValue()

    /**
     * Returns the result of the aggregation of register B (as the left value) and register C (as the right value)
     * creating a 16bit Word
     *
     * @return register B and C together
     */
    public fun getBC(): Int = (getRegister(RegisterNames.B).getValue() shl EIGHT_BITS) +
            getRegister(RegisterNames.C).getValue()

    /**
     * Returns the result of the aggregation of register D (as the left value) and register E (as the right value)
     * creating a 16bit Word
     *
     * @return register D and E together
     */
    public fun getDE(): Int = (getRegister(RegisterNames.D).getValue() shl EIGHT_BITS) +
            getRegister(RegisterNames.E).getValue()

    /**
     * Returns the result of the aggregation of register H (as the left value) and register L (as the right value)
     * creating a 16bit Word
     *
     * @return register H and L together
     */
    public fun getHL(): Int = (getRegister(RegisterNames.H).getValue() shl EIGHT_BITS) +
            getRegister(RegisterNames.L).getValue()

    /**
     * Sets the value of register A (with high 8 bits of value) and F (with low 8 bits of value)
     *
     * @param value to assign
     */

    public fun setAF(value: Int) {
        setRegister(RegisterNames.A, (value and FILTER_TOP_BITS) shr EIGHT_BITS)
        setRegister(RegisterNames.F, value and FILTER_LOWER_BITS)
    }

    /**
     * Sets the value of register B (with high 8 bits of value) and C (with low 8 bits of value)
     *
     * @param value to assign
     */
    public fun setBC(value: Int) {
        setRegister(RegisterNames.B, (value and FILTER_TOP_BITS) shr EIGHT_BITS)
        setRegister(RegisterNames.C, value and FILTER_LOWER_BITS)
    }

    /**
     * Sets the value of register D (with high 8 bits of value) and E (with low 8 bits of value)
     *
     * @param value to assign
     */
    public fun setDE(value: Int) {
        setRegister(RegisterNames.D, (value and FILTER_TOP_BITS) shr EIGHT_BITS)
        setRegister(RegisterNames.E, value and FILTER_LOWER_BITS)
    }

    /**
     * Sets the value of register H (with high 8 bits of value) and L (with low 8 bits of value)
     *
     * @param value to assign
     */
    public fun setHL(value: Int) {
        setRegister(RegisterNames.H, (value and FILTER_TOP_BITS) shr EIGHT_BITS)
        setRegister(RegisterNames.L, value and FILTER_LOWER_BITS)
    }

    /**
     * Builds the content of all CPU registers in the following manner
     * RegisterName: RegisterValue ... Next 4 instructions to execute
     *
     * @return String with debug dump of registers
     */
    @Suppress("ImplicitDefaultLocale", "MagicNumber")
    override fun toString(): String {
        val stringBuilder = StringBuilder();

        registersMap.forEach {
            stringBuilder.append(it.key).append(": ")
                .append(String.format("%02X", getRegister(it.key).getValue()))
                .append(" ");
        }

        stringBuilder.append("SP: ").
        append(String.format("%04X", stackPointer)).append(" ");
        stringBuilder.append("PC: 00:").
        append(String.format("%04X", programCounter)).append(" ");

        stringBuilder.append("(").append(String.format("%02X", bus.getValue(programCounter)));
        stringBuilder.append(" ").append(String.format("%02X", bus.getValue(programCounter + 1)));
        stringBuilder.append(" ").append(String.format("%02X", bus.getValue(programCounter + 2)));
        stringBuilder.append(" ").append(String.format("%02X", bus.getValue(programCounter + 3)));
        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}

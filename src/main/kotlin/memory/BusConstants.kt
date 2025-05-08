package memory

/**
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber")
enum class BusConstants(
    val id: Int
) {

    /**
     * CPU Getter Codes
     */
    GET_FLAGS(0),
    GET_REGISTER(1),
    GET_AF(2),
    GET_BC(3),
    GET_DE(4),
    GET_HL(5),
    GET_PC(6),
    GET_SP(7),
    GET_HALTED(8),
    GET_STOPPED(9),
    GET_MC(10),
    GET_HALT_MC(11),

    /**
     * CPU Execution Codes
     */
    TICK_TIMERS(0),
    SET_REGISTER(1),
    INCR_PC(2),
    SET_PC(3),
    INCR_SP(4),
    SET_SP(5),
    SET_AF(6),
    SET_BC(7),
    SET_DE(8),
    SET_HL(9),
    DISABLE_INT(10),
    ENABLE_INT(11),
    REQUEST_INT(12),
    HALT(13),
    UNHALT(14),
    STOP(15)
}

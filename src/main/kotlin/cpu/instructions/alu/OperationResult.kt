package cpu.instructions.alu

/**
 * Data class used to return operation results to facilitate the process of managing Alu instructions
 *
 * @param value result of the operation
 * @param isZero true if the result is zero, false otherwise
 * @param hasHalfCarry true if the result has a carry on the 4th bit, false otherwise
 * @param hasCarry true if the result has a carry on the 8th bit, false otherwise
 *
 * @author rodrigotimoteo
 **/
data class OperationResult(
    val value: Int,
    val isZero: Boolean,
    val hasHalfCarry: Boolean,
    val hasCarry: Boolean
)

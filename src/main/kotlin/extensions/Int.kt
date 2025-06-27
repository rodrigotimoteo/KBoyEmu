package extensions

/**
 * @author rodrigotimoteo
 **/
fun Int.formatWord(): String {
    val output = "%02X".format(this.and(0xFF))
    return output
}
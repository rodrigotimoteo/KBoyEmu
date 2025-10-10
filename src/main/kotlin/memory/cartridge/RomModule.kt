package memory.cartridge

/**
 * Defines specific behavior hold by memory modules responsible for holding the contents of roms,
 * these are mostly ram bank controllers at least for now
 *
 * @author rodrigotimoteo
 **/
interface RomModule {

    /**
     * Method that checks if ram is currently enabled
     *
     * @return true enabled false otherwise
     */
    fun getRamStatus(): Boolean

    /**
     * Method that returns the number of ram banks present in this rom
     *
     * @return value representing the number of RAM Banks
     */
    fun getRamBanks(): Int
}

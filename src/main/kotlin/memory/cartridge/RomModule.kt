package com.github.memory.cartridge

/**
 * Defines specific behavior hold by memory modules responsible for holding the contents of roms,
 * these are mostly ram bank controllors at least for now
 * @author rodrigotimoteo
 **/
interface RomModule {

    /**
     * Method that checks if ram is currectly enabled
     */
    fun getRamStatus(): Boolean

    /**
     * Method that returns the number of ram banks present in this rom
     */
    fun getRamBanks(): Int
}
package com.github.memory.cartridge

import com.github.memory.MemoryModule

/**
 * Represents the MBC0 (Memory Bank Controller) one of many types of controllers used by the Game Boy
 * This controller normally has 32Kib of ROM and if (most of the time it doesn't) RAM exists 8Kib
 * @author rodrigotimoteo
 **/
class MBC0(
    romBanks: Int,
    ramBanks: Int,
    romContent: Array<Byte>
) : MemoryModule(
    content = romContent,
    size = 0x4000,
    simultaneousActiveBanks = 2,
    memoryOffset = 0x0,
    numberOfBanks = romBanks
), RomModule {
    private val numberOfRamBanks: Int = ramBanks

    override fun getRamStatus(): Boolean {
        return true
    }

    /**
     * Method that returns the number of ram banks present in this rom
     */
    override fun getRamBanks(): Int {
        return numberOfRamBanks
    }

}

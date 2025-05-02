package memory.cartridge

import memory.MemoryModule

/**
 * TODO not implemented yet, MCC0 copy
 * @author rodrigotimoteo
 **/
class MBC1(
    romBanks: Int,
    ramBanks: Int,
    romContent: ByteArray
) : MemoryModule(
    content = romContent,
    size = 0x4000,
    simultaneousBanks = 2,
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

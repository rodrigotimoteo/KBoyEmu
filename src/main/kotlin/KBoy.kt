import memory.cartridge.RomReader
import java.io.File

/**
 * @author rodrigotimoteo
 **/
class KBoy : Thread() {

    private val romName = "/Users/rodrigotimoteo/IdeaProjects/KBoyEmu/cenasatoa/01-read_timing.gb"

    private val romReader: RomReader = RomReader()

    init {
        loadRom(romName)



    }

    private fun loadRom(romName: String) {
        val romFile = File(romName)

        if (!romFile.exists()) return

        romReader.loadRom(romFile)
    }

    override fun run() {
        super.run()
    }
}

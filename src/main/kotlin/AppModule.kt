import cpu.CPU
import org.koin.dsl.module
import ppu.PPU

/**
 * Koin module with the base singletons used
 * @author rodrigotimoteo
 **/
val appModule = module {
    single { CPU() }
    single { PPU() }
}

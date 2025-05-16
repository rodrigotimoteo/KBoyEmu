import org.koin.core.context.startKoin

/**
 * @author rodrigotimoteo
 **/
fun main() {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JBEmu");

    startKoin {

    }

    val kBoy = KBoy()
}

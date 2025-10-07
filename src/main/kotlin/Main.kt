import org.koin.core.context.startKoin

/**
 * Main app entry point
 * @author rodrigotimoteo
 **/
fun main() {
    startKoin {
        modules(appModule)
    }

}

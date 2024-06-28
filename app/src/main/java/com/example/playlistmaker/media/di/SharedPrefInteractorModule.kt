import com.example.playlistmaker.media.domain.interfaces.SharedPrefMediaInteractor
import org.koin.dsl.module

val sharedPrefInteractorModule = module {
    factory {
        SharedPrefMediaInteractor(get())
    }

}
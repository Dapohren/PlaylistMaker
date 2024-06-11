import com.example.playlistmaker.media.domain.db.SharedPrefMediaInteractor
import org.koin.dsl.module

val sharedPrefInteractorModule = module {
    factory {
        SharedPrefMediaInteractor(get())
    }

}
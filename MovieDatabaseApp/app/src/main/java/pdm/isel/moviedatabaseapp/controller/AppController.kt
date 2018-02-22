package pdm.isel.moviedatabaseapp.controller

import com.android.volley.VolleyError
import pdm.isel.moviedatabaseapp.MovieApplication
import pdm.isel.moviedatabaseapp.controller.model.MovieDto
import pdm.isel.moviedatabaseapp.controller.model.MovieListDto
import pdm.isel.moviedatabaseapp.exceptions.*

class AppController {

    companion object {
        const val MOST_POPULAR = "MOST_POPULAR"
        const val UPCOMING = "UPCOMING"
        const val NOW_PLAYING = "NOW_PLAYING"
        const val MOVIE_DETAILS = "MOVIE_DETAILS"

        fun actionHandler(action: String, params: ParametersContainer) {
            return when (action) {
                MOST_POPULAR -> mostPopularMovies(params)
                UPCOMING -> upcomingMovies(params)
                NOW_PLAYING -> nowPlaying(params)
                MOVIE_DETAILS -> movieDetails(params)
                else -> throw UnsupportedOperationException("Action not supported!")
            }
        }

        private fun movieDetails(params: ParametersContainer) {
            when (params.source) {
                UPCOMING, NOW_PLAYING -> params.app.localRepository.getMovieDetails(
                        params.id,
                        params.source,
                        { movie -> params.successCb(Pair(null, movie)) },
                        { error -> params.errorCb(RepoException(error.message.toString())) }
                )
                MOST_POPULAR, MOVIE_DETAILS -> params.app.remoteRepository.getMovieDetails(
                        params.id,
                        params.app,
                        { movie, _ -> params.successCb(Pair(null, movie)) },
                        { error: VolleyError -> params.errorCb(ProviderException()) }
                )
            }
        }

        fun mostPopularMovies(params: ParametersContainer) {
            params.app.remoteRepository.getMostPopularMovies(
                    params.page,
                    params.app,
                    { movies, _ -> params.successCb(Pair(movies, null)) },
                    { error: VolleyError -> params.errorCb(ProviderException("An internet connection is required to access this functionality")) }//TODO: add string to strings.xml
            )
        }

        fun upcomingMovies(params: ParametersContainer) {
            params.app.localRepository.getUpComingMovies(
                    params.page,
                    { movies -> params.successCb(Pair(movies, null)) },
                    { error -> params.errorCb(error) }
            )
        }

        fun nowPlaying(params: ParametersContainer) {
            params.app.localRepository.getNowPlayingMovies(
                    params.page,
                    { movies -> params.successCb(Pair(movies, null)) },
                    { error -> params.errorCb(error) }
            )
        }
    }
}

data class ParametersContainer(
        val app: MovieApplication,
        val id: Int = 0,
        val page: Int = 1,
        val successCb: (Pair<MovieListDto?, MovieDto?>) -> Unit,
        val errorCb: (AppException) -> Unit,
        var source: String = ""
)
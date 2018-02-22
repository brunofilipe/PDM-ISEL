package pdm.isel.moviedatabaseapp.data.repos

import android.content.Context
import android.net.ConnectivityManager
import java.util.UUID;
import com.android.volley.VolleyError
import pdm.isel.moviedatabaseapp.data.comms.HttpRequest
import pdm.isel.moviedatabaseapp.MovieApplication
import pdm.isel.moviedatabaseapp.controller.model.MovieDto
import pdm.isel.moviedatabaseapp.controller.model.MovieListDto
import pdm.isel.moviedatabaseapp.data.repos.base.ITMDBMovieRepository

class TMDBMovieRepository(apikey: String, lang: String) : ITMDBMovieRepository {
    private val API_KEY: String = apikey
    private val MOVIES_BY_NAME_URL = "https://api.themoviedb.org/3/search/movie?api_key=$API_KEY&language=$lang&query=%s&page="
    private val MOVIE_DETAILS_URL = "https://api.themoviedb.org/3/movie/%d?api_key=$API_KEY&language=$lang"
    private val NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=$API_KEY&language=$lang&page="
    private val UPCOMING_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=$API_KEY&language=$lang&page="
    private val MOST_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?api_key=$API_KEY&language=$lang&page="
    private val SIMILAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/%d/similar?api_key=$API_KEY&language=$lang"

    override fun getUpComingMovies(page: Int, ctx: Context, successCb: (MovieListDto, String) -> Unit, errorCb: (VolleyError) -> Unit) {
        if (!isConnected(ctx))
            return errorCb(VolleyError())
        val url = UPCOMING_URL + page
        val tag = UUID.randomUUID().toString()
        val req = HttpRequest(
                url,
                MovieListDto::class.java,
                { movies -> successCb(movies, tag)},
                errorCb
        )
        req.tag = tag
        (ctx as MovieApplication).requestQueue.add(req)
    }

    override fun getMoviesByName(name: String, page: Int, ctx: Context, successCb: (MovieListDto, String) -> Unit, errorCb: (VolleyError) -> Unit) {
        if (!isConnected(ctx))
            return errorCb(VolleyError())
        val url = java.lang.String.format(MOVIES_BY_NAME_URL, name) + page
        val tag = UUID.randomUUID().toString()
        val req = HttpRequest(
                url,
                MovieListDto::class.java,
                { movies -> successCb(movies, tag)},
                errorCb
        )
        req.tag = tag
        (ctx as MovieApplication).requestQueue.add(req)
    }

    override fun getNowPlayingMovies(page: Int, ctx: Context, successCb: (MovieListDto, String) -> Unit, errorCb: (VolleyError) -> Unit) {
        if (!isConnected(ctx))
            return errorCb(VolleyError())
        val url = NOW_PLAYING_URL + page
        val tag = UUID.randomUUID().toString()
        val req = HttpRequest(
                url,
                MovieListDto::class.java,
                { movies -> successCb(movies, tag)},
                errorCb
        )
        req.tag = tag
        (ctx as MovieApplication).requestQueue.add(req)
    }

    override fun getMovieDetails(id: Int, ctx: Context, successCb: (MovieDto, String) -> Unit, errorCb: (VolleyError) -> Unit) {
        if (!isConnected(ctx))
            return errorCb(VolleyError())
        val uri = java.lang.String.format(MOVIE_DETAILS_URL, id)
        val tag = UUID.randomUUID().toString()
        val req = HttpRequest(
                uri,
                MovieDto::class.java,
                { movie -> successCb(movie, tag)},
                errorCb
        )
        req.tag = tag
        (ctx as MovieApplication).requestQueue.add(req)
    }

    override fun getMostPopularMovies(page: Int, ctx: Context, successCb: (MovieListDto, String) -> Unit, errorCb: (VolleyError) -> Unit) {
        if (!isConnected(ctx))
            return errorCb(VolleyError())
        val url =  MOST_POPULAR_URL + page
        val tag = UUID.randomUUID().toString()
        val req = HttpRequest(
                url,
                MovieListDto::class.java,
                { movies -> successCb(movies, tag)},
                errorCb
        )
        req.tag = tag

        (ctx as MovieApplication).requestQueue.add(req)
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
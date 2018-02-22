package pdm.isel.moviedatabaseapp.data.repos.base

import android.net.Uri
import pdm.isel.moviedatabaseapp.controller.model.FollowedMovie
import pdm.isel.moviedatabaseapp.controller.model.MovieDto
import pdm.isel.moviedatabaseapp.controller.model.MovieListDto
import pdm.isel.moviedatabaseapp.exceptions.RepoException

interface ILocalRepository {

    fun getNowPlayingMovies(page: Int, successCb: (MovieListDto) -> Unit, errorCb: (RepoException) -> Unit)
    fun getUpComingMovies(page: Int, successCb: (MovieListDto) -> Unit, errorCb: (RepoException) -> Unit)
    fun getMovieDetails(id: Int, table: String, successCb: (MovieDto) -> Unit, errorCb: (RepoException) -> Unit)
    fun insertMovie(movie: MovieDto, table: String, errorCb: (RepoException) -> Unit)
    fun deleteTable(table: String, errorCb: (RepoException) -> Unit)
    fun followMovie(movieId: Int, title: String, poster: String, releaseDate: String, successCb: (Uri?) -> Unit, errorCb: (RepoException) -> Unit)
    fun unfollowMovie(movieId: Int, successCb: (Int) -> Unit, errorCb: (RepoException) -> Unit)
    fun getFollowedMovies(successCb: (Array<FollowedMovie>) -> Unit, errorCb: (RepoException) -> Unit)
}

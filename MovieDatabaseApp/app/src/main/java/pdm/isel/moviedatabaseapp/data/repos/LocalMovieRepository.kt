package pdm.isel.moviedatabaseapp.data.repos

import android.content.AsyncQueryHandler
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import pdm.isel.moviedatabaseapp.data.content.MovieContentProvider
import pdm.isel.moviedatabaseapp.controller.model.FollowedMovie
import pdm.isel.moviedatabaseapp.controller.model.MovieDto
import pdm.isel.moviedatabaseapp.controller.model.MovieListDto
import pdm.isel.moviedatabaseapp.data.repos.base.ILocalRepository
import pdm.isel.moviedatabaseapp.exceptions.RepoException
import pdm.isel.moviedatabaseapp.controller.mapper.toContentValues
import pdm.isel.moviedatabaseapp.controller.mapper.toFollowedMovies
import pdm.isel.moviedatabaseapp.controller.mapper.toMovieDto
import pdm.isel.moviedatabaseapp.controller.mapper.toMovieListDto

class LocalMovieRepository(private val ctx: Context) : ILocalRepository {

    override fun getMovieDetails(id: Int, table: String, successCb: (MovieDto) -> Unit, errorCb: (RepoException) -> Unit) {
        val selectionArgs = arrayOf(id.toString())
        val selection = "${MovieContentProvider.MOVIE_ID}=?"
        val tableUri = when(table) {
            "NOW_PLAYING" -> MovieContentProvider.NOW_PLAYING_URI
            "UPCOMING" -> MovieContentProvider.UPCOMING_URI
            else -> return errorCb(RepoException("Table not found"))
        }
        val cursor: Cursor? = ctx.contentResolver.query(
                tableUri,
                null,
                selection,
                selectionArgs,
                null
        )
        if( cursor === null )
            return errorCb(RepoException())
        val res = cursor.toMovieDto()
        cursor.close()
        successCb(res)
    }

    override fun getNowPlayingMovies(page: Int, successCb: (MovieListDto) -> Unit, errorCb: (RepoException) -> Unit) {
        val offset = (page-1) * 20
        val limit = offset + 20
        val cursor: Cursor? = ctx.contentResolver.query(
                MovieContentProvider.NOW_PLAYING_URI,
                null,
                null,
                null,
                " ${MovieContentProvider.MOVIE_ID} limit $limit offset $offset"
        )
        if( cursor === null )
            return errorCb(RepoException())
        val res = cursor.toMovieListDto(page)
        cursor.close()
        successCb(res)
    }

    override fun getUpComingMovies(page: Int, successCb: (MovieListDto) -> Unit, errorCb: (RepoException) -> Unit) {
        val offset = (page-1) * 20
        val limit = offset + 20
        val cursor: Cursor? = ctx.contentResolver.query(
                MovieContentProvider.UPCOMING_URI,
                null,
                null,
                null,
                " ${MovieContentProvider.MOVIE_ID} limit $limit offset $offset"
        )
        if( cursor === null )
            return errorCb(RepoException())
        val res = cursor.toMovieListDto(page)
        cursor.close()
        successCb(res)
    }

    override fun getFollowedMovies(successCb: (Array<FollowedMovie>) -> Unit, errorCb: (RepoException) -> Unit) {
        val cursor: Cursor? = ctx.contentResolver.query(
                MovieContentProvider.FOLLOWING_URI,
                null,
                null,
                null,
                null
        )
        if( cursor === null )
            return errorCb(RepoException())
        val res = cursor.toFollowedMovies()
        cursor.close()
        successCb(res)
    }

    override fun insertMovie(movie: MovieDto, table: String, errorCb: (RepoException) -> Unit) {
        val tableUri: Uri = when(table) {
            "NOW_PLAYING" -> MovieContentProvider.NOW_PLAYING_URI
            "UPCOMING" -> MovieContentProvider.UPCOMING_URI
            else -> throw RepoException("Table not found!")
        }
        MyAsyncQueryHandler(
                ctx.contentResolver,
                errorCb
        ).startInsert(1, null, tableUri, movie.toContentValues())
    }

    override fun followMovie(movieId: Int, title: String, poster: String, releaseDate: String, successCb: (Uri?) -> Unit, errorCb: (RepoException) -> Unit) {
        val contentValue = ContentValues()
        with(MovieContentProvider) {
            contentValue.put(MOVIE_ID, movieId)
            contentValue.put(POSTER, poster)
            contentValue.put(TITLE, title)
            contentValue.put(RELEASE_DATE, releaseDate)
        }
        MyAsyncQueryHandler(
                ctx.contentResolver,
                errorCb,
                asyncInsertListener = successCb
        ).startInsert(
                1,
                null,
                MovieContentProvider.FOLLOWING_URI,
                contentValue
        )
    }

    override fun deleteTable(table: String, errorCb: (RepoException) -> Unit) {
        val tableUri: Uri =  when(table) {
            "NOW_PLAYING" -> MovieContentProvider.NOW_PLAYING_URI
            "UPCOMING" -> MovieContentProvider.UPCOMING_URI
            else -> return errorCb(RepoException("Table not found!"))
        }
        MyAsyncQueryHandler(
                ctx.contentResolver,
                errorCb
        ).startDelete(
                1,
                null,
                tableUri,
                null,
                null
        )
    }

    override fun unfollowMovie(movieId: Int, successCb: (Int) -> Unit, errorCb: (RepoException) -> Unit) {
        val selectionArgs = arrayOf(movieId.toString())
        val selection = "${MovieContentProvider.MOVIE_ID}=?"
        MyAsyncQueryHandler(
                ctx.contentResolver,
                errorCb,
                asyncDeleteListener = successCb
        ).startDelete(
                1,
                null,
                MovieContentProvider.FOLLOWING_URI,
                selection,
                selectionArgs
        )
    }
}

class MyAsyncQueryHandler(
        contentResolver: ContentResolver,
        private val errorCb: (RepoException) -> Unit,
        private val asyncInsertListener: ((Uri?) -> Unit)? = null,
        private val asyncUpdateListener: ((Int) -> Unit)? = null,
        private val asyncDeleteListener: ((Int) -> Unit)? = null
) : AsyncQueryHandler(contentResolver) {

    override fun onInsertComplete(token: Int, cookie: Any?, uri: Uri?) {
        if(asyncInsertListener === null || uri === null) return

        asyncInsertListener.invoke(uri)
    }

    override fun onUpdateComplete(token: Int, cookie: Any?, result: Int) {
        if(result < 0) return errorCb(RepoException("Error updating database"))
        if(asyncUpdateListener === null) return

        asyncUpdateListener.invoke(result)
    }

    override fun onDeleteComplete(token: Int, cookie: Any?, result: Int) {
        if(result < 0) return super.onDeleteComplete(token, cookie, result)
        if(asyncDeleteListener === null) return

        asyncDeleteListener.invoke(result)
    }
}


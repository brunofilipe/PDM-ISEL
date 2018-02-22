package pdm.isel.moviedatabaseapp.controller.mapper

import android.content.ContentValues
import android.database.Cursor
import pdm.isel.moviedatabaseapp.data.content.MovieContentProvider
import pdm.isel.moviedatabaseapp.controller.model.FollowedMovie
import pdm.isel.moviedatabaseapp.controller.model.Genres
import pdm.isel.moviedatabaseapp.controller.model.MovieDto
import pdm.isel.moviedatabaseapp.controller.model.MovieListDto

fun MovieDto.toContentValues(): ContentValues {
    val result = ContentValues()
    with(MovieContentProvider) {
        result.put(MOVIE_ID, id)
        result.put(TITLE, title)
        result.put(RELEASE_DATE, releaseDate)
        result.put(POSTER, poster)
        result.put(VOTE_AVERAGE, voteAverage)
        result.put(RUNTIME, runtime)
        result.put(OVERVIEW, overview)
        result.put(POPULARITY, popularity)
        result.put(GENRES, genres?.map { it.name }?.joinToString(", "))
    }
    return result
}

fun Cursor.toMovieListDto(page: Int): MovieListDto {
    val iter = object : AbstractIterator<MovieDto>() {
        override fun computeNext() {
            moveToNext()
            when(isAfterLast) {
                true -> done()
                false -> setNext(mapToMovieDto(this@toMovieListDto))
            }
        }
    }
    val list = mutableListOf<MovieDto>().let { it.addAll(Iterable { iter }); it }.toTypedArray()
    return MovieListDto(
            page,
            null,
            null,
            list,
            null
    )
}

fun Cursor.toMovieDto(): MovieDto {
    val iter = object : AbstractIterator<MovieDto>() {
        override fun computeNext() {
            moveToNext()
            when(isAfterLast) {
                true -> done()
                false -> setNext(mapToMovieDto(this@toMovieDto))
            }
        }
    }
    val list = mutableListOf<MovieDto>().let { it.addAll(Iterable { iter }); it }.toTypedArray()
    return list.first()
}

fun Cursor.toFollowedMovies(): Array<FollowedMovie> {
    val iter = object : AbstractIterator<FollowedMovie>() {
        override fun computeNext() {
            moveToNext()
            when(isAfterLast) {
                true -> done()
                false -> setNext(mapToFollowedMovie(this@toFollowedMovies))
            }
        }
    }
    return mutableListOf<FollowedMovie>().let { it.addAll(Iterable { iter }); it }.toTypedArray()
}

fun mapToFollowedMovie(cursor: Cursor): FollowedMovie {
    return with(MovieContentProvider) {
        FollowedMovie(
                id = cursor.getInt(cursor.getColumnIndex(MOVIE_ID)),
                title = cursor.getString(cursor.getColumnIndex(TITLE)),
                poster = cursor.getString(cursor.getColumnIndex(POSTER)),
                releaseDate = cursor.getString(cursor.getColumnIndex(RELEASE_DATE))
        )
    }
}

fun mapToMovieDto(cursor: Cursor): MovieDto {
    return with(MovieContentProvider) {
        MovieDto(
                id = cursor.getInt(cursor.getColumnIndex(MOVIE_ID)),
                title = cursor.getString(cursor.getColumnIndex(TITLE)),
                runtime = cursor.getInt(cursor.getColumnIndex(RUNTIME)),
                releaseDate = cursor.getString(cursor.getColumnIndex(RELEASE_DATE)),
                poster = cursor.getString(cursor.getColumnIndex(POSTER)),
                voteAverage = cursor.getFloat(cursor.getColumnIndex(VOTE_AVERAGE)),
                overview = cursor.getString(cursor.getColumnIndex(OVERVIEW)),
                popularity = cursor.getFloat(cursor.getColumnIndex(POPULARITY)),
                genres = Genres.create(cursor.getString(cursor.getColumnIndex(GENRES)))
        )
    }
}
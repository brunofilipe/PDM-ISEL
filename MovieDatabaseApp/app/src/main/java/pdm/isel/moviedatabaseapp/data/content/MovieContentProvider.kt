package pdm.isel.moviedatabaseapp.data.content

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class MovieContentProvider : ContentProvider() {

    companion object {
        //Tables
        const val UPCOMING = "Upcoming"
        const val NOW_PLAYING = "NowPlaying"
        const val FOLLOWING = "Following"

        //columns of table
        const val MOVIE_ID = "_id"
        const val TITLE = "title"
        const val RELEASE_DATE = "release_date"
        const val POSTER = "poster"
        const val VOTE_AVERAGE = "vote_average"
        const val RUNTIME = "runtime"
        const val POPULARITY = "popularity"
        const val OVERVIEW = "overview"
        const val GENRES = "genres"

        //configure movie remoteRepository
        const val AUTHORITY = "pdm.isel.moviedatabaseapp"

        //paths for each table
        const val NOW_PLAYING_PATH = "now_playing"
        const val UPCOMING_PATH = "upcoming"
        const val FOLLOWING_PATH = "following"

        //URIs to access db tables
        val NOW_PLAYING_URI = Uri.parse("content://$AUTHORITY/$NOW_PLAYING_PATH")
        val UPCOMING_URI = Uri.parse("content://$AUTHORITY/$UPCOMING_PATH")
        val FOLLOWING_URI = Uri.parse("content://$AUTHORITY/$FOLLOWING_PATH")

        //auxiliary code to return when each uri is matched
        const val UPCOMING_LIST_CODE = 100
        const val UPCOMING_ITEM_CODE = 200
        const val NOW_PLAYING_LIST_CODE = 300
        const val NOW_PLAYING_ITEM_CODE = 400
        const val FOLLOWING_LIST_CODE = 500
        const val FOLLOWING_ITEM_CODE = 600

        //auxiliary types for getType method
        val MOVIE_LIST_CONTENT_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/movies"
        val MOVIE_ITEM_CONTENT_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/movie"

    }

    @Volatile private lateinit var dbHelper: MovieDbHelper
    @Volatile private lateinit var uriMatcher: UriMatcher

    override fun onCreate(): Boolean {
        dbHelper = MovieDbHelper(this@MovieContentProvider.context)
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        with(uriMatcher) {
            addURI(AUTHORITY, UPCOMING_PATH, UPCOMING_LIST_CODE)
            addURI(AUTHORITY, "$UPCOMING_PATH/#", UPCOMING_ITEM_CODE)
            addURI(AUTHORITY, NOW_PLAYING_PATH, NOW_PLAYING_LIST_CODE)
            addURI(AUTHORITY, "$NOW_PLAYING_PATH/#", NOW_PLAYING_ITEM_CODE)
            addURI(AUTHORITY, FOLLOWING_PATH, FOLLOWING_LIST_CODE)
            addURI(AUTHORITY, "$FOLLOWING_PATH/#", FOLLOWING_ITEM_CODE)
        }
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val tableInfo = getTable(uri)
        val db = dbHelper.writableDatabase
        return db.use {
            val id = db.insert(tableInfo.first, null, values)
            if (id < 0) {
                null
            } else {
                context.contentResolver.notifyChange(uri, null)
                Uri.parse("content://$AUTHORITY/${tableInfo.second}/$id")
            }
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sort: String?): Cursor {
        val params = getTableEntry(uri, selection, selectionArgs)
        val db = dbHelper.readableDatabase
        return db.query(params.first, projection, params.second, params.third, null, null, sort)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val params = getTableEntry(uri, selection, selectionArgs)
        val db = dbHelper.writableDatabase
        return db.use {
            val deletedCount = db.delete(params.first, params.second, params.third)
            if (deletedCount != 0)
                context.contentResolver.notifyChange(uri, null)
            deletedCount
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val params = getTableEntry(uri, selection, selectionArgs)
        val db = dbHelper.writableDatabase
        return db.use {
            val updateCount = db.update(params.first, values, params.second, params.third)
            if( updateCount != 0 )
                context.contentResolver.notifyChange(uri, null)
            updateCount
        }
    }

    override fun getType(uri: Uri?): String = when (uriMatcher.match(uri)) {
        UPCOMING_LIST_CODE, NOW_PLAYING_LIST_CODE, FOLLOWING_LIST_CODE -> MOVIE_LIST_CONTENT_TYPE
        UPCOMING_ITEM_CODE, NOW_PLAYING_ITEM_CODE, FOLLOWING_ITEM_CODE -> MOVIE_ITEM_CONTENT_TYPE
        else -> throw IllegalArgumentException("Uri $uri not supported")
    }

    private fun getTableEntry(uri: Uri, selection: String?, selectionArgs: Array<String>?): Triple<String, String?, Array<String>?> {
        val itemSelection = "$MOVIE_ID = ${uri.pathSegments.last()}"
        return when (uriMatcher.match(uri)) {
            UPCOMING_ITEM_CODE -> Triple(UPCOMING, itemSelection, null)
            NOW_PLAYING_ITEM_CODE -> Triple(NOW_PLAYING, itemSelection, null)
            FOLLOWING_ITEM_CODE -> Triple(FOLLOWING, itemSelection, null)
            else -> getTable(uri).let { Triple(it.first, selection, selectionArgs) }
        }
    }

    private fun getTable(uri: Uri): Pair<String, String> = when (uriMatcher.match(uri)) {
        UPCOMING_LIST_CODE -> Pair(UPCOMING, UPCOMING_PATH)
        NOW_PLAYING_LIST_CODE -> Pair(NOW_PLAYING, NOW_PLAYING_PATH)
        FOLLOWING_LIST_CODE -> Pair(FOLLOWING, FOLLOWING_PATH)
        else -> null
    } ?: throw IllegalArgumentException("Uri $uri not supported")
}
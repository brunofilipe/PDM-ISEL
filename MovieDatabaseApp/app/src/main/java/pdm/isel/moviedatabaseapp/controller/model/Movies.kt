package pdm.isel.moviedatabaseapp.controller.model

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

class MovieListDto(
        val page: Int?,
        @JsonProperty("total_result")
        val totalResult: Int?,
        @JsonProperty("total_pages")
        val totalPages: Int?,
        val results: Array<MovieDto>,
        val dates: MyDate?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.createTypedArray(MovieDto),
            parcel.readParcelable(MyDate::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(page)
        parcel.writeValue(totalResult)
        parcel.writeValue(totalPages)
        parcel.writeTypedArray(results, flags)
        parcel.writeParcelable(dates, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MovieListDto> {
        override fun createFromParcel(parcel: Parcel): MovieListDto {
            return MovieListDto(parcel)
        }

        override fun newArray(size: Int): Array<MovieListDto?> {
            return arrayOfNulls(size)
        }
    }
}

data class MovieDto(
        val id: Int,
        val title: String?,
        val runtime: Int?,
        @JsonProperty("release_date")
        val releaseDate: String?,
        @JsonProperty("poster_path")
        val poster: String?,
        @JsonProperty("vote_average")
        val voteAverage: Float?,
        val overview: String?,
        val popularity: Float?,
        val genres: Array<Genres>?
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.readString(),
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.createTypedArray(Genres))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeValue(runtime)
        parcel.writeString(releaseDate)
        parcel.writeString(poster)
        parcel.writeValue(voteAverage)
        parcel.writeString(overview)
        parcel.writeValue(popularity)
        parcel.writeTypedArray(genres, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<MovieDto> {
        override fun createFromParcel(parcel: Parcel) = MovieDto(parcel)

        override fun newArray(size: Int): Array<MovieDto?> = arrayOfNulls(size)
    }
}

data class MyDate(val maximum: String?, val minimum: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(maximum)
        parcel.writeString(minimum)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyDate> {
        override fun createFromParcel(parcel: Parcel): MyDate {
            return MyDate(parcel)
        }

        override fun newArray(size: Int): Array<MyDate?> {
            return arrayOfNulls(size)
        }
    }
}

data class Genres(val id: Int, val name: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Genres> {

        fun create(s:String) : Array<Genres>{
            val strings = s.split(",").toTypedArray()
            return strings.map { Genres(1,it) }.toTypedArray()
        }

        override fun createFromParcel(parcel: Parcel): Genres = Genres(parcel)

        override fun newArray(size: Int): Array<Genres?> = arrayOfNulls(size)
    }
}

data class FollowedMovie(
        val id: Int,
        val title: String,
        val releaseDate: String,
        val poster: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(releaseDate)
        parcel.writeString(poster)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FollowedMovie> {
        override fun createFromParcel(parcel: Parcel): FollowedMovie {
            return FollowedMovie(parcel)
        }

        override fun newArray(size: Int): Array<FollowedMovie?> {
            return arrayOfNulls(size)
        }
    }
}
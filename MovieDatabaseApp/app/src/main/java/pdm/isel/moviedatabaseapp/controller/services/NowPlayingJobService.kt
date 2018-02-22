package pdm.isel.moviedatabaseapp.controller.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Handler
import pdm.isel.moviedatabaseapp.MovieApplication

class NowPlayingJobService : JobService() {
    @Volatile private var onGoingRequests: MutableList<String> = mutableListOf()
    private val handler = Handler()

    companion object {
        const val MAX_PAGES_ALLOWED = 5
        const val MAX_DELAY_MILIS: Long = 6500
        const val JOB_ID = 2000
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        onGoingRequests.forEach {
            (application as MovieApplication).requestQueue.cancelAll(it)
        }
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        (application as MovieApplication).localRepository.deleteTable(
                "NOW_PLAYING",
                { jobFinished(params, true) }
        )
        fillNowPlayingTable(1, params)
        return true
    }

    private fun fillNowPlayingTable(startPage: Int, params: JobParameters?) {
        var page = startPage
        (application as MovieApplication).remoteRepository.getNowPlayingMovies(
                startPage,
                (application as MovieApplication),
                { movies, tag ->
                    onGoingRequests.add(tag)
                    movies.results.forEach {
                        (application as MovieApplication).remoteRepository.getMovieDetails(
                                it.id,
                                (application as MovieApplication),
                                { movie, tag ->
                                    onGoingRequests.add(tag)
                                    (application as MovieApplication).localRepository.insertMovie(
                                            movie,
                                            "NOW_PLAYING",
                                            { jobFinished(params, true) }
                                    )
                                },
                                { jobFinished(params, true) }
                        )
                    }
                    handler.postDelayed({
                        if(++page <= MAX_PAGES_ALLOWED)
                            return@postDelayed fillNowPlayingTable(page, params)
                        else
                            jobFinished(params, false)
                    }, MAX_DELAY_MILIS)
                },
                { jobFinished(params, true) }
        )
    }
}
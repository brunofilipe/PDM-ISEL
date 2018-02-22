package pdm.isel.moviedatabaseapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.graphics.Color
import android.preference.PreferenceManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import pdm.isel.moviedatabaseapp.data.cache.ImageCache
import pdm.isel.moviedatabaseapp.data.repos.LocalMovieRepository
import pdm.isel.moviedatabaseapp.data.repos.TMDBMovieRepository
import pdm.isel.moviedatabaseapp.data.repos.base.ILocalRepository
import pdm.isel.moviedatabaseapp.data.repos.base.ITMDBMovieRepository
import pdm.isel.moviedatabaseapp.controller.services.NowPlayingJobService
import pdm.isel.moviedatabaseapp.controller.services.UpComingJobService

class MovieApplication : Application() {
    @Volatile lateinit var requestQueue: RequestQueue
    @Volatile lateinit var remoteRepository: ITMDBMovieRepository
    @Volatile lateinit var localRepository: ILocalRepository
    @Volatile lateinit var imageLoader: ImageLoader

    private lateinit var nowPlayingBuilder: JobInfo.Builder
    private lateinit var upcomingBuilder: JobInfo.Builder

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        scheduleServices(sharedPreferences)
    }

    override fun onCreate() {
        super.onCreate()
        remoteRepository = TMDBMovieRepository(readAPIKEY(), getLanguage())
        localRepository = LocalMovieRepository(this)
        requestQueue = Volley.newRequestQueue(this)
        imageLoader = ImageLoader(requestQueue, ImageCache())
        PreferenceManager.setDefaultValues(this, R.xml.shared_preferences, false);
        nowPlayingBuilder = JobInfo.Builder(
                NowPlayingJobService.JOB_ID,
                ComponentName(this, NowPlayingJobService::class.java)
        )
        upcomingBuilder = JobInfo.Builder(
                UpComingJobService.JOB_ID,
                ComponentName(this, UpComingJobService::class.java)
        )

        configureNotifications()
        configureJobServices()
    }

    private fun configureNotifications() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = "followed_movies_channel"

        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(id, getString(R.string.notification_channel_name), importance)

        mChannel.description = getString(R.string.notification_channel_description)
        mChannel.enableLights(true)

        mChannel.lightColor = Color.YELLOW
        mChannel.enableVibration(true)
        mNotificationManager.createNotificationChannel(mChannel)
    }


    private fun configureJobServices() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener)
        scheduleServices()
    }

    private fun scheduleServices(
            sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    ) {
        val network =
                when(sharedPreferences.getString("networkType", null)) {
                    resources.getStringArray(R.array.network_list).get(0) -> JobInfo.NETWORK_TYPE_UNMETERED
                    resources.getStringArray(R.array.network_list).get(1) -> JobInfo.NETWORK_TYPE_METERED
                    else -> JobInfo.NETWORK_TYPE_ANY
                }
        val periodic =
                when(sharedPreferences.getString("updateFrequency", null)) {
                    resources.getStringArray(R.array.update_list).get(0) -> TimeUnit.DAYS.toMillis(1)
                    resources.getStringArray(R.array.update_list).get(1) -> TimeUnit.DAYS.toMillis(7)
                    else -> TimeUnit.DAYS.toMillis(30)
                }

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(nowPlayingBuilder
                .setPeriodic(periodic)
                .setRequiredNetworkType(network)
                .build()
        )
        jobScheduler.schedule(upcomingBuilder
                .setPeriodic(periodic)
                .setRequiredNetworkType(network)
                .build()
        )
    }

    private fun readAPIKEY(): String {
        val ip = resources.openRawResource(R.raw.api_key)
        val buffer = BufferedReader(InputStreamReader(ip))
        return buffer.readLine()
    }

    private fun getLanguage(): String = resources.getString(R.string.language)
}

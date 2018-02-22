package pdm.isel.moviedatabaseapp.ui.activity

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_home.*
import pdm.isel.moviedatabaseapp.R

class HomeActivity : BaseLayoutActivity() {
    override val toolbar: Int? = R.id.my_toolbar
    override val menu: Int? = R.menu.menu
    override val layout: Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBar.visibility = View.INVISIBLE
        my_toolbar.title = resources.getString(R.string.home)

        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this, SearchableActivity::class.java)))
        searchView.setIconifiedByDefault(false)

        nowPlayingButton.setOnClickListener({
            sendIntent("NOW_PLAYING", resources.getString(R.string.moviesNowPlaying))
        })

        upcomingMoviesButton.setOnClickListener({
            sendIntent("UPCOMING", resources.getString(R.string.upcomingMoviesList))
        })

        mostPopularMoviesButton.setOnClickListener({
            sendIntent("MOST_POPULAR", resources.getString(R.string.mostPopularMoviesList))
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent: Intent? = null
        when (item?.itemId) {
            R.id.action_about -> intent = Intent(this, ReferencesActivity::class.java)
            R.id.action_home -> {
                intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            R.id.action_preferences -> intent = Intent(this, PreferencesActivity::class.java)
        }
        startActivity(intent!!)
        return true
    }

    private fun sendIntent(action: String, toolbarText: String) {
        val intent = Intent(this, MovieListActivity::class.java)
        intent.putExtra("toolbarText", toolbarText)
        intent.putExtra("action", action)

        startActivity(intent)
    }
}

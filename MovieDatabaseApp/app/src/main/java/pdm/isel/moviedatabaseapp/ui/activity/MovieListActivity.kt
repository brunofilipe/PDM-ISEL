package pdm.isel.moviedatabaseapp.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_movie_list.*
import pdm.isel.moviedatabaseapp.ui.adapter.MovieAdapter
import pdm.isel.moviedatabaseapp.MovieApplication
import pdm.isel.moviedatabaseapp.R
import pdm.isel.moviedatabaseapp.controller.AppController
import pdm.isel.moviedatabaseapp.controller.ParametersContainer
import pdm.isel.moviedatabaseapp.controller.model.MovieListDto
import pdm.isel.moviedatabaseapp.exceptions.AppException

class MovieListActivity : BaseLayoutActivity() {
    override val toolbar: Int? = R.id.my_toolbar
    override val menu: Int? = R.menu.menu
    override val layout: Int = R.layout.activity_movie_list
    var action: String = ""
    private var movieList : MovieListDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarList.indeterminateDrawable.setColorFilter(
                Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN)
        val intent = intent
        action = intent.getStringExtra("action")
        if(savedInstanceState!=null){
            movieList = savedInstanceState.getParcelable("list")
            progressBarList.visibility = View.INVISIBLE
            displayMovies(movieList!!,intent.getStringExtra("toolbarText"))
        }
        else{
            AppController.actionHandler(
                    action,
                    ParametersContainer(
                            app = (application as MovieApplication),
                            successCb = { pair ->
                                run {
                                    progressBarList.visibility = View.INVISIBLE
                                    displayMovies(pair.first!!, intent.getStringExtra("toolbarText"))
                                }
                            },
                            errorCb = { error -> displayError(error) }
                    )
            )
        }
    }

    private fun displayMovies(movies: MovieListDto, toolbarText: String) {
        this.movieList = movies
        if (movies.dates != null)
            this.my_toolbar.subtitle = resources.getString(R.string.DateRange, movies.dates.minimum, movies.dates.maximum)
        this.my_toolbar.title = toolbarText

        configureAdapter(movies)
    }

    private fun configureAdapter(movies: MovieListDto) {
        val movieAdapter = MovieAdapter(
                this,
                R.layout.movie_list_entry_layout,
                movies.results.toMutableList(),
                (application as MovieApplication).imageLoader
        )

        movieListView.adapter = movieAdapter
        movieListView.emptyView = empty

        movieListView.setOnItemClickListener { parent, view, position, id ->
            sendIntent(movieAdapter.getItem(position).id)
        }

        if (movies.page == null)
            return
        movieListView.setOnScrollListener(object : EndlessScrollListener(20, movies.page) {
            override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
                AppController.actionHandler(
                        action,
                        ParametersContainer(
                                app = (application as MovieApplication),
                                page = page,
                                successCb = { pair -> pair.first!!.results.forEach { movieDto -> movieAdapter.add(movieDto) } },
                                errorCb = { error -> displayError(error) }
                        )
                )
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent: Intent? = null
        when (item?.itemId) {
            R.id.action_about -> intent = Intent(this, ReferencesActivity::class.java)
            R.id.action_home -> intent = Intent(this, HomeActivity::class.java)
            R.id.action_preferences ->  intent = Intent(this, PreferencesActivity::class.java)
        }
        startActivity(intent!!)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable("list",movieList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
       this.movieList = savedInstanceState!!.getParcelable("list")
    }

    private fun sendIntent(id: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("source", action)
        startActivity(intent)
    }

    private fun displayError(error: AppException) {
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }
}
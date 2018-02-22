package pdm.isel.moviedatabaseapp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_references.*
import pdm.isel.moviedatabaseapp.R

class ReferencesActivity : BaseLayoutActivity() {
    override val toolbar: Int? = R.id.my_toolbar
    override val menu: Int? = R.menu.menu
    override val layout: Int = R.layout.activity_references

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        my_toolbar.title = resources.getString(R.string.references)
        tmdb_logo.setOnClickListener {
            val url = Uri.parse("https://www.themoviedb.org/")
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

        nuno_github.setOnClickListener {
            val url = Uri.parse(resources.getString(R.string.github_nuno))
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

        gameiro_github.setOnClickListener {
            val url = Uri.parse(resources.getString(R.string.github_gameiro))
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }

        bruno_github.setOnClickListener {
            val url = Uri.parse(resources.getString(R.string.github_bruno))
            startActivity(Intent(Intent.ACTION_VIEW, url))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent: Intent? = null
        when (item?.itemId) {
            R.id.action_about -> {
                intent = Intent(this, ReferencesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            R.id.action_home -> intent = Intent(this, HomeActivity::class.java)
            R.id.action_preferences ->  intent = Intent(this, PreferencesActivity::class.java)
        }
        startActivity(intent!!)
        return true
    }
}
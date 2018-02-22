package pdm.isel.moviedatabaseapp.ui.activity

import android.app.Fragment
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_references.*
import pdm.isel.moviedatabaseapp.R

class PreferencesActivity : BaseLayoutActivity() {
    override val toolbar: Int? = R.id.my_toolbar
    override val menu: Int? = R.menu.menu
    override val layout: Int = R.layout.activity_preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        my_toolbar.title = resources.getString(R.string.preferences)

        var fragment : Fragment = PreferencesScreen()
        var fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
        if(savedInstanceState == null) {
            fragmentTransaction.add(R.id.preferences, fragment, "shared_preferences")
            fragmentTransaction.commit()
        }
        else
            fragment = fragmentManager.findFragmentByTag("shared_preferences")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent: Intent? = null
        when (item?.itemId) {
            R.id.action_about -> intent = Intent(this, ReferencesActivity::class.java)
            R.id.action_home -> intent = Intent(this, HomeActivity::class.java)
            R.id.action_preferences -> {
                intent = Intent(this, PreferencesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
        }
        startActivity(intent!!)
        return true
    }

    class PreferencesScreen : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.shared_preferences)
        }
    }
}
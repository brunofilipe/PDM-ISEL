package pdm.isel.moviedatabaseapp.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import kotlinx.android.synthetic.main.activity_start.*
import pdm.isel.moviedatabaseapp.R

class SplashActivity : BaseLayoutActivity() {
    override val layout: Int = R.layout.activity_start

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBar.indeterminateDrawable.setColorFilter(
                Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN)
        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
        }, 1500)
    }
}
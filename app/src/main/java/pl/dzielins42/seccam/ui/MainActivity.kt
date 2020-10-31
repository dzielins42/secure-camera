package pl.dzielins42.seccam.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import pl.dzielins42.seccam.R
import pl.dzielins42.seccam.util.Registry
import pl.dzielins42.seccam.util.OnBackPressedListener

class MainActivity : AppCompatActivity(), Registry<OnBackPressedListener> {

    private val onBackPressedListenerRegistry: MutableSet<OnBackPressedListener> = mutableSetOf()
    private val navController: NavController
        get() {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            return navHostFragment.navController
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(
            navController,
            AppBarConfiguration.Builder(R.id.passwordFragment, R.id.galleryFragment).build()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return doNavigateUp() && (navController.navigateUp() || super.onSupportNavigateUp())
    }

    override fun onBackPressed() {
        if (!doNavigateUp()) {
            return
        }
        super.onBackPressed()
    }

    //region OnBackPressedListener
    override fun register(element: OnBackPressedListener) {
        onBackPressedListenerRegistry.add(element)
    }

    override fun unregister(element: OnBackPressedListener) {
        onBackPressedListenerRegistry.remove(element)
    }
    //endregion

    fun doNavigateUp(): Boolean {
        return onBackPressedListenerRegistry.none { !it.onBackPressed() }
    }
}
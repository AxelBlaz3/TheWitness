package com.wielabs.thewitness.ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.PopupMenuCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.wielabs.thewitness.R
import com.wielabs.thewitness.databinding.ActivityMainBinding
import com.wielabs.thewitness.ui.home.HomeFragmentDirections
import com.wielabs.thewitness.ui.news.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var newsViewModel: NewsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    private val inputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val popupMenu by lazy {
        PopupMenu(this@MainActivity, binding.overflowMenu).apply {
            menu.add(getString(R.string.logout)).setOnMenuItemClickListener {
                signOut()
                true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        navController.addOnDestinationChangedListener(this)

        setupClickListeners()
    }

    override fun onBackPressed() {
        navController.currentDestination?.let { currentDestination ->
            if (currentDestination.id == R.id.newsDetailFragment)
                mainActivityViewModel.currentPosition = 0
        }
        super.onBackPressed()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        binding.run {
            isSignIn = destination.id == R.id.signInFragment
            updateMenuIconAndClickListener(isSearchFragment = destination.id == R.id.searchFragment)
            when (destination.id) {
                R.id.signInFragment -> {

                }
                R.id.homeFragment -> {
                    hideSearch = false
                    setToolbarScrollFlags(supportsScrolling = true)
                    setToolbarNavigationIcon(isNavigationIconVisible = false)
                }
                R.id.newsDetailFragment -> {
                    hideSearch = true
                    setToolbarScrollFlags(supportsScrolling = false)
                    setToolbarNavigationIcon(isNavigationIconVisible = true)
                }
                R.id.searchFragment -> {
                    hideSearch = false
                    setToolbarNavigationIcon(isNavigationIconVisible = false)
                }
            }
        }
    }

    private fun setToolbarNavigationIcon(isNavigationIconVisible: Boolean) {
        binding.toolbar.navigationIcon =
            if (isNavigationIconVisible)
                ContextCompat.getDrawable(this, R.drawable.ic_outline_arrow_back_24)
            else
                null
    }

    private fun setToolbarScrollFlags(supportsScrolling: Boolean) {
        (toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags = if (supportsScrolling)
            (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        else
            0
    }

    private fun setupClickListeners() {
        binding.run {
            toolbar.setNavigationOnClickListener { navController.navigateUp() }
            searchEditText.setOnFocusChangeListener { _, isFocused ->
                if (isFocused && navController.currentDestination?.id != R.id.searchFragment)
                    navController.navigate(HomeFragmentDirections.actionGlobalSearchFragment())
            }
            overflowMenu.setOnClickListener {
                popupMenu.show()
            }
        }
    }

    private fun updateMenuIconAndClickListener(isSearchFragment: Boolean) {
        binding.run {
            if (isSearchFragment)
                navMenu.apply {
                    setImageResource(R.drawable.ic_outline_arrow_back_24)
                    setOnClickListener {
                        hideKeyboard()
                        searchEditText.clearFocus()
                        navController.navigateUp()
                    }
                }
            else
                navMenu.setImageDrawable(null)
        }
    }

    fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    fun signOut() {
        googleSignInClient.signOut().apply {
            addOnCompleteListener {
                mainActivityViewModel.deleteSession()
            }
            addOnFailureListener {
                Snackbar.make(binding.root, getString(R.string.logout_error), Snackbar.LENGTH_SHORT).apply {
                    setAction(getString(R.string.retry)) { signOut() }
                    show()
                }
            }
        }
    }
}
package com.github.pavelzhurman.fsplayer.ui.main


import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.pavelzhurman.core.base.BaseActivity
import com.github.pavelzhurman.fsplayer.R
import com.google.android.material.navigation.NavigationView


class MainActivity : BaseActivity<MainViewModel>() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun initObservers(viewModel: MainViewModel) {}

    override fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_main_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        navigationView.setupWithNavController(navController)

        setSupportActionBar(findViewById(R.id.toolbar_main))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open,
                R.string.close
            )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override val viewModelClass: Class<MainViewModel> = MainViewModel::class.java
    override val layout: Int = R.layout.activity_main

}
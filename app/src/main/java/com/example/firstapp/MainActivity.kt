package com.example.firstapp

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.firstapp.book_corner.data.BookRepository
import com.example.firstapp.book_corner.data.local.BookDatabase
import kotlinx.android.synthetic.main.activity_main.*
import com.example.firstapp.core.*;
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var mSnackBar: Snackbar? = null

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")

        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        // to enable back button from toolbar
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        appBarConfiguration =
            AppBarConfiguration.Builder(
                R.id.login_fragment,
                R.id.item_list_fragment,
                R.id.item_edit_fragment,
                R.id.chart_fragment)
                .build()
        val navController: NavController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_chart -> {
            findNavController(R.id.nav_host_fragment).navigate(R.id.chart_fragment)
            true
        }
        R.id.action_logout -> {
            findNavController(R.id.nav_host_fragment).navigate(R.id.login_fragment)
            true;
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    // for back button from toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showMessage(isConnected: Boolean) {
        if (!isConnected) {

            if (mSnackBar != null) {
                mSnackBar?.dismiss()
            }
            val messageToUser = "You are offline now." //TODO

            mSnackBar = Snackbar.make(
                findViewById(R.id.rootLayout),
                messageToUser,
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.

            mSnackBar?.duration = 3000
            mSnackBar?.show()
        } else {
            if (mSnackBar != null) {
                mSnackBar?.dismiss()
            }
            val messageToUser = "You are online now." //TODO

//            BookRepository.checkDifferences();

            mSnackBar = Snackbar.make(
                findViewById(R.id.rootLayout),
                messageToUser,
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            mSnackBar?.duration = 3000
            mSnackBar?.show()
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}

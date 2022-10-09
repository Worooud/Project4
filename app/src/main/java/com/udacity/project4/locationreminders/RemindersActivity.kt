package com.udacity.project4.locationreminders

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout -> {
            AuthUI.getInstance().signOut(this)
            goToLogin()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser==null){
           goToLogin()
        }

    }

    fun goToLogin(){
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }
}

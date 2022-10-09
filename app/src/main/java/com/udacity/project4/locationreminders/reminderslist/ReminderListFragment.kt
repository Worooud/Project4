package com.udacity.project4.locationreminders.reminderslist

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {

    private val TAG = ReminderListFragment::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    private var PermissionsGranted = false



    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }

        _viewModel.showLoading.observe(viewLifecycleOwner, Observer{
            binding.refreshLayout.isRefreshing = it
        })

        _viewModel.showToast.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        checkPermissions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        setupRecyclerView()

        binding.addReminderFAB.setOnClickListener {
            PermissionsGranted = requireActivity().hasBaseLocationPermissions()
            if(PermissionsGranted){
                if(gotAllLocationPermissions()){
                    navigateToAddReminder()
                } else {
                    requireActivity().showPermissionSnackBar(binding.root)
                }
            } else {
                requireActivity().requestLocationPermissions()
            }

        }
    }


    private fun checkPermissions(resolve:Boolean = true) {
        requireActivity().LocationRequest(resolve).addOnSuccessListener {
            PermissionsGranted = true
        }
    }
   fun gotAllLocationPermissions(): Boolean {
        return requireActivity().gotBaseLocationPermissions() && gotAndroidQPermissions(requireActivity()) && gotAndroidRPermissions(requireActivity())
    }







    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                 AuthUI.getInstance().signOut(requireContext())
                 goToLogin()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    fun goToLogin(){
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }







}

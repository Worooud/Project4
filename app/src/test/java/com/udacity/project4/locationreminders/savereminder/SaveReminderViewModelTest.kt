package com.udacity.project4.locationreminders.savereminder

import MainCoroutineRule
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val fakeDataSource = FakeDataSource()

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun createViewModel(){
        stopKoin()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }


    @Test
    fun check_loading(){
        val reminderDataItem = ReminderDataItem(
            "Cairo",
            "Madinat Nasr",
            "Egypt",
            30.05090,
            31.34677,
            "1"
        )
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminderDataItem)
        var showLoading = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoading, `is`(true))
        mainCoroutineRule.resumeDispatcher()
        showLoading = saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(showLoading, `is`(false))
    }

    @Test
    fun check_toast(){
        val reminderDataItem = ReminderDataItem(
            "Cairo",
            "Madinat Nasr",
            "Egypt",
            30.05090,
            31.34677,
            "2"
        )
        saveReminderViewModel.saveReminder(reminderDataItem)
        val showToast = saveReminderViewModel.showToast.getOrAwaitValue()
        assertThat(showToast, `is`("Reminder Saved !"))
    }

    @Test
    fun check_navigation(){
        val reminderDataItem = ReminderDataItem(
            "Cairo",
            "Madinat Nasr",
            "Egypt",
            30.05090,
            31.34677,
            "2"
        )
        saveReminderViewModel.saveReminder(reminderDataItem)
        val navigate = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        navigate as NavigationCommand
        assertThat(navigate, instanceOf(NavigationCommand.Back::class.java))
    }

    @Test
    fun shouldReturnError_noTitle() {
        val reminderDataItem = ReminderDataItem(
            null,
            "Capital of Egypt",
            "Cairo",
            30.05090,
            31.34677,
            "2"
        )
        assertThat(saveReminderViewModel.validateEnteredData(reminderDataItem), `is`(false))
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
    }

    @Test
    fun shouldReturnError_noLocation() {
        val reminderDataItem = ReminderDataItem(
            "Cairo",
            "Capital of Egypt",
            null,
            30.05090,
            31.34677,
            "2"
        )
        assertThat(saveReminderViewModel.validateEnteredData(reminderDataItem), `is`(false))
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
    }

}
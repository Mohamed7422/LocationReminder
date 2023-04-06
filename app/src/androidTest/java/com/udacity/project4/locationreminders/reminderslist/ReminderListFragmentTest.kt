package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.get
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest(){





    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var repo: ReminderDataSource
    private lateinit var appContext: Application

    /*
      Use Koin as a Service Locator lib ,And also to test
      our code, so we will initialize Koin related code to use it in testing.
     */
    @Before
    fun init() {
        //stop the original app koin
        stopKoin()
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repo = get()

        //clear the data to start fresh
        runBlocking {
            repo.deleteAllReminders()
        }
    }

    //    TODO: test the navigation of the fragments.

          @Test
          fun clickAddReminderFAB_navigateToSaveReminderFrg(){

              val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)
              val navController = mock(NavController::class.java)
              scenario.onFragment{
               Navigation.setViewNavController(it.view!!,navController)
              }

             onView(withId(R.id.addReminderFAB)).perform(click())
             verify(navController).navigate(
                 ReminderListFragmentDirections.toSaveReminder()
             )

          }
    // testing the displayed data on the UI.

    @Test
    fun reminders_AppendInUI(): Unit = runBlocking {

        val reminder = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)

        repo.saveReminder(reminder)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(reminder.title))
                )
            )
    }

    // adding testing for the error messages.
    @Test
    fun onUI_noDataDisplayed(): Unit = runBlocking {
        val reminder = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)

        repo.saveReminder(reminder)

        repo.deleteAllReminders()

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        Espresso.onView(ViewMatchers.withId(R.id.noDataTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}
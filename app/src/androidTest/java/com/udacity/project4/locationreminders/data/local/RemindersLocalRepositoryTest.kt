package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
// Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database:RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository


    @Before
    fun init_dataBase(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext()
            ,RemindersDatabase::class.java).allowMainThreadQueries().build()
        remindersLocalRepository = RemindersLocalRepository(database.reminderDao(),Dispatchers.Main)

    }

    @After
    fun cleanUp(){
        database.close()
    }

    @Test
    fun insertReminder_andRetrieveReminderById() = runBlocking {

        val reminder = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        remindersLocalRepository.saveReminder(reminder)

        val result  = remindersLocalRepository.getReminder(reminder.id)
        //Then verify that both data is the same
        assertThat(result  is Result.Success,`is`(true))
    }

    @Test
    fun removeRemindersList_EmptyList() = runBlocking {
        val reminder = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        remindersLocalRepository.saveReminder(reminder)

        remindersLocalRepository.deleteAllReminders()
        val result  = remindersLocalRepository.getReminders()
        assertThat(result  is Result.Success, `is`(true))
        result  as Result.Success

        assertThat(result .data, `is`(emptyList()))
    }

    @Test
    fun getReminderById_ReturningError() = runBlocking {

        val reminder = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        remindersLocalRepository.saveReminder(reminder)

        remindersLocalRepository.deleteAllReminders()

        //when returning a specific item in the data
        var result = remindersLocalRepository.getReminder(reminder.id)
        assertThat(result is Result.Error,`is`(true))
        result as Result.Error

        assertThat(result.message,`is`("Reminder not found!"))

    }

}
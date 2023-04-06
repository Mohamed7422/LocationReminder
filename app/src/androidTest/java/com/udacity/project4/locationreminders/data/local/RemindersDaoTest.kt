package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //Adding testing implementation to the RemindersDao.kt

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var db: RemindersDatabase

    @Before
    fun initDataBase(){
        // Using an in-memory database so that the information stored here disappears when the // process is killed.
        db = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = db.close()

    //Inserting and retrieving data using DAO
    @Test
    fun getAllRemindersListFromDataBase() = runBlockingTest{
        val reminderDTO1 = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        val reminderDTO2 = ReminderDTO("CollegeGym","do exercises","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        val reminderDTO3 = ReminderDTO("CollegePlayGround","playing football","SmartVillageOctober",30.081975976141145, 31.017891081549394)

        db.reminderDao().saveReminder(reminderDTO1)
        db.reminderDao().saveReminder(reminderDTO2)
        db.reminderDao().saveReminder(reminderDTO3)

        val remindersList =   db.reminderDao().getReminders()
        assertThat(remindersList, CoreMatchers.`is`(notNullValue()))
    }

    @Test fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN - Insert a task.
        val reminder = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        db.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by id from the database.
        val loaded = db.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    //predictable errors messages like data not found.
    @Test
    fun insertReminders_clearingAllDataAndShowErrorIfNull() = runBlockingTest {
        val reminderDTO1 = ReminderDTO("College","attending the lectures","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        val reminderDTO2 = ReminderDTO("CollegeGym","do exercises","SmartVillageOctober",30.081975976141145, 31.017891081549394)
        val reminderDTO3 = ReminderDTO("CollegePlayGround","playing football","SmartVillageOctober",30.081975976141145, 31.017891081549394)

        db.reminderDao().saveReminder(reminderDTO1)
        db.reminderDao().saveReminder(reminderDTO2)
        db.reminderDao().saveReminder(reminderDTO3)
        db.reminderDao().deleteAllReminders()

        val reminderList = db.reminderDao().getReminders()
        assertThat(reminderList, `is`(emptyList()))

    }
}
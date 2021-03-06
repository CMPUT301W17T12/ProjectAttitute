/*
 * MIT License
 *
 * Copyright (c) 2017 CMPUT301W17T12
 * Authors rsauveho vuk bfleyshe henrywei cs3
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.projectattitude.projectattitude;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.projectattitude.projectattitude.Activities.CreateMoodActivity;
import com.projectattitude.projectattitude.Activities.EditMoodActivity;
import com.projectattitude.projectattitude.Activities.LoginActivity;
import com.projectattitude.projectattitude.Activities.MainActivity;
import com.projectattitude.projectattitude.Activities.ViewMoodActivity;
import com.robotium.solo.Solo;

/**
 * Created by Boris on 12/03/2017.
 *
 * This class starts in the login screen, logs in and attempts to do a series of tests involving
 * making a mood, editing, deleting and other various functions off of the MainActivity.
 *
 * Now holds a suite of tests for testing most functionality
 * @version 0.9
 */

public class MainActivityUITest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;

    //These two handle the location on screen of the fab menu
    final int FAB_X = 890;
    final int FAB_Y = 1664;
    public MainActivityUITest() {
        super(com.projectattitude.projectattitude.Activities.LoginActivity.class);
    }

    /**
     * left as is
     * @throws Exception
     */
    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Left as is
     * @throws Exception
     */
    public void testStart() throws Exception {
        Activity activity = getActivity();
    }


    /**
     * Logs in, creates a happy mood then checks if it exists before deleting
     * Then checks to make sure delete works
     */
    public void testCreateMood(){
        Log.d("Intents", solo.getCurrentActivity().toString());
        if (solo.searchText("Moods")){
            logOut();
        }
        logIn(solo);
        checkMoods();
        createHappy(solo);
        assertTrue(solo.searchText("Happiness"));   //checking if mood exist

        deleteFirstMood();

        assertFalse(solo.searchText("Happiness"));
        logOut();
    }

    /**
     * Making sure if a mood is created, it sticks around and has persistence
     */
    public void testPersistence(){
        if (solo.searchText("Moods")){
            logOut();
        }
        logIn(solo);
        createHappy(solo);
        assertTrue(solo.searchText("Happiness"));   //checking if mood exist
        logOut();
        logIn(solo);
        assertTrue(solo.searchText("Happiness"));   //checking if mood exist
        deleteFirstMood();
        logOut();

    }

    /**
     * Logs in, creates a happy mood then changes it to angry, deletes after
     */
    public void testEditMood(){
        logIn(solo);
        createHappy(solo);

        assertTrue(solo.searchText("Happiness"));   //checking if mood exist

        solo.clickLongInList(0);
        assertTrue(solo.searchText("Edit")); //Make sure edit is there
        solo.clickOnText("Edit");

        solo.assertCurrentActivity("Wrong activity", EditMoodActivity.class); //Ensure activity changed

        // editing happy mood to angry mood
        assertTrue(solo.searchText("Happiness"));
        solo.clickOnText("Happiness");
        assertTrue(solo.searchText("Anger"));
        solo.clickOnText("Anger");

        //Change situation
        assertTrue(solo.searchText("Alone"));
        solo.clickOnText("Alone");//Edit the emotional state
        solo.clickOnText("With a crowd");
        assertTrue(solo.searchText("With a crowd"));


        assertTrue(solo.searchButton("Save"));
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.saveButton));

        //Ensure save still works
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);   // checking if mood changed
        assertTrue(solo.searchText("Anger"));
        assertFalse(solo.searchButton("Happiness"));
        deleteFirstMood();
        logOut();
    }

    /**
     * Makes sure you can see everything you want in view mood
     */
    public void testViewMood(){ //Making sure view mood works
        logIn(solo);
        createHappy(solo);

        solo.clickInList(0);

        solo.assertCurrentActivity("Wrong activity", ViewMoodActivity.class);
        //Make sure the proper fields come up
        assertTrue(solo.searchText("Happiness"));
        assertTrue(solo.searchText("Alone"));
        solo.goBack();
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
        deleteFirstMood();
        assertFalse(solo.searchText("Happiness"));
        logOut();
    }

    /**
     * Tests to make sure the date and filtering by date works
     */
    public void testFilterByDay(){
        if (solo.searchText("Moods")){
            logOut();
        }
        logIn(solo);
        createHappy(solo);

        createHappy(solo);  //creating second mood

        solo.clickLongInList(1);
        assertTrue(solo.searchText("Edit"));
        solo.clickOnText("Edit");
        solo.assertCurrentActivity("Wrong activity", EditMoodActivity.class);
        assertTrue(solo.searchText("Happiness"));
        solo.clickOnText("Happiness");

        assertTrue(solo.searchText("Anger"));
        solo.clickOnText("Anger");  //changing to anger emotion to distinguish

        solo.clickOnText("2017");
        solo.setDatePicker(0, 2017, 3, 17);

        solo.clickOnText("OK");

        assertTrue(solo.searchText("Save"));
        solo.clickOnText("Save"); //Probably clicks on the save location
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.saveButton));

        solo.clickOnImageButton(0);
        solo.clickOnText("Filter By Time");
        solo.clickOnText("Day");

        assertTrue(solo.searchText("Happiness"));
        assertFalse(solo.searchText("Anger"));

        // clean up
        deleteFirstMood();
        solo.clickOnImageButton(0);
        solo.clickOnText("Clear Filters");
        deleteFirstMood();
        logOut();
    }

    /**
     * Makes sure filtering by moods works
     */
    public void testFilterMood() throws InterruptedException {
        if (solo.searchText("Moods")){
            logOut();
        }
        logIn(solo);
        createHappy(solo);

        createHappy(solo);  // creating a second mood as happy and editing it to be angry
        solo.clickLongInList(1);
        solo.clickOnText("Edit");
        solo.clickOnText("Happiness");
        solo.clickOnText("Anger");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.saveButton));

        solo.clickOnImageButton(0);
        solo.clickOnText("Emotions");
        solo.clickOnScreen(677,450);

        assertTrue(solo.searchText("Anger"));   // only anger should be present
        assertFalse(solo.searchText("Happiness"));


        // clean up
        deleteFirstMood();
        solo.clickOnImageButton(0);
        solo.clickOnText("Clear Filters");
        deleteFirstMood();
        logOut();
    }

    /**'
     * deletes the first mood in the list
     */
    public void deleteFirstMood(){ //deletes the first mood in the list
        solo.clickLongInList(0);
        assertTrue(solo.searchText("Delete"));
        solo.clickOnText("Delete");
    }
    //TODO Maybe make a method to clean out the list fully?

    /**
     * logs in tester
     * @param solo
     */
    public void logIn(Solo solo){ //Logs in with tester account
        solo.enterText((EditText)solo.getView(R.id.usernameField), "tester");
        solo.clickOnView(solo.getView(R.id.signInButton));
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
    }

    /**
     * Logs in tester2
     */
    public void logInTest2(){
        solo.enterText((EditText)solo.getView(R.id.usernameField), "tester2");
        solo.clickOnView(solo.getView(R.id.signInButton));
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

    }

    /**
     * Checks to make sure all the options are there when making moods
     */
    public void checkMoods(){ //Makes sure the minimum fields exist within a mood
        solo.clickOnScreen(890, 1664); //The location of the fab button
        solo.clickOnView(solo.getView(R.id.fabAddMood));
        solo.assertCurrentActivity("Wrong activity", CreateMoodActivity.class);

        assertTrue(solo.searchText("Select an emotional state"));
        solo.clickOnText("Select an emotional state");

        //Tests to make sure the required moods exist
        assertTrue(solo.searchText("Anger"));
        assertTrue(solo.searchText("Confusion"));
        assertTrue(solo.searchText("Disgust"));
        assertTrue(solo.searchText("Fear"));
        assertTrue(solo.searchText("Happiness"));
        assertTrue(solo.searchText("Sadness"));
        assertTrue(solo.searchText("Shame"));
        assertTrue(solo.searchText("Surprise"));
        solo.clickOnText("Happiness");

        //Testing social situations
        solo.clickOnText("Select a social situation");
        assertTrue(solo.searchText("Alone"));
        assertTrue(solo.searchText("With one other person"));
        assertTrue(solo.searchText("With two to several people"));
        assertTrue(solo.searchText("With a crowd"));
        solo.clickOnText("Alone");


        assertTrue(solo.searchButton("Save"));  //saving mood
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.saveButton));
        deleteFirstMood();

    }

    /**
     * This test checks if a users name pops up in the profile screen
     * Also checks that the latest mood is there and correct
     */
    //TODO: Add following moods
    public void testProfile(){
        if (solo.searchText("Moods")){
            logOut();
        }
        logIn(solo);
        createHappy(solo);
        solo.clickLongInList(0);
        solo.clickOnText("Edit");
        solo.clickOnText("Happiness");
        solo.clickOnText("Disgust");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.saveButton));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnScreen(890, 1664); //The location of the fab button
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.fabProfile));
        assertTrue(solo.searchText("tester")); //Checks for the user name
        assertTrue(solo.searchText("Disgust")); //Checks most recent mood
        solo.goBack();
        deleteFirstMood();//Clean up
        logOut();
    }

    /**
     * This tests to make sure following works
     * Due to the inability to remove followers it assumes tester follows tester2 already
     */
    public void testFollow(){
        //This chunk creates a disgust mood in the person we intend to follow
        if (solo.searchText("Moods")){
            logOut();
        }
        logInTest2();
        createHappy(solo);
        solo.clickLongInList(0);
        solo.clickOnText("Edit");
        solo.clickOnText("Happiness");
        solo.clickOnText("Disgust");
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.saveButton));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        logOut();
        //tester2 is logged out
        //tester now logs in back in
        logIn(solo);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.moodToggle));
        assertTrue(solo.searchText("tester2")); // Checks to see if the mood appears after following
        logOut();
        //Test is now done, clean up time
        logInTest2();
        deleteFirstMood();
        logOut();


    }

    /**
     * Creates a happy mood to use for testing
     * @param solo solo is a global, dont really need it, should refactor it out
     */
    public void createHappy(Solo solo){ //Creates a happy mood and returns to main
        solo.clickOnScreen(890, 1664); //This is the location of the fab button
        solo.clickOnView(solo.getView(R.id.fabAddMood));
        solo.assertCurrentActivity("Wrong activity", CreateMoodActivity.class);

        //Set mood to happy
        assertTrue(solo.searchText("Select an emotional state"));
        solo.clickOnText("Select an emotional state");
        solo.clickOnText("Happiness");

        //Set trigger to Test
        solo.enterText((EditText) solo.getCurrentActivity().findViewById(R.id.triggerField), "Test");
        assertTrue(solo.searchText("Test"));

        //Set situation to Alone
        solo.clickOnText("Select a social situation");
        solo.clickOnText("Alone");

        assertTrue(solo.searchButton("Save"));  //saving mood
        solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.saveButton));

        solo.assertCurrentActivity("Wrong activity", MainActivity.class);
    }

    /**
     * clicks on the log out button
     */
    public void logOut(){
        solo.clickOnScreen(890, 1664);
        solo.clickOnView(solo.getView(R.id.fabLogout));
    }


    /**
     * I left this as is
     * @throws Exception
     */
    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}

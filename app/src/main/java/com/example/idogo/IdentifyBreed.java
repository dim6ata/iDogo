package com.example.idogo;

//Copyright 2020 dim6ata
/*
 * REFERENCES
 *
 * https://www.youtube.com/watch?time_continue=411&v=urQp7KsQhW8&feature=emb_logo - spinner tutorial (Coding Demos, 2016).
 * https://www.tutorialspoint.com/how-to-make-a-countdown-timer-in-android - timer tutorial (Yadav, 2019).
 *
 * */

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import static android.view.View.VISIBLE;
import static com.example.idogo.MainActivity.breedNameList;
import static com.example.idogo.MainActivity.dogoMap;

/**
OVERVIEW:
IdentifyBreed class allows user to see a randomly generated image of a dog and select a breed name from a dropdown list.
When submit button pressed, the program will compare user's answer to correct value and display "Correct" or "Wrong" and also show the correct breed name on screen.
There is an additional level with a timer, which is enabled from the parent activity (MainActivity) switch button.
When the timer runs out and nothing or wrong answer is selected, automated "Wrong!" gets displayed on screen. Similarly if correct answer give it displays "Correct".
The program takes care of rotation of screen as well as going in and out of the app.
 @author dim6ata
**/
public class IdentifyBreed extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Intent intent;
    private static String imageFile;
    private String selectedBreed;
    private static String currentBreed;
    private ImageView image;
    private TextView tv, tv2;
    private TextView countTime;
    private Button button;
    private boolean flag = true;
    private Spinner spinner;
    private Boolean boolTimer;
    protected static int counter;
    private CountDownTimer countDownTimer;
    private static int resourceId;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_breed);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_dogo);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Section that deals with screen rotation:
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (savedInstanceState != null) {

            initialiseVariables();


            tv.setVisibility(savedInstanceState.getInt("tvVisibility"));
            tv.setText(savedInstanceState.getString("tvValue"));
            countTime.setVisibility(savedInstanceState.getInt("countTimeVisibility"));
            tv2.setVisibility(savedInstanceState.getInt("tv2Visibility"));
            tv2.setText(savedInstanceState.getString("textTv2"));
            spinner.setEnabled(savedInstanceState.getBoolean("spinnerEnabled"));
            selectedBreed = savedInstanceState.getString("selectedBreed");
            currentBreed = savedInstanceState.getString("currentBreed");
            button.setEnabled(savedInstanceState.getBoolean("buttonEnabled"));
            button.setText(savedInstanceState.getString("buttonString"));
            flag = savedInstanceState.getBoolean("flag");


            if (flag) {//this checks if any attempts have been made in guessing
                button.setBackground(getResources().getDrawable(R.drawable.round_button_sml6));
            }


            if (boolTimer && counter > 0) {


                if (tv.getVisibility() == View.INVISIBLE) {//checks if time is still running: if the Correct sign is on then time cannot be on.
                    startTimer((counter - 1) * 1000);
                } else {
                    //in case time is stopped sets the countTime to a set value of when it has been stopped.
                    countTime.setText(savedInstanceState.getString("countTimeText"));

                }

            }


            if (tv.getVisibility() == View.VISIBLE) {


                if (tv.getResources().getText(R.string.correct).equals(tv.getText().toString())) {
                    //for cases when correct is displayed on screen.

                    tv.setBackground(getResources().getDrawable(R.drawable.round_button_sml2));

                    button.setBackground(getResources().getDrawable(R.drawable.round_button_sml4));


                } else {
                    //for cases when wrong is displayed

                    tv.setBackground(getResources().getDrawable(R.drawable.round_button_sml3));

                    button.setBackground(getResources().getDrawable(R.drawable.round_button_sml6));

                }


            }//end if result is visible


            image.setImageResource(resourceId);

        } else {

            //first time activity is loaded
            loadActivity();

        }
    }//end onCreate()


    /**
     * INITIALISE VARIABLES: method that initialises variables. Used every time an activity is loaded or screen rotated
     */
    public void initialiseVariables() {

        intent = getIntent();
        countTime = findViewById(R.id.countdown_id);
        boolTimer = getIntent().getExtras().getBoolean("booleanTimer");
        image = findViewById(R.id.image_id);
        tv = findViewById(R.id.text_correct_id);
        tv2 = findViewById(R.id.text_breed_id);
        button = (Button) findViewById(R.id.breed_button_id);
        spinner = findViewById(R.id.spinner_id);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//      spinner.setPrompt("Select breed:");
        adapter.add("...");
        adapter.addAll(breedNameList);
        spinner.setAdapter(adapter);
        spinner.setBackground(getResources().getDrawable(R.drawable.round_button_sml1));
        spinner.setOnItemSelectedListener(this);

    }


    /**
     * START TIMER: method that creates a timer if switch in main activity is enabled.
     * @param startingTime
     */
    public void startTimer(long startingTime) {


        if (boolTimer) {//only runs if switch button in MainActivity is on.


            countTime.setVisibility(VISIBLE);
            countDownTimer = new CountDownTimer(startingTime, 1000) {
                //Parameter startingTime is used when a new level starts, or when there is a screen rotation and the timer has to be reset from current value of counter.
                //the interval 1000 means that each interval will be exactly 1 second = 1000 milliseconds long.
                @Override
                public void onTick(long millisUntilFinished) {
                    countTime.setText(String.valueOf(counter));//visualises the value of counter.
                    counter--;
                }

                @Override
                public void onFinish() {

                    submitProcedure();
                    countTime.setVisibility(View.INVISIBLE);

                }
            }.start();
        } else {
            countTime.setVisibility(View.INVISIBLE);

        }

    }//end startTimer()

    /**
     * LOAD ACTIVITY: used to populate activity
     */
    private void loadActivity() {

        initialiseVariables();

        counter = 10;
        startTimer(counter * 1000);

        currentBreed = breedNameList.get(randomNumGenerator(0, breedNameList.size() - 1));//randomly generates a breed
        //randomly generates a file name from Map. It looks through ArrayList that belongs to the key value of currentBreed:
        imageFile = dogoMap.get(currentBreed).get(randomNumGenerator(0, dogoMap.get(currentBreed).size() - 1));
        resourceId = getResources().getIdentifier(imageFile, "drawable", "com.example.w1696151_cwk1");
        image.setImageResource(resourceId);


        tv.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);

        button.setText(getResources().getText(R.string.submit_button));
        button.setBackground(getResources().getDrawable(R.drawable.round_button_sml6));

        spinner.setEnabled(true);

    }//end loadActivity()


    /**
     * ON BUTTON CLICK: method that deals with Submit/Next Button clicks
     * @param view
     */
    public void onSubmit(View view) {

        submitProcedure();

    }//end onSubmit()

    /**
     * SUBMISSION PROCEDURE: method that is called either when submit button clicked or when time runs out.
     */
    public void submitProcedure() {

        if (flag) {//this is enabled when the submit button is pressed

            if (selectedBreed.equals(currentBreed)) {//in the case when the answer is correct

//                tv.setWidth(image.getWidth());
                tv.setText(getResources().getText(R.string.correct));
                tv.setBackground(getResources().getDrawable(R.drawable.round_button_sml2));
                tv.setVisibility(VISIBLE);
                button.setBackground(getResources().getDrawable(R.drawable.round_button_sml4));


            } else {//in the case when answer is incorrect

//                tv.setWidth(image.getWidth());
                tv.setText(getResources().getText(R.string.incorrect));
                tv.setBackground(getResources().getDrawable(R.drawable.round_button_sml3));
                tv.setVisibility(VISIBLE);
                button.setBackground(getResources().getDrawable(R.drawable.round_button_sml6));

            }

            //change conditions after answer:
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            spinner.setEnabled(false);
            button.setText(getResources().getText(R.string.next_button));
            tv2.setText(getResources().getText(R.string.correct_breed) + " " + currentBreed); //sets the correct breed name display after answer is given.
            tv2.setVisibility(VISIBLE);
            flag = false;

        } else {//when next button is pressed

            loadActivity();
            flag = true;
        }

    }//end submitProcedure

    /**
     * ON SPINNER SELECTION: sets text to whatever user has selected in spinner
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedBreed = parent.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    /**
     * RANDOM NUMBER GENERATOR: method that returns a random number between two values
     * @param min
     * @param max
     * @return
     */
    public static int randomNumGenerator(int min, int max) {

        int randGen = max - ((int) Math.round((Math.random()) * (max - min)));
        return randGen;

    }//end randomNumGen...


    /**
     * ON SAVE: saves current state, which is used when rotating the screen.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        outState.putInt("countTimeVisibility", countTime.getVisibility());
        outState.putString("countTimeText", countTime.getText().toString());
        outState.putInt("tvVisibility", tv.getVisibility());
        outState.putString("tvValue", tv.getText().toString());
        outState.putBoolean("buttonEnabled", button.isEnabled());
        outState.putString("buttonString", button.getText().toString());
        outState.putInt("tv2Visibility", tv2.getVisibility());
        outState.putString("textTv2", tv2.getText().toString());
        outState.putBoolean("spinnerEnabled", spinner.isEnabled());
        outState.putString("selectedBreed", selectedBreed);
        outState.putString("currentBreed", currentBreed);
        outState.putBoolean("flag", flag);


    }//end onSave...


}//end IdentifyBreed Class


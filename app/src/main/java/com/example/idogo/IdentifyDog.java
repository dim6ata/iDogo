package com.example.idogo;
//Copyright 2020 dim6ata
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.VISIBLE;
import static com.example.idogo.MainActivity.breedNameList;
import static com.example.idogo.MainActivity.dogoMap;

/**
OVERVIEW:
IdentifyDog class allows user to select clickable images, in order to determine whether an image belongs to a requested breed type.
The program loads random images from 3 distinct breed types and it stores information about previous turn to avoid having same breeds appearing in the next level.
When the image is clicked the breed name of the image gets compared to the breed name of the correct answer.
If it is correct, a "Correct!" message gets displayed and the clicked image background colour gets changed to green.
Otherwise "Wrong!" is displayed and the clicked image background turns to grey.
The "Next" button colours are set to change from green when waiting for an answer to yellow, when the user wants to play again.
There is an additional level with a timer, which is enabled from the parent activity (MainActivity) switch button.
When the timer runs out and nothing is selected, automated "Wrong!" gets displayed on screen.
The program takes care of rotation of screen as well as going in and out of the app.
 @author dim6ata
* */
public class IdentifyDog extends AppCompatActivity {

    private ArrayList<String> currentBreedList;
    private ArrayList<String> previousBreedList;
    private Intent intent;
    private static String imageFile;
    private static String chosenBreed;
    private static int resourceId1, resourceId2, resourceId3;
    private ImageView image1, image2, image3;
    private TextView breedToSelect, result;
    private TextView countTime;
    private Boolean boolTimer;
    protected static int counter;//static variable used for the countdown timer in case a screen is rotated.
    private int currentSelection;
    private CountDownTimer countDownTimer;
    private LinearLayout l1, l2, l3;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_dog);

        //sets logo in action bar:
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_dogo);


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Section that deals with screen rotation:
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (savedInstanceState != null) {

            initialiseVariables();

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            //Restore states of variables saved before rotation:
            previousBreedList = new ArrayList<>();
            currentBreedList = savedInstanceState.getStringArrayList("currentList");
            previousBreedList = savedInstanceState.getStringArrayList("pastList");
            chosenBreed = savedInstanceState.getString("chosenBreed");
            currentSelection = savedInstanceState.getInt("currentSelection");
            breedToSelect.setText(savedInstanceState.getString("breedToSelect"));//check for errors
            nextButton.setEnabled(savedInstanceState.getBoolean("nextButtonEnabled"));
            result.setVisibility(savedInstanceState.getInt("resultVisible"));
            result.setText(savedInstanceState.getString("resultValue"));
            countTime.setVisibility(savedInstanceState.getInt("countTimeVisibility"));
            image1.setImageResource(resourceId1);
            image2.setImageResource(resourceId2);
            image3.setImageResource(resourceId3);

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            //Checks if timer was enabled and visible:
            if (boolTimer && counter > 0) {


                if (result.getVisibility() == View.INVISIBLE) {
                    startTimer((counter - 1) * 1000);//counter - 1 because the countdown timer has to continue from the next number not from the one it just displayed.
                } else {

                    countTime.setText(savedInstanceState.getString("countTimeText"));

                }

            }


            ///////////////////////////////////////////////////////////////////////////////////////////////////
            //sets colour to result box depending on whether it is correct or incorrect:
            if (result.getVisibility() == VISIBLE) {

                if (result.getText().toString().equals(R.string.correct)) {

                    //checks if the result displays correct
                    result.setBackground(getResources().getDrawable(R.drawable.round_button_sml2));

                } else {

                    //for cases when wrong is displayed
                    result.setBackground(getResources().getDrawable(R.drawable.round_button_sml3));

                }


                ///////////////////////////////////////////////////////////////////////////////////////////////////
                //in the cases when result is enabled, then I have to set the colour to the image that is clicked.
                //if none i.e currentSelection == 0, then it remains unchanged.
                if (currentSelection == 1) {

                    changeAnswerBackground(verifyAnswer(currentBreedList.get(0)), l1);
                } else if (currentSelection == 2) {

                    changeAnswerBackground(verifyAnswer(currentBreedList.get(1)), l2);

                } else if (currentSelection == 3) {

                    changeAnswerBackground(verifyAnswer(currentBreedList.get(2)), l3);
                }


            }//end if result is visible

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            //check if the button is enabled and set its colour accordingly
            if (!nextButton.isEnabled()) {

                nextButton.setBackground(getResources().getDrawable(R.drawable.round_button_sml6));

            } else {

                nextButton.setBackground(getResources().getDrawable(R.drawable.round_button_sml1));

            }

        } else {
            //first time an activity is ran.

            populateActivity();

        }
    }//end onCreate()


    /**
     * INITIALISE VARIABLES: method that initialises variables. Used every time an activity is loaded or screen rotated
     */
    public void initialiseVariables() {

        intent = getIntent();
        countTime = findViewById(R.id.countdown2_id);
        boolTimer = getIntent().getExtras().getBoolean("booleanTimer");
        breedToSelect = findViewById(R.id.label_dog_q);
        image1 = findViewById(R.id.image1_id);
        image2 = findViewById(R.id.image2_id);
        image3 = findViewById(R.id.image3_id);
        l1 = findViewById(R.id.lin1_id);
        l2 = findViewById(R.id.lin2_id);
        l3 = findViewById(R.id.lin3_id);
        result = findViewById(R.id.dog_correct_id);
        nextButton = findViewById(R.id.dog_button_id);
        currentBreedList = new ArrayList<String>();

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

                    countTime.setVisibility(View.INVISIBLE);
                    nextButton.setEnabled(true);
                    nextButton.setBackground(getResources().getDrawable(R.drawable.round_button_sml1));
                    verifyAnswer("wrong");


                }
            }.start();

        } else {
            countTime.setVisibility(View.INVISIBLE);
        }
    }//end startTimer


    /**
     * POPULATE ACTIVITY: method that initialises activity
     */
    private void populateActivity() {

        //initialise:
        initialiseVariables();


        counter = 10;//counter used for timer.

        startTimer(counter * 1000);//initial start of counter. Will only run if boolTimer is true.


        currentSelection = 0;//zero when nothing selected, 1,2,3 for respective images 1-3


        result.setVisibility(View.INVISIBLE);

        nextButton.setEnabled(false);
        nextButton.setBackground(getResources().getDrawable(R.drawable.round_button_sml6));


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Image setup:

        image1.setEnabled(true);
        image2.setEnabled(true);
        image3.setEnabled(true);

        //resets image background colour
        l1.setBackground(getResources().getDrawable(R.drawable.image_style));
        l2.setBackground(getResources().getDrawable(R.drawable.image_style));
        l3.setBackground(getResources().getDrawable(R.drawable.image_style));


        selectBreeds();//select 3 distinct breeds
        chosenBreed = currentBreedList.get(randomNumGenerator(0, currentBreedList.size() - 1));//choose 1 breed that the user will need to guess.
        breedToSelect.setText((getResources().getText(R.string.select_the)).toString() + "\n" + chosenBreed + "?");//modify user guidance text for each new breed asked.

        imageFile = dogoMap.get(currentBreedList.get(0)).get(randomNumGenerator(0, dogoMap.get(currentBreedList.get(0)).size() - 1));
        resourceId1 = getResources().getIdentifier(imageFile, "drawable", "com.example.w1696151_cwk1");
        image1.setImageResource(resourceId1);

        imageFile = dogoMap.get(currentBreedList.get(1)).get(randomNumGenerator(0, dogoMap.get(currentBreedList.get(1)).size() - 1));
        resourceId2 = getResources().getIdentifier(imageFile, "drawable", "com.example.w1696151_cwk1");
        image2.setImageResource(resourceId2);

        imageFile = dogoMap.get(currentBreedList.get(2)).get(randomNumGenerator(0, dogoMap.get(currentBreedList.get(2)).size() - 1));
        resourceId3 = getResources().getIdentifier(imageFile, "drawable", "com.example.w1696151_cwk1");
        image3.setImageResource(resourceId3);


    }//end populateActivity()


    /**
     * SELECT BREEDS: selects 3 different breeds to be displayed. Also checks against previous list of 3 to not repeat
     */
    private void selectBreeds() {
        /*
        currentBreedList gets populated with 3 distinct breed names.
        The check is against the elements of the array itself as well as the elements in the previous game which are stored in previousBreedList.
        * */

        String breed;

        while (currentBreedList.size() != 3) {//stops when the list has 3 elements.


            breed = breedNameList.get(randomNumGenerator(0, breedNameList.size() - 1));//randomly generates a breed.
            if (previousBreedList != null) { //runs to check against the contents of the previousBreedList.
                if (!previousBreedList.contains(breed) && !currentBreedList.contains(breed)) {
                    //only executed when the previousBreedList and currentBreedList do NOT contain the randomly generated breed name.
                    currentBreedList.add(breed);//item gets added to the list.
                }

            }//end if not null
            else {//takes care of the first time when there has not been a previous game and previousBreedList has not yet been populated.

                if (!currentBreedList.contains(breed)) {
                    //when the currentBreedList does not contain the selected breed, item gets added to the list:
                    currentBreedList.add(breed);
                }

            }

        }//end while loop

    }//end selectBreeds

    /**
     * VERIFY ANSWER: method that checks chosen answer against correct one
     * @param text
     * @return
     */
    private String verifyAnswer(String text) {


        if (countDownTimer != null) {//stops the countDownTimer if it is not null.
            countDownTimer.cancel();
        }

        image1.setEnabled(false);//prevents user to click images after answer is given
        image2.setEnabled(false);//----II-----
        image3.setEnabled(false);//----II-----

        result.setVisibility(View.VISIBLE);
        previousBreedList = new ArrayList<String>();//a new instance created for a new set of values for previousBreedList.
        previousBreedList = currentBreedList;//copies current list so that the next page is different from current.

        if (text.equals(chosenBreed)) {

            result.setText(getResources().getText(R.string.correct));
            result.setBackground(getResources().getDrawable(R.drawable.round_button_sml2));

            return result.getResources().getText(R.string.correct).toString();//returns a string with correct value to be used when changing background of clicked image.


        } else {

            result.setText(getResources().getText(R.string.incorrect));
            result.setBackground(getResources().getDrawable(R.drawable.round_button_sml3));

            return result.getResources().getText(R.string.incorrect).toString();//returns a string with wrong value to be used when changing background of clicked image.

        }

    }//end verifyAnswer

    /**
     * CHANGE ANSWER BACKGROUND: changes selected image background depending on whether answer is true or false.
     * @param answer
     * @param background
     */
    public void changeAnswerBackground(String answer, LinearLayout background) {

        if (answer.equals(result.getResources().getText(R.string.correct).toString())) {

            background.setBackground(getResources().getDrawable(R.drawable.image_correct));

        } else {

            background.setBackground(getResources().getDrawable(R.drawable.image_wrong));
        }

        nextButton.setEnabled(true);
        nextButton.setBackground(getResources().getDrawable(R.drawable.round_button_sml1));

    }


    /**
     * IMAGE CLICKS: methods that deal with each individual image click
     * @param view
     */
    public void clickIMG1(View view) {

        changeAnswerBackground(verifyAnswer(currentBreedList.get(0)), l1);
        currentSelection = 1;

    }

    public void clickIMG2(View view) {

        changeAnswerBackground(verifyAnswer(currentBreedList.get(1)), l2);
        currentSelection = 2;
    }

    public void clickIMG3(View view) {

        changeAnswerBackground(verifyAnswer(currentBreedList.get(2)), l3);
        currentSelection = 3;
    }

    /**
     * NEXT: listener method that captures when NEXT button is clicked
     * @param view
     */
    public void onNext(View view) {

        populateActivity();
    }


    /**
     * RANDOM NUMBER GENERATOR: method that returns a random number between two values
     * @param min
     * @param max
     * @return
     */
    public int randomNumGenerator(int min, int max) {

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
        outState.putStringArrayList("currentList", currentBreedList);
        outState.putStringArrayList("pastList", previousBreedList);
        outState.putInt("resultVisible", result.getVisibility());
        outState.putString("resultValue", result.getText().toString());
        outState.putBoolean("nextButtonEnabled", nextButton.isEnabled());//when enabled then colour is 4, otherwise 6.
        outState.putString("chosenBreed", chosenBreed);
        outState.putInt("currentSelection", currentSelection);//this will determine if one has been selected. then if it is correct will be green else gray.
        outState.putString("breedToSelect", breedToSelect.getText().toString());


    }//end onSave.

}//end class

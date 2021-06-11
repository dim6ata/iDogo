package com.example.idogo;

/*REFERENCES:
 * https://stackoverflow.com/questions/31921927/how-to-get-all-drawable-resources - how to put images in a list (Mauker, 2015).
 * https://stackoverflow.com/questions/4662215/how-to-extract-a-substring-using-regex - how to extract string using regex (Byers, 2011).
 * https://attacomsian.com/blog/capitalize-first-letter-of-string-java - how to capitalise first letters (Attacomsian, 2019).
 * https://stackoverflow.com/questions/27834317/convert-list-to-a-hasmap-of-string-arraylist - adding list to a map (Agola, 2015).
 * https://stackoverflow.com/questions/11278507/android-widget-switch-on-off-event-listener - how to implement a switch button event listener (Sam, 2012 (edited by AshesToAshes 2015)).
 * https://stackoverflow.com/questions/47063564/font-is-not-applying-on-checkbox-and-switch-android-studio-3 - how to set font to switch (Zolnai, 2017).
 * https://www.youtube.com/watch?v=4XEvSshOUAE - how to set icon to action bar (clientuser.net, 2015).
 *
 * * */

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Copyright 2020 dim6ata

/**
OVERVIEW:
MainActivity is a class that allows the user to select 3 different levels of game,
which are consequently expanded in IdentifyBreed, IdentifyDog and SearchDog classes.
It also has a switch button, which enables advanced timer levels in the individual games.
This is the main class where all the other classes are called from.
It is also the class where when run for the first time the files in drawable populate the breedNameList and dogoMap.
 @author dim6ata
*/
public class MainActivity extends AppCompatActivity {

    //map containing breed names as keys and list of corresponding files as values:
    protected static Map<String, ArrayList<String>> dogoMap = new HashMap<String, ArrayList<String>>();
    protected static ArrayList<String> breedNameList = new ArrayList<>();//list of breeds
    private static boolean flag = false;
    private Switch aSwitch;
    private static boolean switchCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_dogo);//adds logo to action bar


        if (!flag) {//helps populate files only once when program is loaded.
            populateLists();//calls the method that adds breed names and file names in drawable to breedNameList && dogoMap
            flag = true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Switch Setup:
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        aSwitch = findViewById(R.id.timer_switch);

        aSwitch.setTypeface(ResourcesCompat.getFont(this, R.font.fredericka_the_great));//changes font of switch.
        aSwitch.setTypeface(aSwitch.getTypeface(), Typeface.BOLD);//makes switch bold style


        if (switchCheck) {//condition that determines the state of the switch button
            aSwitch.setChecked(true);
        } else {

            aSwitch.setChecked(false);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Switch Listener: listener that sets the state of the switch button to true when on and false when off
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    switchCheck = true;

                } else {

                    switchCheck = false;

                }

            }
        });

    }//end onCreate

    /**
     * ON CLICK LISTENER IDENTIFY BREED:
     * @param view
     */
    public void onClickIDBreed(View view) {

        Intent intent = new Intent(this, IdentifyBreed.class);
        intent.putExtra("booleanTimer", switchCheck);//passes the boolean value that is generated by the switch button

        startActivity(intent);//starts Identify Breed activity

    }//end onClickIDBreed

    /**
     * ON CLICK LISTENER IDENTIFY DOG:
     * @param view
     */
    public void onClickIDDog(View view) {

        Intent intent = new Intent(this, IdentifyDog.class);
        intent.putExtra("booleanTimer", switchCheck);

        startActivity(intent);

    }//end onClickIDDog


    /**
     * ON CLICK LISTENER SEARCH DOG:
     * @param view
     */
    public void onClickSearchDog(View view) {

        Intent intent = new Intent(this, SearchDog.class);
        startActivity(intent);

    }//end onClickSearchDog


    /**
     * POPULATE MAP: Populate dogoMap with breed names as keys and array lists of file names as values.
     */
    public void populateLists() {

        int counter = 1;//used for last print
        String fileName;
        String breedName, tempName, tempFile = "";
        Field[] drawablesFields = R.drawable.class.getDeclaredFields();//creates an array of all files that are contained within drawable
        ArrayList<String> drawables = null;//a list that contains current breed's file names
        Pattern pattern = Pattern.compile("[^(dgo)][\\w]*[^.jpg0-9]");//regex pattern that is used to retrieve dog breed names from jpg dogo image files.
        Matcher matcher;

        try {

            for (Field field : drawablesFields) {

                if (field.getName().contains("dogo")) {//this avoids adding other files that are in drawable folder

                    fileName = field.getName();

                    matcher = pattern.matcher(fileName);//matches name of file to regex pattern that is set above.

                    if (matcher.find()) {//if the matcher is successful at finding a match the following is executed:

                        if (!tempFile.equals(matcher.group())) {
                            /*
                             Runs the first time around a new breed file name is found.
                             If the current file name that is held in the matcher does not match the file in
                             the temporary file (tempFile) means that we have come across a new breed and
                             therefore no more elements (file names) of the old breed are present.
                             The old breed can be added to the map as done below:
                             */

                            if (!breedNameList.isEmpty()) {//will run only when there is something added in breedNameList
                                /*
                                Populates the map with the breed name as keys and the entire arraylist of file names
                                corresponding to the breed in drawables as values.
                                */

                                dogoMap.put(breedNameList.get(breedNameList.size() - 1), drawables);


                            }

                            if (matcher.group().contains("_")) {
                                /*
                                This part is intended to extract and format the breed name from the file name segment
                                that was already picked by the matcher and regex pattern.
                                */

                                String[] temp = matcher.group().split("_");//splits string into an array every time it sees a dash (_).

                                if (temp.length > 2) {
                                    /*
                                    Checks if the breed name is composed of more than one word.
                                    Here it is > 2 because the regex pattern returns an example of "_afghan_hound" from original string "dogo_afghan_hound_1.jpg"
                                    this is split into 3 parts
                                    1. An empty string "" before the first _ (dash)
                                    2. First name(afghan) after the first _ and before the second _
                                    3. Second name(hound) after the second dash
                                    therefore the length of the split string will be == 3(or >2) only when the name of breed is composed of more than 1 word
                                    */

                                    breedName = temp[1].substring(0, 1).toUpperCase() +//makes every word start with a capital letter and adds final name to breedName
                                            temp[1].substring(1) + " " +
                                            temp[2].substring(0, 1).toUpperCase() +
                                            temp[2].substring(1);

                                } else {//breed names when they are made of one word

                                    breedName = temp[1].substring(0, 1).toUpperCase() + temp[1].substring(1); //starting from temp[1] because temp[0] is an "" as explained above.
                                }

                                breedNameList.add(breedName);//breedName gets added to list.
                                tempFile = matcher.group();//tempFile becomes the value of the current breed file
                                drawables = new ArrayList<>();//sets/resets drawables
                                drawables.add(fileName);//adds to drawables the file name of the new breed.
                            }

                        }//end if tempFile!=
                        else {

                            drawables.add(fileName);//adds fileName to drawables, every time the matcher value is the same with what is contained in tempFile

                        }

                    }//end if matcher.find

                }//end if dogo contains


                //last condition that adds to map the last breedName and drawables list.
                if (counter == drawablesFields.length) {
                    dogoMap.put(breedNameList.get(breedNameList.size() - 1), drawables);

                }
                counter++;//counter gets incremented at the end of every for loop
            }//end for loop

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//end populateLists


}//end main activity



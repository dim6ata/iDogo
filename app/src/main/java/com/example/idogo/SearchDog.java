package com.example.idogo;

//Copyright 2020 dim6ata
/*
 * REFERENCES:
 * https://stackoverflow.com/questions/1645209/how-can-i-filter-listview-data-when-typing-on-edittext-in-android - ideas on how to implement a text search (Mahesh, 2012).ŸŸ
 * https://stackoverflow.com/questions/14750330/hide-an-android-listview-until-search-string-is-entered - hide/show list view (Schubert, 2013).
 * https://stackoverflow.com/questions/8307844/display-mulitple-images-one-after-the-other-in-my-imageview - image switcher (Jave, 2011).
 * https://www.youtube.com/watch?v=V54largrb7E - how to hide the keyboard when needed (Coding Demos, 2018).
 *
 *  */


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import static com.example.idogo.MainActivity.breedNameList;
import static com.example.idogo.MainActivity.dogoMap;

/**
 OVERVIEW:
 SearchDog Class allows user to enter text in editText field, which then enables a listView drop down menu from which
 a dog breed can be selected. Once a breed is selected the submit button is enabled; when pressed the submit button
 a slideshow of randomly generated images of the chosen breed are displayed with interval of 5 seconds between each image.
 When stop is pressed - slideshow stops and editText box is enabled for another search.
 The program takes care of rotation of screen as well as going in and out of the app.
 @author dim6ata
* */
public class SearchDog extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Intent intent;
    private EditText editText;
    private static ListView listView;
    private TextWatcher textWatcher;
    private ArrayAdapter<String> adapter;
    private String searchedBreed;
    private static ImageView imageSwitcher, imageDogo;
    private static boolean flag;
    private static boolean btnStopBool, btnSubmitBool, textBoolean;
    private static int resource_id;
    private Button buttonSubmit, buttonStop;
    private LinearLayout linLayImage;
    //private static int counter = 0;//from older version kept for reference.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dog);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_dogo);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Section that deals with screen rotation
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (savedInstanceState != null) {

            instantiateVariables();

            //loads states of variables that were saved before rotation takes place:
            listView.setVisibility(savedInstanceState.getInt("listViewVisibility"));
            linLayImage.setVisibility(savedInstanceState.getInt("linLayVisibility"));
            imageSwitcher.setVisibility(savedInstanceState.getInt("imageVisibility"));
            imageDogo.setVisibility(savedInstanceState.getInt("imageDogoVisibility"));
            searchedBreed = savedInstanceState.getString("searchBreedKey");

            if (imageSwitcher.getVisibility() == View.VISIBLE) {

                imageSwitcher.setImageResource(resource_id);//sets image to the previous image if it is visible.

            }


            buttonSubmit.setEnabled(btnSubmitBool);
            buttonStop.setEnabled(btnStopBool);


            if (savedInstanceState.getBoolean("editTextBoolean")) {//sets the editText to either enabled or disabled accordingly.

                editText.setEnabled(true);
            } else {
                editText.setEnabled(false);
            }

            fillActivity();

        } else {
            //the first time the program runs and until savedInstanceState changes to a non-NULL value.
            instantiateVariables();
            btnStopBool = false;
            btnSubmitBool = false;
            imageDogo.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            searchedBreed = "";
            editText.setEnabled(true);
            linLayImage.setVisibility(View.INVISIBLE);
            fillActivity();
        }

    }


    /**
     * INSTANTIATE VARIABLES:
     */
    private void instantiateVariables() {

        intent = getIntent();
        editText = findViewById(R.id.edit_text_id);
        listView = findViewById(R.id.list_view_id);
        imageSwitcher = (ImageView) findViewById(R.id.image_switcher_id);//slide show images displayed in this image view
        imageDogo = findViewById(R.id.image_dogo_id);//iDogo logo.
        buttonSubmit = (Button) findViewById(R.id.submit_btn_search_id);
        buttonStop = (Button) findViewById(R.id.stop_button_id);
        linLayImage = findViewById(R.id.linearLayout_image_id);//frame around image


    }

    /**
     * FILL ACTIVITY: sets up activity
     */
    private void fillActivity() {


        buttonSubmit.setEnabled(btnSubmitBool);
        buttonStop.setEnabled(btnStopBool);

        //adapter that is used to display breeds names from breedNameList in listView:
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, breedNameList);


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //TEXT WATCHER SETUP: allows list view to be enabled when text box is searched.
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //list view invisible before text has been changed
                listView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //resets list view to invisible when edit text string becomes empty:
                if (s.equals("")) {
                    listView.setVisibility(View.INVISIBLE);

                }
                //checks if the edit text string compares to the one selected in listView:
                if (editText.getText().toString().equals(searchedBreed)) {
                    listView.setVisibility(View.INVISIBLE);//if it is then it hides the listView
                } else {

                    listView.setAdapter(adapter);//sets adapter to listView
                    adapter.getFilter().filter(s);//filters through adapter with every character that is being entered in editText box.
                    listView.setVisibility(View.VISIBLE);//makes listView available.

                }

            }//end onTextChanged()

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {//ensures that when the length of a string is 0 the listView is NOT visible.
                    listView.setVisibility(View.INVISIBLE);

                } else {//ensures that when the length of the string is different than 0 the listView is visible.

                    listView.setVisibility(View.VISIBLE);

                }

            }
        };//end text watcher definition


        editText.addTextChangedListener(textWatcher);
        listView.setOnItemClickListener(this);

    }


    /**
     * ITEM CLICK: when an item is clicked in listView the following sequence is enacted:
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        searchedBreed = parent.getItemAtPosition(position).toString();
        editText.setText(searchedBreed);
        btnSubmitBool = true;
        buttonSubmit.setEnabled(btnSubmitBool);//enables submit button only when an element is selected from listView
        listView.setVisibility(View.INVISIBLE);
        editText.onEditorAction(EditorInfo.IME_ACTION_DONE);//hides keyboard once listView is selected and hidden.
        editText.setEnabled(false);

    }

    /**
     * ON SUBMIT: when submit button is pressed.
     * @param view
     */
    public void onSubmitSearch(View view) {

        slideShow();

    }


    /**
     * SLIDE SHOW: methods that sets up a new image to display every 5 seconds.
     */
    public void slideShow() {

        imageDogo.setVisibility(View.INVISIBLE);
        btnSubmitBool = false;
        btnStopBool = true;
        buttonSubmit.setEnabled(btnSubmitBool);
        buttonStop.setEnabled(btnStopBool);
        flag = false;

        imageSwitcher.postDelayed
                (new Runnable() {


                     @Override
                     public void run() {
                         if (!flag) {

                             resource_id = getResources().getIdentifier(dogoMap.get(searchedBreed)
                                             .get(randomNumGenerator(0, dogoMap.get(searchedBreed).size() - 1)),
                                     "drawable", "com.example.w1696151_cwk1");

                             imageSwitcher.setImageResource(resource_id);
                             imageSwitcher.postDelayed(this, 5000);

                             //counter++;//left for reference

                         }

                     }
                 }
                        , 0);
        linLayImage.setVisibility(View.VISIBLE);
        imageSwitcher.setVisibility(View.VISIBLE);


    }//end slideShow()


    /**
     * ON STOP: when stop button is pressed the image view is reset.
     * @param view
     */
    public void onStopButton(View view) {

        flag = true;//flag will stop the runnable thread that shows a new image every 5 seconds.
        //reset to prepare for a new run of fillActivity:
        imageSwitcher.setImageResource(android.R.color.transparent);//hides image view
        imageSwitcher.setVisibility(View.INVISIBLE);
        imageDogo.setVisibility(View.VISIBLE);
        linLayImage.setVisibility(View.INVISIBLE);
        editText.setEnabled(true);
        editText.setText("");
        btnStopBool = false;

        fillActivity();


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

        //used to pass image, no longer needed, leaving for reference:
        //        bitmapDrawable = (BitmapDrawable) imageSwitcher.getDrawable();
        //        bitmap = bitmapDrawable.getBitmap();
        //        outState.putParcelable("image", bitmap);

        outState.putInt("listViewVisibility", listView.getVisibility());
        outState.putInt("imageVisibility", imageSwitcher.getVisibility());
        outState.putInt("linLayVisibility", linLayImage.getVisibility());
        outState.putInt("imageDogoVisibility", imageDogo.getVisibility());
        outState.putString("searchBreedKey", searchedBreed);
        outState.putBoolean("submitBool", btnSubmitBool);
        outState.putBoolean("stopBool", btnStopBool);
        outState.putString("editText", editText.getText().toString());
        outState.putBoolean("editTextBoolean", editText.isEnabled());

    }//end onSave...
}//end SearchDog class

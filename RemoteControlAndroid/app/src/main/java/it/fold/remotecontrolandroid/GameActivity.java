package it.fold.remotecontrolandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
* Class for activities using the action bar library
*/
public class GameActivity extends Activity {

    @Override
    /**
    * initializes based off of Bundle
    *
    * @param Bundle savedInstanceState parseable strings used for init
    */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }


    @Override
    /**
    * initializes the options menu
    *
    * @param Menu menu information for the options menu
    * @return boolean true
    */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    /**
    * whenever an option is selected, handles it
    *
    * @param MenuItem item that has been selected
    * @return boolean true
    */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //sends CLEV_MODKEY_UP info 0,CLEV_MODKEY_UP info 2, this sets both control and shift up
    public void onLeftClickButton()
    {

    }

    //sends CLEV_MODKEY_DOWN info 0, CLEV_MODKEY_UP info 2, this sets control down, shift up
    public void onRightClickButton()
    {

    }

    //sends CLEV_MODKEY_UP info 0, CLEV_MODKEY_DOWN info 2, this sets control up, shift down
    public void onMiddleClickButton()
    {

    }

}

/*
 * Subject: CS6301.022
 * By Arvind Pandiyan : axp141630
 * By Dhrupad Kaneria : dck140030
 * 
 * Date: 11/23/2014
 * Description: This activity is displayed on start of the game. 
 * It has 3 features.
 * 1. Settings (Action Bar)
 * 2. Play
 * 3. High Score
 */

package com.UIDesign.barrelrace;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity implements OnClickListener {

	private Button startBtn;
	private Button highscore;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		/*
		 * When settings action icon is pressed, the settings activity is called.
		 */
	    switch (item.getItemId()) 
	    {
	        case R.id.action_settings:
	        	startActivity(new Intent(Home.this, SettingsActivity.class));
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		/*
		 * The references for the buttons are created and ActionListerners are set.
		 */
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		startBtn = (Button) findViewById(R.id.start_button);
		startBtn.setOnClickListener(this);
		highscore = (Button) findViewById(R.id.highscore);
		highscore.setOnClickListener(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();

	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
			case R.id.start_button:
				startActivity(new Intent(Home.this, BarrelRaceActivity.class));
				finish();
				break;
	
			case R.id.highscore:
				startActivity(new Intent(Home.this, HighScore.class));
				finish();
				break;
		}
	}
}

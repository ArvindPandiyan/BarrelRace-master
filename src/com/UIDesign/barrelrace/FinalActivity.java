/*
 * Subject: CS6301.022
 * By Arvind Pandiyan : axp141630
 * By Dhrupad Kaneria : dck140030
 * 
 * Date: 11/23/2014
 * Description: This activity is showed after the game is completed. 
 * It checks for the win or lose and displays corresponding messages.
 */

package com.UIDesign.barrelrace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FinalActivity extends Activity implements OnClickListener 
{

	private Button replay;
	private TextView scoreValueView;
	private TextView statusValueView;
	private TextView highScoreStatus;
	private ScoreList sl;
	private EditText nameTextField;
	private String t;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final);
		
		replay = (Button) findViewById(R.id.replay);
		replay.setOnClickListener(this);

		scoreValueView = (TextView) findViewById(R.id.scoreValueView);
		statusValueView = (TextView) findViewById(R.id.statusValueView);
		highScoreStatus = (TextView) findViewById(R.id.highScoreStatus);
		t = (String) getIntent().getExtras().get("time");
		Boolean win = (Boolean) getIntent().getExtras().get("win");
		scoreValueView.setText(t);
		/*
		 * Checks if the player has completed the game or no
		 */
		if(!win)
		{
			statusValueView.setText("You Lose");
		}
		else
		{
			statusValueView.setText("You Win");
			sl = new ScoreList();
	        sl.readFromFile(getFilesDir());
	        boolean getname = false;
	        /*
	         * Checks if he/she has made it to the highscore or no
	         */
	        if(sl.slist.size() > 0)
	        {
		        if(t.compareTo(sl.slist.get(sl.slist.size() - 1).getTime()) < 0)
		        {
		        	highScoreStatus.setText("You made it to the High Score");
		        	getname = true;
		        }
	        }
	        else
	        {
	        	highScoreStatus.setText("You made it to the High Score");
	        	getname = true;
	        }
	        if(getname)
	        {
	        	TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
	        	nameTextField = (EditText) findViewById(R.id.nameTextField);
	        	nameTextView.setVisibility(View.VISIBLE);
	        	nameTextField.setVisibility(View.VISIBLE);
	        	Button save = (Button) findViewById(R.id.save);
	        	save.setOnClickListener(this);
	        	save.setVisibility(View.VISIBLE);
	        }
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.replay:
				startActivity(new Intent(FinalActivity.this, BarrelRaceActivity.class));
				finish();
				break;
			
			case R.id.save:
				sl.add(new ScoreDetail(nameTextField.getText().toString(), t));
				sl.writeToFile(getFilesDir());
				startActivity(new Intent(FinalActivity.this, HighScore.class));
				finish();
				break;
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		startActivity(new Intent(FinalActivity.this, Home.class));
		finish();
	}
}

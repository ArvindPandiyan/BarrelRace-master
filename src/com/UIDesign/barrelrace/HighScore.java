/*
 * Subject: CS6301.022
 * By Dhrupad Kaneria : dck140030
 * 
 * Date: 11/23/2014
 * Description: The highscore is displayed. 
 * The name and time is sorted and displayed in a list fashion.
 */

package com.UIDesign.barrelrace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class HighScore extends Activity
{
	private ListView lv;
	private ScoreList sl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore_display);
        
        lv = (ListView)findViewById(R.id.listView1);
        sl = new ScoreList();
        sl.readFromFile(getFilesDir());
        /*
         * CustomAdapter helps map the ArrayList to the ListView.
         */
        CustomAdapter adapter = new CustomAdapter(this, sl.getList());
		lv.setAdapter(adapter);
	}
	@Override
	public void onBackPressed() 
	{
		startActivity(new Intent(HighScore.this, Home.class));
		finish();
	}
}

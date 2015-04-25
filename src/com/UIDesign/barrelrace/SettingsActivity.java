/*
 * Subject: CS6301.022
 * By Arvind Pandiyan : axp141630
 * 
 * Date: 11/23/2014
 * Description: This shows the settings options like colors for barrel, horse and background
 */

package com.UIDesign.barrelrace;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends Activity implements OnItemSelectedListener 
{
	private Spinner horseSpinner;
	private Spinner barrelSpinner;
	private Spinner bgSpinner;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		SharedPreferences getcolor = getSharedPreferences("settingColor", 0);
		if(getcolor != null)
		{
			editor = getcolor.edit();
		}
		
		/*
		 * Get all the drop down values, and display the details.
		 */
		bgSpinner = (Spinner) findViewById(R.id.spinnerbg);
		barrelSpinner = (Spinner) findViewById(R.id.spinnerbarrel);
		horseSpinner = (Spinner) findViewById(R.id.spinnerhorse);
		
		ArrayAdapter<CharSequence> adapterBarrel = ArrayAdapter.createFromResource(this, R.array.barrel_color_array,android.R.layout.simple_spinner_item);
		adapterBarrel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		barrelSpinner.setAdapter(adapterBarrel);

		ArrayAdapter<CharSequence> adapterHorse = ArrayAdapter.createFromResource(this, R.array.barrel_color_array, android.R.layout.simple_spinner_item);
		adapterHorse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		horseSpinner.setAdapter(adapterHorse);

		ArrayAdapter<CharSequence> adapterBg = ArrayAdapter.createFromResource(this, R.array.bg_color_array,android.R.layout.simple_spinner_item);
		adapterBg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bgSpinner.setAdapter(adapterBg);
		bgSpinner.setOnItemSelectedListener(this);
		barrelSpinner.setOnItemSelectedListener(this);
		horseSpinner.setOnItemSelectedListener(this);

		SharedPreferences settingsPref = getSharedPreferences("settingColor", 0);
		editor = settingsPref.edit();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
	{
		/*
		 * When an item is selected, set the color for the particular object.
		 */
		switch (parent.getId()) 
		{
		case R.id.spinnerbarrel:
			editor.putString("barrelColor", parent.getItemAtPosition(pos).toString());
			editor.commit();
			break;
		case R.id.spinnerhorse:
			editor.putString("horseColor", parent.getItemAtPosition(pos).toString());
			editor.commit();
			break;
		case R.id.spinnerbg:
			editor.putString("bgColor", parent.getItemAtPosition(pos).toString());
			editor.commit();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) 
	{
	}

}

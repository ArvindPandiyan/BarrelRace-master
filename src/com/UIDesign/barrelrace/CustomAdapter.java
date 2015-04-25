/*
 * Subject: CS6301.022
 * By Arvind Pandiyan : axp141630
 * 
 * Date: 11/22/2014
 * Description: Gets the details from scoresList and maps it onto the Listview.
 */

package com.UIDesign.barrelrace;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<ScoreDetail> 
{
    public CustomAdapter(Context context, ArrayList<ScoreDetail> users) 
    {
       super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	ScoreDetail user = getItem(position);    
       if (convertView == null) 
       {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.highscore_list, parent, false);
       }
       TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
       TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
       tvName.setText(user.getName());
       tvName.setTextSize(28);
       tvName.setTextColor(Color.parseColor("#000000"));
       tvTime.setText(user.getTime());
       tvTime.setTextSize(22);
       return convertView;
   }
}
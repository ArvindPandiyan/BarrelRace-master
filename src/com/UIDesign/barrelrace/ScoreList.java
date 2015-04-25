/*
 * Subject: CS6301.022
 * By Dhrupad Kaneria : dck140030
 * 
 * Date: 11/22/2014
 * Description: A class to manipulate the scores saved in the text file.
 */

package com.UIDesign.barrelrace;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreList 
{
	ArrayList<ScoreDetail> slist;
	
	public ScoreList()
	{
		slist = new ArrayList<ScoreDetail>();
	}
	public ArrayList<ScoreDetail> getList()
	{
		return this.slist;
	}
	public void add(ScoreDetail entry)
	{
		slist.add(entry);
		sort();
		if(slist.size() == 11)
		{
			slist.remove(10);
		}
	}
	/*
	 * Used to sort the ArrayList to arrange the top scores.
	 */
	private void sort()
	{
		Collections.sort(slist);
	}
	/*
	 * Reads data from file
	 */
	public void readFromFile(File d)
	{
		String line ="";
		File f = new File(d, "highscore.txt");
		try 
		{
			FileInputStream fis = new FileInputStream(f);
        	DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null ) 
            {
				String[] parts = line.split(";");
				slist.add(new ScoreDetail(parts[0], parts[1]));
			}
            br.close();
		} 
        catch (FileNotFoundException e) 
        {
         e.printStackTrace();
        } 
		catch (IOException e)
        {
         e.printStackTrace(); 
        }
	}
	/*
	 * Writes the file
	 */
	public void writeToFile(File d)
	{
		String line ="";
		File f = new File(d, "highscore.txt");
		try 
		{
			FileOutputStream fis = new FileOutputStream(f, false);
        	for(ScoreDetail i : slist)
        	{
        		line = i.getName() + ";" + i.getTime() + "\n";
        		fis.write(line.getBytes());
        	}
        	fis.close();
		} 
        catch (IOException e) 
        {
        	e.printStackTrace(); 
        }
	}
}

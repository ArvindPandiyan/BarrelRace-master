/*
 * Subject: CS6301.022
 * By Dhrupad Kaneria : dck140030
 * 
 * Date: 11/23/2014
 * Description: A class to maintain each entry of a score.
 * It provides basic operations for manipulating the high scores.
 */

package com.UIDesign.barrelrace;

public class ScoreDetail implements Comparable<ScoreDetail>
{
	String Name;
	String Time;
	
	public ScoreDetail(String n, String t)
	{
		Name = n;
		Time = t;
	}
	public String getName()
	{
		return Name;
	}
	public String getTime()
	{
		return Time;
	}
	
	@Override
	public int compareTo(ScoreDetail another) 
	{
		return getTime().compareTo(another.getTime());
	}
}

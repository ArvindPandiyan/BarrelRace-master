/*
 * Subject: CS6301.022
 * By Arvind Pandiyan : axp141630
 * By Dhrupad Kaneria : dck140030
 * 
 * Date: 11/23/2014
 * Description: This class has the main logic for the horse.
 * It runs the game loop which updates the position of the ball frame by frame.
 */

package com.UIDesign.barrelrace;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
public class BarrelRaceActivity extends Activity implements Callback, SensorEventListener, android.view.View.OnClickListener 
{
	private static final int BALL_RADIUS = 30;
	public static final int BARREL_RADIUS = 40;
	public static int BOTTOM_PADDING;
	private SurfaceView surface;
	private SurfaceHolder holder;
	private final BarrelRaceModel model = new BarrelRaceModel(BALL_RADIUS);
	private GameLoop gameLoop;
	private Paint backgroundPaint;
	private Paint borderPaint;
	private boolean sensorRegister;
	private Paint ballPaint;
	private long lastSensorUpdate = -1;
	public static int barrelLeftX;
	public static int barrelLeftY;
	public static int barrelRightX;
	public static int barrelRightY;
	public static int barrelMiddleX;
	public static int barrelMiddleY;
	private SensorManager mSensorManager;
	private Sensor mAccel;
	private TextView timeTextView;
	private Handler timeHandler = new Handler();
	private ImageButton playButton;
	private ImageButton resetButton;
	public boolean barrelColorLeft = false;
	public boolean barrelColorRight = false;
	public boolean barrelColorMiddle = false;
	private Paint barrelPaintLeft;
	private Paint barrelPaintRight;
	private Paint barrelPaintMiddle;
	private Runnable updateTimerThread;
	private static Long savedTime = 9223372036854775807L;
	private Long currTime = 0L;
	private String minTime;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.bouncing_ball);
		
		/*
		 * Setting the sensors before the start of the game.
		 */
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		model.setVibrator(vibrator);
		surface = (SurfaceView) findViewById(R.id.bouncing_ball_surface);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		screenWidth = Math.min(screenHeight, screenWidth);

		LayoutParams lp = surface.getLayoutParams();
		lp.width = screenWidth;
		BOTTOM_PADDING = BALL_RADIUS * 2 + 10;
		lp.height = screenWidth + BOTTOM_PADDING;
		surface.setLayoutParams(lp);
		holder = surface.getHolder();
		surface.getHolder().addCallback(this);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorRegister=mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_GAME);
		SharedPreferences settingsPref = getSharedPreferences("settingColor", 0);
		backgroundPaint = new Paint();
		/*
		 * Setting the context for the game.
		 */
		if (settingsPref.getString("bgColor", "light gray").equals("white")) 
		{
			backgroundPaint.setColor(Color.WHITE);
		} 
		else 
		{
			backgroundPaint.setColor(Color.LTGRAY);
		}
		ballPaint = new Paint();
		ballPaint.setAntiAlias(true);
		if (settingsPref.getString("horseColor", "Black").equals("Blue")) 
		{
			ballPaint.setColor(Color.BLUE);
		} 
		else if (settingsPref.getString("horseColor", "Black").equals("Red")) 
		{
			ballPaint.setColor(Color.RED);
		} 
		else
		{
			ballPaint.setColor(Color.BLACK);
		}

		barrelPaintLeft = new Paint();
		barrelPaintLeft.setAntiAlias(true);
		barrelPaintRight = new Paint();
		barrelPaintRight.setAntiAlias(true);
		barrelPaintMiddle = new Paint();
		barrelPaintMiddle.setAntiAlias(true);

		/*
		 * Setting the barrel color.
		 */
		if (settingsPref.getString("barrelColor", "Yellow").equals("Blue")) 
		{
			barrelPaintLeft.setColor(Color.BLUE);
			barrelPaintMiddle.setColor(Color.BLUE);
			barrelPaintRight.setColor(Color.BLUE);
		} 
		else if (settingsPref.getString("barrelColor", "Yellow").equals("Black")) 
		{
			barrelPaintLeft.setColor(Color.BLACK);
			barrelPaintMiddle.setColor(Color.BLACK);
			barrelPaintRight.setColor(Color.BLACK);
		} 
		else 
		{
			barrelPaintLeft.setColor(Color.RED);
			barrelPaintMiddle.setColor(Color.RED);
			barrelPaintRight.setColor(Color.RED);
		}

		borderPaint = new Paint();
		borderPaint.setColor(Color.DKGRAY);
		borderPaint.setAntiAlias(true);
		borderPaint.setStrokeWidth(10);
		model.moveBall(lp.width / 2, lp.height);
		timeTextView = (TextView) findViewById(R.id.time);
		timeTextView.setBackgroundColor(color.holo_blue_bright);
		Typeface font = Typeface.createFromAsset(getAssets(), "LCD.ttf");
		timeTextView.setTypeface(font);
		playButton = (ImageButton) findViewById(R.id.play_button);
		playButton.setOnClickListener(this);
		resetButton = (ImageButton) findViewById(R.id.reset_button);
		resetButton.setOnClickListener(this);
	}

	@Override
	protected void onPause() 
	{
		/*
		 * Unregistering the sensor onPause and pausing the model.
		 */
		super.onPause();
		try 
		{
			if (mSensorManager != null && this != null) 
			{
				mSensorManager.unregisterListener(this);
			}
		} 
		catch (Exception e) 
		{
			Log.w("exceptions", e.getMessage());
		}
		model.setVibrator(null);
		model.setAccel(0, 0);
		sensorRegister=false;
		model.pause=true;
		model.pauseTime=SystemClock.uptimeMillis();
	}

	@Override
	protected void onResume() 
	{
		/*
		 * Check if the control is coming from pause state, if so, display the dialog to ask the user to continue or not.
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		{
	           @Override
			public void onClick(DialogInterface dialog, int id) 
	        {
		        	model.pause=false;
		        	mSensorManager.registerListener(BarrelRaceActivity.this, mAccel, SensorManager.SENSOR_DELAY_GAME);
		       		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		       		model.setVibrator(vibrator);
	        }
	    });
		/*
		 * If the user doesnot want to continue, take him back to the hoem page.
		 */
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
		{
	        @Override
			public void onClick(DialogInterface dialog, int id) 
	        {
	        	   startActivity(new Intent(BarrelRaceActivity.this, Home.class));
	       			finish();
	        }
	    });
		builder.setMessage("Do you wish to continue?");
		if(sensorRegister==false)
		{
			builder.create().show();
		}
		super.onResume();
	}
	
	@Override
	protected void onStart() 
	{
		super.onStart();
		mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_GAME);
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		model.setVibrator(vibrator);
	}

	@Override
	protected void onStop() 
	{
		super.onStop();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		model.setSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		draw();
	}

	private void draw() 
	{
		Canvas c = null;
		try 
		{
			c = holder.lockCanvas();
			if (c != null)
			{
				doDraw(c);
			}
		} 
		finally 
		{
			if (c != null) 
			{
				holder.unlockCanvasAndPost(c);
			}
		}
	}
	
	private void doDraw(Canvas c) 
	{
		/*
		 * Drawing the surface view.
		 */
		int width = c.getWidth();
		int height = c.getHeight();
		c.drawRect(0, 0, width, height, backgroundPaint);
		c.drawLine(5, 0, 5, height, borderPaint);
		c.drawLine(width - 5, 0, width - 5, height, borderPaint); 
		c.drawLine(0, 5, width, 5, borderPaint); 
		c.drawLine(0, height - BOTTOM_PADDING, width / 2 - BOTTOM_PADDING / 2, height - BOTTOM_PADDING, borderPaint);
		c.drawLine(width / 2 + BOTTOM_PADDING / 2, height - BOTTOM_PADDING, width, height - BOTTOM_PADDING, borderPaint);

		barrelLeftX = width / 4;
		barrelLeftY = height / 4;
		barrelRightX = width * 3 / 4;
		barrelRightY = height / 4;
		barrelMiddleX = width / 2;
		barrelMiddleY = height * 2 / 3;

		if (barrelColorLeft) 
		{
			barrelPaintLeft.setColor(Color.GREEN);
		}
		c.drawCircle(barrelLeftX, barrelLeftY, BARREL_RADIUS, barrelPaintLeft);

		if (barrelColorRight) 
		{
			barrelPaintRight.setColor(Color.GREEN);
		}
		c.drawCircle(barrelRightX, barrelRightY, BARREL_RADIUS,
				barrelPaintRight);

		if (barrelColorMiddle) 
		{
			barrelPaintMiddle.setColor(Color.GREEN);
		}
		c.drawCircle(barrelMiddleX, barrelMiddleY, BARREL_RADIUS,
				barrelPaintMiddle);

		float ballX, ballY;
		synchronized (model.LOCK) 
		{
			ballX = model.ballPixelX;
			ballY = model.ballPixelY;
		}
		c.drawCircle(ballX, ballY, BALL_RADIUS, ballPaint);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		/*
		 * Clearing the surface
		 */
		try 
		{
			model.setSize(0, 0);
		} 
		finally 
		{
			gameLoop = null;
		}
	}

	private class GameLoop extends Thread 
	{
		/*
		 * Creating the gameLoop which will run in the background which updates continuously. 
		 */
		private volatile boolean running = true;
		private String currTimeString;

		@Override
		public void run() 
		{
			while (running) 
			{
				draw();
				model.updatePhysics();

				int roundStateChanged[] = new int[3];
				roundStateChanged = model.isCompletedCircle();

				if (roundStateChanged[0] == 1) 
				{
					barrelColorLeft = true;
					Log.d(POWER_SERVICE, "Touched");
				}

				if (roundStateChanged[1] == 1) 
				{
					barrelColorRight = true;
					Log.d(POWER_SERVICE, "Touched");
				}

				if (roundStateChanged[2] == 1) 
				{
					barrelColorMiddle = true;
					Log.d(POWER_SERVICE, "Touched");
				}

				if (barrelColorLeft && barrelColorRight && barrelColorMiddle) 
				{
					if (model.ballPixelX <= (model.getPixelWidth() / 2) + 15
							&& model.ballPixelX >= (model.getPixelWidth() / 2) - 15
							&& model.ballPixelY >= model.getPixelHeight()/2) 
					{
						timeHandler.removeCallbacks(updateTimerThread);

						currTime = model.getUpdatedTime();

						if (savedTime > currTime) 
						{
							savedTime = currTime;
						}
						/*
						 * create and time string
						 */
						int secs = (int) (savedTime / 1000);
						int mins = secs / 60;
						secs = secs % 60;
						int milliseconds = (int) (savedTime % 1000);
						minTime = new String("" + mins + ":" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds));

						int secs1 = (int) (currTime / 1000);
						int mins1 = secs1 / 60;
						secs1 = secs1 % 60;
						int milliseconds1 = (int) (currTime % 1000);
						currTimeString = new String("" + mins1 + ":" + String.format("%02d", secs1) + ":" + String.format("%03d", milliseconds1));

						SharedPreferences minT = getSharedPreferences("findhighscore", 0);
						SharedPreferences.Editor editor = minT.edit();
						editor.putString("highscore", minTime);
						editor.commit();
						Intent winIntent = new Intent(BarrelRaceActivity.this, FinalActivity.class);
						winIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						winIntent.putExtra("win", true);
						winIntent.putExtra("time", currTimeString);
						startActivity(winIntent);
					}
				}
				if (model.isUserLost()) 
				{
					timeHandler.removeCallbacks(updateTimerThread);
					Intent lostIntent = new Intent(BarrelRaceActivity.this,FinalActivity.class);
					lostIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					lostIntent.putExtra("win", false);
					lostIntent.putExtra("time", model.getTimeString());
					lostIntent.putExtra("updatedtime", model.getUpdatedTime());
					startActivity(lostIntent);
					safeStop();
					finish();
				}
			}
		}

		public void safeStop() 
		{
			running = false;
			interrupt();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) 
	{
	}

	@Override
	public void onSensorChanged(SensorEvent evt) 
	{
		/*
		 * When the sensor is changed, update the model.
		 */
		if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
		{
			long curTime = System.currentTimeMillis();
			if (lastSensorUpdate == -1 || (curTime - lastSensorUpdate) > 20) 
			{
				lastSensorUpdate = curTime;
				model.setAccel(-evt.values[0], -evt.values[1]);
			}
		}
	}

	@Override
	public void onClick(View v) 
	{
		/*
		 * Setting functions for play and replay buttons.
		 */
		switch (v.getId()) 
		{
			case R.id.play_button:
				model.setStartTime(SystemClock.uptimeMillis());
				gameLoop = new GameLoop();
				gameLoop.start();
				updateTimerThread = new Runnable() 
				{
					@Override
					public void run() 
					{
						timeTextView.setText(model.getTimeString());
						timeHandler.postDelayed(this, 0);
					}
				};
				timeHandler.postDelayed(updateTimerThread, 0);
				playButton.setEnabled(false);
				break;
	
			case R.id.reset_button:
				startActivity(new Intent(this, BarrelRaceActivity.class));
	
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		startActivity(new Intent(BarrelRaceActivity.this, Home.class));
		finish();
	}
}

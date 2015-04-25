/*
 * Subject: CS6301.022
 * By Arvind Pandiyan : axp141630
 * By Dhrupad Kaneria : dck140030
 * 
 * Date: 11/23/2014
 * Description: This class is used to define the physics of the game.
 * It consists of the timer, handles - the playing field dimensions, the object dimensions as well as the logic.
 */

package com.UIDesign.barrelrace;

import java.util.concurrent.atomic.AtomicReference;
import android.os.SystemClock;
import android.os.Vibrator;

public class BarrelRaceModel 
{
	private final float pixelsPerMeter = 10;
	private final int ballRadius;
	private String timeString;
	private long timeInMilliseconds = 0L;
	private long updatedTime = 0L;
	public long pauseTime = 0L;
	long startTime = 0L;
	boolean barrelLCompleted = false;
	public boolean barrelRCompleted = false;
	public boolean barrelMCompleted = false;
	public boolean pause=false;
	public int flagBarrelLeft[] = new int[4];
	public int flagBarrelRight[] = new int[4];
	public int flagBarrelMiddle[] = new int[4];
	public int roundStateChanged[] = new int[3];
	public static boolean alternateAxis = false;
	int[][] traceMatrix = null;
	public float ballPixelX, ballPixelY;
	private int pixelWidth, pixelHeight;
	private float velocityX, velocityY;
	private float accelX, accelY;
	private static final float rebound = 0.5f;
	private static final float STOP_BOUNCING_VELOCITY = 2f;
	private volatile long lastTimeMs = -1;
	public final Object LOCK = new Object();
	private static int BOTTOM_PADDING;
	private boolean bouncedX = false;
	private boolean bouncedY = false;
	private AtomicReference<Vibrator> vibratorRef = new AtomicReference<Vibrator>();
	
	public String getTimeString() 
	{
		return timeString;
	}

	public void setTimeString(String timeString) 
	{
		this.timeString = timeString;
	}

	public long getUpdatedTime() 
	{
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) 
	{
		this.updatedTime = updatedTime;
	}

	public long getStartTime()
	{
		return startTime;
	}

	public void setStartTime(long startTime) 
	{
		this.startTime = startTime;
	}
	
	public int getPixelWidth() 
	{
		return pixelWidth;
	}

	public void setPixelWidth(int pixelWidth) 
	{
		this.pixelWidth = pixelWidth;
	}

	public int getPixelHeight() 
	{
		return pixelHeight;
	}

	public void setPixelHeight(int pixelHeight) 
	{
		this.pixelHeight = pixelHeight;
	}

	public BarrelRaceModel(int ballRadius) 
	{
		this.ballRadius = ballRadius;
	}

	public void setAccel(float ax, float ay) 
	{
		synchronized (LOCK) 
		{
			this.accelX = ax;
			this.accelY = ay;
		}
	}

	public void setSize(int width, int height) 
	{
		synchronized (LOCK) 
		{
			this.pixelWidth = width;
			this.pixelHeight = height;
			traceMatrix = new int[width + 100][height + 100];
		}
	}

	public int getBallRadius() 
	{
		return ballRadius;
	}

	public void moveBall(int ballX, int ballY)
	/*
	 * This method is used to reposition the ball to a new point
	 */
	{
		synchronized (LOCK) 
		{
			this.ballPixelX = ballX;
			this.ballPixelY = ballY;
			velocityX = 0;
			velocityY = 0;
		}
	}

	public boolean isUserLost() 
	{
		double distLX = BarrelRaceActivity.barrelLeftX - ballPixelX;
		double distLY = BarrelRaceActivity.barrelLeftY - ballPixelY;
		double distFromLeftBarrel = distLX * distLX + distLY * distLY;

		double distRX = BarrelRaceActivity.barrelRightX - ballPixelX;
		double distRY = BarrelRaceActivity.barrelRightY - ballPixelY;
		double distFromRightBarrel = distRX * distRX + distRY * distRY;

		double distMX = BarrelRaceActivity.barrelMiddleX - ballPixelX;
		double distMY = BarrelRaceActivity.barrelMiddleY - ballPixelY;
		double distFromMiddleBarrel = distMX * distMX + distMY * distMY;

		int sumOfRadius = ballRadius + BarrelRaceActivity.BARREL_RADIUS;
		sumOfRadius = sumOfRadius * sumOfRadius;
		if (sumOfRadius >= distFromLeftBarrel || sumOfRadius >= distFromMiddleBarrel || sumOfRadius >= distFromRightBarrel) 
		{
			moveBall((int) ballPixelX + 1, (int) ballPixelY + 1);
			Vibrator v = vibratorRef.get();
			if (v != null) 
			{
				v.vibrate(35L);
			}
			return true;
		} 
		else
			return false;

	}

	public void updatePhysics()
	{
		/*
		 * It is called to update every frame.
		 * It handles the logic to move the ball and change the timer value.
		 */
		if(!pause)
		{
			// copy everything to local vars (hence the 'l' prefix)
			/*if ((accelX < 0 & velocityX > 0) || (accelX > 0 & velocityX < 0)) 
			{
				accelX = accelX;
			}
			if ((accelY < 0 & velocityY < 0) || (accelY > 0 & velocityY > 0)) 
			{
				accelY = accelY;
			}*/
	
			float lWidth, lHeight, lBallX, lBallY, lAx, lAy, lVx, lVy;
			synchronized (LOCK) 
			{
				BOTTOM_PADDING = BarrelRaceActivity.BOTTOM_PADDING;
				lWidth = pixelWidth;
				lHeight = pixelHeight;
				lBallX = ballPixelX;
				lBallY = ballPixelY;
				lVx = velocityX;
				lVy = velocityY;
				lAx = accelX;
				lAy = -accelY;
			}
			
			if (lWidth <= 0 || lHeight <= 0) 
			{
				return;
			}
			long curTime = System.currentTimeMillis();
			if (lastTimeMs < 0) 
			{
				lastTimeMs = curTime;
				return;
			}
	
			long elapsedMs = curTime - lastTimeMs;
			lastTimeMs = curTime;
	
			/*
			 * Updating the velocity.
			 * Converting it to m/s
			 */
			lVx += ((elapsedMs * lAx) / 1000) * pixelsPerMeter;
			lVy += ((elapsedMs * lAy) / 1000) * pixelsPerMeter;
	
			/*
			 * Updating the position.
			 */
			lBallX += ((lVx * elapsedMs) / 1000) * pixelsPerMeter;
			lBallY += ((lVy * elapsedMs) / 1000) * pixelsPerMeter;
	
			bouncedX = false;
			bouncedY = false;
			/*
			 * This logic is used to make the ball bounce back.
			 * *************************************************************************
			 */
			if (lBallY - ballRadius < 0)
			{
				lBallY = ballRadius;
				lVy = -lVy * rebound;
				bouncedY = true;
			}
			else if (lBallY + ballRadius > (lHeight - BOTTOM_PADDING)) 
			{
				lBallY = lHeight - ballRadius - BOTTOM_PADDING;
				lVy = -lVy * rebound;
				bouncedY = true;
			}
			if (bouncedY && Math.abs(lVy) < STOP_BOUNCING_VELOCITY) 
			{
				lVy = 0;
				bouncedY = false;
			}
	
			if (lBallX - ballRadius < 0) 
			{
				lBallX = ballRadius;
				lVx = -lVx * rebound;
				bouncedX = true;
			} 
			else if (lBallX + ballRadius > lWidth) 
			{
				lBallX = lWidth - ballRadius;
				lVx = -lVx * rebound;
				bouncedX = true;
			}
			if (bouncedX && Math.abs(lVx) < STOP_BOUNCING_VELOCITY) 
			{
				lVx = 0;
				bouncedX = false;
			}
			/*
			 * End of Bounce block
			 * ***************************************************************
			 */
			synchronized (LOCK) 
			{
				ballPixelX = lBallX;
				ballPixelY = lBallY;
				velocityX = lVx;
				velocityY = lVy;
			}
	
			if (bouncedX || bouncedY)
			/*
			 * Increase the time by 5 seconds since the ball touched the wall.
			 */
			{
				Vibrator v = vibratorRef.get();
				startTime -= 5000;
				if (v != null) {
					v.vibrate(20L);
				}
			}
			// Updating the Timer
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeInMilliseconds;
			int secs = (int) (updatedTime / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			int milliseconds = (int) (updatedTime % 1000);
			timeString = new String("" + mins + ":" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds));
		}
		else
		{
			/*
			 * Updating the pause time when the activity is paused.
			 */
			startTime += SystemClock.uptimeMillis()-pauseTime;
			pauseTime = startTime;
		}

	}

	/*
	 * This method is used to check if a a barrel has been circled or no.
	 */
	public int[] isCompletedCircle() 
	{
		float ballX, ballY;
		float barrelLeftX, barrelLeftY;
		float barrelRightX, barrelRightY;
		float barrelMiddleX, barrelMiddleY;

		synchronized (LOCK) 
		{
			ballX = (int) ballPixelX;
			ballY = (int) ballPixelY;
			barrelLeftX = BarrelRaceActivity.barrelLeftX;
			barrelLeftY = BarrelRaceActivity.barrelLeftY;
			barrelMiddleX = BarrelRaceActivity.barrelMiddleX;
			barrelMiddleY = BarrelRaceActivity.barrelMiddleY;
			barrelRightX = BarrelRaceActivity.barrelRightX;
			barrelRightY = BarrelRaceActivity.barrelRightY;
		}
		
		/*
		 * Checks for top-left barrel
		 */
		if (ballX > (barrelLeftX - 100) && ballX <= barrelLeftX) 
		{
			if (!alternateAxis) 
			{
				flagBarrelLeft[0] = 1;
				alternateAxis = true;
			}
		}
		if (ballX < (barrelLeftX + 100) && ballX > barrelLeftX) 
		{
			if (!alternateAxis) 
			{
				flagBarrelLeft[1] = 1;
				alternateAxis = true;
			}
		}
		if (ballY > (barrelLeftY - 100) && ballY <= barrelLeftY) 
		{
			if (alternateAxis) 
			{
				flagBarrelLeft[2] = 1;
				alternateAxis = false;
			}
		}

		if (ballY < (barrelLeftY + 100) && ballY > barrelLeftY) 
		{
			if (alternateAxis) 
			{
				flagBarrelLeft[3] = 1;
				alternateAxis = false;
			}
		}

		if (flagBarrelLeft[0] + flagBarrelLeft[1] + flagBarrelLeft[2] + flagBarrelLeft[3] == 4) 
		{
			roundStateChanged[0] = 1;

			flagBarrelLeft[0] = 0;
			flagBarrelLeft[1] = 0;
			flagBarrelLeft[2] = 0;
			flagBarrelLeft[3] = 0;

			flagBarrelRight[0] = 0;
			flagBarrelRight[1] = 0;
			flagBarrelRight[2] = 0;
			flagBarrelRight[3] = 0;

			flagBarrelMiddle[0] = 0;
			flagBarrelMiddle[1] = 0;
			flagBarrelMiddle[2] = 0;
			flagBarrelMiddle[3] = 0;

		}


		/*
		 * Checks for top-right barrel
		 */
		if (ballX > (barrelRightX - 100) && ballX <= barrelRightX) 
		{
			if (!alternateAxis) 
			{
				flagBarrelRight[0] = 1;
				alternateAxis = true;
			}
		}
		if (ballX < (barrelRightX + 100) && ballX > barrelRightX) 
		{
			if (!alternateAxis) 
			{
				flagBarrelRight[1] = 1;
				alternateAxis = true;
			}
		}

		if (ballY > (barrelRightY - 100) && ballY <= barrelRightY) 
		{
			if (alternateAxis) 
			{
				flagBarrelRight[2] = 1;
				alternateAxis = false;
			}
		}

		if (ballY < (barrelRightY + 100) && ballY > barrelRightY) 
		{
			if (alternateAxis) 
			{
				flagBarrelRight[3] = 1;
				alternateAxis = false;
			}
		}

		if (flagBarrelRight[0] + flagBarrelRight[1] + flagBarrelRight[2] + flagBarrelRight[3] == 4) 
		{
			roundStateChanged[1] = 1;

			flagBarrelLeft[0] = 0;
			flagBarrelLeft[1] = 0;
			flagBarrelLeft[2] = 0;
			flagBarrelLeft[3] = 0;

			flagBarrelRight[0] = 0;
			flagBarrelRight[1] = 0;
			flagBarrelRight[2] = 0;
			flagBarrelRight[3] = 0;

			flagBarrelMiddle[0] = 0;
			flagBarrelMiddle[1] = 0;
			flagBarrelMiddle[2] = 0;
			flagBarrelMiddle[3] = 0;
		}

		/*
		 * Checks for middle barrel
		 */
		if (ballX > (barrelMiddleX - 100) && ballX <= barrelMiddleX) 
		{
			if (!alternateAxis) 
			{
				flagBarrelMiddle[0] = 1;
				alternateAxis = true;
			}
		}
		if (ballX < (barrelMiddleX + 100) && ballX > barrelMiddleX) 
		{
			if (!alternateAxis) 
			{
				flagBarrelMiddle[1] = 1;
				alternateAxis = true;
			}
		}

		if (ballY > (barrelMiddleY - 100) && ballY <= barrelMiddleY)
		{
			if (alternateAxis) 
			{
				flagBarrelMiddle[2] = 1;
				alternateAxis = false;
			}
		}

		if (ballY < (barrelMiddleY + 100) && ballY > barrelMiddleY) 
		{
			if (alternateAxis) 
			{
				flagBarrelMiddle[3] = 1;
				alternateAxis = false;
			}
		}
		if (flagBarrelMiddle[0] + flagBarrelMiddle[1] + flagBarrelMiddle[2] + flagBarrelMiddle[3] == 4) 
		{
			roundStateChanged[2] = 1;

			flagBarrelLeft[0] = 0;
			flagBarrelLeft[1] = 0;
			flagBarrelLeft[2] = 0;
			flagBarrelLeft[3] = 0;

			flagBarrelRight[0] = 0;
			flagBarrelRight[1] = 0;
			flagBarrelRight[2] = 0;
			flagBarrelRight[3] = 0;

			flagBarrelMiddle[0] = 0;
			flagBarrelMiddle[1] = 0;
			flagBarrelMiddle[2] = 0;
			flagBarrelMiddle[3] = 0;

		}
		return roundStateChanged;
	}
	
	public void setVibrator(Vibrator v) 
	{
		vibratorRef.set(v);
	}
}

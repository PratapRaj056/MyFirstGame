package game.prs.darkeagle.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import game.prs.darkeagle.myfirstgame.MainActivity;
import game.prs.darkeagle.myfirstgame.R;

public class GameView extends SurfaceView implements Runnable {
	
	//boolean variable to track if the game is playing or not
	volatile boolean playing;
	
	//the game thread
	private Thread gameThread = null;
	
	//adding the player to this class
	private Player player;
	
	//These objects will be used for drawing
	private Paint paint;
	private Canvas canvas;
	private SurfaceHolder surfaceHolder;
	
	//Adding an stars list
	private ArrayList<Star> stars = new
			ArrayList<Star>();
	
	//created a reference of the class Friend
	private Friend friend;
	
	private Enemy enemy;
	
	/*//Adding enemies object array
	private Enemy[] enemies;
	
	//Adding 3 enemies you may increase the size
	private int enemyCount = 3;*/
	
	//defining a boom object to display blast
	private Boom boom;
	
	//a screenX holder
	int screenX;
	
	//to count the number of Misses
	int countMisses;
	
	//indicator that the enemy has just entered the game screen
	boolean flag;
	
	//an indicator if the game is Over
	private boolean isGameOver;
	
	//the score holder
	int score;
	
	//the high Scores Holder
	int highScore[] = new int[5];
	
	//Shared Prefernces to store the High Scores
	SharedPreferences sharedPreferences;
	
	//the mediaplayer objects to configure the background music
	//static MediaPlayer gameOnsound;
	final MediaPlayer killedEnemysound;
	final MediaPlayer gameOversound;
	
	//context to be used in onTouchEvent to cause the activity transition from GameAvtivity to MainActivity.
	Context context;
	
	//Class constructor
	public GameView(Context context, int screenX, int screenY) {
		super(context);
		//initializing player object
		//this time also passing screen size to player constructor
		player = new Player(context, screenX, screenY);
		
		//initializing drawing objects
		surfaceHolder = getHolder();
		paint = new Paint();
		
		//adding 100 stars you may increase the number
		int starNums = 100;
		for(int i = 0; i < starNums; i++) {
			Star star = new Star(screenX, screenY);
			stars.add(star);
		}
		
		/*//initializing enemy object array
		enemies = new Enemy[enemyCount];
		for(int i = 0; i < enemyCount; i++) {
			enemies[i] = new Enemy(context, screenX, screenY);
		}*/
		
		//single enemy initialization
		enemy = new Enemy(context, screenX, screenY);
		
		//initializing the Friend class object
		friend = new Friend(context, screenX, screenY);
		
		//initializing boom object
		boom = new Boom(context);
		
		this.screenX = screenX;
		
		countMisses = 0;
		
		isGameOver = false;
		
		//setting the score to 0 initially
		score = 0;
		
		sharedPreferences = context.getSharedPreferences("My_Game", Context.MODE_PRIVATE);

		//initializing the array high scores with the previous values
		highScore[0] = sharedPreferences.getInt("score1", 0);
		highScore[1] = sharedPreferences.getInt("score2", 0);
		highScore[2] = sharedPreferences.getInt("score3", 0);
		highScore[3] = sharedPreferences.getInt("score4", 0);
		
		//initializing the media players for the game sounds
		//gameOnsound = MediaPlayer.create(context, R.raw.gameon);
		killedEnemysound = MediaPlayer.create(context, R.raw.killedenemy);
		gameOversound = MediaPlayer.create(context, R.raw.gameover);

		//starting the game music as the game starts
		//gameOnsound.start();
		
		//initializing context
		this.context = context;
		
	}
	
	@Override
	public void run() {
		while(playing) {
			//to update the frame
			update();
			
			//to draw the frame
			draw();
			
			//to control
			control();
		}
	}
	
	
	private void update() {
		//incrementing score as time passes
		score++;
		
		//updating player position
		player.update();
		
		//setting boom outside the screen
		boom.setX(-250);
		boom.setY(-250);
		
		//Updating the stars with player speed
		for(Star star : stars) {
			star.update(player.getSpeed());
		}
		
		/*//updating the enemy coordinate with respect to player speed
		for(int i = 0; i < enemyCount; i++) {
			enemies[i].update(player.getSpeed());
			
			//if collision occurrs with player
			if(Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision())) {
				
				//displaying boom at that location
				boom.setX(enemies[i].getX());
				boom.setY(enemies[i].getY());
				
				enemies[i].setX(-250);
			}
		}*/
		
		//setting the flag true when the enemy just enters the screen
		if(enemy.getX() == screenX) {
			flag = true;
		}
		
		enemy.update(player.getSpeed());
		//if collision occurs with player
		if(Rect.intersects(player.getDetectCollision(), enemy.getDetectCollision())) {
			//displaying boom at that location
			boom.setX(enemy.getX());
			boom.setY(enemy.getY());
			
			//will play a sound at the collision between player and the enemy
			//playing a sound at the collision between player and the enemy
			killedEnemysound.start();
			
			enemy.setX(-250);
		} else {
			//if the enemy has just entered
			if(flag) {
				//if player's x coordinate is more than the enemies's x coordinate.i.e. enemy has just passed across the player
				if( player.getDetectCollision().exactCenterX() >=
				enemy.getDetectCollision().exactCenterX()){
					//increment countMisses
					countMisses++;
					
					//setting the flag false so that the else part is executed only when new enemy enters the screen
					flag = false;
					//if no of Misses is equal to 5, then game is over.
					if(countMisses == 5) {
						//setting playing false to stop the game.
						playing = false;
						isGameOver = true;
						
						//stopping the gameon music
//						gameOnsound.stop();
//						gameOnsound.reset();
						//play the game over sound
						gameOversound.start();
						
						//Assigning the scores to the highscore integer array
						for(int i = 0; i < 5 ;
						i++){
							if( highScore[i] <
							score){
								
								final int finalI = i;
								highScore[i] = score;
								break;
							}
						}
						
						//storing the scores through shared Preferences
						SharedPreferences.Editor e = sharedPreferences.edit();
						for(int i = 0; i < 5 ;
						i++){
							int j = i + 1;
							e.putInt("score" + j, highScore[i]);
						}
						e.apply();
					}
				}
			}
		}
		
		//updating the friend ships coordinates
		friend.update(player.getSpeed());
		//checking for a collision between player and a friend
		if(Rect.intersects(player.getDetectCollision(), friend.getDetectCollision())) {
			
			//displaying the boom at the collision
			boom.setX(friend.getX());
			boom.setY(friend.getY());
			//setting playing false to stop the game
			playing = false;
			//setting the isGameOver true as the game is over
			isGameOver = true;
			
			//stopping the gameon music
//			gameOnsound.stop();
//			gameOnsound.reset();
			//play the game over sound
			gameOversound.start();
			
			//Assigning the scores to the highscore integer array
			for(int i = 0; i < 5 ;
			i++){
				if( highScore[i] <
				score){
					
					final int finalI = i;
					highScore[i] = score;
					break;
				}
			}
			
			//storing the scores through shared Preferences
			SharedPreferences.Editor e = sharedPreferences.edit();
			for(int i = 0; i < 5 ;
			i++){
				int j = i + 1;
				e.putInt("score" + j, highScore[i]);
			}
			e.apply();
		}
	}
	
	private void draw() {
		//checking if surface is valid
		if(surfaceHolder.getSurface().isValid()) {
			//locking the canvas
			canvas = surfaceHolder.lockCanvas();
			//drawing a background color for canvas
			canvas.drawColor(Color.BLACK);
			
			//setting the paint color to white to draw the stars
			paint.setColor(Color.WHITE);
			
			//drawing all stars
			for(Star star : stars) {
				paint.setStrokeWidth(star.getStarWidth());
				canvas.drawPoint(star.getX(), star.getY(), paint);
			}
			
			//Drawing the player
			canvas.drawBitmap(
					player.getBitmap(),
					player.getX(),
					player.getY(),
					paint);
			
			/*//drawing the enemies
			for(int i = 0; i < enemyCount; i++) {
				canvas.drawBitmap(
						enemies[i].getBitmap(),
						enemies[i].getX(),
						enemies[i].getY(),
						paint
				);
			}*/
			
			//drawing the enemy
			canvas.drawBitmap(
					enemy.getBitmap(),
					enemy.getX(),
					enemy.getY(),
					paint
			);
			
			//drawing friends image
			canvas.drawBitmap(
					
					friend.getBitmap(),
					friend.getX(),
					friend.getY(),
					paint
			);
			
			//drawing boom image
			canvas.drawBitmap(
					boom.getBitmap(),
					boom.getX(),
					boom.getY(),
					paint
			);
			
			//drawing the score on the game screen
			paint.setTextSize(30);
			canvas.drawText("Score:" + score, 100, 50, paint);
			canvas.drawText("Misses:" + countMisses+"/5", 100, 80, paint);
			
			//draw game Over when the game is over
			if(isGameOver) {
				paint.setTextSize(150);
				paint.setTextAlign(Paint.Align.CENTER);
				
				int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
				canvas.drawText("Game Over", canvas.getWidth() / 2, yPos, paint);
			}
			
			//Unlocking the canvas
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}
	
	private void control() {
		try {
			gameThread.sleep(17);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		//when the game is paused
		//setting the variable to false
		playing = false;
		try {
			//stopping the thread
			gameThread.join();
		} catch(InterruptedException e) {
		}
	}
	
	public void resume() {
		//when the game is resumed
		//starting the thread again
		playing = true;
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:
				//When the user presses on the screen
				//we will do something here
				//stopping the boosting when screen is released
				player.stopBoosting();
				break;
			case MotionEvent.ACTION_DOWN:
				//When the user releases the screen
				//do something here
				//boosting the space jet when screen is pressed
				player.setBoosting();
				break;
		}
		//if the game's over, tappin on game Over screen sends you to MainActivity
		if(isGameOver) {
			if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//				gameOnsound.stop();
//				gameOnsound.reset();
				context.startActivity(new Intent(context, MainActivity.class));
			}
		}
		return true;
	}
	
	//stop the music on exit
	public static void stopMusic() {
//		if(gameOnsound!=null){
//			gameOnsound.stop();
//			gameOnsound.reset();
//		}
		
	}
}

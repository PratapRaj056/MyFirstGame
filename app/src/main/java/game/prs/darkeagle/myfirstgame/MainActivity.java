package game.prs.darkeagle.myfirstgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import game.prs.darkeagle.game.GameView;

public class MainActivity extends AppCompatActivity {

	private ImageButton buttonPlay;
	private ImageButton buttonHighScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//setting the orientation to landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//getting the button
		buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
		//getting the button
		buttonHighScore = (ImageButton) findViewById(R.id.buttonScore);
		
		
		//adding a click listener
		buttonPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//starting game activity
				startActivity(new Intent(MainActivity.this, GameActivity.class));
			}
		});
		
		//adding a click listener
		buttonHighScore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//starting game activity
				startActivity(new Intent(MainActivity.this, HighScoreActivity.class));
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						
						GameView.stopMusic();
						Intent startMain = new Intent(Intent.ACTION_MAIN);
						startMain.addCategory(Intent.CATEGORY_HOME);
						startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(startMain);
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
		
	}
}

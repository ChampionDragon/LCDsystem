package com.smdt.androidapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.smdt.SmdtManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenActivity extends Activity {

	private Button btn_screen;
	private VideoView videoView;
	private SmdtManager smdt;
	private String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.screen);

		btn_screen = (Button)findViewById(R.id.btn_screen);
		videoView = (VideoView)findViewById(R.id.videoView);
		final String sdPath = Environment.getExternalStorageDirectory().getPath().toString() + File.separator + "1.mp4";  
		File file = new File(sdPath);	
		Log.e("sdPath", "===file:" + file + ", sdPath: " + sdPath );
		if(file.exists()){	
			videoView.setMediaController(new MediaController(this));
			videoView.setVideoPath(sdPath);
		//	videoView.setVideoURI(Uri.parse("http://mvvideo1.meitudata.com/572552eaf0d841441.mp4"));
			videoView.start();
			videoView.requestFocus();
			videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
					mp.setLooping(true);
				}
			});
			videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {
					videoView.setVideoPath(sdPath);
					videoView.start();
				}
				
			});
		}
		
		btn_screen.setOnClickListener(new View.OnClickListener() {		
			@SuppressLint("SimpleDateFormat") 
			@Override
			public void onClick(View arg0) {
				smdt = SmdtManager.create(getApplicationContext());
				SimpleDateFormat sdformats = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
                String fileNames = sdformats.format(new Date(System.currentTimeMillis())) + ".png";
				smdt.smdtTakeScreenshot(filePath, fileNames, getApplicationContext());
			}
		});
		
	}
	
}

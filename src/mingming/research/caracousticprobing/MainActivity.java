package mingming.research.caracousticprobing;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements OnClickListener, OnItemSelectedListener , SensorEventListener{

	Spinner spinner_motortype;
	Spinner spinner_volume;
	Spinner spinner_soundsource;
	Spinner spinner_year;
	Spinner spinner_seat;
	Spinner spinner_area;
	Spinner spinner_subarea;
	Spinner spinner_country;
	Spinner spinner_onoff;
	
	Button bt_probing;
	CheckBox cb_auto;
	
	EditText et_note;
	TextView tv_counter;
	
	String motortype = "";
	String motoryear = "";
	String seat = "";
	String seatArea = "";
	String seatSubArea = "";
	String country = "";
	String onoff = "";
	
	float volumeRatio = 0.5f;
	
	File myDir;
	String mRecordFileName;  // file name of recorded sound clip
	File  mRecordFile = null;
	Handler handler = new Handler();
	
	String  timestamp = "timestamp";
	String note = "";
	
	ExtAudioRecorder extAudioRecorder = null;
	MediaPlayer mp = null;
	int mediaPlayedId = R.raw.sin20hz20000hzlin_0db16bit44100hz;
	
	Timer mTimerPeriodic = null;
	Timer mTimerOneTime = null;
	
	
	boolean autoprobing = false;
	boolean running = false;
	
	int counter = 0;
	
	
	
	
	private SensorManager mSensorManager;
	private Sensor mMagnetic;
	private Sensor mPressure;
	private Sensor mAccelerometer;
	private Sensor mGyro;
	float azimuth_angle  = 0;
	float pitch_angle  = 0;
	float roll_angle  = 0;
	float millibars_of_pressure = 0;
	float acc_x = 0;
	float acc_y = 0;
	float acc_z = 0;
	float gyo_x = 0;
	float gyo_y = 0;
	float gyo_z = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		spinner_motortype = (Spinner)findViewById(R.id.spinner_mt);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.motortypes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_motortype.setAdapter(adapter);
		spinner_motortype.setOnItemSelectedListener(this);

			
		spinner_volume = (Spinner)findViewById(R.id.spinner_volume);
		
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.volume, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_volume.setAdapter(adapter2);
		spinner_volume.setOnItemSelectedListener(this);
		
		
		spinner_soundsource = (Spinner)findViewById(R.id.spinner_soundsource);
		
		ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.soundsource, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_soundsource.setAdapter(adapter3);
		spinner_soundsource.setOnItemSelectedListener(this);
				
		
		spinner_year = (Spinner)findViewById(R.id.spinner_cy);
		
		ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.caryear, android.R.layout.simple_spinner_item);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_year.setAdapter(adapter4);
		spinner_year.setOnItemSelectedListener(this);
		
		
		spinner_seat = (Spinner)findViewById(R.id.spinner_seat);
		
		ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this, R.array.seat, android.R.layout.simple_spinner_item);
		adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_seat.setAdapter(adapter5);
		spinner_seat.setOnItemSelectedListener(this);
		
		spinner_area = (Spinner)findViewById(R.id.spinner_frontrear);
		
		ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(this, R.array.area, android.R.layout.simple_spinner_item);
		adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_area.setAdapter(adapter6);
		spinner_area.setOnItemSelectedListener(this);
		
		
		spinner_subarea = (Spinner)findViewById(R.id.spinner_subarea);
		
		ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(this, R.array.subarea, android.R.layout.simple_spinner_item);
		adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_subarea.setAdapter(adapter7);
		spinner_subarea.setOnItemSelectedListener(this);
		
		
		spinner_country = (Spinner)findViewById(R.id.spinner_country);
		
		ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(this, R.array.country, android.R.layout.simple_spinner_item);
		adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_country.setAdapter(adapter8);
		spinner_country.setOnItemSelectedListener(this);
		
		
		spinner_onoff = (Spinner)findViewById(R.id.spinner_onoff);
		
		ArrayAdapter<CharSequence> adapter9 = ArrayAdapter.createFromResource(this, R.array.motoronoff, android.R.layout.simple_spinner_item);
		adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_onoff.setAdapter(adapter9);
		spinner_onoff.setOnItemSelectedListener(this);
		
		bt_probing = (Button)findViewById(R.id.button_probing);
		bt_probing.setOnClickListener(this);
		bt_probing.setBackgroundColor(Color.GREEN);
		
		cb_auto = (CheckBox)findViewById(R.id.checkBox_auto);
		
		et_note = (EditText)findViewById(R.id.editText_note);
		
		tv_counter = (TextView)findViewById(R.id.textView_counter);
		
   	    String root = Environment.getExternalStorageDirectory().toString();
	    myDir = new File(root + "/soundprobingdata");    
	    myDir.mkdirs();
	    
	    
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch(parent.getId())
		{
			case R.id.spinner_mt:
				motortype = parent.getItemAtPosition(position).toString();
				break;
				
			case R.id.spinner_cy:
				motoryear = parent.getItemAtPosition(position).toString();
				break;
			
			case R.id.spinner_seat:
				seat = parent.getItemAtPosition(position).toString();
				break;
			
			case R.id.spinner_frontrear:
				seatArea = parent.getItemAtPosition(position).toString();
				break;
				
			case R.id.spinner_subarea:
				seatSubArea = parent.getItemAtPosition(position).toString();
				break;
				
			case R.id.spinner_country:
				country = parent.getItemAtPosition(position).toString();
				break;
				
			case R.id.spinner_onoff:
				onoff =  parent.getItemAtPosition(position).toString();
				break;
				
			case R.id.spinner_volume:
				
				volumeRatio = Float.parseFloat(parent.getItemAtPosition(position).toString());
				
				break;
				
			case R.id.spinner_soundsource:
				
				switch(position)
				{
					case 0:
						mediaPlayedId = R.raw.sin20hz20000hzlin_0db16bit44100hz;
						break;
					case 1:
						mediaPlayedId = R.raw.sweep16000hz20000hz05;
						break;
					default:
						break;
				}
			
				break;
							
			default:
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.button_probing:
				
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
				    // Do something for froyo and above versions
					final Date currentTime = new Date();
					final SimpleDateFormat sdf =  new SimpleDateFormat("MM-dd HH:mm:ss",Locale.US);
					timestamp = sdf.format(currentTime);
				} else{
				    // do something for phones running an SDK before ICE 
					//Log.i("Tag","Version: " + currentapiVersion);
				}
				
				note = et_note.getText().toString();
				
				mRecordFileName =  seat + "_" + seatArea + "_" + seatSubArea + "_" +  azimuth_angle + "_" + pitch_angle + "_" + roll_angle + "_"  + acc_x +"_" + acc_y +"_" + acc_z +"_" + gyo_x +"_" + gyo_y + "_" + gyo_z +"_" + millibars_of_pressure + "_" + motortype + "_" + onoff +"_"  + motoryear + "_" + country + "_" + note  + "_"  + volumeRatio + "_" + timestamp + ".wav";
				
				mRecordFile = new File(myDir,mRecordFileName);
				
				autoprobing  = cb_auto.isChecked();
				
				if(autoprobing)
				{
					running = !running;
					if(running)
					{
						bt_probing.setText("Stop");
						bt_probing.setBackgroundColor(Color.RED);
						startPeriodProbing();
					}
					else
					{
						bt_probing.setText("Probing");
						bt_probing.setBackgroundColor(Color.GREEN);
						stopPeriodProbing();
					}
				}
				else
				{
					bt_probing.setClickable(false);
					bt_probing.setBackgroundColor(Color.GRAY);
					startOneProbing();
					counter++;
					tv_counter.setText(counter + "");
				}

				break;
			default:
				break;
		}
	}
		
	
	private void startRecordSound()
	{
		// Start recording
		//extAudioRecorder = ExtAudioRecorder.getInstanse(true);	  // Compressed recording (AMR)
		extAudioRecorder = ExtAudioRecorder.getInstanse(false); // Uncompressed recording (WAV)

		extAudioRecorder.setOutputFile(mRecordFile.getAbsolutePath());
		//Log.i("TAG", "after init extAudioRecorder");
		
		extAudioRecorder.prepare();
		//Log.i("TAG", "after preparing");
		
		extAudioRecorder.start();		
	}
	
	private void playSweepSound() {
		/*
		 * <item>SineSweep</item> <item>SineSweep(15k-20k)</item>
		 * <item>MLS</item> <item>PinkNoise</item> <item>WhiteNoise</item>
		 */
		final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				(int) (volumeRatio * mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
		// Log.i("TAG","Max Volume: " +
		// mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		mp = MediaPlayer.create(getApplicationContext(),mediaPlayedId);
		
		if (mp != null) {
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					ReleaseMediaPlayer();
					startTimerTask(Utils.probing_duration);
				}
			});

		}

	}
	
	private void startTimerTask(int timeinterval)
	{
		
		 new CountDownTimer(timeinterval, timeinterval) {

		     public void onTick(long millisUntilFinished) {
		    	 //bt_sweep.setText("seconds remaining: " + millisUntilFinished / 1000);
		     }

		     public void onFinish() {		    	
		    	 StopRecordSound();
		    	 if(!autoprobing)
		    	 {
			    	 bt_probing.setText("Probing");
			    	 bt_probing.setClickable(true);
			    	 bt_probing.setBackgroundColor(Color.GREEN);
		    	 }
		     }
		  }.start();
	}
	
	
	private void ReleaseMediaPlayer()
	{
		if( mp != null)
		{
	    	 mp.reset();
	    	 mp.release();
    	     mp = null;
		}
	}
	
	private void StopRecordSound()
	{
		// Stop recording
		extAudioRecorder.stop();
		extAudioRecorder.reset();
		extAudioRecorder.release();
	}
	
	
	private void startPeriodProbing()
	{
		if(mTimerPeriodic != null)
		{
			mTimerPeriodic.cancel();
		}
		
		mTimerPeriodic = new Timer();
		mTimerPeriodic.scheduleAtFixedRate(new myTimerTask(), 0, Utils.repeat_period); 
	}
	
	class myTimerTask extends TimerTask{

		@Override
		public void run() {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			    // Do something for froyo and above versions
				final Date currentTime = new Date();
				final SimpleDateFormat sdf =  new SimpleDateFormat("MM-dd HH:mm:ss",Locale.US);
				timestamp = sdf.format(currentTime);
			} else{
			    // do something for phones running an SDK before ICE 
				//Log.i("Tag","Version: " + currentapiVersion);
			}
			
			note = et_note.getText().toString();
			
			mRecordFileName =  seat + "_" + seatArea + "_" + seatSubArea + "_" +  azimuth_angle + "_" + pitch_angle + "_" + roll_angle + "_"  + acc_x +"_" + acc_y +"_" + acc_z +"_" + gyo_x +"_" + gyo_y + "_" + gyo_z +"_" + millibars_of_pressure + "_" + motortype + "_" + onoff +"_"  + motoryear + "_" + country + "_" + note  + "_"  + volumeRatio + "_" + timestamp + ".wav";
			
			mRecordFile = new File(myDir,mRecordFileName);
			
			
			startRecordSound();
			
			playSweepSound();
			
			
			
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tv_counter.setText(counter + "");
				}});
			
			counter ++;
		}
	}
	
	private void stopPeriodProbing()
	{
		if(mTimerPeriodic != null)
		{
			mTimerPeriodic.cancel();
		}
	}
	
	
	private void startOneProbing()
	{
		startRecordSound();
		
		playSweepSound();
	}
		
	
	protected void onResume()
	{		
		super.onResume();
		mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onPause()
	{	
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		 if( Sensor.TYPE_PRESSURE == event.sensor.getType() ) 
		 {
			 millibars_of_pressure = event.values[0];
		 }
		 else  if( Sensor.TYPE_MAGNETIC_FIELD == event.sensor.getType() ) 
		 {
			azimuth_angle  = event.values[0];  // degrees of rotation around the z axis
			pitch_angle  = event.values[1];    //degrees of rotation around the x axis
			roll_angle  = event.values[2];     //degrees of rotation around the y axis
		 }
		 else  if( Sensor.TYPE_ACCELEROMETER == event.sensor.getType() ) 
		 {
			acc_x  = event.values[0];  // degrees of rotation around the z axis
			acc_y  = event.values[1];    //degrees of rotation around the x axis
			acc_z  = event.values[2];     //degrees of rotation around the y axis
		 }
		 else  if( Sensor.TYPE_GYROSCOPE == event.sensor.getType() ) 
		 {
			gyo_x  = event.values[0];  // degrees of rotation around the z axis
			gyo_y  = event.values[1];    //degrees of rotation around the x axis
			gyo_z  = event.values[2];     //degrees of rotation around the y axis
		 }
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}

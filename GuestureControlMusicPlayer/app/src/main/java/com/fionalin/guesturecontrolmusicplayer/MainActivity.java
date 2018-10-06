package com.fionalin.guesturecontrolmusicplayer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private Handler mHandler = new Handler();

    // Sensor
    private SensorManager mSensorManager;
    private Sensor mLight;

    // field for keep track of troung counts
    private int troungCount = 0;
    private LightSensorData data = new LightSensorData(4);
    private long from = System.currentTimeMillis();
    private double lastXPoint = 0;

    private GraphView graph;
    private LineGraphSeries<DataPoint> series;


    private Button forward, pause, play, backward, stop;
    private MediaPlayer mediaPlayer;


    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();

    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView name, time, duration;


    public static int oneTimeOnly = 0;
    private String STATE = "START";
    private String NEW_STATE = "START";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_UI);

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{});
        initGraphView(series, graph);

        mHandler.postDelayed(updateGraphTroung, 20);
        mHandler.postDelayed(updatePlayer, 2500);

        initUI(R.raw.song);
    }

    private Runnable updateGraphTroung = new Runnable() {
        @Override
        public void run() {
            updateGraph();
            getCtrlCount();
            myHandler.postDelayed(this, 20);
        }
        // 0.02 second, sensor sample data period = 5 sec / sampleSize
    };

    private Runnable updatePlayer = new Runnable() {
        @Override
        public void run() {
            updateCtrl();
            myHandler.postDelayed(this, 3750);
        }
    };

    private void updateGraph() {
        Map.Entry<Double, Double> lastData = this.data.getLastData();
        if (lastData == null) {
            return;
        }
        series.appendData(new DataPoint(lastData.getKey(), lastData.getValue()), false, 550);

        graph.getViewport().setMinX(lastData.getKey() - 5);
        graph.getViewport().setMaxX(lastData.getKey() + 0.01);
    }

    private void getCtrlCount() {
        if (lastXPoint > 5) {
            int old = this.troungCount;
            this.troungCount = data.getTroungCount(data.getLastData().getKey(), 0.75);

            if (old == troungCount || old > troungCount) {
            } else {
                switch (this.troungCount) {
                    case 1:
                        if (NEW_STATE != "PLAY" && STATE != "PLAY") {
                            NEW_STATE = "PLAY";
                        } else if (STATE == "PLAY" && NEW_STATE != "PAUSE") {
                            NEW_STATE = "PAUSE";
                        }
                        break;
                    case 2:
                        if (STATE == "PLAY" && NEW_STATE != "FORWARD") {
                            NEW_STATE = "FORWARD";
                        }
                        break;
                    case 3:
                        if (STATE == "PLAY" && NEW_STATE != "BACKWARD") {
                            NEW_STATE = "BACKWARD";
                        }
                        break;
                    case 4:
                        if (STATE != "STOP" && NEW_STATE != "STOP") {
                            NEW_STATE = "STOP";
                        }
                        break;
                    default:
                }
            }

        }
        String tCount = String.format("STATE: %s, NEXT STATE: %s, Troung Count: %d on %.2f ", this.STATE, this.NEW_STATE, this.troungCount, lastXPoint);
        TextView countText = findViewById(R.id.troungCount);
        countText.setText(tCount);
    }

    private void updateCtrl() {
        if (this.NEW_STATE == this.STATE) {
            return;
        }
        switch (this.NEW_STATE) {
            case "PLAY":
                playSong();
                break;
            case "PAUSE":
                pauseSong();
                break;
            case "STOP":
                stopSong();
                break;
            case "FORWARD":
                forward();
                break;
            case "BACKWARD":
                backward();
                break;
            default:
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
//            Sensor.TYPE_LIGHT:
//            values[0]: Ambient light level in SI lux units

            long now = System.currentTimeMillis();

            lastXPoint += (now - from) / 1000.0;

            this.data.putData(lastXPoint, event.values[0]);

            this.from = now;

        }
    }

    private void initUI(int song) {
        forward = (Button) findViewById(R.id.forward);
        pause = (Button) findViewById(R.id.pause);
        play = (Button) findViewById(R.id.play);
        backward = (Button) findViewById(R.id.backward);
        stop = (Button) findViewById(R.id.stop);

        name = (TextView) findViewById(R.id.songname);
        time = (TextView) findViewById(R.id.timing);
        duration = (TextView) findViewById(R.id.duration);
        name.setText("Song.mp3");

        time.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );

        duration.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );
        mediaPlayer = MediaPlayer.create(this, song);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        pause.setEnabled(false);


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSong();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseSong();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward();
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backward();
            }
        });


    }

    private void backward() {
        Toast.makeText(getApplicationContext(), this.STATE + " " + this.NEW_STATE, Toast.LENGTH_SHORT).show();

        this.STATE = "BACKWARD";
        int temp = (int) startTime;

        if ((temp - backwardTime) > 0) {
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(), "You have Jumped backward 5seconds", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
        }
        this.STATE = "PLAY";
        this.NEW_STATE = "PLAY";
    }

    private void forward() {
        Toast.makeText(getApplicationContext(), this.STATE + " " + this.NEW_STATE, Toast.LENGTH_SHORT).show();

        this.STATE = "FORWARD";
        int temp = (int) startTime;

        if ((temp + forwardTime) <= finalTime) {
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
        }
        this.STATE = "PLAY";
        this.NEW_STATE = "PLAY";
    }

    private void pauseSong() {
        Toast.makeText(getApplicationContext(), this.STATE + " " + this.NEW_STATE, Toast.LENGTH_SHORT).show();

        this.STATE = "PAUSE";

        mediaPlayer.pause();
        pause.setEnabled(false);
        play.setEnabled(true);
        this.NEW_STATE = "START";

    }

    private void stopSong() {
        if (this.mediaPlayer.isPlaying()) {
            this.STATE = "STOP";
            Toast.makeText(getApplicationContext(), this.STATE + " " + this.NEW_STATE, Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(), "Stopping sound", Toast.LENGTH_SHORT).show();
            mediaPlayer.pause();
            this.startTime = 0;
            mediaPlayer.seekTo(0);
            pause.setEnabled(false);
            play.setEnabled(true);
            myHandler.removeCallbacks(UpdateSongTime);
            time.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int) startTime);
            this.NEW_STATE = "START";
        }
    }

    private void playSong() {
        if (!this.mediaPlayer.isPlaying()) {
            Toast.makeText(getApplicationContext(), this.STATE + " " + this.NEW_STATE, Toast.LENGTH_SHORT).show();
            this.STATE = "PLAY";
            mediaPlayer.start();
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();

            if (oneTimeOnly == 0) {
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }

            duration.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    finalTime)))
            );

            time.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    startTime)))
            );
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
            pause.setEnabled(true);
            play.setEnabled(false);
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            time.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    private void initGraphView(LineGraphSeries series, GraphView graph) {
        graph.addSeries(series);

        Viewport viewport = graph.getViewport();
        graph.onDataChanged(false, false);

        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(lastXPoint - 5);
        viewport.setMaxX(lastXPoint + 5);

        viewport.setYAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        viewport.setMinY(0);
        viewport.setMaxY(40000);
        viewport.setScrollable(true);
        viewport.setScalable(true);
        viewport.setScalableY(true);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

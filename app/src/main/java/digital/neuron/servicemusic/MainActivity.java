package digital.neuron.servicemusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import digital.neuron.servicemusic.data.Albom;
import digital.neuron.servicemusic.data.Track;
import digital.neuron.servicemusic.network.MusicApi;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private MusicController controller;
    private Handler handler;
    private ImageView playPause;
    private SeekBar seekBar;
    private TrackAdapter trackAdapter;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            controller = ((MyBinder)service).getMusicController();
            startProgressListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            controller = null;
            stopProgressListener();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showListMusic();
        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(MainActivity.this, MusicService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopProgressListener();
        unbindService(connection);
        controller = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE )
    void showListMusic() {
        RecyclerView trackListView = findViewById(R.id.trackList);
        trackListView.setLayoutManager(new LinearLayoutManager(this));
        trackAdapter = new TrackAdapter();
        trackAdapter.setTrackClickListener(new TrackHolder.TrackClickListener() {
            @Override
            public void onTrackClicked(Track track) {
                MusicService.start(MainActivity.this, track.getPath());
                startProgressListener();
                updatePlayPauseBtn(true);
            }
        });
        trackListView.setAdapter(trackAdapter);

        playPause = findViewById(R.id.playPauseBtn);
        seekBar = findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && controller != null) {
                    controller.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller != null && !controller.isPlaying()) {
                    controller.play();
                    startProgressListener();
                    updatePlayPauseBtn(true);
                } else if (controller != null && controller.isPlaying()) {
                    controller.pause();
                    stopProgressListener();
                    updatePlayPauseBtn(false);
                }
            }
        });
        findViewById(R.id.stopBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicService.stop(MainActivity.this);
                updatePlayPauseBtn(false);
            }
        });

        getMusic();
    }

    private void updatePlayPauseBtn(boolean isPlaying) {
        if (isPlaying) {
            playPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));
        } else {
            playPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
        }
    }

    private void getMusic() {
        MusicApi.getMusicService().getAlbom(null).enqueue(new Callback<List<Albom>>() {
            @Override
            public void onResponse(Call<List<Albom>> call, Response<List<Albom>> response) {
                List<Track> tracks = new ArrayList<>();
                for (Albom albom : response.body()) {
                    tracks.addAll(albom.getTracks());
                }
                trackAdapter.setTracks(tracks);
            }

            @Override
            public void onFailure(Call<List<Albom>> call, Throwable t) {

            }
        });
    }

    private void startProgressListener() {
        handler.removeCallbacks(null);
        if (controller != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (controller != null && controller.isPlaying()) {
                        seekBar.setMax(controller.getDuration());
                        seekBar.setProgress(controller.getSeek());
                        handler.postDelayed(this, 1000);
                    }
                }
            });
        }
    }

    private void stopProgressListener() {
        handler.removeCallbacks(null);
    }

}

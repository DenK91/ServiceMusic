package digital.neuron.servicemusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private List<Track> mMusicList = new ArrayList<>();
    private MusicController controller;
    private Handler handler;
    private ImageView playPause;
    private SeekBar seekBar;
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
        getMusic();

        ListView mListView = findViewById(R.id.listView1);
        String[] names = new String[mMusicList.size()];
        for (int i = 0; i < mMusicList.size(); i++) {
            names[i] = mMusicList.get(i).getName();
        }
        mListView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MusicService.start(MainActivity.this, mMusicList.get(arg2).getPath());
                startProgressListener();
                updatePlayPauseBtn(true);
            }
        });

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
    }

    private void updatePlayPauseBtn(boolean isPlaying) {
        if (isPlaying) {
            playPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause));
        } else {
            playPause.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
        }
    }

    private void getMusic() {
        final Cursor musicCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA},
                null,
                null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        if (musicCursor != null) {
            mMusicList.clear();
            if (musicCursor.moveToFirst()) {
                do {
                    mMusicList.add(new Track(
                            musicCursor.getString(0),
                            musicCursor.getString(1)));
                } while (musicCursor.moveToNext());
            }
            musicCursor.close();
        }
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

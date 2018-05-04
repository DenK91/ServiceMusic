package digital.neuron.servicemusic;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service implements MusicController {

    public final static String START_FOREGROUND = "MusicServiceStart";
    public final static String STOP_FOREGROUND = "MusicServiceStop";
    public final static String PATH = "path_key";
    private MyBinder binder = new MyBinder(this);
    private MediaPlayer mMediaPlayer;

    public static void start(Context context, String path) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(START_FOREGROUND);
        intent.putExtra(PATH, path);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(STOP_FOREGROUND);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (START_FOREGROUND.equals(intent.getAction())) {
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    startForeground(1, createNotif("Notification's title", "Notification's text", R.drawable.ic_stat_name));
                }
                start(intent.getStringExtra(PATH));
            } else if (STOP_FOREGROUND.equals(intent.getAction())) {
                stopForeground(true);
                stop();
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void start(String path) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void play() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public int getSeek() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    private Notification createNotif(String title, String text, int iconId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channel_01";
            return new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(iconId)
                    .build();
        } else {
            return new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(iconId)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .build();
        }
    }

}

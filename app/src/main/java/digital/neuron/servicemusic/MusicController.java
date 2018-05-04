package digital.neuron.servicemusic;

public interface MusicController {
    void play();
    void pause();
    void seekTo(int pos);
    int getSeek();
    int getDuration();
    boolean isPlaying();
}

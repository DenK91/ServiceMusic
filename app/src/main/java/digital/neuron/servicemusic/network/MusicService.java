package digital.neuron.servicemusic.network;

import java.util.List;

import digital.neuron.servicemusic.data.Albom;
import digital.neuron.servicemusic.data.Track;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicService {

    @GET("albom")
    Call<List<Albom>> getAlbom(@Query("name") String name);

    @GET("tracks")
    Call<List<Track>> getTrack(@Query("id") String id);
}

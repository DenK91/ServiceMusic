package digital.neuron.servicemusic.data;

import com.google.gson.annotations.SerializedName;

import digital.neuron.servicemusic.network.MusicApi;

public class Track {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("artist")
    private Artist artist;

    @SerializedName("albom")
    private String albom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getAlbom() {
        return albom;
    }

    public void setAlbom(String albom) {
        this.albom = albom;
    }

    public String getPath() {
        return MusicApi.ENDPOINT + "tracks/" + id + "/path";
    }

    @Override
    public String toString() {
        return "Track{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artist=" + artist +
                ", albom='" + albom + '\'' +
                '}';
    }
}

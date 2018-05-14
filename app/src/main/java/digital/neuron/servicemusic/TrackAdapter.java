package digital.neuron.servicemusic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import digital.neuron.servicemusic.data.Track;

public class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {

    private List<Track> tracks = new ArrayList<>();
    private TrackHolder.TrackClickListener trackClickListener;

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item, parent, false),
                trackClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackHolder holder, int position) {
        holder.bind(tracks.get(position));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void setTrackClickListener(TrackHolder.TrackClickListener trackClickListener) {
        this.trackClickListener = trackClickListener;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks.clear();
        this.tracks.addAll(tracks);
        notifyDataSetChanged();
    }
}

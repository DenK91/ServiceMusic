package digital.neuron.servicemusic;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import digital.neuron.servicemusic.data.Track;

public class TrackHolder extends RecyclerView.ViewHolder {

    private Track track;
    private TextView trackName;

    public interface TrackClickListener {
        void onTrackClicked(Track track);
    }

    public TrackHolder(View item, final TrackClickListener trackClickListener) {
        super(item);
        trackName = item.findViewById(R.id.trackName);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trackClickListener != null) {
                    trackClickListener.onTrackClicked(track);
                }
            }
        });
    }

    public void bind(Track track) {
        this.track = track;
        trackName.setText(track.getArtist().getName() + " - " + track.getName());
    }
}

package digital.neuron.servicemusic;

import android.os.Binder;

class MyBinder extends Binder {
    private MusicController controller;

    public MyBinder(MusicController controller) {
        this.controller = controller;
    }

    MusicController getMusicController() {
        return controller;
    }
}

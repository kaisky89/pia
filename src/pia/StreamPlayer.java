package pia;

import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * StreamPlayer is an audio player for an audio stream or file.
 * It's a proxy of VLCJ's headless player with many convenience methods.
 * This class is meant to be instantiated once for every URI. IF you need to play another file,
 * create a new instance.
 *
 */
public class StreamPlayer extends MediaPlayerEventHandler {

    private final String url;
    private final AudioMediaPlayerComponent mediaPlayerComponent;
    private final MediaPlayer player;

    // alternative:
    //private final MediaPlayerFactory factory = new MediaPlayerFactory();
    //private final MediaPlayer player = factory.newHeadlessMediaPlayer();

    public StreamPlayer(String url) {
        mediaPlayerComponent = new AudioMediaPlayerComponent();
        player = mediaPlayerComponent.getMediaPlayer();
        setupEventListeners();
        this.url = url;
        // Many methods of MediaPlayer will return null (e.g. getMediaState()) if prepareMedia()
        // or playMedia() wasn't called at least once!
        player.prepareMedia(url);

        //player.playMedia(url);
        //player.stop();
    }

    public void play() {
        System.out.println("play");
        if (isPaused())
            player.play();
        else if (isStopped())
            player.playMedia(url);
    }
    public void pause() {
        System.out.println("pause");
        player.pause();
    }
    public void seekRelative(long seconds) {
        long newTime = player.getTime() + seconds*1000;
        if (newTime < 0) {
            player.setTime(0);
        } else {
            player.setTime(newTime);
        }
    }
    public void seek(long seconds) {
        player.setTime(seconds*1000);
    }

    public boolean isPlaying() {
        return player.getMediaState().equals(libvlc_state_t.libvlc_Playing);
    }
    public boolean isPaused() {
        return player.getMediaState().equals(libvlc_state_t.libvlc_Paused);
    }
    public void setVolume(int value) {
        if (value < 0)
            value = 0;
        else if (value > 200)
            value = 200;
        player.setVolume(value);
    }

    /**
     * returns true if playback state is stopped or 'nothing special', otherwise false
     * @return
     */
    public boolean isStopped() {
        libvlc_state_t state = player.getMediaState();
        return (state.equals(libvlc_state_t.libvlc_Stopped) ||
                state.equals(libvlc_state_t.libvlc_NothingSpecial) ||
                state.equals(libvlc_state_t.libvlc_Ended));
    }
    public float getBufferingProgress() {
        return 0;
    }

    public void release() {
        player.stop();
        mediaPlayerComponent.release(true);
    }

    @Override
    MediaPlayer getMediaPlayer() {
        return player;
    }

}
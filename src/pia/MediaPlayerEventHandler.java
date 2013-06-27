package pia;

import sun.plugin.dom.exception.InvalidStateException;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

import java.util.Vector;

/**
 * This is a base class for media players that implements a nicer event handling and a clean API.
 * Be sure to call this class' constructor from subclass (<code>super()</code>).
 */
abstract public class MediaPlayerEventHandler {

    private Vector<PlayerEventListener> playingListeners    = new Vector<>(3, 1);
    private Vector<PlayerEventListener> pausedListeners     = new Vector<>(3, 1);
    private Vector<PlayerEventListener> stoppedListeners    = new Vector<>(3, 1);
    private Vector<PlayerEventListener> finishedListeners   = new Vector<>(3, 1);
    private Vector<PlayerEventListener> timeListeners       = new Vector<>(3, 1);
    private Vector<PlayerEventListener> errorListeners      = new Vector<>(3, 1);
    private Vector<PlayerEventListener> stateListeners      = new Vector<>(3, 1);
    private Vector<PlayerEventListener> bufferingListeners  = new Vector<>(3, 1);

    private boolean isSetUp = false;

    abstract MediaPlayer getMediaPlayer();

    /**
     * This should be called in the constructor after a player instance has been created.
     * It connects the clunky player event handler with a more flexible one.
     * Subsequent calls will have no effect
     */
    public void setupEventListeners() {
        if (! isSetUp)
            getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventListener() {
                @Override
                public void mediaChanged(MediaPlayer player, libvlc_media_t t, String s) {

                }

                @Override
                public void opening(MediaPlayer player) {

                }

                @Override
                public void buffering(MediaPlayer player, float v) {
                    for (PlayerEventListener listener : bufferingListeners)
                        listener.actionPerformed(player);
                }

                @Override
                public void playing(MediaPlayer player) {
                    for (PlayerEventListener listener : playingListeners)
                        listener.actionPerformed(player);
                }

                @Override
                public void paused(MediaPlayer player) {
                    for (PlayerEventListener listener : pausedListeners)
                        listener.actionPerformed(player);
                }

                @Override
                public void stopped(MediaPlayer player) {
                    for (PlayerEventListener listener : stoppedListeners)
                        listener.actionPerformed(player);
                }

                @Override
                public void forward(MediaPlayer player) {

                }

                @Override
                public void backward(MediaPlayer player) {

                }

                @Override
                public void finished(MediaPlayer player) {
                    for (PlayerEventListener listener : finishedListeners)
                        listener.actionPerformed(player);
                }

                @Override
                public void timeChanged(MediaPlayer player, long time) {
                    for (PlayerEventListener listener : timeListeners)
                        listener.actionPerformed(player);

                }

                @Override
                public void positionChanged(MediaPlayer player, float v) {

                }

                @Override
                public void seekableChanged(MediaPlayer player, int i) {

                }

                @Override
                public void pausableChanged(MediaPlayer player, int i) {

                }

                @Override
                public void titleChanged(MediaPlayer player, int i) {

                }

                @Override
                public void snapshotTaken(MediaPlayer player, String s) {

                }

                @Override
                public void lengthChanged(MediaPlayer player, long l) {

                }

                @Override
                public void videoOutput(MediaPlayer player, int i) {

                }

                @Override
                public void error(MediaPlayer player) {
                    for (PlayerEventListener listener : errorListeners)
                        listener.actionPerformed(player);
                }

                @Override
                public void mediaMetaChanged(MediaPlayer player, int i) {

                }

                @Override
                public void mediaSubItemAdded(MediaPlayer player, libvlc_media_t t) {

                }

                @Override
                public void mediaDurationChanged(MediaPlayer player, long l) {

                }

                @Override
                public void mediaParsedChanged(MediaPlayer player, int i) {

                }

                @Override
                public void mediaFreed(MediaPlayer player) {

                }

                @Override
                public void mediaStateChanged(MediaPlayer player, int newState) {
                    for (PlayerEventListener listener : stateListeners)
                        listener.actionPerformed(player);
                }

                @Override
                public void newMedia(MediaPlayer player) {

                }

                @Override
                public void subItemPlayed(MediaPlayer player, int i) {

                }

                @Override
                public void subItemFinished(MediaPlayer player, int i) {

                }

                @Override
                public void endOfSubItems(MediaPlayer player) {

                }
            });
        isSetUp = true;
    }

    public void addPlayingListener(PlayerEventListener listener) {
        checkSetup();
        playingListeners.add(listener);
    }

    public void addPausedListener(PlayerEventListener listener) {
        checkSetup();
        pausedListeners.add(listener);
    }

    public void addStoppedListener(PlayerEventListener listener) {
        checkSetup();
        stoppedListeners.add(listener);
    }

    public void addFinishedListener(PlayerEventListener listener) {
        checkSetup();
        finishedListeners.add(listener);
    }

    public void addTimeListener(PlayerEventListener listener) {
        checkSetup();
        timeListeners.add(listener);
    }

    public void addErrorListener(PlayerEventListener listener) {
        checkSetup();
        errorListeners.add(listener);
    }
    public void addStateListener(PlayerEventListener listener) {
        checkSetup();
        stateListeners.add(listener);
    }
    // check if this class' constructor has been called and throw an error if not
    private void checkSetup() throws InvalidStateException {
        if (! isSetUp) {
            throw new InvalidStateException("Call "+getClass().getCanonicalName()+"'s " +
                    "setupEventListeners() in subclass!");
        }
    }
}

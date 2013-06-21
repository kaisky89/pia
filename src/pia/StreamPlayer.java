package pia;

import uk.co.caprica.vlcj.player.*;
import uk.co.caprica.vlcj.player.headless.*;

/**
 * Audio player for an audio stream or file.
 * The <code>play()</code> method creates a new thread, so make sure you stop playback or call
 * <code>Thread.currentThread().join()</code> to wait for it to exit by itself (which it doesn't
 * yet).
 */
public class StreamPlayer {

    private final String url;
    private final MediaPlayerFactory factory;
    private static StreamPlayer globalInstance;

    public final HeadlessMediaPlayer player;

    public StreamPlayer createInstance(String url) {
        if (globalInstance == null)
            return globalInstance = new StreamPlayer(url);
        else
            throw new IllegalStateException("Global instance already created");
    }

    public static StreamPlayer getInstance() {
        if (globalInstance != null)
            return globalInstance;
        else
            throw new IllegalStateException("Please create an instance first!");

    }

    public StreamPlayer(String url) {
        this.url = url;
        factory = new MediaPlayerFactory();
        player = factory.newHeadlessMediaPlayer();
    }

    // This class is executable for test purposes
    public static void main(String[] args) {
        String url = "http://mp1.somafm.com:8808";
        new StreamPlayer(url).playStream();
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void playStream() {
        player.playMedia(url);
    }

    public void destroy() {
        player.stop();
        player.release();
        factory.release();
        //try {
        //    System.out.println("join");
        //    Thread.currentThread().join();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        //System.out.println("joined");
    }
}
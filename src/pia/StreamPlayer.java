package pia;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;

public class StreamPlayer {
    private HeadlessMediaPlayer player;

    public static void main(String[] args) {
        new StreamPlayer().play();
    }

    public void play() {
        MediaPlayerFactory factory = new MediaPlayerFactory();
        player = factory.newHeadlessMediaPlayer();
        String url = "http://mp1.somafm.com:8808";
        //String url = "somafm.com/startstream=lush130.pls";
        //String url = "http://somafm.com/play/lush";
        //String url = "http://meta.metaebene.me/media/mm/mm112-in-werbung-bewegen.mp3";
        System.out.println(player.playMedia(url));
//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
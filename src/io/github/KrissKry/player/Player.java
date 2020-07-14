package io.github.KrissKry.player;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Player {
    private static ListView<String> queue = new ListView<>();


    private static Media trackMedia;// = new Media()

    private static MediaPlayer mediaplayer;

    private static String track = "";


    // play/pause button has been pressed
    public static void updateMediaplayerStatus(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) {

        //no tracks loaded
        if (appMusicList.getItems().size() == 0)
            return;

        //no track chosen
        //ewentualnie dodac ze jak cos gra to zatrzxymac??
        if (appMusicList.getSelectionModel().getSelectedItem() == null)
            return;


        //if mediaplayer has not been used before or is in unknown status, load new track
        if ( mediaplayer == null || mediaplayer.getStatus().equals(MediaPlayer.Status.UNKNOWN) ) {


            track = appMusicList.getSelectionModel().getSelectedItem();
            loadTrack(appMusicList, fullTrackListPath);


        //if music is playing
        } else if ( mediaplayer.getStatus().equals(MediaPlayer.Status.PLAYING) ) {
                mediaplayer.pause();


        //if music is paused
        } else if ( mediaplayer.getStatus().equals(MediaPlayer.Status.PAUSED) ) {

            //still on the same track selected, resume playing
            if ( appMusicList.getSelectionModel().getSelectedItem().equals(track) ) {

                mediaplayer.play();

            //different song selected, new MediaPlayer, resume playing
            } else {


                track = appMusicList.getSelectionModel().getSelectedItem();
                loadTrack(appMusicList, fullTrackListPath);

            }
        }
    }


    public static boolean isPlaying() {

        if ( mediaplayer != null) {
            System.out.println(mediaplayer.getStatus().toString());

            if (mediaplayer.getStatus() == MediaPlayer.Status.PLAYING)
                return true;
        }
        return false;
    }


    public static void nextSong(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) {

        //empty queue
        if ( queue.getItems().size() == 0 ) {

            //get currently selected track
            int index = appMusicList.getSelectionModel().getSelectedIndex();

            //go to beginning if at the end of tracklist
            if (index == (appMusicList.getItems().size() - 1))
                index = 0;
            else
                index++;

            //select new(next) track
            appMusicList.getSelectionModel().select(index);

            try {
                mediaplayer.pause();
            } catch (NullPointerException x) {
                System.out.println(x.getMessage());
            }

            //load track
            track = appMusicList.getItems().get(index);
            loadTrack(appMusicList, fullTrackListPath);

            //tracks in queue, play next from queue
        } else {
            try {
                mediaplayer.pause();
            } catch (NullPointerException x) {
                System.out.println(x.getMessage());
            }

            //get next track from queue
            track = queue.getItems().get(0);

            //select track in app tracklist
            appMusicList.getSelectionModel().select( songTitle(track) );

            //remove track from queue (as its playing)
            queue.getItems().remove(0);

            //turn on media
            trackMedia = new Media( new File( track ).toURI().toString() );
            mediaplayer = new MediaPlayer(trackMedia);
            Runnable playMusic = () -> mediaplayer.play();
            mediaplayer.setOnReady(playMusic);

        }
    }

    public static void prevSong(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) {

        //check if index in boundaries
        int index = appMusicList.getSelectionModel().getSelectedIndex();

        //keep index in boundaries
        if (index != 0)
            index--;


        //select prev track
        appMusicList.getSelectionModel().select(index);


        try {
            //if sth is playing catch exception if mediaplayer not yet created
            mediaplayer.pause();
        } catch (NullPointerException x) {
            System.out.println(x.getMessage());
        }


        //update track String, load track in app
        track = appMusicList.getItems().get(index);
        loadTrack(appMusicList, fullTrackListPath);

    }

    public static void loadTrack(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) {

        int trackIndex = appMusicList.getSelectionModel().getSelectedIndex();
        trackMedia = new Media( new File( fullTrackListPath.get(trackIndex) ).toURI().toString());

        mediaplayer = new MediaPlayer(trackMedia);
        Runnable playMusic = () -> mediaplayer.play();
        mediaplayer.setOnReady(playMusic);
    }
    public static String whatIsPlaying() { return songTitle(track); }


    public static void addToQueue(String track) {
        queue.getItems().add(track);
        System.out.println(track);
    }

    public static String songTitle(String trackFullPath) { return trackFullPath.substring(trackFullPath.lastIndexOf("\\") + 1 ); }
    /*
    public static void removeFromQueue() {
        queue.getItems().remove(queue.getSelectionModel().getSelectedItem());
    }
    */
}

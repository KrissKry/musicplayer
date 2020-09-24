package io.github.KrissKry.player;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class Player {
    //private static ListView<String> queue = new ListView<>();

    private TextField currentTime;
    private Slider slider;

    private int trackChosenBeforeQ;
    //private int trackChosenFromQ;
    private int currentlyChosenTrack;

    private static String progress = "";
    private static String duration = "";
    private static Media trackMedia;// = new Media()

    private static MediaPlayer mediaplayer;

    private static String track = "";

    Queue queue;


    public Player() {
        queue = new Queue();
    }



    public boolean updateMediaPlayerStatus(ObservableList<String> fullTrackListPath, int index) {

        //no tracks
        if (fullTrackListPath == null || fullTrackListPath.size() <= 0)
            return false;

        //no track playing rn / missing first run
//        if ( track == null || track.equals("") )
//            return false;

        //if mediaplayer has not been used before or is in unknown status, load new track
        if ( mediaplayer == null || mediaplayer.getStatus().equals(MediaPlayer.Status.UNKNOWN) || mediaplayer.getStatus().equals(MediaPlayer.Status.STOPPED) ) {

            track = fullTrackListPath.get(index);
            trackChosenBeforeQ = index;
            currentlyChosenTrack = index;
            loadTrack(fullTrackListPath, track);
            return true;

            //if music is playing
        } else if ( mediaplayer.getStatus().equals(MediaPlayer.Status.PLAYING) ) {

            mediaplayer.pause();
            return false;


            //if music is paused
        } else if ( mediaplayer.getStatus().equals(MediaPlayer.Status.PAUSED) ) {


            //still on the same track selected, resume playing
            if ( new File( track ).toURI().toString() == mediaplayer.getMedia().getSource()) {
                mediaplayer.play();


                //different song selected, new MediaPlayer, resume playing
            } else {

                track = fullTrackListPath.get(index);
                currentlyChosenTrack = index;
                trackChosenBeforeQ = index;
                loadTrack(fullTrackListPath, track);
            }
            return true;
        }

        return false;
    }



    public boolean nextSong(ObservableList<String> fullTrackListPath) {

        //no tracks overall
        if (fullTrackListPath == null || fullTrackListPath.size() == 0)
            return false;

        //no tracks in queue
        if ( !queue.hasTracksInQueue() ) {

            //move to next track
            trackChosenBeforeQ++;


            //reached end of tracklist, pause music, reset index
            if (trackChosenBeforeQ >= fullTrackListPath.size() ) {

                trackChosenBeforeQ = 0;
                currentlyChosenTrack = trackChosenBeforeQ;
                try {
                    mediaplayer.pause();
                } catch (NullPointerException x) {
                    System.out.println("Media player wasn't playing");

                }
                return false;


            //next track available theoretically
            } else {
                track = fullTrackListPath.get(trackChosenBeforeQ);
                currentlyChosenTrack = trackChosenBeforeQ;
                //loadTrack(track);
            }

        //there are tracks in queue
        } else {
            track = queue.getNextFromQueue();

            currentlyChosenTrack = fullTrackListPath.indexOf(track);
            //currentlyChosenTrack = trackChosenFromQ;
        }

        try {
            loadTrack(fullTrackListPath, track);
            return true;
        } catch (Exception x) {
            System.out.println("Exception on loading track " + x.getMessage());
            return false;
        }

//        return false;
    }




    public boolean prevSong(ObservableList<String> fullTrackListPath) {

        if ( fullTrackListPath == null || fullTrackListPath.size() == 0)
            return false;

        if (trackChosenBeforeQ > 0)
            trackChosenBeforeQ--;

        currentlyChosenTrack = trackChosenBeforeQ;

        try {
            //if sth is playing catch exception if mediaplayer not yet created
            mediaplayer.pause();
        } catch (NullPointerException x) {
            System.out.println(x.getMessage());
        }

        track = fullTrackListPath.get(trackChosenBeforeQ);


        try {
            loadTrack(fullTrackListPath, track);
            return true;
        } catch (Exception x) {
            return false;
        }
    }



    public void loadTrack(ObservableList<String> fullTrackListPath, String track) {

        trackMedia = new Media( new File( track ).toURI().toString());

        mediaplayer = new MediaPlayer(trackMedia);

        mediaplayer.setOnReady(() -> {

            duration = Double.toString( mediaplayer.getTotalDuration().toSeconds() );
            duration = duration.substring(0, duration.indexOf('.'));
            updateSlider();
            mediaplayer.play();
        });

        mediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {

            slider.setValue(newValue.toSeconds());
            String temp = Double.toString(newValue.toSeconds());
            temp = temp.substring(0, temp.indexOf('.'));
            progress = temp + "s/" + duration + "s";
            currentTime.setText(progress);

        });
        mediaplayer.setOnEndOfMedia(() -> {
            slider.setValue(0);
            nextSong(fullTrackListPath);
//            automaticNextSong(fullTrackL);
        });
    }

    public static String whatIsPlaying() { return songTitle(track); }


    public static String songTitle(String trackFullPath) {
        try {
            return trackFullPath.substring(trackFullPath.lastIndexOf("\\") + 1 );
        } catch (NullPointerException x) {
            System.out.println("fked song title: " + trackFullPath);
            return null;
        }
    }

    /*public void setNowPlaying(TextField nowPlaying) {
        this.nowPlaying = nowPlaying;
    }*/

    public void setSlider(Slider slider) {
        this.slider = slider;
        sliderSetup();
    }

    public void setCurrentTime(TextField currentTime) {
        this.currentTime = currentTime;
    }

    private void updateSlider() {
        slider.setValue(0);
        slider.setMax(mediaplayer.getTotalDuration().toSeconds());
    }

    private void sliderSetup() {
        slider.setMin(0);

        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (! slider.isValueChanging()) {
                double currentTime = mediaplayer.getCurrentTime().toSeconds();
                double sliderTime = newValue.doubleValue();
                if (Math.abs(currentTime - sliderTime) > 0.5) {
                    mediaplayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });
    }

    public Queue getQueue() { return queue; }

//    public void setTrackChosenBeforeQ(int index) {
//        trackChosenBeforeQ = index;
//    }
//
//    public void setTrackChosenFromQ(int index) {
//        trackChosenFromQ = index;
//    }

//    public int getTrackChosenBeforeQ() { return trackChosenBeforeQ; }

    public int getCurrentlyChosenTrack() { return currentlyChosenTrack; }

//    public int getTrackChosenFromQ() { return  trackChosenFromQ; }
}

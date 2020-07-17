package io.github.KrissKry.player;

import io.github.KrissKry.controller.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class Player {
    private static ListView<String> queue = new ListView<>();

    private TextField currentTime;
    private Slider slider;

    private static String progress = "";
    private static String duration = "";
    private static Media trackMedia;// = new Media()

    private static MediaPlayer mediaplayer;

    private static String track = "";

    public Player() {}

    // play/pause button has been pressed
    public boolean updateMediaplayerStatus(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) throws NullPointerException {

        //no tracks loaded
        if (appMusicList.getItems().size() == 0)
            return false;

        //no track chosen
        //ewentualnie dodac ze jak cos gra to zatrzxymac??
        if (appMusicList.getSelectionModel().getSelectedItem() == null)
            return false;


        //if mediaplayer has not been used before or is in unknown status, load new track
        if ( mediaplayer == null || mediaplayer.getStatus().equals(MediaPlayer.Status.UNKNOWN) || mediaplayer.getStatus().equals(MediaPlayer.Status.STOPPED) ) {

            track = appMusicList.getSelectionModel().getSelectedItem();
            loadTrack(appMusicList, fullTrackListPath);
            return true;

        //if music is playing
        } else if ( mediaplayer.getStatus().equals(MediaPlayer.Status.PLAYING) ) {
                mediaplayer.pause();
                return false;

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
            return true;
        }
        return false;
    }

    public boolean nextSong(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) {

        if (appMusicList.getItems().size() == 0 || fullTrackListPath.size() == 0)
            return false;

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
            return true;
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
            loadTrack(appMusicList, fullTrackListPath);
            return true;
        }
    }

    public boolean prevSong(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) {

        if (appMusicList.getItems().size() == 0 || fullTrackListPath.size() == 0)
            return false;

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
        return true;
    }

    public void loadTrack(ListView<String> appMusicList, ObservableList<String> fullTrackListPath) {


        int trackIndex = appMusicList.getSelectionModel().getSelectedIndex();
        System.out.println("Loading track " + trackIndex);
        trackMedia = new Media( new File( fullTrackListPath.get(trackIndex) ).toURI().toString());

        mediaplayer = new MediaPlayer(trackMedia);

        mediaplayer.setOnReady(() -> {

            duration = Double.toString( mediaplayer.getTotalDuration().toSeconds() );
            duration = duration.substring(0, duration.indexOf('.'));
            updateSlider();
            mediaplayer.play();
//            System.out.println("Now Playing!");
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
            nextSong(appMusicList, fullTrackListPath);
        });

    }

    public static String whatIsPlaying() { return songTitle(track); }

    public static void addToQueue(String track) {
        queue.getItems().add(track);
        System.out.println(track);
    }

    public static String songTitle(String trackFullPath) { return trackFullPath.substring(trackFullPath.lastIndexOf("\\") + 1 ); }

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

}

package io.github.KrissKry.controller;

import io.github.KrissKry.filehandler.MusicLister;
import io.github.KrissKry.player.Player;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;

import java.util.List;


public class Controller {
    //MusicLister ml = new MusicLister();
    private static Stage primarystage;
    @FXML
    private Button Tracks;
    @FXML
    private Button Creators;
    @FXML
    private Button Albums;
    @FXML
    private Button Playlists;
    @FXML
    private Button Search;
    @FXML
    private Button Settings;
    @FXML
    private Button PlayPause;
    @FXML
    private Button Prev;
    @FXML
    private Button Next;
    @FXML
    private Button Queue;
    @FXML
    private Button LoadFiles;
    @FXML
    private ListView<String> contentList = new ListView<>();
    @FXML
    private TextField nowPlaying;
    private Slider slider;
    ObservableList<String> fullTrackListPath = FXCollections.observableArrayList();
    ObservableList<String> trackList = FXCollections.observableArrayList();
//    ObservableList<String> authorList;
//    ObservableList<String> albumList;
//    ObservableList<String> playlistList;

    public Controller() {
        System.out.println("Constructed");
    }

    @FXML
    private void initialize() {
        LoadFiles.setOnAction( e -> handleButtonAction(e) );
        PlayPause.setOnAction( e -> handleButtonAction(e) );
        Next.setOnAction( e -> handleButtonAction(e) );
        Prev.setOnAction( e -> handleButtonAction(e) );
        System.out.println("Initalized");
    }

    @FXML
    public void handleButtonAction(ActionEvent e) {
        var symbol = ((Button) e.getSource() ).getText();

        switch(symbol) {
            case "Load": {


                ObservableList<String> newTracksLoaded = MusicLister.getFiles();

                for(String newTrack : newTracksLoaded) {
                    if(!fullTrackListPath.contains(newTrack) )
                        fullTrackListPath.add(newTrack);
                }

                trackList.clear();

                for (String trackPath : fullTrackListPath)
                        trackList.add( Player.songTitle(trackPath) );

                contentList.setItems(FXCollections.observableArrayList(trackList));
                updateContext();
                System.out.println("Tracklist updated");
            }
            break;
            case "ll": {
                if ( Player.isPlaying() )
                    PlayPause.setText("▶");

                Player.updateMediaplayerStatus(contentList, fullTrackListPath);


            }
            break;
            case "▶": {
                if ( !Player.isPlaying() )
                    PlayPause.setText("ll");
                Player.updateMediaplayerStatus(contentList, fullTrackListPath);
                nowPlaying.setText(Player.whatIsPlaying());
            }
            break;
            case ">>": {
                System.out.println("Next Song");
                Player.nextSong(contentList, fullTrackListPath);
                PlayPause.setText("ll");
                nowPlaying.setText(Player.whatIsPlaying());
            }
            break;
            case "<<": {
                System.out.println("Prev Song");
                Player.prevSong(contentList, fullTrackListPath);
                PlayPause.setText("ll");
                nowPlaying.setText(Player.whatIsPlaying());
            }
            break;
            default: {
                System.out.println("Clicked: " + symbol);
            }


        }
    }

    //no clue tf it's doing but it does it's job fucking perfectly
    private void updateContext() {
        contentList.setCellFactory(lv -> {

            //new cell list
            ListCell<String> cell = new ListCell<>();

            //new context menu for each cell
            ContextMenu contextmenu = new ContextMenu();

            //new Menu Item to add to queue
            MenuItem addToQ = new MenuItem();
            addToQ.textProperty().bind(Bindings.format("Add to Queue"));

            //on click event -> Call Player to add track to queue
            addToQ.setOnAction(event -> {
                //gets direct
//                String item = cell.getItem();
                int trackIndex = cell.getIndex();
                String newInQueue = fullTrackListPath.get(trackIndex);
                Player.addToQueue(newInQueue);

            });

            //new Menu Item to delete from tracklist
            MenuItem deleteItem = new MenuItem();
            deleteItem.textProperty().bind(Bindings.format("Remove from tracklist temporarily"));

            deleteItem.setOnAction( event -> {
                contentList.getItems().remove( cell.getItem() );
                fullTrackListPath.remove( cell.getIndex() );
            });

            //add both items to context menu for that cell
            contextmenu.getItems().addAll(addToQ, deleteItem);

            //xd
            cell.textProperty().bind(cell.itemProperty());

            //xd avoiding nullpointerX?
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextmenu);
                }
            });
            return cell;
        });
    }


}

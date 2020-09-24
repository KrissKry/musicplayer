package io.github.KrissKry.controller;

import io.github.KrissKry.filehandler.MusicLister;
import io.github.KrissKry.player.Player;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;



public class Controller {

    Player player;
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
    private TextField nowPlaying = new TextField();
    @FXML
    private TextField currentTime = new TextField();
    @FXML
    private Slider slider = new Slider();


    /*    Stores full path to a track: ie "C://Users/... "      */
    ObservableList<String> fullTrackListPath = FXCollections.observableArrayList();

    /*    Stores only track name for display purposes in ListView in app: ie "03. DTKJ "      */
    ObservableList<String> trackList = FXCollections.observableArrayList();


    public Controller() {
        player = new Player();
        MusicLister.setStage(primarystage);
        System.out.println("Constructed");
    }

    @FXML
    private void initialize() {

        LoadFiles.setOnAction( e -> handleButtonAction(e) );
        PlayPause.setOnAction( e -> handleButtonAction(e) );
        Next.setOnAction( e -> handleButtonAction(e) );
        Prev.setOnAction( e -> handleButtonAction(e) );
        Queue.setOnAction( e -> queuePopup(e) );

        nowPlaying.setEditable(false);

        currentTime.setEditable(false);
        currentTime.setAlignment(Pos.TOP_RIGHT);

        PlayPause.setPadding(Insets.EMPTY);

        LoadFiles.setPadding(Insets.EMPTY);

        //vertical 3 dots xd
        Settings.setRotate(90);
        Settings.setAlignment(Pos.TOP_CENTER);
        Settings.setPadding(Insets.EMPTY);

        try {
            player.setCurrentTime(currentTime);
            player.setSlider(slider);
        } catch (NullPointerException x) {
            System.out.println(x.getMessage());
        }

        System.out.println("Initialized");
    }

    @FXML
    public void handleButtonAction(ActionEvent e) {
        var symbol = ((Button) e.getSource() ).getText();

        switch(symbol) {
            case "Load": {

                ObservableList<String> newTracksLoaded = MusicLister.getFiles();
                if (newTracksLoaded != null) {

                    for (String newTrack : newTracksLoaded) {
                        if (!fullTrackListPath.contains(newTrack))
                            fullTrackListPath.add(newTrack);
                    }
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

                if( !player.updateMediaPlayerStatus( fullTrackListPath, contentList.getSelectionModel().getSelectedIndex() ))
                    PlayPause.setText("▶");

            }
            break;
            case "▶": {

                if( player.updateMediaPlayerStatus( fullTrackListPath, contentList.getSelectionModel().getSelectedIndex() ) ) {
                    PlayPause.setText("ll");
                    nowPlaying.setText(Player.whatIsPlaying());
                }
            }
            break;
            case "⏭": {
                //System.out.println("Next Song");
                //if( player.nextSong(contentList, fullTrackListPath) ) {
                if ( player.nextSong(fullTrackListPath) ) {


                    PlayPause.setText("ll");
                    nowPlaying.setText(Player.whatIsPlaying());
                    contentList.getSelectionModel().select( player.getCurrentlyChosenTrack() );
                } else {
                    PlayPause.setText("▶");
                }
            }
            break;
            case "⏮": {
                //System.out.println("Prev Song");
                //if (player.prevSong(contentList, fullTrackListPath)) {
                if ( player.prevSong(fullTrackListPath) ) {


                    PlayPause.setText("ll");
                    nowPlaying.setText(Player.whatIsPlaying());
                    contentList.getSelectionModel().select( player.getCurrentlyChosenTrack() );
                }
            }
            break;
            default: {
                System.out.println("Clicked: " + symbol);
            }


        }
    }

    @FXML
    private void queuePopup(ActionEvent e) {
        //if is not visible -> we want to open the queue
        if ( !player.getQueue().getVisible() ) {

            player.getQueue().setVisible(true);
            contentList.getItems().clear();
            contentList.getItems().addAll( player.getQueue().getQueuedTracksName() );
        } else {
            player.getQueue().setVisible(false);
            contentList.getItems().clear();
            contentList.getItems().addAll( trackList );
            contentList.getSelectionModel().select( player.getCurrentlyChosenTrack() );
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

                int trackIndex = cell.getIndex();
                String queueTrackPath = fullTrackListPath.get(trackIndex);
                player.getQueue().addToQueue(queueTrackPath);
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

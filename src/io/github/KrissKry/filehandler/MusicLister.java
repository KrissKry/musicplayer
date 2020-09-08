package io.github.KrissKry.filehandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MusicLister {
    private static Stage primaryStage;

    public static void main(String args[]) {
    }

    public static ObservableList<String> getFiles() throws NullPointerException {

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.m4a", "*.wma") );

        fileChooser.setTitle("Select music files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

        try {
            ObservableList<String> list = FXCollections.observableArrayList();

            //add all files read to list
            for(File f : selectedFiles)
                list.add( f.toString() );

            //filter all non-audio files from the list
            list.removeIf( element -> ( !element.contains(".mp3") && !element.contains(".m4a") && !element.contains(".wma") ));

            //create a sorted list from audio files
            SortedList<String> sortedTrackList = new SortedList<>(list);
            return sortedTrackList;

        } catch (NullPointerException x) {
            // No files were read, can't create a list of strings from null
            System.out.println(x.getLocalizedMessage() + "at creating sorted list of new tracks.");
        }
        return null;
    }


    public static void setStage(Stage s) {
        primaryStage = s;
    }
}

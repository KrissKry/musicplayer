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

    public static void main(String args[]) throws IOException {
    }

    public static ObservableList<String> getFiles() throws NullPointerException {

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Music Files", "*.mp3"));

        fileChooser.setTitle("Select music files");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);

        try {
            ObservableList<String> list = FXCollections.observableArrayList();

            for(File f : selectedFiles)
                list.add(f.toString());

            list.removeIf( element -> !element.contains(".mp3"));
            SortedList<String> sortedTrackList = new SortedList<>(list);
            return sortedTrackList;
        } catch (NullPointerException x) {
            System.out.println(x.getLocalizedMessage());
        }
        return null;
    }


    public static void setStage(Stage s) {
        primaryStage = s;
    }
}

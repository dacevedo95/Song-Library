package songlibrary.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import songlibrary.model.Song;

public class SongLibraryController {

	@FXML
	Text nameDisplay;
	@FXML
	Text artistDisplay;
	@FXML
	Text albumDisplay;
	@FXML
	Text yearDisplay;
	@FXML
	TextField nameEntry;
	@FXML
	TextField artistEntry;
	@FXML
	TextField albumEntry;
	@FXML
	TextField yearEntry;
	@FXML
	ListView<Song> listView;
	
	private ObservableList<Song> obsList;
	private Stage mainStage;
	
	public void start(Stage mainStage) throws IOException {
		
		this.mainStage = mainStage;
		ArrayList<Song> songs = readSongsFromFile();
		obsList = FXCollections.observableArrayList(songs);
		listView.setItems(obsList);
		
		listView.getSelectionModel()
				.selectedItemProperty()
				.addListener((obs, oldVal, newVal) -> displaySong());
		
		if (obsList.size() != 0) {
			listView.getSelectionModel().selectFirst();
		}
	}
	
	private ArrayList<Song> readSongsFromFile() throws IOException {
		
		ArrayList<Song> songs = new ArrayList<Song>();
		BufferedReader br = new BufferedReader(new FileReader("src/songlibrary/controller/songs.txt"));
		while (br.ready()) {
			String name = br.readLine();
			String artist = br.readLine();
			String album = br.readLine();
			if (album.equals("")) {
				album = null;
			}
			String year = br.readLine();
			if (year.equals("")) {
				year = null;
			}
			
			Song temp = new Song(name, artist, album, year);
			songs.add(temp);
		}
		br.close();
		return songs;
	}
	
	private void displaySong() {
		
		if (obsList.isEmpty()) {
			resetSongDisplay();
			return;
		}
		
		String name = listView.getSelectionModel().getSelectedItem().getName();
		nameDisplay.setText(name);
		
		String artist = listView.getSelectionModel().getSelectedItem().getArtist();
		artistDisplay.setText(artist);
		
		String album = listView.getSelectionModel().getSelectedItem().getAlbum();
		albumDisplay.setText(album);
		
		String year = listView.getSelectionModel().getSelectedItem().getYear();
		yearDisplay.setText(year);
	
	}
	
	public void addSong(ActionEvent e) {
		
		String name = nameEntry.getText();
		if (name.equals("")) {
			displayEnterValidSongErrorDialog();
			return;
		}
		String artist = artistEntry.getText();
		if (artist.equals("")) {
			displayEnterValidSongErrorDialog();
			return;
		}
		String album = albumEntry.getText();
		if (album.equals("")) {
			album = null;
		}
		String year = yearEntry.getText();
		if (year.equals("")) {
			year = null;
		}
		
		Song song = new Song(name, artist, album, year);
		
		if (obsList.contains(song)) {
			displayAlreadyExistsErrorDialog();
			return;
		}
		addSongToSortedLibrary(song);
	}
	
	public void editSong(ActionEvent e) {
		
		Song song = listView.getSelectionModel().getSelectedItem();
		String name = nameEntry.getText();
		if (name.equals("")) {
			displayEnterValidSongErrorDialog();
			return;
		}
		String artist = artistEntry.getText();
		if (artist.equals("")) {
			displayEnterValidSongErrorDialog();
			return;
		}
		String album = albumEntry.getText();
		if (album.equals("")) {
			album = null;
		}
		String year = yearEntry.getText();
		if (year.equals("")) {
			year = null;
		}
		
		if (obsList.contains(new Song(name, artist))) {
			displayAlreadyExistsErrorDialog();
			return;
		}
		int index = obsList.indexOf(song);
		Song temp = obsList.remove(index);
		temp.setName(name);
		temp.setArtist(artist);
		temp.setAlbum(album);
		temp.setYear(year);
		addSongToSortedLibrary(temp);
	}
	
	public void deleteSong(ActionEvent e) {
		
		Song song = listView.getSelectionModel().getSelectedItem();
		int index = obsList.indexOf(song);
		obsList.remove(song);
		if (index == obsList.size()) {
			listView.getSelectionModel().select(index - 1);
		}
		else if (!obsList.isEmpty()) {
			listView.getSelectionModel().select(index);
		}
	}
	
	public void cancelAction(ActionEvent e) {
		resetTextFields();
	}
	
	private void addSongToSortedLibrary(Song song) {
		if (obsList.isEmpty()) {
			obsList.add(song);
			resetTextFields();
			listView.getSelectionModel().select(0);
			return;
		}
		boolean inserted = false;
		for (int i = 0; i < obsList.size(); i++) {
			if (song.compareTo(obsList.get(i)) < 0) {
				obsList.add(i, song);
				listView.getSelectionModel().select(i);
				inserted = true;
				break;
			}
		}
		if (!inserted) {
			obsList.add(song);
			listView.getSelectionModel().select(obsList.size() - 1);
		}
		resetTextFields();
	}
	
	private void resetTextFields() {
		nameEntry.setText("");
		artistEntry.setText("");
		albumEntry.setText("");
		yearEntry.setText("");
	}
	
	private void resetSongDisplay() {
		nameDisplay.setText("");
		artistDisplay.setText("");
		albumDisplay.setText("");
		yearDisplay.setText("");
	}
	
	private void displayAlreadyExistsErrorDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(mainStage);
		alert.setHeaderText("Error");
		String content = "The specified song already exists in the library.";
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	private void displayEnterValidSongErrorDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(mainStage);
		alert.setHeaderText("Error");
		String content = "Name and Artist are required fields.";
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	public void saveSongLibrary() throws IOException {
		PrintWriter writer = new PrintWriter("src/songlibrary/controller/songs.txt");
		for (Song song : obsList) {
			writer.print(song.getName() + "\n");
			writer.print(song.getArtist() + "\n");
			writer.print(song.getAlbum() + "\n");
			writer.print(song.getYear() + "\n");
		}
		writer.close();
	}
}

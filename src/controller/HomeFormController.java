package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFormController {


    public ProgressBar prgSong;
    public Button btnPlay;
    public Button btnPause;
    public Button btnReset;
    public Button btnPrevious;
    public Button btnNext;
    public ComboBox<String> cmbSpeed;
    public Slider sideVolume;
    public Label lblSong;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNumber=0;
    private int [] speeds= {25,50,75,100,125,150,200};

    private Timer timer;
    private TimerTask task;
    private  boolean running;

    public void initialize(){

        songs=new ArrayList<File>();
        directory = new File("lib/music");
        files = directory.listFiles();

        if(files != null){
            for(File file:files){
                songs.add(file);
            }
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        lblSong.setText(songs.get(songNumber).getName());

        for (int i=0;i<speeds.length;i++){
            cmbSpeed.getItems().add(Integer.toString(speeds[i])+"%");
        }
        cmbSpeed.setOnAction(this::cmbChangeSpeedOnAction);

        sideVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(sideVolume.getValue()*0.01);
            }
        });

        prgSong.setStyle("-fx-accent: green");
    }

    public void btnPlayOnAction(ActionEvent actionEvent) {

        beginTimer();
        cmbChangeSpeedOnAction(null);
        mediaPlayer.setVolume(sideVolume.getValue()*0.01);
        mediaPlayer.play();
    }

    public void btnPauseOnAction(ActionEvent actionEvent) {
        cancelTimer();
        mediaPlayer.pause();
    }

    public void btnResetOnAction(ActionEvent actionEvent) {
        prgSong.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void btnPreviousOnAction(ActionEvent actionEvent) {
        if(songNumber>0){
            songNumber--;
            mediaPlayer.stop();

            if(running){
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            lblSong.setText(songs.get(songNumber).getName());
        }
        else{
            songNumber=songs.size()-1;
            mediaPlayer.stop();

            if(running){
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            lblSong.setText(songs.get(songNumber).getName());
        }
        btnPlayOnAction(actionEvent);
    }

    public void btnNextOnAction(ActionEvent actionEvent) {
        if(songNumber<songs.size()-1){
            songNumber++;
            mediaPlayer.stop();

            if(running){
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            lblSong.setText(songs.get(songNumber).getName());
        }
        else{
            songNumber=0;
            mediaPlayer.stop();

            if(running){
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            lblSong.setText(songs.get(songNumber).getName());
        }
        btnPlayOnAction(actionEvent);
    }

    public void cmbChangeSpeedOnAction(ActionEvent actionEvent) {
        if(cmbSpeed.getValue()==null){
            mediaPlayer.setRate(1);
        }else {
            mediaPlayer.setRate(Integer.parseInt(cmbSpeed.getValue().substring(0,cmbSpeed.getValue().length()-1))*0.01);
        }

    }

    public void beginTimer(){
        timer = new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                running=true;
                double current=mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                prgSong.setProgress(current/end);

                if(current/end==1){
                    cancelTimer();

                }
            }
        };
        timer.scheduleAtFixedRate(task,0,1000);
    }
    public void cancelTimer(){
        running=false;
        timer.cancel();

    }

}

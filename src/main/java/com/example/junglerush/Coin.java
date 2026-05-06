package com.example.junglerush;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;

public class Coin {
    double x;
    double y;
    boolean collected;
    Image coinImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("coin.png")));
    MediaPlayer collectSound;

    public Coin(double x, double y) {
        this.x = x;
        this.y = y;
        this.collected = false;
        try {
            Media media = new Media(Objects.requireNonNull(getClass().getResource("audio/coinCollect.mp3")).toURI().toString());
            collectSound = new MediaPlayer(media);
        } catch (Exception e) {
            System.out.println("Could not load coin sound");
        }
    }

    public void update(double speed) {
        x -= speed;
    }

    public void collect() {
        collected = true;
        collectSound.stop();
        collectSound.play();
    }

    public void draw(GraphicsContext gc) {
        if (!collected) {
            gc.drawImage(coinImage, x, y, 55, 55);
        }
    }
}
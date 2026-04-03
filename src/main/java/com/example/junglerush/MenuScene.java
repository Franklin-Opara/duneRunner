package com.example.junglerush;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class MenuScene {
    private final Stage stage;
    private final MainGame app;
    NumberFormat nf = NumberFormat.getInstance(Locale.US);

    Image menuBgSky = new Image(Objects.requireNonNull(getClass().getResourceAsStream("menuBgSky.png")));
    Image sitting = new Image(Objects.requireNonNull(getClass().getResourceAsStream("sitting.png")));
    Image coinIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("staticCoin.png")));

    public MenuScene(Stage stage, MainGame app) {
        this.stage = stage;
        this.app = app;
    }

    public Scene getScene() {
        Canvas canvas = new Canvas(800, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Group root = new Group(canvas);
        Scene scene = new Scene(root);

        drawMenu(gc);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.S) {
                app.startGame(stage);
            } else if (event.getCode() == KeyCode.Q) {
                System.exit(0);
            }
        });

        return scene;
    }

    private void drawMenu(GraphicsContext gc) {
        // background
        gc.drawImage(menuBgSky, 0, 0);
        // trees in same positions as game
        for (Tree tree : app.trees) {
            tree.draw(gc);
        }
        gc.drawImage(sitting, 100,248,71, 64 );



        // stats top left
        gc.setFont(Font.font("Alatsi", 16));
        gc.setFill(Color.rgb(38,38,38));
        gc.fillText("Best: " + nf.format(app.highScore), 580, 38);

        gc.setFill(Color.rgb(255,255,185));
        String coinText = nf.format(app.lifetimeCoins);
        gc.fillText(coinText, 689, 38);


        for (int i = 1; i < 9; i++){
            if (coinText.length() == i){
                gc.drawImage(coinIcon, 689 + (i*10 + 4), 23);
            }
        }


        // controls
        gc.setFill(Color.rgb(38, 38, 38));
        gc.setFont(Font.font("Alatsi",20));
        gc.fillText("press [S] to start", 329, 258);
        gc.fillText("press [Q] to quit", 332, 294);
    }
}
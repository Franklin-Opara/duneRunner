//This is the main game file with most of the game logic

//============================PACKAGE NAME============================
package com.example.junglerush;

//============================NECESSARY IMPORTS FOR THE PROGRAM============================

//=================JAVAFX IMPORTS=====================
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

//===================JAVA IMPORTS=======================
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;


//==========================MAIN CLASS================================
public class MainGame extends Application {

    //==============ALL GLOBAL FIELDS IN THE MAIN CLASS ARE DEFINED HERE==================

    //=================OBJECT DECLARATION===============================
    Player player = new Player(100);
    Tree[] trees = new Tree[3];
    ArrayList<Coin> coins = new ArrayList<>();
    ArrayList<Rock> obstacles = new ArrayList<>();


    //===============GLOBAL VARIABLES==============
    boolean paused;
    boolean gameOver = false;
    boolean scoreSaved = false;
    boolean fadeIn = false;
    double speed = 5;
    double mountain1X = 0;
    double mountain2X = 0;
    double mountain3X = 0;
    double groundX = 0;
    double overlayOpacity = 0;
    int score = 0;
    int highScore = 0;
    int lifetimeCoins;
    int coinTimer = 0;
    int totalCoins = 0;
    AnimationTimer gameTimer;
    NumberFormat nf = NumberFormat.getInstance(Locale.US);//======FOR THOUSAND SEPARATOR COMMA=========

    //======================IMAGES===============================
    Image groundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("ground.png")));
    Image bgSky = new Image(Objects.requireNonNull(getClass().getResourceAsStream("bgSky.png")));
    Image bgMountain1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("bgMountain1.png")));
    Image bgMountain2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("bgMountain2.png")));
    Image bgMountain3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("bgMountain3.png")));
    Image coinIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("staticCoin.png")));

    //=======================SOUNDS=======================
    MediaPlayer bgMusic;
    MediaPlayer gameOverSound;


    @Override
    //THINGS THAT HAPPEN AT THE START OF THE GAME
    public void start(Stage stage){
        //============LOADING HIGH SCORE AND COINS=============
        try{
            loadHighScore();

        }catch (Exception e){
            System.out.println("No high score file found");
        }

        try{
            loadCoins();
        }catch (Exception e){
            System.out.println("No coins file found");
        }

        trees[0] = new Tree(90);
        trees[1] = new Tree(550);
        trees[2] = new Tree(750); //INITIAL DEFINITION OF TREE POSITIONS


        //=======TRY CATCH BLOCK FOR SOUNDS============
        try {
            Media bgMedia = new Media(Objects.requireNonNull(getClass().getResource("audio/backgroundMusic.mp3")).toURI().toString());
            bgMusic = new MediaPlayer(bgMedia);
            bgMusic.setVolume(0.4);
            bgMusic.setCycleCount(MediaPlayer.INDEFINITE);
            bgMusic.play();

            Media gameOverMedia = new Media(Objects.requireNonNull(getClass().getResource("audio/gameOver.mp3")).toURI().toString());
            gameOverSound = new MediaPlayer(gameOverMedia);
        } catch (Exception e) {
            System.out.println("Could not load audio");
        }

        stage.setTitle("Dune Runner");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png"))));
        MenuScene menuScene = new MenuScene(stage, this);
        stage.setScene(menuScene.getScene());
        stage.show();
    }

    private Scene getScene(Canvas canvas, Stage stage) {
        Group root = new Group(canvas);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> { //PLAYER JUMP
            if (event.getCode() == KeyCode.SPACE && !paused) {
                player.jump();
            }

            if (event.getCode() == KeyCode.P && !gameOver && !player.isDead) {
                paused = !paused;
                if (paused) {
                    fadeIn = true;
                    bgMusic.pause();
                } else {
                    overlayOpacity = 0;
                    fadeIn = false;
                    bgMusic.play();
                }
            }

            if (event.getCode() == KeyCode.R && (gameOver || paused)){
                scoreSaved = false;
                player.y = 240;
                player.velocityY = 0;
                score = 0;
                speed = 5;
                obstacles.clear();
                obstacles.add(new Rock(800));
                mountain1X = 0;
                mountain2X = 0;
                mountain3X = 0;
                trees[0] = new Tree(90);
                trees[1] = new Tree(550);
                trees[2] = new Tree(750);
                bgMusic.play();
                player.isDead = false;
                player.deathTimer = 0;
                player.velocityY = 0;
                player.y = 240;
                coins.clear();
                coinTimer = 0;
                totalCoins = 0;
                overlayOpacity = 0;
                fadeIn = false;

                if (gameOver){
                    gameOver = false;
                    gameOverSound.stop();
                } else if (paused) {
                    bgMusic.stop();
                    paused = false;
                    bgMusic.play();
                }


            }

            if (event.getCode() == KeyCode.M && (gameOver || paused)) {
                // reset everything
                scoreSaved = false;
                gameOver = false;
                paused = false;
                player.isDead = false;
                player.deathTimer = 0;
                player.y = 240;
                player.velocityY = 0;
                score = 0;
                speed = 5;
                obstacles.clear();
                coins.clear();
                coinTimer = 0;
                totalCoins = 0;
                mountain1X = 0;
                mountain2X = 0;
                mountain3X = 0;

                trees[0] = new Tree(90);
                trees[1] = new Tree(550);
                trees[2] = new Tree(750);
                gameOverSound.stop();
                bgMusic.stop();
                bgMusic.play();

                lifetimeCoins += totalCoins;
                try {
                    saveCoins();
                } catch (Exception e) {
                    System.out.println("Could not save coins");
                }

                // go back to menu
                MenuScene menuScene = new MenuScene(stage, this);
                stage.setScene(menuScene.getScene());
                gameTimer.stop();
            }

        });

        return scene;
    }

    public void render(GraphicsContext gc){

        if (gameOver || paused) {
            if (gameOver) {
                fadeIn = true;
                if (score > highScore) {
                    highScore = score;
                }
                if (!scoreSaved) {
                    try {
                        saveHighScore();
                        scoreSaved = true;
                    } catch (Exception e) {
                        System.out.println("Could not save high score");
                    }

                    lifetimeCoins += totalCoins;
                    try {
                        saveCoins();
                    } catch (Exception e) {
                        System.out.println("Could not save coins");
                    }
                    bgMusic.stop();
                    gameOverSound.play();
                }
            }

            if (fadeIn && overlayOpacity < 1) {
                overlayOpacity += 0.05;
            }

            gc.drawImage(bgSky, 0, 0, 800, 400);
            gc.drawImage(bgMountain1, mountain1X, 0, 800, 400);
            gc.drawImage(bgMountain1, mountain1X + 800, 0, 800, 400);
            gc.drawImage(bgMountain2, mountain2X, 0, 800, 400);
            gc.drawImage(bgMountain2, mountain2X + 800, 0, 800, 400);
            gc.drawImage(bgMountain3, mountain3X, 0, 800, 400);
            gc.drawImage(bgMountain3, mountain3X + 800, 0, 800, 400);
            gc.drawImage(groundImage, groundX, 310, 800, 400);
            gc.drawImage(groundImage, groundX + 800, 310, 800, 400);

            for (Tree tree : trees) tree.draw(gc);
            for (Coin coin : coins) coin.draw(gc);
            for (Rock obstacle : obstacles) obstacle.draw(gc);
            player.draw(gc);

            gc.setFill(Color.rgb(255, 255, 255, overlayOpacity*0.3));
            gc.fillRect(0, 0, 800, 400);


            gc.setFill(Color.rgb(255, 255, 255, Math.min(overlayOpacity, 1)));

            if (gameOver) {
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setFill(Color.rgb(0, 0, 0, overlayOpacity*0.7));
                gc.fillRoundRect(200, 100, 400, 200, 15, 15);
                gc.setFont(Font.font("Alatsi", 16));
                gc.setFill(Color.WHITE);
                gc.fillText("Score: "+ nf.format(score), 400, 158);
                gc.fillText("High score: " + nf.format(highScore), 400, 188);
                gc.fillText("Coins collected: " + nf.format(totalCoins), 400, 218);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText("R — Restart", 289, 278);
                gc.fillText("M — Menu", 440, 278);
            }

            if (paused) {
                gc.setFill(Color.rgb(0, 0, 0, overlayOpacity*0.7));
                gc.fillRect(0, 0, 285, 400);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Alatsi", 16));
                gc.fillText("Score: " + nf.format(score), 31, 58);
                gc.fillText("High score: " + nf.format(highScore), 31, 88);
                gc.setFill(Color.rgb(255, 255, 185, Math.min(overlayOpacity, 1)));

                gc.drawImage(coinIcon, 31, 102);
                gc.fillText(nf.format(totalCoins), 53, 118);

                gc.setFill(Color.rgb(255, 255, 255, Math.min(overlayOpacity, 1)));
                gc.fillText("M — Menu", 31, 302);
                gc.fillText("R — Restart", 31, 332);
                gc.fillText("P — Resume", 31, 362);

            }

            return;
        }

        score++;
        speed += 0.001;


        coinTimer++;
        if (coinTimer > 180 + Math.random() * 200) {
            spawnCoin();
            coinTimer = 0;
        }

        player.update();

        if (player.isDead && player.deathTimer > 30 && player.onGround) {
            gameOver = true;
        }

        // Sky - static, no scrolling
        gc.drawImage(bgSky, 0, 0, 800, 400);

// Mountains - slow scroll
        mountain1X -= 0.2;
        if (mountain1X <= -800) mountain1X = 0;
        gc.drawImage(bgMountain1, mountain1X, 0, 800, 400);
        gc.drawImage(bgMountain1, mountain1X + 800, 0, 800, 400);

// Temples - slightly faster scroll
        mountain2X -= 0.3;
        if (mountain2X <= -800) mountain2X = 0;
        gc.drawImage(bgMountain2, mountain2X, 0, 800, 400);
        gc.drawImage(bgMountain2, mountain2X + 800, 0, 800, 400);

// Temples - slightly faster scroll
        mountain3X -= 0.5;
        if (mountain3X <= -800) mountain3X = 0;
        gc.drawImage(bgMountain3, mountain3X, 0, 800, 400);
        gc.drawImage(bgMountain3, mountain3X + 800, 0, 800, 400);

        // scroll ground

        groundX -= speed;
        if (groundX <= -800) groundX = 0;
        gc.drawImage(groundImage, groundX, 310);
        gc.drawImage(groundImage, groundX + 800, 310);










        for (Tree tree : trees) {
            tree.update(speed);
            tree.draw(gc);
        }

        // Iterate backwards to avoid skipping the next item when one is removed
        for (int i = coins.size() - 1; i >= 0; i--) {
            Coin coin = coins.get(i);
            coin.update(speed);

            if (coin.x + 62 < 0) {
                coins.remove(i);
                continue;
            }

            coin.draw(gc);

            if (!coin.collected &&
                    player.x + 49 > coin.x + 10 && player.x < coin.x + 30 &&
                    player.y + 70 > coin.y + 10 && player.y < coin.y + 30)
            {
                coin.collect();
                totalCoins++;

            }
        }





        player.draw(gc); //SHOWS PLAYER

        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Rock obstacle = obstacles.get(i);
            obstacle.update(speed);

            if (obstacle.x + obstacle.width< 0) {
                obstacles.remove(i);
                obstacles.add(new Rock(800 + Math.random() * 400));
            }

            obstacle.draw(gc);

            if (player.x + 40 > obstacle.x && player.x < obstacle.x + obstacle.width &&
                    player.y + 60 > obstacle.y && player.y < obstacle.y + obstacle.height) {
                player.isDead = true;
                gameOverSound.play();
                bgMusic.stop();
                player.velocityY = -5;
            }
        }
        gc.setFont(Font.font("Alatsi", 16));
        gc.setFill(Color.rgb(38, 38, 38));
        gc.fillText("Score: " + nf.format(score), 16, 20);
        gc.fillText("Best: " + nf.format(highScore), 16, 45);

        gc.setFill(Color.rgb(255,255,185));

        gc.drawImage(coinIcon, 16 , 60);
        gc.fillText(nf.format(totalCoins), 38, 75);
    }

    public void startGame(Stage stage){
        Canvas canvas = new Canvas(800, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        obstacles.clear();
        obstacles.add(new Rock(800)); //ADDING OBSTACLE
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                render(gc);
            }
        };
        gameTimer.start();
        Scene scene = getScene(canvas, stage);

        stage.setScene(scene);
        stage.show();
    }

    private void saveHighScore() throws IOException {
        FileWriter writer = new FileWriter("highscore.txt");
        writer.write(String.valueOf(highScore));
        writer.close();
    }

    private void loadHighScore() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("highscore.txt"));
        highScore = scanner.nextInt();
        scanner.close();
    }

    private void saveCoins() throws IOException {
        FileWriter writer = new FileWriter("coins.txt");
        writer.write(String.valueOf(lifetimeCoins));
        writer.close();
    }

    private void loadCoins() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("coins.txt"));
        lifetimeCoins = scanner.nextInt();
        scanner.close();
    }



    private void spawnCoin() {
        double[] heights = {230, 150};
        double y = heights[(int)(Math.random() * 2)];
        int count = 1 + (int)(Math.random() * 5); // 1 to 5 coins
        for (int i = 0; i < count; i++) {
            coins.add(new Coin(900 + i * 40, y)); // space coins 40px apart
        }
    }


}
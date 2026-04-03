package com.example.junglerush;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {
    double x;
    double y = 240;
    double velocityY = 0;
    boolean onGround = false;
    Image[] runFrames = new Image[36];
    Image jumpImage;
    Image deadImage;
    int currentFrame = 0;
    int frameTimer = 0;
    boolean isDead = false;
    int deathTimer = 0;

    public Player(double x) {
        this.x = x;
        this.velocityY = 0;
        for (int i = 0; i < 36; i++) {
            String filename = i < 10 ? "running/frame_00" + i + ".png" : "running/frame_0" + i + ".png";
            runFrames[i] = new Image(getClass().getResourceAsStream(filename));
        }
        jumpImage = new Image(getClass().getResourceAsStream("jump.png"));
        deadImage = new Image(getClass().getResourceAsStream("fall.png"));
    }

    public void update() {
        if (isDead) {
            deathTimer++;
            velocityY += 0.3;
            y += velocityY;
            if (y > 240) {
                y = 240;
                velocityY = 0;
                onGround = true;
            } else {
                onGround = false;
            }
            return;
        }

        velocityY += 0.75;
        y += velocityY;

        if (y > 240) {
            y = 240;
            velocityY = 0;
            onGround = true;
        } else {
            onGround = false;
        }

        frameTimer++;
        if (frameTimer >= 3) {
            currentFrame++;
            if (currentFrame >= 36) currentFrame = 0;
            frameTimer = 0;
        }
    }

    public void draw(GraphicsContext gc) {
        if (isDead) {
            gc.drawImage(deadImage, x, y+9, 80, 80);
        } else if (!onGround) {
            gc.drawImage(jumpImage, x, y, 71, 71);
        } else {
            gc.drawImage(runFrames[currentFrame], x, y, 80, 71);
        }
    }

    public void jump() {
        if (onGround && !isDead) {
            velocityY = -14.50;
        }
    }
}
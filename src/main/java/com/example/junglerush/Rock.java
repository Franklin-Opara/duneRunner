package com.example.junglerush;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Rock {
    double x;
    double y;
    double width;
    double height;
    Image[] rockImages = new Image[4];
    Image currentRock;

    public Rock(double x) {
        this.x = x;
        this.y = 260;

        for (int i = 0; i < 4; i++) {
            rockImages[i] = new Image(Objects.requireNonNull(getClass().getResourceAsStream("rocks/rock_" + (i + 1) + ".png")));
        }

        currentRock = rockImages[(int)(Math.random() * 4)];
        this.width = currentRock.getWidth();
        this.height = currentRock.getHeight();

        if (currentRock == rockImages[0]) this.y = 277;
        else if (currentRock == rockImages[1]) this.y = 287;
        else if (currentRock == rockImages[2]) this.y = 257;
        else if (currentRock == rockImages[3]) this.y = 274;
    }

    public void update(double speed) {
        x -= speed;
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(currentRock, x, y);
    }
}
package com.example.junglerush;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Tree {
    double x;
    double y;
    Image image;
    Image[] treeImages = new Image[9];

    public Tree(double x) {
        this.x = x;

        for (int i = 0; i < 9; i++) {
            treeImages[i] = new Image(Objects.requireNonNull(getClass().getResourceAsStream("trees/tree_" + (i + 1) + ".png")));
        }

        int type = (int)(Math.random() * 9);
        this.image = treeImages[type];
        this.y = getYForTree(type);
    }

    private double getYForTree(int type) {
        return switch (type) {
            case 0 -> 72;
            case 1 -> 211;
            case 2 -> 217;
            case 3 -> 229;
            case 4 -> 101;
            case 5 -> 131;
            case 6 -> 79;
            case 7 -> 132;
            case 8 -> 192;
            default -> 200;
        };
    }

    public void update(double speed) {
        x -= speed * 0.6;
        if (x + image.getWidth() < 0) {
            x = 900 + Math.random() * 600;
            int type = (int)(Math.random() * 9);
            this.image = treeImages[type];
            this.y = getYForTree(type);
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(image, x, y);
    }
}
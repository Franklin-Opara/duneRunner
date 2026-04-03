package com.example.junglerush;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tree {
    double x;
    double y;
    Image image;
    Image[] treeImages = new Image[9];

    public Tree(double x) {
        this.x = x;

        for (int i = 0; i < 9; i++) {
            treeImages[i] = new Image(getClass().getResourceAsStream("trees/tree_" + (i + 1) + ".png"));
        }

        int type = (int)(Math.random() * 9);
        this.image = treeImages[type];
        this.y = getYForTree(type);
    }

    private double getYForTree(int type) {
        switch (type) {
            case 0: return 72;
            case 1: return 211;
            case 2: return 217;
            case 3: return 229;
            case 4: return 101;
            case 5: return 131;
            case 6: return 79;
            case 7: return 132;
            case 8: return 192;
            default: return 200;
        }
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
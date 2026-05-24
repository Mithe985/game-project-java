package game;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class LevelDesign {
    private ArrayList<Rectangle> pipes;
    private Random random;
    private final int width = 600;
    private final int height = 600;
    private final int pipeWidth = 75;
    private final int pipeGap = 165;

    public LevelDesign() {
        pipes = new ArrayList<>();
        random = new Random();
    }

    public ArrayList<Rectangle> getPipes() {
        return pipes;
    }

    public void createPipe() {
        int groundHeight = 120;
        int minHeight = 50;
        int maxHeight = height - pipeGap - minHeight - groundHeight;
        int topHeight = minHeight + random.nextInt(maxHeight);

        pipes.add(new Rectangle(width + 40, 0, pipeWidth, topHeight));
        pipes.add(new Rectangle(width + 40, topHeight + pipeGap, pipeWidth, height - topHeight - pipeGap - groundHeight));
    }

    public void movePipes() {
        for (int i = 0; i < pipes.size(); i++) {
            Rectangle p = pipes.get(i);
            p.x -= 4;
        }

        if (!pipes.isEmpty() && pipes.get(0).x + pipeWidth < 0) {
            pipes.remove(0);
            pipes.remove(0);
        }
    }

    public boolean checkHit(Rectangle birdRect) {
        if (birdRect.y <= 0 || birdRect.y + birdRect.height >= height - 120) {
            return true;
        }

        for (Rectangle p : pipes) {
            if (p.intersects(birdRect)) {
                return true;
            }
        }
        return false;
    }

    public void clearAll() {
        pipes.clear();
    }
}
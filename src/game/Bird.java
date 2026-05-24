package game;

import java.awt.Rectangle;

public class Bird {
    private Rectangle body;
    private int speedY;

    public Bird(int x, int y, int width, int height) {
        this.body = new Rectangle(x, y, width, height);
        this.speedY = 0;
    }

    public Rectangle getBody() {
        return body;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public void fall(int gravity) {
        this.speedY += gravity;
        if (this.speedY > 12) this.speedY = 12;
        this.body.y += this.speedY;
    }

    public void jump(int power) {
        this.speedY = -power;
    }

    public void setPosition(int y) {
        this.body.y = y;
        this.speedY = 0;
    }
}

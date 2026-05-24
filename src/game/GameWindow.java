package game;

import javax.swing.JFrame;

public abstract class GameWindow extends JFrame {
    protected final String windowId;

    public GameWindow(String windowId, String title) {
        super(title);
        this.windowId = windowId;
    }

    public abstract void createUI();
}
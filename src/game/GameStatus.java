package game;

public abstract class GameStatus {
    protected final String statusId;

    public GameStatus(String id) {
        this.statusId = id;
    }

    public abstract void resetStatus();
}
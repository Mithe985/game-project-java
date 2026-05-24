package game;

public class Player extends GameStatus {
    private String playerName;
    private int score;
    public static int totalPlayers = 0;

    public Player(String id, String name) {
        super(id);
        this.playerName = name;
        this.score = 0;
        totalPlayers++;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public void addPoint() {
        this.score++;
    }

    @Override
    public void resetStatus() {
        this.score = 0;
    }
}

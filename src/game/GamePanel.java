package game;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends GameWindow implements GameControl, ActionListener {
    private Player user = new Player("PL-01", "Player 1");
    private LevelDesign level = new LevelDesign();
    private Bird flappyBird = new Bird(150, 600 / 2 - 40, 32, 26);
    private Timer gameTimer;
    private BufferedImage titleImage;
    private Clip gameOverClip;

    private int gameState = 0, timeCount = 0, lastScoredPipeId = -1;
    private boolean isGameStartedPlaying = false;
    private final int screenWidth = 600, screenHeight = 600;
    private Font gameFont = new Font("Segoe UI", Font.BOLD, 16);

    public GamePanel(String id) {
        super(id, "Color Flappy Bird (OOP Edition)");
        try {
            titleImage = ImageIO.read(new File("src/game/Image.png"));
        } catch (IOException e) {
            System.err.println("Error: Could not load Image.png");
        }
        loadSound();
        gameTimer = new Timer(20, this);
        createUI();
    }

    private void loadSound() {
        try {
            File gameOverFile = new File("src/game/GameOverSound.wav");
            if (gameOverFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(gameOverFile);
                gameOverClip = AudioSystem.getClip();
                gameOverClip.open(audioStream);
            }
        } catch (Exception e) {
            System.err.println("Sound System Error: " + e.getMessage());
        }
    }

    private void playGameOverSound() {
        if (gameOverClip != null) {
            gameOverClip.setFramePosition(0);
            gameOverClip.start();
        }
    }

    @Override
    public void createUI() {
        setSize(screenWidth, screenHeight);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (gameState == 0 && key == KeyEvent.VK_S) {
                    startOrRestartGame();
                } else if (gameState == 1 && key == KeyEvent.VK_UP) {
                    isGameStartedPlaying = true;
                    flappyBird.jump(9);
                } else if (gameState == 2 && key == KeyEvent.VK_R) {
                    user.resetStatus();
                    startOrRestartGame();
                }
            }
        });

        setLayout(new BorderLayout());
        add(new DrawPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    private void startOrRestartGame() {
        gameState = 1;
        isGameStartedPlaying = false;
        reloadGame();
        gameTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == 1 && isGameStartedPlaying) {
            timeCount++;
            if (timeCount == 1 || timeCount % 70 == 0) level.createPipe();
            level.movePipes();

            if (timeCount % 2 == 0) flappyBird.fall(1);
            else flappyBird.getBody().y += flappyBird.getSpeedY();

            for (Rectangle topPipe : level.getPipes()) {
                if (topPipe.y == 0 && flappyBird.getBody().x > topPipe.x + topPipe.width) {
                    if (lastScoredPipeId != topPipe.hashCode()) {
                        user.addPoint();
                        lastScoredPipeId = topPipe.hashCode();
                    }
                }
            }

            if (level.checkHit(flappyBird.getBody())) {
                gameState = 2;
                gameTimer.stop();
                playGameOverSound();
            }
        }
        repaint();
    }

    @Override
    public void reloadGame() {
        flappyBird.setPosition(screenHeight / 2 - 40);
        timeCount = 0;
        lastScoredPipeId = -1;
        level.clearAll();
    }

    private class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(210, 200, 230));
            g2.fillRect(0, 0, screenWidth, screenHeight);

            drawPipesAndGround(g2);

            if (gameState == 0) drawStartScreen(g2);
            else drawGameScreen(g2);
        }

        private void drawPipesAndGround(Graphics2D g2) {
            for (Rectangle p : level.getPipes()) {
                g2.setColor(new Color(45, 52, 71));
                g2.fillRect(p.x, p.y, p.width, p.height);
                g2.setColor(new Color(30, 35, 50));
                g2.fillRect(p.x + p.width - 12, p.y, 12, p.height);
                g2.setColor(new Color(15, 20, 30));
                g2.drawRect(p.x, p.y, p.width, p.height);

                int capY = (p.y == 0) ? p.height - 25 : p.y;
                g2.setColor(new Color(45, 52, 71));
                g2.fillRect(p.x - 4, capY, p.width + 8, 25);
                g2.setColor(new Color(15, 20, 30));
                g2.drawRect(p.x - 4, capY, p.width + 8, 25);
            }

            g2.setColor(new Color(230, 210, 180));
            g2.fillRect(0, screenHeight - 120, screenWidth, 120);
            g2.setColor(new Color(100, 80, 60));
            g2.fillRect(0, screenHeight - 120, screenWidth, 16);
            g2.setColor(new Color(60, 40, 20));
            g2.drawLine(0, screenHeight - 120, screenWidth, screenHeight - 120);
        }

        private void drawBird(Graphics2D g2, int x, int y) {
            g2.setColor(new Color(247, 182, 49));
            g2.fillOval(x, y, 32, 26);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, 32, 26);

            g2.setColor(new Color(255, 230, 120));
            g2.fillOval(x + 4, y + 6, 14, 12);
            g2.setColor(Color.BLACK);
            g2.drawOval(x + 4, y + 6, 14, 12);

            g2.setColor(Color.WHITE);
            g2.fillOval(x + 20, y + 4, 8, 8);
            g2.setColor(Color.BLACK);
            g2.fillOval(x + 24, y + 6, 4, 4);

            g2.setColor(new Color(247, 100, 30));
            int[] xP = {x + 30, x + 39, x + 29}, yP = {y + 10, y + 13, y + 17};
            g2.fillPolygon(xP, yP, 3);
            g2.setColor(Color.BLACK);
            g2.drawPolygon(xP, yP, 3);
        }

        private void drawStartScreen(Graphics2D g2) {
            if (titleImage != null) g2.drawImage(titleImage, 120, 40, 360, 200, this);
            
            drawBird(g2, 130, 310);

            g2.setColor(new Color(45, 52, 71));
            g2.setFont(gameFont.deriveFont(Font.BOLD, 24f));
            g2.drawString("PRESS [ S ] TO START", 200, 338);

            g2.setFont(gameFont.deriveFont(Font.PLAIN, 14f));
            String hint = "Controls: Use UP ARROW to Fly / Jump";
            g2.drawString(hint, (screenWidth - g2.getFontMetrics().stringWidth(hint)) / 2, 385);
        }

        private void drawGameScreen(Graphics2D g2) {
            Rectangle b = flappyBird.getBody();
            drawBird(g2, b.x, b.y);

            if (gameState == 1) {
                g2.setFont(gameFont.deriveFont(Font.BOLD, 30f));
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString("" + user.getScore(), screenWidth / 2 - 8, 52);
                g2.setColor(Color.WHITE);
                g2.drawString("" + user.getScore(), screenWidth / 2 - 10, 50);

                if (!isGameStartedPlaying) {
                    g2.setFont(gameFont.deriveFont(Font.BOLD, 18f));
                    g2.setColor(new Color(50, 50, 50));
                    String ready = "Press [ UP ARROW ] to Fly!";
                    g2.drawString(ready, (screenWidth - g2.getFontMetrics().stringWidth(ready)) / 2, screenHeight / 2 - 80);
                }
            } else if (gameState == 2) {
                g2.setColor(new Color(245, 240, 230));
                g2.fillRect(150, screenHeight / 2 - 100, 300, 180);
                g2.setColor(new Color(80, 70, 60));
                g2.drawRect(150, screenHeight / 2 - 100, 300, 180);

                g2.setFont(gameFont.deriveFont(Font.BOLD, 26f));
                g2.setColor(new Color(150, 30, 30));
                String t1 = "GAME OVER";
                g2.drawString(t1, (screenWidth - g2.getFontMetrics().stringWidth(t1)) / 2, screenHeight / 2 - 50);

                g2.setFont(gameFont.deriveFont(Font.BOLD, 18f));
                g2.setColor(Color.DARK_GRAY);
                String t2 = "Score: " + user.getScore();
                g2.drawString(t2, (screenWidth - g2.getFontMetrics().stringWidth(t2)) / 2, screenHeight / 2 - 10);

                g2.setFont(gameFont.deriveFont(Font.PLAIN, 15f));
                g2.setColor(Color.BLACK);
                String t3 = "Press [ R ] to Restart Flight";
                g2.drawString(t3, (screenWidth - g2.getFontMetrics().stringWidth(t3)) / 2, screenHeight / 2 + 40);
            }
        }
    }
}
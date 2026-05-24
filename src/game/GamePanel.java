package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends GameWindow implements GameControl, ActionListener {
    private Player user;
    private LevelDesign level;
    private Bird flappyBird;
    private Timer gameTimer;

    private int gameState = 0;
    private boolean isGameStartedPlaying = false;
    private int timeCount = 0;
    private int lastScoredPipeId = -1;

    private final int screenWidth = 600;
    private final int screenHeight = 600;

    private Font gameFont = new Font("Segoe UI", Font.BOLD, 16);

    public GamePanel(String id) {
        super(id, "Color Flappy Bird (OOP Edition)");
        user = new Player("PL-01", "Player 1");
        flappyBird = new Bird(150, screenHeight / 2 - 40, 32, 26);
        level = new LevelDesign();

        gameTimer = new Timer(20, this);
        createUI();
    }

    @Override
    public void createUI() {
        this.setSize(screenWidth, screenHeight);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                if (gameState == 0 && key == KeyEvent.VK_S) {
                    gameState = 1;
                    isGameStartedPlaying = false;
                    reloadGame();
                    gameTimer.start();
                } else if (gameState == 1 && key == KeyEvent.VK_UP) {
                    if (!isGameStartedPlaying) {
                        isGameStartedPlaying = true;
                    }
                    flappyBird.jump(9);
                } else if (gameState == 2 && key == KeyEvent.VK_R) {
                    user.resetStatus();
                    gameState = 1;
                    isGameStartedPlaying = false;
                    reloadGame();
                    gameTimer.start();
                }
            }
        });
        this.setFocusable(true);

        DrawPanel drawPanel = new DrawPanel();
        this.setLayout(new BorderLayout());
        this.add(drawPanel, BorderLayout.CENTER);

        System.out.println("Game Engine Ready. Active players: " + Player.totalPlayers);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == 1) {
            if (isGameStartedPlaying) {
                timeCount++;

                if (timeCount == 1 || timeCount % 70 == 0) {
                    level.createPipe();
                }
                level.movePipes();

                if (timeCount % 2 == 0) {
                    flappyBird.fall(1);
                } else {
                    flappyBird.getBody().y += flappyBird.getSpeedY();
                }

                for (int i = 0; i < level.getPipes().size(); i += 2) {
                    Rectangle topPipe = level.getPipes().get(i);
                    
                    if (flappyBird.getBody().x > topPipe.x + topPipe.width) {
                        if (lastScoredPipeId != topPipe.hashCode()) {
                            user.addPoint();
                            lastScoredPipeId = topPipe.hashCode();
                        }
                    }
                }

                if (level.checkHit(flappyBird.getBody())) {
                    gameState = 2;
                    gameTimer.stop();
                }
            }
        }
        this.repaint();
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

            for (Rectangle p : level.getPipes()) {
                g2.setColor(new Color(45, 52, 71));
                g2.fillRect(p.x, p.y, p.width, p.height);
                
                g2.setColor(new Color(30, 35, 50));
                g2.fillRect(p.x + p.width - 12, p.y, 12, p.height);

                g2.setColor(new Color(15, 20, 30));
                g2.drawRect(p.x, p.y, p.width, p.height);

                if (p.y == 0) {
                    g2.setColor(new Color(45, 52, 71));
                    g2.fillRect(p.x - 4, p.height - 25, p.width + 8, 25);
                    g2.setColor(new Color(15, 20, 30));
                    g2.drawRect(p.x - 4, p.height - 25, p.width + 8, 25);
                } else {
                    g2.setColor(new Color(45, 52, 71));
                    g2.fillRect(p.x - 4, p.y, p.width + 8, 25);
                    g2.setColor(new Color(15, 20, 30));
                    g2.drawRect(p.x - 4, p.y, p.width + 8, 25);
                }
            }

            g2.setColor(new Color(230, 210, 180));
            g2.fillRect(0, screenHeight - 120, screenWidth, 120);
            
            g2.setColor(new Color(100, 80, 60));
            g2.fillRect(0, screenHeight - 120, screenWidth, 16);
            
            g2.setColor(new Color(60, 40, 20));
            g2.drawLine(0, screenHeight - 120, screenWidth, screenHeight - 120);

            Rectangle b = flappyBird.getBody();
            g2.setColor(new Color(247, 182, 49));
            g2.fillOval(b.x, b.y, b.width, b.height);
            g2.setColor(Color.BLACK);
            g2.drawOval(b.x, b.y, b.width, b.height);

            g2.setColor(new Color(255, 230, 120));
            g2.fillOval(b.x + 4, b.y + 6, 14, 12);
            g2.setColor(Color.BLACK);
            g2.drawOval(b.x + 4, b.y + 6, 14, 12);

            g2.setColor(Color.WHITE);
            g2.fillOval(b.x + 20, b.y + 4, 8, 8);
            g2.setColor(Color.BLACK);
            g2.fillOval(b.x + 24, b.y + 6, 4, 4);

            g2.setColor(new Color(247, 100, 30));
            int[] xPoints = {b.x + 30, b.x + 39, b.x + 29};
            int[] yPoints = {b.y + 10, b.y + 13, b.y + 17};
            g2.fillPolygon(xPoints, yPoints, 3);
            g2.setColor(Color.BLACK);
            g2.drawPolygon(xPoints, yPoints, 3);

            if (gameState == 0) {
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(120, screenHeight / 2 - 60, 360, 100);
                
                g2.setFont(gameFont.deriveFont(Font.BOLD, 22f));
                g2.setColor(Color.WHITE);
                String msg = "PRESS [ S ] TO START";
                int w = g2.getFontMetrics().stringWidth(msg);
                g2.drawString(msg, (screenWidth - w) / 2, screenHeight / 2);
                
                g2.setFont(gameFont.deriveFont(Font.PLAIN, 13f));
                String hint = "Controls: Use UP ARROW to Fly / Jump";
                int wHint = g2.getFontMetrics().stringWidth(hint);
                g2.drawString(hint, (screenWidth - wHint) / 2, screenHeight / 2 + 25);
                
            } else if (gameState == 1) {
                g2.setFont(gameFont.deriveFont(Font.BOLD, 30f));
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString("" + user.getScore(), screenWidth / 2 - 8, 52);
                g2.setColor(Color.WHITE);
                g2.drawString("" + user.getScore(), screenWidth / 2 - 10, 50);

                if (!isGameStartedPlaying) {
                    g2.setFont(gameFont.deriveFont(Font.BOLD, 18f));
                    g2.setColor(new Color(50, 50, 50));
                    String readyMsg = "Press [ UP ARROW ] to Fly!";
                    int wReady = g2.getFontMetrics().stringWidth(readyMsg);
                    g2.drawString(readyMsg, (screenWidth - wReady) / 2, screenHeight / 2 - 80);
                }
                
            } else if (gameState == 2) {
                g2.setColor(new Color(245, 240, 230));
                g2.fillRect(150, screenHeight / 2 - 100, 300, 180);
                g2.setColor(new Color(80, 70, 60));
                g2.drawRect(150, screenHeight / 2 - 100, 300, 180);

                g2.setFont(gameFont.deriveFont(Font.BOLD, 26f));
                g2.setColor(new Color(150, 30, 30));
                String t1 = "GAME OVER";
                int w1 = g2.getFontMetrics().stringWidth(t1);
                g2.drawString(t1, (screenWidth - w1) / 2, screenHeight / 2 - 50);

                g2.setFont(gameFont.deriveFont(Font.BOLD, 18f));
                g2.setColor(Color.DARK_GRAY);
                String t2 = "Score: " + user.getScore();
                int w2 = g2.getFontMetrics().stringWidth(t2);
                g2.drawString(t2, (screenWidth - w2) / 2, screenHeight / 2 - 10);

                g2.setFont(gameFont.deriveFont(Font.PLAIN, 15f));
                g2.setColor(Color.BLACK);
                String t3 = "Press [ R ] to Restart Flight";
                int w3 = g2.getFontMetrics().stringWidth(t3);
                g2.drawString(t3, (screenWidth - w3) / 2, screenHeight / 2 + 40);
            }
        }
    }
}

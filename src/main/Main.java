package src.main;

import java.util.concurrent.TimeUnit;
import javax.swing.JTextField;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;

import src.main.globals.Keys;
import src.main.globals.Time;
import src.main.scenes.SceneManager;
import src.main.utils.KeyListener;

public class Main {
    public static Canvas canvas;
    public static SceneManager sceneManager = new SceneManager();
    
    JFrame frame = new JFrame();
    Component keyListener = new JTextField();
    long currentTime = 0;
    long fpsStartTime = 0;
    int fpsCounter = 0;

    public Main() {
        canvas = new Canvas();
        canvas.addKeyListener(new KeyListener());
        canvas.setFocusable(true);
        canvas.setPreferredSize(new Dimension(Config.FRAME_SIZE[0], Config.FRAME_SIZE[1]));
        
        frame.add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Config.FRAME_TITLE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();

        while (true) {
            main.update();
        }
    }

    public void update() throws InterruptedException {
        // Calculate framerate
        if (System.currentTimeMillis() - fpsStartTime > 1000){
            fpsStartTime = System.currentTimeMillis();
            System.out.println(fpsCounter);
            fpsCounter = 0;
        }
        fpsCounter++;

        // Calculate deltatime to keep physics at a constant speed
        long prevTime = currentTime;
        currentTime = System.currentTimeMillis();
        Time.deltaTime = currentTime - prevTime;

        sceneManager.updateScene();
        updateKeyPressed();
        canvas.repaint();

        // Keep framerate at 60 fps
        long elapsedTime = System.currentTimeMillis() - currentTime;
        TimeUnit.MILLISECONDS.sleep(Math.max(1000/60 - elapsedTime, 0));
    }

    public void updateKeyPressed() {
        // For behaviors that require checking if a key is being held

        Keys.upPressed = Keys.up;
        Keys.leftPressed = Keys.left;
        Keys.downPressed = Keys.down;
        Keys.rightPressed = Keys.right;
        Keys.spacePressed = Keys.space;
        Keys.enterPressed = Keys.enter;
    }

    public class Canvas extends JPanel {
        // The main canvas component, where every sprite is drawn on

        private static final long serialVersionUID = 1L;

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            // Color the background
            g2d.setColor(Config.BACKGROUND_COLOR);
            g2d.fillRect(0, 0, Config.FRAME_SIZE[0], Config.FRAME_SIZE[1]);

            sceneManager.render(g2d);
        }
    }
}

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Image variables declaration
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;
    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    //Game Logic :
    Bird bird;
    int velocityX = -3; //moves pipes to the left (simulates bird moving right)
    int velocityY = 0;
    int gravity = 1;
    ArrayList<Pipe> pipes;
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        setFocusable(true);
        addKeyListener(this);

        //Load images into the image variables
        backgroundImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./flappybirdbg.png"))).getImage();
        birdImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./flappybird.png"))).getImage();
        topPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./toppipe.png"))).getImage();
        bottomPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./bottompipe.png"))).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        //pipes render timer
        placePipesTimer = new Timer(1500, e -> placePipes());
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

    }

    //pipe rendering function
    public void placePipes() {
        int randomPipeY = (int) (pipeY - (double) pipeHeight / 4 - Math.random() * ((double) pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    //graphics rendering functions
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over! Score " + (int) score, 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; //0.5 bcz there are 2 pipes always, 1 top 1 bottom so is going to be 1
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&     //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&    //a's top right corner passes b's top left corner
                a.y < b.y + b.height &&   //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;     // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    //player actions
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                //restart the game by resetting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }
}

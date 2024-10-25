import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;




public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 640;
    int boardHeight = 360;

    //Images 
    Image backgroundImg;
    Image fishieImg;
    Image PipeObenImg;
    Image PipeUntenImg; 

    //FishieBird

    int fishieBirdX = boardWidth/8;
    int fishieBirdY = boardHeight/2;
    int fishieBirdWidth = 24;
    int fishieBirdHeight = 34;

    class FishieBird{
        int x = fishieBirdX;
        int y = fishieBirdY;
        int width = fishieBirdWidth;
        int height = fishieBirdHeight;
        Image img;

        FishieBird(Image img) {
            this.img = img;
        }

    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 128;
    int pipeHeight = 256;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic 
    FishieBird fishiebird;
    int velocityX = -4; // move pipes to the left speed (simulates bird moving right)
    int velocityY = -6; //move bird up/down
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameloop;
    Timer placePipesTimer;

    boolean gameOver = false;

    double score = 0; 


    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this); 

        // load Images
        backgroundImg = new ImageIcon(getClass().getResource("./background.jpg")).getImage();
        fishieImg = new ImageIcon(getClass().getResource("./FlappyFishie.png")).getImage();
        PipeObenImg = new ImageIcon(getClass().getResource("./pipeOben.png")).getImage();
        PipeUntenImg = new ImageIcon(getClass().getResource("./pipeUnten.png")).getImage();

        //bird
        fishiebird = new FishieBird(fishieImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipesTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        } ); // 2 sek

        placePipesTimer.start();

        // game timer
        gameloop = new Timer(1000/60, this); //1000/60 = 16,6
        gameloop.start();
    }

    public void placePipes(){
        //(0-1) * pipeHeight/2 -> (0-256)
        //128
        //0 - 128 - (0-256) --> 1/4 pipeHeight --> 3/4 pipeHeight

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(PipeObenImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(PipeUntenImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //fishie
        g.drawImage(fishiebird.img, fishiebird.x, fishiebird.y, fishiebird.width, fishiebird.height, null);
        
        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver){
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else{
            g.drawString("Score: " + String.valueOf((int) score), 10, 35);
        }
    }

    public void move(){
        // fishiebird
        velocityY += gravity;
        fishiebird.y += velocityY;
        fishiebird.y = Math.max(fishiebird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && fishiebird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; //0,5 because there are 2 pipes! so 0,5*2 = 1, 1 for each set of pipes
            }

            if (collision(fishiebird, pipe)) {
                gameOver = true;
            }
        }

        if (fishiebird.y > boardHeight){
            gameOver = true;
        }
    }

    public boolean collision(FishieBird a, Pipe b){
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x && // a's top right corner passes b's top left corner
               a.y < b.y + b.height && //a's top left corner doesnt reach b's bottom left corner
               a.y + a.height > b.y; //a's bottom left corner passes b's top left corner 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameloop.stop();
        }
    }

   

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if (gameOver){
                //restart the game by resetting the conditions
                fishiebird.y = fishieBirdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameloop.start();
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

}

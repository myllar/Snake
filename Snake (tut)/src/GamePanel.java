import javax.swing.*;
import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener; //E.G. MOUSECLICK, BUTTONS.
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//CAN SHORTHAND BY WRITING:
import java.awt.event.*;
import java.security.PublicKey;
import java.util.IllegalFormatCodePointException;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 20;
    //CALCULATE HOW MANY OBJECTS WE CAN FIT ON THE SCREEN:
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 20;
    //BODY PARTS OF THE SNAKE:
    //SIZE SET TO GAME_UNITS, AS IT WILL NOT EXCEED THE SIZE OF THE GAME.
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6; //INITIAL SNAKE SIZE
    int applesEaten; //START WITH NO APPLES EATEN
    int appleX; //RANDOM SPAWN
    int appleY; //RANDOM SPAWN
    char direction = 'R'; //INITIAL DIRECTION MOVEMENT. (R, L, U, D)
    boolean running = false;
    Timer timer; //INSTANCE TIMER
    Random random; //INSTANCE OF THE RANDOM CLASS


    GamePanel(){
        //FINISH CREATING THE INSTANCE OF THE RANDOM CLASS:
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setForeground(Color.white);
        this.setFocusable(true);
        //Focus is so important because keyboard and mouse wheel events (for example)
        //get sent to the focus owner.
        this.addKeyListener(new myKeyAdapter());
        startGame();
    }


    public void startGame(){
        newApple(); //START SPAWN A APPLE
        running = true; //INITIAL FALSE
        timer = new Timer(DELAY,this); //PASS IN THIS, CUS WE USE THE ACTION LISTENER (SEE CLASS GAMEPANEL)
        timer.start();
    }


    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }


    public void draw(Graphics g){

        if (running){
        //DRAW A GRID FOR READABILITY:
        for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++){
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
        }

        //DRAW AN APPLE:
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); //X, Y, WIDTH, HEIGHT.

        //DRAW SNAKE HEAD AND BODY:
        for(int i = 0; i < bodyParts; i++){
            //THIS EQUALS SNAKE HEAD:
            if (i == 0){
                g.setColor(Color.GREEN);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }else{
                //SNAKE BODY
                g.setColor(new Color(35, 107, 9));
                //TO STROBE SNAKE LENGTH:
                //g.setColor((new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
        //TRACK SCORE:
        g.setColor(Color.red);
        g.setFont(new Font("ink free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth(("SCORE: " + applesEaten))) / 2, g.getFont().getSize());
    }else{
         gameOver(g);
        }
    }


    //TO POPULATE THE GAME:
    public void newApple(){
        //RANGE IN (). CAST AS AN INT() TO PREVENT A CRASH
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }


    public void move(){
        //DRAW SNAKE.
        //FIRST FOR LOOP TO ITERATE ALL BODY PARTS
        for(int i = bodyParts; i>0; i--){
            x[i] = x[i-1]; //SHIFTING ALL COORDINATES BY ONE SPOT
            y[i] = y[i-1];
        }
        //CREATE A SWITCH TO ADJUST DIRECTION OF THE SNAKE
        //REF THE DIRECTION VARIABLE;CHAR VALUE R, L, U, D
        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;    //TAKE Y AT THE INDEX 0 - UNIT SIZE
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }


    public void checkApple(){
        if ((x[0] == appleX) && y[0] == appleY){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }


    public void checkCollisions(){
        //CHECKS IF HEAD COLLIDES WITH BODY
        for (int i = bodyParts; i > 0; i--){
            if ((x[0] == x[i])&& (y[0] == y[i])){
                running = false;
            }
        }
        //CHECK IF HEAD TOUCHES LEFT BORDER
        if (x[0] < 0){
            running = false;
        }
        //CHECK IF HEAD TOUCHES RIGHT BORDER
        if (x[0] > SCREEN_WIDTH){
            running = false;
        }
        //CHECK IF HEAD TOUCHES TOP BORDER
        if (y[0] < 0){
            running = false;
        }
        //CHECK IF HEAD TOUCHES BOTTOM BORDER
        if (y[0] > SCREEN_HEIGHT){
            running = false;
        }
        if (!running){
            timer.stop();
        }
    }


    public void gameOver(Graphics g){
        //GAME OVER TEXT
        g.setColor(Color.red);
        g.setFont(new Font("ink free", Font.BOLD, 75));
        //FONT METRICS TO LINE THE TEXT ON THE SCREEN
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth(("Game Over"))) / 2, SCREEN_HEIGHT / 2);

        //FINAL SCORE:
        g.setColor(Color.red);
        g.setFont(new Font("ink free", Font.BOLD, 40));
        //FONT METRICS TO LINE THE TEXT ON THE SCREEN:
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth(("SCORE: " + applesEaten))) / 2, g.getFont().getSize());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }


    //inner class;
    public class myKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_A:
                    //LIMIT THE USER TO 90deg TURNS
                    if (direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_D:
                    if (direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_W:
                    if (direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_S:
                    if (direction != 'U'){
                        direction = 'D';
                    }
                    break;
            }

        }
    }


}

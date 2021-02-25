package PacChasers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class ghost implements KeyListener, ActionListener {
    Timer t;
    boolean positionSet = false;
    int x = 0;
    int y = 0;
    int velX = 0;
    int velY = 0;
    int currGh = 0;
    int currGhIncrement = 1;
    boolean gotBall = false;
    boolean edible = false;

    Image[] ghostImage = new Image[12];

    public ghost(int speed) {
        t = new Timer(speed, this); //speed of the ghost the higher the slower
        t.start();
        try {
            ghostImage[0] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost1.png"));
            ghostImage[1] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost1-2.png"));
            ghostImage[2] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost2.png"));
            ghostImage[3] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost2-2.png"));
            ghostImage[4] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost3.png"));
            ghostImage[5] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost3-2.png"));
            ghostImage[6] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost4.png"));
            ghostImage[7] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost4-2.png"));
            ghostImage[8] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost5.png"));
            ghostImage[9] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost5-2.png"));
            ghostImage[10] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost6.png"));
            ghostImage[11] = ImageIO.read(this.getClass().getResource("/res/ghost/ghost6-2.png"));
        } catch (IOException e) {
            System.err.println("couldnt find pacman resources");
        }
        ActionListener ghCurImg = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(!gotBall && !edible) {
                    if(currGh % 2 != 0) currGh=0;
                    if(currGh == 0) currGhIncrement = 2;
                    if(currGh == 10) currGhIncrement = -2;
                }else if(gotBall && !edible){
                    if(currGh % 2 == 0) currGh=1;
                    if(currGh == 1) currGhIncrement = 2;
                    if (currGh == 11) currGhIncrement = -2;
                }
                if(edible){
                    if(currGh == 0) currGhIncrement = 1;
                    if(currGh == 11) currGhIncrement = -1;

                }
                if(currGh == 10 && currGhIncrement == 2){
                    currGh++;
                    currGhIncrement = -1;
                }else{
                    currGh += currGhIncrement;
                }

            }
        };
        Timer pacT = new Timer(170, ghCurImg);
        pacT.start();
    }

//    public void paintComponent(Graphics g){
//        super.paintComponent(g);
//
//        g.drawImage(pac[1],0,0, null);
//    }


    public Image getImage(){
        return ghostImage[currGh];
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        x += velX;
        y += velY;
    }

    public void up(){
        velY = -1;
        velX = 0;
    }

    public void down(){
        velY = 1;
        velX = 0;
    }

    public void right(){
        velY = 0;
        velX = 1;
    }

    public void left(){
        velY = 0;
        velX = -1;
    }

    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();
        if(keycode == KeyEvent.VK_UP){
            up();
        }
        if(keycode == KeyEvent.VK_DOWN){
            down();
        }
        if(keycode == KeyEvent.VK_LEFT){
            left();
        }
        if(keycode == KeyEvent.VK_RIGHT){
            right();
        }
    }
}

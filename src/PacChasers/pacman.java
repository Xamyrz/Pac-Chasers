package PacChasers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class pacman implements KeyListener, ActionListener {
    Timer t = new Timer(10, this); //speed of the pacman the higher the slower
    boolean positionSet = false;
    int x = 0;
    int y = 0;
    int velX = 0;
    int velY = 0;
    int currPac = 0;
    int currpacIncrement = 1;
    int pacFacing = 2;
    int turning = -1;
    boolean movingSet = false;

    Image[] pacImage = new Image[6];

    public pacman() {
        t.start();
        try {
            pacImage[0] = ImageIO.read(this.getClass().getResource("/res/pacman/pac1.png"));
            pacImage[1] = ImageIO.read(this.getClass().getResource("/res/pacman/pac2.png"));
            pacImage[2] = ImageIO.read(this.getClass().getResource("/res/pacman/pac3.png"));
            pacImage[3] = ImageIO.read(this.getClass().getResource("/res/pacman/pac4.png"));
            pacImage[4] = ImageIO.read(this.getClass().getResource("/res/pacman/pac5.png"));
            pacImage[5] = ImageIO.read(this.getClass().getResource("/res/pacman/pac6.png"));
        } catch (IOException e) {
            System.err.println("couldnt find pacman resources");
        }
        ActionListener pacCurImg = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(currPac == 0) currpacIncrement=1;
                if(currPac == 5) currpacIncrement=-1;
                currPac += currpacIncrement;
            }
        };
        Timer pacT = new Timer(70, pacCurImg);
        pacT.start();
    }

//    public void paintComponent(Graphics g){
//        super.paintComponent(g);
//
//        g.drawImage(pac[1],0,0, null);
//    }


    public Image getImage(){
        return pacImage[currPac];
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
        pacFacing = 0;
        turning = -1;
        velY = -1;
        velX = 0;
    }

    public void down(){
        pacFacing = 1;
        turning = -1;
        velY = 1;
        velX = 0;
    }

    public void right(){
        pacFacing = 2;
        turning = -1;
        velY = 0;
        velX = 1;
    }

    public void left(){
        pacFacing = 3;
        turning = -1;
        velY = 0;
        velX = -1;
    }

    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();
        if(keycode == KeyEvent.VK_W){
            if(movingSet) turning=0;
            else up();
            movingSet = true;

        }
        if(keycode == KeyEvent.VK_S){
            if(movingSet) turning=1;
            else down();
            movingSet = true;
        }
        if(keycode == KeyEvent.VK_A){
            if(movingSet)turning=2;
            else left();
            movingSet = true;
        }
        if(keycode == KeyEvent.VK_D){
            if(movingSet)turning=3;
            else right();
            movingSet = true;
        }
    }
}

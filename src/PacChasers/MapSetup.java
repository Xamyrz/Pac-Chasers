package PacChasers;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class MapSetup extends JPanel {
    private volatile boolean runningGhBall = true;
    public Thread Tgh;
    private volatile boolean runningPacBall = true;
    public Thread Tpac;
    final int TILE_SIZE_PX = 24;
    List<int[]> wallList = new ArrayList<int[]>();
    ArrayList<ArrayList<String>> map = new ArrayList<ArrayList<String>>();
    //List<int[]> foodList = new ArrayList<int[]>();
    ArrayList<food> foodList = new ArrayList<food>();
    boolean wallSet = false;
    boolean foodSet = false;
    int foodLeft = 0;
    Font myFont;
    Scanner scanner;
    pacman pac = new pacman();
    ghost gh = new ghost(13);
    Timer repaintT;
    ActionListener repaintA;
    Sounds sfx = new Sounds();


    boolean ghostWin = false;
    boolean pacmanWin = false;
    public MapSetup() {

        setFocusable(true);
        addKeyListener(pac);
        addKeyListener(gh);
        //loading the font taken from https://docs.oracle.com/javase/tutorial/2d/text/fonts.html
        //font taken from https://www.dafont.com/04b-30.font
        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/res/font/04b30.ttf")).deriveFont(12f);
        } catch (IOException | FontFormatException e) {
            System.out.println(e);
        }

        setBackground(Color.BLACK);

        setLayout(null);

        //load the map
        int lineIndex = 0;
        int charIndex = 0;
        int x=0,y=0;
        InputStream in = getClass().getResourceAsStream("/res/map/map.txt");
        scanner = new Scanner(in);
        while(scanner.hasNextLine()) {
            Scanner scLine = new Scanner(scanner.nextLine());
            while(scLine.hasNext()) {
                String c = scLine.next();

                map.add(new ArrayList<>());
                map.get(lineIndex).add(c);
                map.get(lineIndex).set(charIndex, c);
                if(c.equals("*")){
                    if(!foodSet){
                        foodList.add(new food(x,y, false, false));
                        foodLeft++;
                    }
                }
                if(c.equals("b")){
                    if(!foodSet){
                        foodList.add(new food(x,y, false, true));
                        foodLeft++;
                    }
                }
                //sets starting position of pacman when found on the map file
                if(c.equals("p")){
                    pac.positionSet = true;
                    pac.x=x;
                    pac.y=y;
                }
                //sets starting position of ghost when found on the map file
                if(c.equals("g")){
                    gh.positionSet = true;
                    gh.x=x;
                    gh.y=y;
                }


                charIndex++;
                x += TILE_SIZE_PX;

            }
            //System.out.println(map.get(0).size());
            charIndex=0;
            lineIndex++;
            x = 0;
            y += TILE_SIZE_PX;
        }
        foodSet = true;
        wallSet = true;
        repaintA = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                repaint();
            }
        };
        repaintT = new Timer(7,repaintA);
        repaintT.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        //draw map
        int x=0,y=0;
        for(int col=0; map.get(col).size() != 0; col++) {
            for(int row=0; row<map.get(0).size(); row++) {
                if(map.get(col).get(row).equals("x")) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, TILE_SIZE_PX, TILE_SIZE_PX);
                }
                x += TILE_SIZE_PX;
            }
            x=0;
            y += TILE_SIZE_PX;
        }

        //draw food
        for (food food : foodList) {
            if (!food.eaten && !food.foodBall) {
                g.setColor(Color.yellow);
                g.fillOval(food.x + 8, food.y + 8, 7, 7);
            }else if(!food.eaten && food.foodBall){
                g.setColor(Color.RED);
                g.fillOval(food.x + 5, food.y + 5, 14, 14);
            }
        }
        //checks the distance between the walls and the pacman to detect colision
//        for(int i = 0; i< wallList.size(); i++){
//            double a = pac.x+12 - wallList.get(i)[0];
//            double b = pac.y+12 - wallList.get(i)[1];
//            if(Math.sqrt(a*a+b*b) < 23){
//                //System.out.println("hit");
//                pac.velX = 0;
//                pac.velY = 0;
//            }
//        }

        //check the distance between the pacman and the food,
        //the distance between the ghost and the foodball
        for(int i=0 ; i<foodList.size() ; i++){
            //check the distance between the pacman and the food,
            double ap = pac.x - foodList.get(i).x;
            double bp = pac.y - foodList.get(i).y;
            boolean dp = Math.sqrt(ap * ap + bp * bp) < 10;
            if(dp && !foodList.get(i).eaten && !foodList.get(i).foodBall){
                foodList.get(i).eaten = true;
                foodLeft--;
            }else if(dp && !foodList.get(i).eaten) {
                foodList.get(i).eaten = true;
                runningPacBall = true;
                Tpac = new Thread(new Runnable() {
                    public void run() {
                        while (runningPacBall) {
                            try {
                                sfx.music.stop();
                                sfx.effect.loop(Clip.LOOP_CONTINUOUSLY);
                                sfx.effect.start();
                                gh.edible = true;
                                Thread.sleep(10000);
                                gh.edible = false;
                                sfx.effect.stop();
                                sfx.music.loop(Clip.LOOP_CONTINUOUSLY);
                                if (ghostWin || pacmanWin) {
                                    sfx.music.stop();
                                } else {
                                    sfx.music.start();
                                }
                                stopThread();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                if (!runningPacBall){
                                    break;
                                }
                            }
                        }
                    }
                    public void stopThread() {
                        runningPacBall = false;
                        Tpac.interrupt();
                    }
                });
                Tpac.start();
                foodLeft--;
            }

            double ag = gh.x - foodList.get(i).x;
            double bg = gh.y - foodList.get(i).y;
            boolean dg = Math.sqrt(ag * ag + bg * bg) < 10;
            if(dg && !foodList.get(i).eaten && foodList.get(i).foodBall){
                foodList.get(i).eaten = true;
                runningGhBall = true;
                Tgh = new Thread(new Runnable() {
                    public void run() {
                        while (runningGhBall) {
                            try {
                                sfx.music.stop();
                                sfx.effect.loop(Clip.LOOP_CONTINUOUSLY);
                                sfx.effect.start();
                                gh.t.stop();
                                int gX = gh.x;
                                int gY = gh.y;
                                int gvx = gh.velX;
                                int gvy = gh.velY;
                                gh = new ghost(7);
                                addKeyListener(gh);
                                gh.x = gX;
                                gh.y = gY;
                                gh.velX = gvx;
                                gh.velY = gvy;
                                gh.gotBall = true;
                                Thread.sleep(10000);
                                gX = gh.x;
                                gY = gh.y;
                                gvx = gh.velX;
                                gvy = gh.velY;
                                gh = new ghost(13);
                                addKeyListener(gh);
                                gh.x = gX;
                                gh.y = gY;
                                gh.velX = gvx;
                                gh.velY = gvy;
                                gh.gotBall = false;
                                sfx.effect.stop();
                                sfx.music.loop(Clip.LOOP_CONTINUOUSLY);
                                sfx.music.start();
                                if (ghostWin || pacmanWin) {
                                    sfx.music.stop();
                                } else {
                                    sfx.music.start();
                                }
                                stopThread();
                            } catch (InterruptedException e) {
                                if(!runningGhBall){
                                    break;
                                }
                            }
                        }
                    }
                    public void stopThread() {
                        runningGhBall = false;
                        Tgh.interrupt();
                    }
                });
                Tgh.start();
                foodLeft--;
            }
        }

        g.setFont(myFont);
        g.setColor(Color.ORANGE);
        g.drawString("Food left: "+ foodLeft, 18, 18);

        //distance between pacman and ghost
        double a = gh.x - pac.x;
        double b = gh.y - pac.y;
        boolean d = Math.sqrt(a * a + b * b) < 20;
        //ghost win
        if(((!gh.edible && d) || ghostWin) && !pacmanWin) {
            Font ghWinFont = null;
            try {
                ghWinFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/res/font/04b30.ttf")).deriveFont(24f);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
            g.setFont(ghWinFont);
            g.setColor(Color.RED);
            g.drawString("GHOST WINS!", 220, 358);
            gh.velX=0;
            gh.velY=0;
            removeKeyListener(gh);
            pac.velX=0;
            pac.velY=0;
            removeKeyListener(pac);
            ghostWin = true;
            sfx.effect.stop();
            sfx.music.stop();
        }
        //pacman wins
        if((((gh.edible && d) || pacmanWin) && !ghostWin) || foodLeft==0){
            Font ghWinFont = null;
            try {
                ghWinFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/res/font/04b30.ttf")).deriveFont(24f);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
            g.setFont(ghWinFont);
            g.setColor(Color.ORANGE);
            g.drawString("PACMAN WINS!", 220, 358);
            gh.velX=0;
            gh.velY=0;
            removeKeyListener(gh);
            pac.velX=0;
            pac.velY=0;
            removeKeyListener(pac);
            pacmanWin = true;
            sfx.effect.stop();
            sfx.music.stop();
        }


        //Pacman wall Collisions
        if(pac.velX == 1){
            if(pac.turning == -1 && map.get((int) Math.ceil((double) pac.y / TILE_SIZE_PX)).get((pac.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(pac.y, TILE_SIZE_PX)).get((pac.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                pac.velX=0;
                pac.velY=0;

                pac.x = pac.x/TILE_SIZE_PX*TILE_SIZE_PX;
            }
            if(pac.turning == 0){
                if(!map.get((pac.y-TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(pac.x,TILE_SIZE_PX)).equals("x")){
                    pac.up();
                }
            }else if(pac.turning == 1){
                if(!map.get((pac.y+TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(pac.x, TILE_SIZE_PX)).equals("x")){
                    pac.down();
                }
            }else if(pac.turning == 2){
                if(!map.get((int) Math.ceil((double) pac.y / TILE_SIZE_PX)).get(pac.x/TILE_SIZE_PX).equals("x")){
                    pac.left();
                }
            }

        }
        else if(pac.velX == -1){
            if(pac.turning == -1 && map.get((int) Math.ceil((double) pac.y / TILE_SIZE_PX)).get(pac.x/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(pac.y, TILE_SIZE_PX)).get(pac.x/TILE_SIZE_PX).equals("x")){
                pac.velX=0;
                pac.velY=0;
                pac.x = (pac.x+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }


            if(pac.turning == 0){
                if(!map.get((pac.y-TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x / TILE_SIZE_PX)).equals("x")){
                    pac.up();
                }
            }else if(pac.turning == 1){
                if(!map.get((pac.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x / TILE_SIZE_PX)).equals("x")){
                    pac.down();
                }
            }else if(pac.turning == 3){
                if(!map.get(Math.floorDiv(pac.y, TILE_SIZE_PX)).get(pac.x/TILE_SIZE_PX).equals("x")){
                    pac.right();
                }
            }
            //

        }
        else if(pac.velY == 1){
            if(pac.turning == -1 && map.get((pac.y+TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(pac.x,TILE_SIZE_PX)).equals("x") ||
                    map.get((pac.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x/TILE_SIZE_PX)).equals("x")){
                pac.velX=0;
                pac.velY=0;
                pac.y = pac.y/TILE_SIZE_PX*TILE_SIZE_PX;
            }

            if(pac.turning == 3){
                if(!map.get(Math.floorDiv(pac.y, TILE_SIZE_PX)).get((pac.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    pac.right();
                }
            }else if(pac.turning == 2){
                if(!map.get(Math.floorDiv(pac.y, TILE_SIZE_PX)).get((pac.x-TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    pac.left();
                }
            }else if(pac.turning == 0){
                if(!map.get((pac.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x/TILE_SIZE_PX)).equals("x")){
                    pac.up();
                }
            }
            //
        }
        else if(pac.velY == -1){
            if(pac.turning == -1 && map.get((pac.y)/TILE_SIZE_PX).get(Math.floorDiv(pac.x, TILE_SIZE_PX)).equals("x") ||
                    map.get((pac.y)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x/TILE_SIZE_PX)).equals("x")){
                pac.velX=0;
                pac.velY=0;
                pac.y = (pac.y+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }

            if(pac.turning == 3){
                if(!map.get((int) Math.ceil((double)pac.y / TILE_SIZE_PX)).get((pac.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    pac.right();
                }
            }else if(pac.turning == 2){
                if(!map.get((int) Math.ceil((double)pac.y / TILE_SIZE_PX)).get((pac.x-TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    pac.left();
                }
            }else if(pac.turning == 1){
                if(!map.get((pac.y)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x/TILE_SIZE_PX)).equals("x")){
                    pac.down();
                }
            }
            //
        }
        if(pac.turning != -1 && (pac.velX==0 && pac.velY==0)){
            if(pac.turning == 0) pac.up();
            if(pac.turning == 1) pac.down();
            if(pac.turning == 2) pac.left();
            if(pac.turning == 3) pac.right();
        }

        //Ghost wall Collisions
        if(gh.velX == 1){
            if(gh.turning == -1 && map.get((int) Math.ceil((double) gh.y / TILE_SIZE_PX)).get((gh.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(gh.y, TILE_SIZE_PX)).get((gh.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                gh.velX=0;
                gh.velY=0;

                gh.x = gh.x/TILE_SIZE_PX*TILE_SIZE_PX;
            }
            if(gh.turning == 0){
                if(!map.get((gh.y-TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(gh.x,TILE_SIZE_PX)).equals("x")){
                    gh.up();
                }
            }else if(gh.turning == 1){
                if(!map.get((gh.y+TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(gh.x, TILE_SIZE_PX)).equals("x")){
                    gh.down();
                }
            }else if(gh.turning == 2){
                if(!map.get((int) Math.ceil((double) gh.y / TILE_SIZE_PX)).get(gh.x/TILE_SIZE_PX).equals("x")){
                    gh.left();
                }
            }
//            System.out.println("gh.y+TILE_SIZE_PX: "+ Math.floorDiv(gh.y, TILE_SIZE_PX));
//            System.out.println("gh.y/TILE_SIZE_PX: "+ (int) Math.ceil((double) gh.y / TILE_SIZE_PX));

        }
        else if(gh.velX == -1){
            if(gh.turning == -1 && map.get((int) Math.ceil((double) gh.y / TILE_SIZE_PX)).get(gh.x/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(gh.y, TILE_SIZE_PX)).get(gh.x/TILE_SIZE_PX).equals("x")){
                gh.velX=0;
                gh.velY=0;
                gh.x = (gh.x+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }


            if(gh.turning == 0){
                if(!map.get((gh.y-TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x / TILE_SIZE_PX)).equals("x")){
                    gh.up();
                }
            }else if(gh.turning == 1){
                if(!map.get((gh.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x / TILE_SIZE_PX)).equals("x")){
                    gh.down();
                }
            }else if(gh.turning == 3){
                if(!map.get(Math.floorDiv(gh.y, TILE_SIZE_PX)).get(gh.x/TILE_SIZE_PX).equals("x")){
                    gh.right();
                }
            }
            //

        }
        else if(gh.velY == 1){
            if(gh.turning == -1 && map.get((gh.y+TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(gh.x,TILE_SIZE_PX)).equals("x") ||
                    map.get((gh.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x/TILE_SIZE_PX)).equals("x")){
                gh.velX=0;
                gh.velY=0;
                gh.y = gh.y/TILE_SIZE_PX*TILE_SIZE_PX;
            }

            if(gh.turning == 3){
                if(!map.get(Math.floorDiv(gh.y, TILE_SIZE_PX)).get((gh.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    gh.right();
                }
            }else if(gh.turning == 2){
                if(!map.get(Math.floorDiv(gh.y, TILE_SIZE_PX)).get((gh.x-TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    gh.left();
                }
            }else if(gh.turning == 0){
                if(!map.get((gh.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x/TILE_SIZE_PX)).equals("x")){
                    gh.up();
                }
            }
            //
        }
        else if(gh.velY == -1){
            if(gh.turning == -1 && map.get((gh.y)/TILE_SIZE_PX).get(Math.floorDiv(gh.x, TILE_SIZE_PX)).equals("x") ||
                    map.get((gh.y)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x/TILE_SIZE_PX)).equals("x")){
                gh.velX=0;
                gh.velY=0;
                gh.y = (gh.y+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }

            if(gh.turning == 3){
                if(!map.get((int) Math.ceil((double)gh.y / TILE_SIZE_PX)).get((gh.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    gh.right();
                }
            }else if(gh.turning == 2){
                if(!map.get((int) Math.ceil((double)gh.y / TILE_SIZE_PX)).get((gh.x-TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                    gh.left();
                }
            }else if(gh.turning == 1){
                if(!map.get((gh.y)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x/TILE_SIZE_PX)).equals("x")){
                    gh.down();
                }
            }
            //
        }
        if(gh.turning != -1 && (gh.velX==0 && gh.velY==0)){
            if(gh.turning == 0) gh.up();
            if(gh.turning == 1) gh.down();
            if(gh.turning == 2) gh.left();
            if(gh.turning == 3) gh.right();
        }

        g.drawImage(gh.getImage(), gh.x,gh.y, null);
        //rotation of image learned from example on https://www.programcreek.com/java-api-examples/?class=java.awt.Graphics2D&method=rotate
        if(pac.pacFacing == 0){
            Graphics2D g2d = (Graphics2D) g;
            g2d.rotate(Math.toRadians(270), pac.getImage().getHeight(null) / 2, pac.getImage().getWidth(null) / 2);
            g2d.drawImage(pac.getImage(), -pac.y, pac.x , null);
        }
        //facing down
        if(pac.pacFacing == 1){
            Graphics2D g2d = (Graphics2D) g;
            g2d.rotate(Math.toRadians(90), pac.getImage().getHeight(null) / 2, pac.getImage().getWidth(null) / 2);
            g2d.drawImage(pac.getImage(), pac.y, -pac.x , null);
        }
        //facing right
        if(pac.pacFacing == 2){
            g.drawImage(pac.getImage(), pac.x,pac.y, null);
        }
        //facing left
        if(pac.pacFacing == 3){
            Graphics2D g2d = (Graphics2D) g;
            g2d.rotate(Math.toRadians(180), pac.getImage().getHeight(null) / 2, pac.getImage().getWidth(null) / 2);
            //pac.x and pac.y needs to be inverted after rotation
            g2d.drawImage(pac.getImage(), -pac.x, -pac.y , null);
        }
    }
}
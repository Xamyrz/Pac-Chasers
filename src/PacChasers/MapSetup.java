package PacChasers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class MapSetup extends JPanel {
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
    ghost gh = new ghost();
    Timer repaintT;
    ActionListener repaintA;
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
        int lineIndex = 0;
        int charIndex = 0;
        int x=0,y=0;
        super.paintComponents(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        InputStream in = getClass().getResourceAsStream("/res/map/map.txt");
        scanner = new Scanner(in);
        while(scanner.hasNextLine()) {
            Scanner scLine = new Scanner(scanner.nextLine());
            while(scLine.hasNext()) {
                String c = scLine.next();

                //draw walls
                if(c.equals("x")){
                    g.setColor(Color.BLUE);
                    g.fillRect(x,y,TILE_SIZE_PX,TILE_SIZE_PX);
                }
                if(!wallSet){
                    map.add(new ArrayList<>());
                    map.get(lineIndex).add(c);
                    map.get(lineIndex).set(charIndex, c);
                }
                if(c.equals("*")){
                    if(!foodSet){
                        foodList.add(new food(x,y, false));
                        foodLeft++;
                    }
                }
                //sets position of pacman when found on the map file
                if(c.equals("p") && !pac.positionSet){
                    pac.positionSet = true;
                    pac.x=x;
                    pac.y=y;
                }
                if(c.equals("g") && !gh.positionSet){
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
        for (food food : foodList) {
            if (!food.eaten) {
                g.setColor(Color.yellow);
                g.fillOval(food.x + 8, food.y + 8, 6, 6);
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

        //check the distance between the pacman and the food
        for(int i=0 ; i<foodList.size() ; i++){
            double a = pac.x - foodList.get(i).x;
            double b = pac.y - foodList.get(i).y;
            if(Math.sqrt(a*a+b*b) < 10 && !foodList.get(i).eaten){
                foodList.get(i).eaten = true;
                foodLeft--;
                System.out.println(foodList.get(i).eaten);
            }
        }
        //rotation of image learned from example on https://www.programcreek.com/java-api-examples/?class=java.awt.Graphics2D&method=rotate

        g.setFont(myFont);
        g.setColor(Color.ORANGE);
        g.drawString("Food left: "+ foodLeft, 18, 18);

        //Pacman Collisions
        if(pac.velX == 1){
//            System.out.println("pac.y+TILE_SIZE_PX: "+ Math.floorDiv(pac.y, TILE_SIZE_PX));
//            System.out.println("pac.y/TILE_SIZE_PX: "+ (int) Math.ceil((double) pac.y / TILE_SIZE_PX));
            if(map.get((int) Math.ceil((double) pac.y / TILE_SIZE_PX)).get((pac.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(pac.y, TILE_SIZE_PX)).get((pac.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                pac.velX=0;
                pac.velY=0;

                pac.x = pac.x/TILE_SIZE_PX*TILE_SIZE_PX;
            }
        }
        else if(pac.velX == -1){
            if(map.get((int) Math.ceil((double) pac.y / TILE_SIZE_PX)).get(pac.x/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(pac.y, TILE_SIZE_PX)).get(pac.x/TILE_SIZE_PX).equals("x")){
                pac.velX=0;
                pac.velY=0;
                System.out.println("hit");
                pac.x = (pac.x+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }

        }
        else if(pac.velY == 1){
            if(map.get((pac.y+TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(pac.x,TILE_SIZE_PX)).equals("x") ||
                    map.get((pac.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x/TILE_SIZE_PX)).equals("x")){
                pac.velX=0;
                pac.velY=0;

                pac.y = pac.y/TILE_SIZE_PX*TILE_SIZE_PX;
            }
        }
        else if(pac.velY == -1){
            if(map.get((pac.y)/TILE_SIZE_PX).get(Math.floorDiv(pac.x, TILE_SIZE_PX)).equals("x") ||
                    map.get((pac.y)/TILE_SIZE_PX).get((int) Math.ceil((double) pac.x/TILE_SIZE_PX)).equals("x")){
                pac.velX=0;
                pac.velY=0;
                System.out.println("hit");
                pac.y = (pac.y+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }
        }

        //Ghost Collisions
        if(gh.velX == 1){
            if(map.get((int) Math.ceil((double) gh.y / TILE_SIZE_PX)).get((gh.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(gh.y, TILE_SIZE_PX)).get((gh.x+TILE_SIZE_PX)/TILE_SIZE_PX).equals("x")){
                gh.velX=0;
                gh.velY=0;

                gh.x = gh.x/TILE_SIZE_PX*TILE_SIZE_PX;
            }
        }
        else if(gh.velX == -1){
            if(map.get((int) Math.ceil((double) gh.y / TILE_SIZE_PX)).get(gh.x/TILE_SIZE_PX).equals("x") ||
                    map.get(Math.floorDiv(gh.y, TILE_SIZE_PX)).get(gh.x/TILE_SIZE_PX).equals("x")){
                gh.velX=0;
                gh.velY=0;
                System.out.println("hit");
                gh.x = (gh.x+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }

        }
        else if(gh.velY == 1){
            if(map.get((gh.y+TILE_SIZE_PX)/TILE_SIZE_PX).get(Math.floorDiv(gh.x,TILE_SIZE_PX)).equals("x") ||
                    map.get((gh.y+TILE_SIZE_PX)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x/TILE_SIZE_PX)).equals("x")){
                gh.velX=0;
                gh.velY=0;

                gh.y = gh.y/TILE_SIZE_PX*TILE_SIZE_PX;
            }
        }
        else if(gh.velY == -1){
            if(map.get((gh.y)/TILE_SIZE_PX).get(Math.floorDiv(gh.x, TILE_SIZE_PX)).equals("x") ||
                    map.get((gh.y)/TILE_SIZE_PX).get((int) Math.ceil((double) gh.x/TILE_SIZE_PX)).equals("x")){
                gh.velX=0;
                gh.velY=0;
                System.out.println("hit");
                gh.y = (gh.y+TILE_SIZE_PX)/TILE_SIZE_PX*TILE_SIZE_PX;
            }
        }

        g.drawImage(gh.getImage(), gh.x,gh.y, null);
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
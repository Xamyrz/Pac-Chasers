//Kamil Michalski
//18469806
package PacChasers;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class LoadMap extends JFrame {
    MapSetup gameMap;

    public LoadMap(String map){
        setLayout(new BorderLayout());
        setResizable(false);
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        gameMap = new MapSetup(map);
        add(gameMap);
        setSize(685,730);
        setVisible(true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    while(!gameMap.ghostWin && !gameMap.pacmanWin){
                        Thread.sleep(1000);
                    }
                    Thread.sleep(5000);
                    dispose();
                    gameMap.sfx.effect.stop();
                    gameMap.sfx.music.stop();
                    gameMap = null;
                    MainWindow pacchasers = new MainWindow();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}

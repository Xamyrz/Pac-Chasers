package PacChasers;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class LoadMap extends JFrame {

    public LoadMap(){
        setLayout(new BorderLayout());
        setResizable(false);
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MapSetup gameMap = new MapSetup();
        add(gameMap);
        setSize(685,730);
        setVisible(true);

    }


}

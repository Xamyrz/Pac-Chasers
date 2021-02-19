package PacChasers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainWindow extends JFrame{
    public Font myFont;

    public MainWindow() {
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        java.net.URL imgURL = getClass().getResource("/res/Menu/logo.png");
        ImageIcon Logo = new ImageIcon(imgURL);
        Image mapImage = Logo.getImage();
        Image scaledImage = mapImage.getScaledInstance(400,300, Image.SCALE_SMOOTH);
        Logo = new ImageIcon(scaledImage);

        JLabel StartMenuImage = new JLabel(Logo);
        StartMenuImage.setBackground(Color.BLACK);
        StartMenuImage.setOpaque(true);
        add(StartMenuImage, BorderLayout.NORTH);

        //loading the font taken from https://docs.oracle.com/javase/tutorial/2d/text/fonts.html
        //font taken from https://www.dafont.com/04b-30.font
        try {
            myFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/res/font/04B_30__.TTF")).deriveFont(48f);
        } catch (IOException|FontFormatException e) {
            System.out.println(e);
        }

        JPanel ButtonPanel = new JPanel();
        ButtonPanel.setLayout(new BoxLayout(ButtonPanel,2));
        ButtonPanel.setBackground(Color.BLACK);

        JButton StartButton = new JButton("Start ");
        StartButton.setFont(myFont);
        StartButton.setBorder(null);
        StartButton.setBorderPainted(false);
        StartButton.setContentAreaFilled(false);
        StartButton.setForeground(Color.ORANGE);


        //StartButton Mouse Listeners
        StartButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                StartButton.setForeground(Color.GRAY);
            }

            public void mouseExited(MouseEvent e) {
                StartButton.setForeground(Color.ORANGE);
            }

            public void mouseClicked(MouseEvent e){
                System.out.println("done");
                LoadMap board = new LoadMap();
                dispose();
            }
        });

        JButton LevelsButton = new JButton(" Levels");
        LevelsButton.setFont(myFont);
        LevelsButton.setBorder(null);
        LevelsButton.setBorderPainted(false);
        LevelsButton.setContentAreaFilled(false);
        LevelsButton.setForeground(Color.ORANGE);


        //StartButton Mouse Listeners
        LevelsButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                LevelsButton.setForeground(Color.GRAY);
            }

            public void mouseExited(MouseEvent e) {
                LevelsButton.setForeground(Color.ORANGE);
            }

            public void mouseClicked(MouseEvent e){
                System.out.println("done");
            }
        });



        ButtonPanel.add(StartButton);
        ButtonPanel.add(LevelsButton);
        add(ButtonPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }
}

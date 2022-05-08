import view.ChessGameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Welcome extends JFrame {

    private JPanel WelcomePanel;
    private JButton PlayButton;
    private JButton SettingsButton;
    private JButton AboutUsButton;
    private int WIDTH;
    private int HEIGHT;

    public Welcome(int width, int height) {
        setTitle("Checkmate"); //设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        setContentPane(WelcomePanel);
        this.pack();
        SettingsButton.setSize(200,50);
        SettingsButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        SettingsButton.setLocation(WIDTH/2-100,HEIGHT/2+30);
        PlayButton.setSize(200, 50);
        PlayButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        PlayButton.setLocation(WIDTH/2-100,HEIGHT/2-30);
        AboutUsButton.setSize(200,50);
        AboutUsButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        AboutUsButton.setLocation(WIDTH/2-100,HEIGHT/2+90);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setIconImages(null);//这个应该可以用来改变窗体icon
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);
        PlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Play button clicked");
                ChessGameFrame chessGameFrame = new ChessGameFrame(1000, 760);
                chessGameFrame.setVisible(true);
                Main.welcomeFrame.setVisible((false));
            }
        });
        SettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        AboutUsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        //23:14

        //2022/5/8 23:13
    }
}

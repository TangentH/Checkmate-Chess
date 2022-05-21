package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Welcome extends JFrame {

    private JPanel WelcomePanel;
    private JButton PlayButton;
    private JButton SettingsButton;
    private JButton AboutUsButton;
    private JLabel Bg;
    private JPanel SettingPanel;
    private JLabel musicLabel;
    private JLabel themeLabel;
    private JLabel animationLabel;
    private JButton menuButton;
    private JRadioButton bgmRadioButton;
    private JRadioButton fxRadioButton;
    private JRadioButton chesscomThemeButton;
    private JRadioButton demoThemeRadioButton;
    private JRadioButton disabledRadioButton;
    private JRadioButton dragonTailRadioButton;
    private JRadioButton sneakAttackRadioButton;
    private JLabel spacing1;
    private JLabel spacing2;
    private JLabel settingsBackground;
    private int WIDTH;
    private int HEIGHT;

    public Welcome(int width, int height) {
        setTitle("Checkmate"); //设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        setContentPane(WelcomePanel);
        this.setResizable(false);//静止调整窗体大小
        this.pack();
        SettingsButton.setSize(200, 50);
        SettingsButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        SettingsButton.setLocation(WIDTH / 2 - 100, HEIGHT / 2 + 30);
        PlayButton.setSize(200, 50);
        PlayButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        PlayButton.setLocation(WIDTH / 2 - 100, HEIGHT / 2 - 30);
        AboutUsButton.setSize(200, 50);
        AboutUsButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        AboutUsButton.setLocation(WIDTH / 2 - 100, HEIGHT / 2 + 90);
        Bg.setSize(550, 760);
        ImageIcon picture = new ImageIcon("images/WelcomeBackground.png");  //load a picture from computer
        Bg.setIcon(picture);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setIconImages(null);//这个应该可以用来改变窗体icon
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        PlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Play button clicked");
                ChessGameFrame chessGameFrame = new ChessGameFrame(1050, 820);
                chessGameFrame.setVisible(true);
                chessGameFrame.setIconImage(new ImageIcon("./images/Icon.png").getImage());
                Main.welcomeFrame.setVisible((false));
            }
        });
        SettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSettingPanel();
            }
        });
        AboutUsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContentPane(WelcomePanel);
            }
        });
        bgmRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!bgmRadioButton.isSelected()){
                    System.out.println("bgm stop!");
                }else{
                }
            }
        });
        fxRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        chesscomThemeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void generateSettingPanel() {
        this.setContentPane(SettingPanel);
        SettingPanel.setLayout(null);//这句很关键，不然无法改变组件位置（被gridlayout锁定了），而且无法让背景图片置于底层
        //设置字体
        musicLabel.setFont(new Font("Rockwell", Font.BOLD, 30));
        themeLabel.setFont(new Font("Rockwell", Font.BOLD, 30));
        animationLabel.setFont(new Font("Rockwell", Font.BOLD, 30));
        bgmRadioButton.setFont(new Font("Rockwell", Font.ITALIC, 25));
        fxRadioButton.setFont(new Font("Rockwell", Font.ITALIC, 25));
        chesscomThemeButton.setFont(new Font("Rockwell", Font.ITALIC, 25));
        demoThemeRadioButton.setFont(new Font("Rockwell", Font.ITALIC, 25));
        disabledRadioButton.setFont(new Font("Rockwell", Font.ITALIC, 25));
        dragonTailRadioButton.setFont(new Font("Rockwell", Font.ITALIC, 25));
        sneakAttackRadioButton.setFont(new Font("Rockwell", Font.ITALIC, 25));
        menuButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        //加入背景图片
        settingsBackground.setSize(550,760);
        settingsBackground.setIcon(new ImageIcon("images/SettingsBackground.png"));
        settingsBackground.setLocation(0,0);
        //调整所有的Label
        musicLabel.setLocation(70,50);
        musicLabel.setSize(200,100);
        themeLabel.setLocation(70,200);
        themeLabel.setSize(200,100);
        animationLabel.setLocation(70,350);
        animationLabel.setSize(200,100);
        //调整所有的radioButton
        bgmRadioButton.setLocation(80,125);
        bgmRadioButton.setSize(250,50);
        fxRadioButton.setLocation(80,175);
        fxRadioButton.setSize(250,50);
        chesscomThemeButton.setLocation(80,250+25);
        chesscomThemeButton.setSize(250,50);
        demoThemeRadioButton.setLocation(80,300+25);
        demoThemeRadioButton.setSize(250,50);
        disabledRadioButton.setLocation(80,400+25);
        disabledRadioButton.setSize(250,50);
        dragonTailRadioButton.setLocation(80,450+25);
        dragonTailRadioButton.setSize(250,50);
        sneakAttackRadioButton.setLocation(80,500+25);
        sneakAttackRadioButton.setSize(250,50);
        //设置按钮位置
        menuButton.setLocation(300,600);
        menuButton.setSize(200,40);
    }

}

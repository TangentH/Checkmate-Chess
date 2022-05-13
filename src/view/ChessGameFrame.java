package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGTH;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;//TODO:这个对象是用于导入外部文件的
    private Chessboard chessboard;
    private JLabel colorLabel;

    public ChessGameFrame(int width, int height) {
        this.setResizable(false);
        setTitle("Checkmate"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;
        this.CHESSBOARD_SIZE = HEIGTH * 4 / 5;//通过窗体的大小来设置棋盘大小  //TODO:自由变换大小的棋盘可能不能使用final字段

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); // Center the window.
        setIconImages(null);//这个应该可以用来改变窗体icon
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

//以下是需要显示的窗体组件
        addLabel();
        addChessboard();
        addHelloButton();
        addLoadButton();
        addRestartButton();
        addBackButton();
    }


    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE, colorLabel);       //TODO
        gameController = new GameController(chessboard);                    //TODO:chessboard基本集成了最重要的功能
        chessboard.setLocation(HEIGTH / 10, HEIGTH / 10);
        add(chessboard);//这句话一定要加，相当于把棋盘挂载到窗口上
    }

    /**
     * 在游戏面板中添加标签
     */
    private void addLabel() {
        JLabel statusLabel = new JLabel("Current Player");
        colorLabel = new JLabel("WHITE");
        statusLabel.setLocation(HEIGTH - 30, 70);//通过窗体的高度计算出来的位置
        statusLabel.setSize(500, 80);//文本框的大小
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 30));//宋体楷体都能用
        colorLabel.setLocation(HEIGTH + 25, 100);//通过窗体的高度计算出来的位置
        colorLabel.setSize(500, 80);//文本框的大小
        colorLabel.setFont(new Font("Rockwell", Font.BOLD, 30));//宋体楷体都能用
        add(statusLabel);//把label添加到调用对象中
        add(colorLabel);
    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addHelloButton() {
        JButton button = new JButton("Show Hello Here");
        button.addActionListener((e) -> {
            System.out.println("Button clicked");
            JOptionPane.showMessageDialog(this, "Hello, world!");
            System.out.println("Hello");
        });  //TODO
        //nambda表达式，点击这个button后要执行的效果
        button.setLocation(HEIGTH, HEIGTH / 10 + 120);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");   //TODO
        button.setLocation(HEIGTH, HEIGTH / 10 + 240);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> { //TODO：了解actionListener如何使用
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this, "Input Path here");
            gameController.loadGameFromFile(path);
        });
    }

    private void addRestartButton() {
        JButton button = new JButton("Restart");
        button.setLocation(HEIGTH, HEIGTH / 10 + 360);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> { //TODO：了解actionListener如何使用
            System.out.println("Click Restart");
            remove(chessboard);
            addChessboard();
            this.repaint();
            colorLabel.setText("WHITE");
            JOptionPane.showMessageDialog(this, "Restart Successfully!");
        });
    }

    private void addBackButton() {
        JButton button = new JButton("Back");
        button.setLocation(HEIGTH, HEIGTH / 10 + 480);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            this.setVisible(false);
            Main.welcomeFrame.setVisible(true);
        });
    }

}

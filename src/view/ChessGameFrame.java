package view;

import controller.GameController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGTH;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;//TODO:这个对象是用于导入外部文件的
    public static Chessboard chessboard;
    private JLabel colorLabel;
    public JButton wRook, wQueen, wBishop, wKnight, bRook, bQueen, bBishop, bKnight;//用于兵底线升变的按钮
    private JLabel background;

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
        Chessboard.chessGameFrame = this;//给chessboard做一个备份，方便后续添加升变按钮时找到对应的chessGameFrame(在swapChessComponent中要用到)
        ChessComponent.chessGameFrame = this;//同理，给chessComponent做一个备份，因为swapLocation方法体在ChessComponent中
//以下是需要显示的窗体组件
        addLabel();
        addChessboard();
//        addHelloButton();
        addLoadButton();
        addRestartButton();
        addBackButton();
        addSaveButton();
        addUndoButton();
        addTodoButton();
        addBackgroundPic();
    }


    private void addBackgroundPic() {
        background = new JLabel();
        background.setSize(1050, 794);
        background.setIcon(new ImageIcon("images/Background2.png"));
        background.setLocation(-10, -10);
        add(background);
    }

    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        chessboard = new Chessboard(608, 608, colorLabel);       //TODO
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
        statusLabel.setLocation(HEIGTH - 30 - 20, 70);//通过窗体的高度计算出来的位置
        statusLabel.setSize(500, 80);//文本框的大小
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 30));//宋体楷体都能用
        colorLabel.setLocation(HEIGTH + 5, 100);//通过窗体的高度计算出来的位置
        colorLabel.setSize(500, 80);//文本框的大小
        colorLabel.setFont(new Font("Rockwell", Font.BOLD, 30));//宋体楷体都能用
        add(statusLabel);//把label添加到调用对象中
        add(colorLabel);
    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会显示Hello, world!
     */

//    private void addHelloButton() {
//        JButton button = new JButton("Show Hello Here");
//        button.addActionListener((e) -> {
//            System.out.println("Button clicked");
//            JOptionPane.showMessageDialog(this, "Hello, world!");
//            System.out.println("Hello");
//        });  //TODO
//        //nambda表达式，点击这个button后要执行的效果
//        button.setLocation(HEIGTH, HEIGTH / 10 + 120);
//        button.setSize(200, 60);
//        button.setFont(new Font("Rockwell", Font.BOLD, 20));
//        add(button);
//    }
    private void addLoadButton() {
        JButton button = new JButton("Load");   //TODO
        button.setLocation(750, 530);
        button.setSize(120, 50);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.setBackground(new Color(149, 195, 230, 255));

        button.addActionListener(e -> { //TODO：了解actionListener如何使用
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this, "Input Path here");
            gameController.loadGameFromFile(path);
        });
    }

    private void addRestartButton() {
        JButton button = new JButton("+");
        button.setLocation(800 + 50, 500 + 100);
        button.setSize(47, 47);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.setBackground(new Color(149, 195, 230, 255));


        button.addActionListener(e -> { //TODO：了解actionListener如何使用
            int result = JOptionPane.showConfirmDialog(this, "Confirm to restart?");
            if (result == 0) {//点击了确认按钮
                System.out.println("Click Restart");
                remove(chessboard);
                remove(background);//背景图片也需要重新加载
                addChessboard();
                addBackgroundPic();
                this.repaint();
                colorLabel.setText("WHITE");
                //如果在升变过程中遇到了restart，要将升变按钮删除
                if (wRook != null) {
                    removeWhitePromotionButtons();
                }
                if (bRook != null) {
                    removeBlackPromotionButtons();
                }
                JOptionPane.showMessageDialog(this, "Restart Successfully!");
            }
        });
    }

    private void addBackButton() {
        JButton button = new JButton("Menu");
        button.setLocation(800 + 100, 500 + 100);
        button.setSize(100, 47);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.setBackground(new Color(149, 195, 230, 255));

        button.addActionListener(e -> {
            this.setVisible(false);
            Main.welcomeFrame.setVisible(true);
        });
    }

    private void addSaveButton() {
        JButton button = new JButton("Save");
        button.setLocation(750 + 125 + 5, 530);
        button.setSize(120, 50);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.setBackground(new Color(149, 195, 230, 255));

        button.addActionListener(e -> {
            System.out.println("Click save");
            String path = JOptionPane.showInputDialog(this, "Input Path here");
            gameController.saveGameFromFile(path);
        });
    }

    //存档
    public static void saveGame(String fileName) throws IOException {
        if (fileName.equals("")) {  //如果输入的文件名为空，默认存入Game.txt文件
            fileName = "Game";
        }

        File file = new File("resource\\" + fileName + ".txt");
        if (!file.exists()) {  //如果文件不存在，新建一个文件
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream("resource\\" + fileName + ".txt");  //创建文件输出流对象

        for (int i = 0; i < chessboard.step.size(); i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    fos.write(chessboard.step.get(i).get(j).charAt(k));  //将棋盘转为字符写入文件
                }
                fos.write("\n".getBytes(StandardCharsets.UTF_8));  //换行符
            }
            fos.write(chessboard.step.get(i).get(8).getBytes(StandardCharsets.UTF_8));
            fos.write("\n".getBytes(StandardCharsets.UTF_8));  //换行符
        }

        fos.close();  //释放资源
    }
//用于悔棋过程中更改colorLabel的字段
    private void swapColorLabel(){
        if(colorLabel.getText().equals("WHITE")){
            colorLabel.setText("BLACK");
        }else{
            colorLabel.setText("WHITE");
        }
    }
    // 反悔棋按钮
    private void addUndoButton() {
        JButton button = new JButton("<");
        button.setLocation(800 - 50, 500 + 100);
        button.setSize(47, 47);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.setBackground(new Color(149, 195, 230, 255));

        button.addActionListener(e -> {
            System.out.println("Click Undo");

            if (chessboard.step2.size() > 1) {
                chessboard.step2.remove(chessboard.step2.size() - 1);
                chessboard.loadGame2(chessboard.step.get(chessboard.step2.size() - 1));
                swapColorLabel();
            }


        });
    }

    // 正悔棋按钮
    private void addTodoButton() {
        JButton button = new JButton(">");
        button.setLocation(800, 500 + 100);
        button.setSize(47, 47);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.setBackground(new Color(149, 195, 230, 255));

        button.addActionListener(e -> {
            System.out.println("Click Todo");

            if (chessboard.step2.size() < chessboard.step.size()) {
                chessboard.step2.add(chessboard.step.get(chessboard.step2.size()));
                chessboard.loadGame2(chessboard.step.get(chessboard.step2.size() - 1));
                swapColorLabel();
            }


        });
    }


    //TODO：添加白兵升变时候显示的按钮
    public void addWhitePromotionButtons() {
        wRook = new JButton();
        wQueen = new JButton();
        wKnight = new JButton();
        wBishop = new JButton();
        wRook.setSize(76, 76);
        wRook.setLocation(82, 5);
        wRook.setBackground(Color.orange);
        wRook.setIcon(new ImageIcon(RookChessComponent.getRookWhite().getScaledInstance(wRook.getWidth(), wRook.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        wRook.addActionListener(e -> {
            System.out.println("Click wRook");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[0][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[0][i];
                    chessboard.remove(pawn);
                    chessComponents[0][i] = new RookChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'r');
                    chessboard.putChessOnBoard(chessComponents[0][i]);
                    removeWhitePromotionButtons();
                    this.repaint();
                }
            }
        });
        add(wRook);
        wQueen.setSize(76, 76);
        wQueen.setLocation(82 + 76 * 2, 5);
        wQueen.setBackground(Color.orange);
        wQueen.setIcon(new ImageIcon(QueenChessComponent.getQueenWhite().getScaledInstance(wQueen.getWidth(), wQueen.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        wQueen.addActionListener(e -> {
            System.out.println("Click wQueen");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[0][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[0][i];
                    chessboard.remove(pawn);
                    chessComponents[0][i] = new QueenChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'q');
                    chessboard.putChessOnBoard(chessComponents[0][i]);
                    removeWhitePromotionButtons();
                    this.repaint();
                }
            }
        });
        add(wQueen);
        wKnight.setSize(76, 76);
        wKnight.setLocation(82 + 76 * 4, 5);
        wKnight.setBackground(Color.orange);
        wKnight.setIcon(new ImageIcon(KnightChessComponent.getKnightWhite().getScaledInstance(wKnight.getWidth(), wKnight.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        wKnight.addActionListener(e -> {
            System.out.println("Click wKnight");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[0][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[0][i];
                    chessboard.remove(pawn);
                    chessComponents[0][i] = new KnightChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'n');
                    chessboard.putChessOnBoard(chessComponents[0][i]);
                    removeWhitePromotionButtons();
                    this.repaint();
                }
            }
        });
        add(wKnight);
        wBishop.setSize(76, 76);
        wBishop.setLocation(82 + 76 * 6, 5);
        wBishop.setBackground(Color.orange);
        wBishop.setIcon(new ImageIcon(BishopChessComponent.getBishopWhite().getScaledInstance(wBishop.getWidth(), wBishop.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        wBishop.addActionListener(e -> {
            System.out.println("Click wBishop");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[0][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[0][i];
                    chessboard.remove(pawn);
                    chessComponents[0][i] = new BishopChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'b');
                    chessboard.putChessOnBoard(chessComponents[0][i]);
                    removeWhitePromotionButtons();
                    this.repaint();
                }
            }
        });
        add(wBishop);
        remove(background);
        addBackgroundPic();
    }

    //TODO：添加黑兵升变时显示的按钮
    public void addBlackPromotionButtons() {
        bRook = new JButton();
        bQueen = new JButton();
        bKnight = new JButton();
        bBishop = new JButton();
        bRook.setSize(76, 76);
        bRook.setLocation(82, 5 + 76 * 9 + 3);
        bRook.setBackground(Color.orange);
        bRook.setIcon(new ImageIcon(RookChessComponent.getRookBlack().getScaledInstance(bRook.getWidth(), bRook.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        bRook.addActionListener(e -> {
            System.out.println("Click bRook");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[7][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[7][i];
                    chessboard.remove(pawn);
                    chessComponents[7][i] = new RookChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'R');
                    chessboard.putChessOnBoard(chessComponents[7][i]);
                    removeBlackPromotionButtons();
                    this.repaint();
                }
            }
        });
        add(bRook);
        bQueen.setSize(76, 76);
        bQueen.setLocation(82 + 76 * 2, 5 + 76 * 9 + 3);
        bQueen.setBackground(Color.orange);
        bQueen.setIcon(new ImageIcon(QueenChessComponent.getQueenBlack().getScaledInstance(bQueen.getWidth(), bQueen.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        bQueen.addActionListener(e -> {
            System.out.println("Click bQueen");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[7][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[7][i];
                    chessboard.remove(pawn);
                    chessComponents[7][i] = new QueenChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'Q');
                    chessboard.putChessOnBoard(chessComponents[7][i]);
                    removeBlackPromotionButtons();
                    this.repaint();
                }
            }
        });
        add(bQueen);
        bKnight.setSize(76, 76);
        bKnight.setLocation(82 + 76 * 4, 5 + 76 * 9 + 3);
        bKnight.setBackground(Color.orange);
        bKnight.setIcon(new ImageIcon(KnightChessComponent.getKnightBlack().getScaledInstance(bKnight.getWidth(), bKnight.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        bKnight.addActionListener(e -> {
            System.out.println("Click bKnight");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[7][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[7][i];
                    chessboard.remove(pawn);
                    chessComponents[7][i] = new KnightChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'N');
                    chessboard.putChessOnBoard(chessComponents[7][i]);
                    removeBlackPromotionButtons();
                    this.repaint();
                }
            }
        });
        add(bKnight);
        bBishop.setSize(76, 76);
        bBishop.setLocation(82 + 76 * 6, 5 + 76 * 9 + 3);
        bBishop.setBackground(Color.orange);
        bBishop.setIcon(new ImageIcon(BishopChessComponent.getBishopBlack().getScaledInstance(bBishop.getWidth(), bBishop.getHeight(), Image.SCALE_AREA_AVERAGING)));
        //获取具体ChessComponent的图像，并在缩放后设置为icon
        bBishop.addActionListener(e -> {
            System.out.println("Click bBishop");
            for (int i = 0; i < 8; i++) {
                ChessComponent[][] chessComponents = chessboard.getChessComponents();
                if (chessComponents[7][i] instanceof PawnChessComponent) {
                    PawnChessComponent pawn = (PawnChessComponent) chessComponents[7][i];
                    chessboard.remove(pawn);
                    chessComponents[7][i] = new BishopChessComponent(pawn.getChessboardPoint(), pawn.getLocation(), pawn.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), 'B');
                    chessboard.putChessOnBoard(chessComponents[7][i]);
                    removeBlackPromotionButtons();
                    this.repaint();
                }
            }
        });
        add(bBishop);
        remove(background);
        addBackgroundPic();
    }

    public void removeWhitePromotionButtons() {//清除白子的升变按钮
        remove(wRook);
        remove(wQueen);
        remove(wBishop);
        remove(wKnight);
        wRook = null;
        wQueen = null;
        wBishop = null;
        wKnight = null;
        //TODO:同时要设置成空指针，便于检查此时有没有一方的兵在升变
    }

    public void removeBlackPromotionButtons() {
        remove(bRook);
        remove(bQueen);
        remove(bBishop);
        remove(bKnight);
        bRook = null;
        bQueen = null;
        bBishop = null;
        bKnight = null;
    }


}

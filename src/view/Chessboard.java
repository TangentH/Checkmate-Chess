package view;


import model.*;
import controller.ClickController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static model.ChessComponent.record;

/**
 * 这个类表示面板上的棋盘组件对象
 */
public class Chessboard extends JComponent {
    /**
     * CHESSBOARD_SIZE： 棋盘是8 * 8的
     * <br>
     * BACKGROUND_COLORS: 棋盘的两种背景颜色
     * <br>
     * chessListener：棋盘监听棋子的行动
     * <br>
     * chessboard: 表示8 * 8的棋盘
     * <br>
     * currentColor: 当前行棋方
     */
    private static final int CHESSBOARD_SIZE = 8;

    private final ChessComponent[][] chessComponents = new ChessComponent[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    //用一个二维数组存放chessComponents对象,final：指针不能改变，但是里面指向的对象可以改变属性和内容,前一个是坐标row,后一个坐标是col
    private ChessColor currentColor = ChessColor.WHITE;//启动时的行棋方为白子
    //all chessComponents in this chessboard are shared only one model controller
    private final ClickController clickController = new ClickController(this);  //TODO
    private final int CHESS_SIZE;
    private JLabel colorLabel;//用于显示当前行棋方的label


    public Chessboard(int width, int height, JLabel colorLabel) {
        this.colorLabel = colorLabel;
        setLayout(null); // Use absolute layout.    //TODO：layout的作用
        setSize(width, height);//棋盘的大小
        CHESS_SIZE = width / 8;//每个棋盘格的大小   //TODO：改变窗体大小需要连带改动棋盘格大小
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);//打印出来可以用于debug

        initiateEmptyChessboard();

        // FIXME: Initialize chessboard for testing only.
        initRookOnBoard(0, 0, ChessColor.BLACK);//rook：车，将棋子放在棋盘上
        initRookOnBoard(0, CHESSBOARD_SIZE - 1, ChessColor.BLACK);
        initRookOnBoard(CHESSBOARD_SIZE - 1, 0, ChessColor.WHITE);
        initRookOnBoard(7, 7, ChessColor.WHITE);//简化：直接写坐标即可
        initKingOnBoard(7, 4, ChessColor.WHITE);
        initKingOnBoard(0, 4, ChessColor.BLACK);
        initQueenOnBoard(0, 3, ChessColor.BLACK);
        initQueenOnBoard(7, 3, ChessColor.WHITE);
        initBishopOnBoard(0, 2, ChessColor.BLACK);
        initBishopOnBoard(0, 5, ChessColor.BLACK);
        initBishopOnBoard(7, 2, ChessColor.WHITE);
        initBishopOnBoard(7, 5, ChessColor.WHITE);
        initKnightOnBoard(0, 1, ChessColor.BLACK);
        initKnightOnBoard(0, 6, ChessColor.BLACK);
        initKnightOnBoard(7, 1, ChessColor.WHITE);
        initKnightOnBoard(7, 6, ChessColor.WHITE);
        initPawnOnBoard(1, 0, ChessColor.BLACK);
        initPawnOnBoard(1, 1, ChessColor.BLACK);
        initPawnOnBoard(1, 2, ChessColor.BLACK);
        initPawnOnBoard(1, 3, ChessColor.BLACK);
        initPawnOnBoard(1, 4, ChessColor.BLACK);
        initPawnOnBoard(1, 5, ChessColor.BLACK);
        initPawnOnBoard(1, 6, ChessColor.BLACK);
        initPawnOnBoard(1, 7, ChessColor.BLACK);
        initPawnOnBoard(6, 0, ChessColor.WHITE);
        initPawnOnBoard(6, 1, ChessColor.WHITE);
        initPawnOnBoard(6, 2, ChessColor.WHITE);
        initPawnOnBoard(6, 3, ChessColor.WHITE);
        initPawnOnBoard(6, 4, ChessColor.WHITE);
        initPawnOnBoard(6, 5, ChessColor.WHITE);
        initPawnOnBoard(6, 6, ChessColor.WHITE);
        initPawnOnBoard(6, 7, ChessColor.WHITE);
    }

    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void putChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();

        if (chessComponents[row][col] != null) {
            remove(chessComponents[row][col]);
        }
        add(chessComponents[row][col] = chessComponent);
    }

    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (chess1 instanceof PawnChessComponent){
            record[0] = new PawnChessComponent(chess1.getChessboardPoint(), chess1.getLocation(), chess1.getChessColor(), clickController, CHESS_SIZE);  //将上一步棋存入record
            record[1] = new PawnChessComponent(chess2.getChessboardPoint(), chess2.getLocation(), chess2.getChessColor(), clickController, CHESS_SIZE);
        }

        if (chess1 instanceof RookChessComponent && !(chess2 instanceof KingChessComponent)) {//如果先选中rook，再选择一个非king的棋子，则判定该rook已经移动过，不能进行王车易位
            ((RookChessComponent) chess1).setRookCanCastle(false);
        }
        if (chess1 instanceof KingChessComponent && !(chess2 instanceof RookChessComponent)) {
            ((KingChessComponent) chess1).setKingCanCastle(false);
        }
        if (!(chess2 instanceof EmptySlotComponent)) {  //吃子操作
            remove(chess2);     //直接从所有组件中移除
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, CHESS_SIZE));   //chess2指向了空棋子这个对象
        }
        if (chess1 instanceof PawnChessComponent && chess1.getChessboardPoint().getY() != chess2.getChessboardPoint().getY() && chessComponents[chess1.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()] instanceof PawnChessComponent && chessComponents[chess1.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()].getChessColor() != chess1.getChessColor()){  //如果是吃过路兵
            //把被吃的兵移除并更换为空棋子
            remove(chessComponents[chess1.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()]);
            add(chessComponents[chess1.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()] = new EmptySlotComponent(chessComponents[chess1.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()].getChessboardPoint(), chessComponents[chess1.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()].getLocation(), clickController, CHESS_SIZE));
            chessComponents[chess1.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()].repaint();
        }
        chess1.swapLocation(chess2);//如果目标位置是对方棋子，则上面操作将对方棋子先更换为空白棋子，然后swap；如果不满足以上if条件，则可以直接swap
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;
//swapLocation仅仅只是在chessComponent的字段上更改了两个棋子的位置，但是在棋盘(chessComponent中，两者位置还没有调换和repaint)
        chess1.repaint();
        chess2.repaint();
    }

    public void castle(KingChessComponent king, RookChessComponent rook) {
        if (rook.getChessboardPoint().getY() == 0) {//长易位，对于黑白子皆可用
            king.swapLocation(chessComponents[king.getChessboardPoint().getX()][2]);
            ChessComponent empty1 = chessComponents[king.getChessboardPoint().getX()][2];
            chessComponents[king.getChessboardPoint().getX()][2] = king;//需要在二维数组中也调换王车易位中变换过的棋子
            chessComponents[king.getChessboardPoint().getX()][4] = empty1;
            rook.swapLocation(chessComponents[king.getChessboardPoint().getX()][3]);
            ChessComponent empty2 = chessComponents[king.getChessboardPoint().getX()][3];
            chessComponents[king.getChessboardPoint().getX()][3] = rook;
            chessComponents[king.getChessboardPoint().getX()][0] = empty2;
            king.repaint();
            rook.repaint();
            chessComponents[king.getChessboardPoint().getX()][0].repaint();
            chessComponents[king.getChessboardPoint().getX()][4].repaint();
            king.setKingCanCastle(false);
        } else {
            king.swapLocation(chessComponents[king.getChessboardPoint().getX()][6]);
            ChessComponent empty1 = chessComponents[king.getChessboardPoint().getX()][6];
            chessComponents[king.getChessboardPoint().getX()][6] = king;
            chessComponents[king.getChessboardPoint().getX()][4] = empty1;
            rook.swapLocation(chessComponents[king.getChessboardPoint().getX()][5]);
            ChessComponent empty2 = chessComponents[king.getChessboardPoint().getX()][5];
            chessComponents[king.getChessboardPoint().getX()][5] = rook;
            chessComponents[king.getChessboardPoint().getX()][7] = empty2;
            king.repaint();
            rook.repaint();
            chessComponents[king.getChessboardPoint().getX()][7].repaint();
            chessComponents[king.getChessboardPoint().getX()][4].repaint();
            king.setKingCanCastle(false);
        }
    }

    //TODO
    public void initiateEmptyChessboard() {
        for (int i = 0; i < chessComponents.length; i++) {
            for (int j = 0; j < chessComponents[i].length; j++) {
                putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController, CHESS_SIZE));
            }
        }
    }

    //更换行棋方的方法
    public void swapColor() {
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
        if (currentColor == ChessColor.WHITE) {
            colorLabel.setText("WHITE");
        } else {
            colorLabel.setText("BLACK");
        }
    }

    private void initRookOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new RookChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initKingOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KingChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initQueenOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new QueenChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initBishopOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new BishopChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initKnightOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KnightChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initPawnOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new PawnChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    public void loadGame(List<String> chessData) {
        chessData.forEach(System.out::println);
    }
}

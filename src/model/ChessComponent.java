package model;

import view.ChessGameFrame;
import view.Chessboard;
import view.ChessboardPoint;
import controller.ClickController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 这个类是一个抽象类，主要表示8*8棋盘上每个格子的棋子情况，当前有两个子类继承它，分别是EmptySlotComponent(空棋子)和RookChessComponent(车)。
 */
public abstract class ChessComponent extends JComponent {

    /**
     * CHESSGRID_SIZE: 主要用于确定每个棋子在页面中显示的大小。
     * <br>
     * 在这个设计中，每个棋子的图案是用图片画出来的（由于国际象棋那个棋子手动画太难了）
     * <br>
     * 因此每个棋子占用的形状是一个正方形，大小是50*50//TODO:重绘棋子时需要留意棋子大小
     */

//    private static final Dimension CHESSGRID_SIZE = new Dimension(1080 / 4 * 3 / 8, 1080 / 4 * 3 / 8);    //TODO:这段代码的意思是？
    private static final Color[] BACKGROUND_COLORS = {new Color(235, 236, 208, 255), new Color(119, 149, 86, 255)};
    //应该指的是chessComponent有两种颜色选择,最后一个值应该是透明度
    protected static Color mouseOn = new Color(186, 202, 43, 255);
    protected static Color clicked = new Color(246, 246, 105, 255);
    private boolean isMouseOn = false;
    private boolean canBeCaptured = false;
    //在显示合法落子点的时候用于检测该棋子能否被captured，空棋子和具体棋子的应对方式不同
    /**
     * handle click event
     */
    private ClickController clickController;

    /**
     * chessboardPoint: 表示8*8棋盘中，当前棋子在棋格对应的位置，如(0, 0), (1, 0), (0, 7),(7, 7)等等
     * <br>
     * chessColor: 表示这个棋子的颜色，有白色，黑色，无色三种
     * <br>
     * selected: 表示这个棋子是否被选中
     */
    private ChessboardPoint chessboardPoint;//用于记录某个chessComponent所在的位置
    protected final ChessColor chessColor;
    private boolean selected;
    public static ChessComponent[] record = new ChessComponent[2];  //对走过的兵进行记录
    //以下两个字段是为了方便swapLocation方法中，进行升变按钮的显示
    public static ChessGameFrame chessGameFrame;
    public static ChessComponent[][] chessComponents;
    private static Chessboard chessboard;
    protected Color squareColor;//记录这个棋盘格下面的颜色
    protected boolean isMoving;

    public ChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);    //TODO:这段代码的意思
        setLocation(location);
        setSize(size, size);
        this.chessboardPoint = chessboardPoint;
        this.chessColor = chessColor;
        this.selected = false;
        this.clickController = clickController;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public boolean getCanBeCaptured() {
        return canBeCaptured;
    }

    public void setCanBeCaptured(boolean canBeCaptured) {
        this.canBeCaptured = canBeCaptured;
    }

    //设置和获取chessboard只是为了获得当前行棋方，方便鼠标移动到棋盘格上，只有当前行棋方的棋盘格会变色
    public static Chessboard getChessboard() {
        return chessboard;
    }

    public static void setChessboard(Chessboard chessboard) {
        ChessComponent.chessboard = chessboard;
    }

    public ChessboardPoint getChessboardPoint() {
        return chessboardPoint;
    }

    public void setChessboardPoint(ChessboardPoint chessboardPoint) {
        this.chessboardPoint = chessboardPoint;
    }

    public ChessColor getChessColor() {
        return chessColor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @param another 主要用于和另外一个棋子交换位置
     *                <br>
     *                调用时机是在移动棋子的时候，将操控的棋子和对应的空位置棋子(EmptySlotComponent)做交换
     */
    //注意：此处只完成交换操作，不完成检查操作，而且只能交换空白棋子
    public void swapLocation(ChessComponent another) {
        ChessboardPoint chessboardPoint1 = getChessboardPoint(), chessboardPoint2 = another.getChessboardPoint();
        Point point1 = getLocation(), point2 = another.getLocation();//TODO: chessboardPoint1和point1的区别？
        setChessboardPoint(chessboardPoint2);
        setLocation(point2);
        another.setChessboardPoint(chessboardPoint1);
        another.setLocation(point1);
        //TODO：兵底线升变，当只没有吃子的时候
        for (int wPawnLocation = 0; wPawnLocation < 8; wPawnLocation++) {
            if (chessComponents[0][wPawnLocation] instanceof PawnChessComponent) {//白子兵底线检查
                chessGameFrame.addWhitePromotionButtons();
                chessGameFrame.repaint();
            }
        }
        for (int bPawnLocation = 0; bPawnLocation < 8; bPawnLocation++) {
            if (chessComponents[7][bPawnLocation] instanceof PawnChessComponent) {//黑子兵底线检查
                chessGameFrame.addBlackPromotionButtons();
                chessGameFrame.repaint();
            }
        }
    }

    /**
     * @param e 响应鼠标监听事件
     *          <br>
     *          当接收到鼠标动作的时候，这个方法就会自动被调用，调用所有监听者的onClick方法，处理棋子的选中，移动等等行为。
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
//TODO:鼠标划过棋盘格的方法在这里写
        if (e.getID() == MouseEvent.MOUSE_PRESSED && chessGameFrame.wRook == null && chessGameFrame.bRook == null) {
            //TODO:只有不存在升变按钮时，才可以正常下棋，只检查双方的其中一个升变按钮
            System.out.printf("Click [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
            try {
                clickController.onClick(this);//把自己传入clickController
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (e.getID() == MouseEvent.MOUSE_ENTERED && chessGameFrame.wRook == null && chessGameFrame.bRook == null) {
            //鼠标移入
            System.out.printf("Mouse On [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
            if (!(this instanceof EmptySlotComponent) && getChessboard().getCurrentColor() == this.getChessColor()) {
                //只有当前行棋方的棋盘格可以变色
                isMouseOn = true;
                this.repaint();
            }
        }
        if (e.getID() == MouseEvent.MOUSE_EXITED && chessGameFrame.wRook == null && chessGameFrame.bRook == null) {
            //鼠标移出
            System.out.printf("Mouse Off [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
            isMouseOn = false;
            this.repaint();
        }
    }

    /**
     * @param chessboard  棋盘
     * @param destination 目标位置，如(0, 0), (0, 7)等等
     * @return this棋子对象的移动规则和当前位置(chessboardPoint)能否到达目标位置
     * <br>
     * 这个方法主要是检查移动的合法性，如果合法就返回true，反之是false
     */
    public abstract boolean canMoveTo(ChessComponent[][] chessboard, ChessboardPoint destination);

    /**
     * 这个方法主要用于加载一些特定资源，如棋子图片等等。
     *
     * @throws IOException 如果一些资源找不到(如棋子图片路径错误)，就会抛出异常
     */
    public abstract void loadResource() throws IOException;

    @Override
    protected void paintComponent(Graphics g) {//TODO:此处包含了黑白棋盘格的绘制,每次调用repaint()方法时都会自动调用这个重写的方法，这个方法本身不需要自己来调用
        super.paintComponents(g);
        System.out.printf("repaint chess [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
        if (isMouseOn) {
            //当鼠标移动到棋子上时
            squareColor = mouseOn;
        } else {
            squareColor = BACKGROUND_COLORS[(chessboardPoint.getX() + chessboardPoint.getY()) % 2];
        }
        //通过计算当前位置，确定棋盘格的颜色
        g.setColor(squareColor);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}

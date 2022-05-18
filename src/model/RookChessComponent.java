package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示国际象棋里面的车
 */
public class RookChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image ROOK_WHITE;
    private static Image ROOK_BLACK;

    /**
     * 车棋子对象自身的图片，是上面两种中的一种
     */
    private Image rookImage;
    private boolean rookCanCastle = true;//用于检查rook是否移动过，从而判断能否castle

    public static Image getRookWhite() {//用于获取pawn升变时生成按钮的图像
        return ROOK_WHITE;
    }

    public static Image getRookBlack() {//用于获取pawn升变时生成按钮的图像
        return ROOK_BLACK;
    }

    /**
     * 读取加载车棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (ROOK_WHITE == null) {
            ROOK_WHITE = ImageIO.read(new File("./images/Chess.comTheme/rook-white1.png"));//读取车的贴图信息
        }

        if (ROOK_BLACK == null) {
            ROOK_BLACK = ImageIO.read(new File("./images/Chess.comTheme/rook-black1.png"));
        }
    }

    public boolean isRookCanCastle() {
        return rookCanCastle;
    }

    public void setRookCanCastle(boolean rookCanCastle) {
        this.rookCanCastle = rookCanCastle;
    }

    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                rookImage = ROOK_WHITE;
            } else if (color == ChessColor.BLACK) {
                rookImage = ROOK_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RookChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size, char name) {
        super(chessboardPoint, location, color, listener, size, name);
        initiateRookImage(color);
    }

    /**
     * 车棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 车棋子移动的合法性
     */

    @Override
    //以下记录了rook的行棋规则
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();  //用于记录该棋子当前位置
        //先判断是不是横向或者纵向行走
        if (source.getX() == destination.getX()) {
            int row = source.getX();
            for (int col = Math.min(source.getY(), destination.getY()) + 1; col < Math.max(source.getY(), destination.getY()); col++) {
                //从当前行和列，沿着列的方向从左向右遍历棋盘格，检查沿途是不是空棋子
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else if (source.getY() == destination.getY()) {
            int col = source.getY();
            for (int row = Math.min(source.getX(), destination.getX()) + 1; row < Math.max(source.getX(), destination.getX()); row++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else { // Not on the same row or the same column.
            return false;
        }
        return true;
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(clicked);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else if (getCanBeCaptured()) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawOval(0, 0, getWidth(), getHeight());
            g.drawOval(0, 0, getWidth(), getHeight());
            g.drawOval(1, 1, getWidth() - 1, getHeight() - 2);
            g.drawOval(1, 1, getWidth() - 2, getHeight() - 1);
            g.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
            g.drawOval(2, 2, getWidth() - 3, getHeight() - 4);
            g.drawOval(2, 2, getWidth() - 4, getHeight() - 3);
            g.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
            g.drawOval(3, 3, getWidth() - 5, getHeight() - 6);
            g.drawOval(3, 3, getWidth() - 6, getHeight() - 5);
            g.drawOval(3, 3, getWidth() - 6, getHeight() - 6);
        }
        g.drawImage(rookImage, 0, 0, getWidth(), getHeight(), this);
    }
}

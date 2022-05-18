package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class KnightChessComponent extends ChessComponent {
    /**
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image KNIGHT_WHITE;
    private static Image KNIGHT_BLACK;
    private Image knightImage;

    public static Image getKnightWhite() {
        return KNIGHT_WHITE;
    }

    public static Image getKnightBlack() {
        return KNIGHT_BLACK;
    }

    /**
     * 读取加载棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (KNIGHT_WHITE == null) {
            KNIGHT_WHITE = ImageIO.read(new File("./images/Chess.comTheme/knight-white1.png"));//读取车的贴图信息
        }

        if (KNIGHT_BLACK == null) {
            KNIGHT_BLACK = ImageIO.read(new File("./images/Chess.comTheme/knight-black1.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateKnightImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                knightImage = KNIGHT_WHITE;
            } else if (color == ChessColor.BLACK) {
                knightImage = KNIGHT_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KnightChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateKnightImage(color);
    }

    /**
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 棋子移动的合法性
     */

    @Override
    //以下记录了knight的行棋规则
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();  //用于记录该棋子当前位置
        if (Math.abs(destination.getY() - source.getY()) == 2) {
            if (Math.abs(destination.getX() - source.getX()) == 1) {
                return true;
            }
        } else if (Math.abs(destination.getX() - source.getX()) == 2) {
            if (Math.abs(destination.getY() - source.getY()) == 1) {
                return true;
            }
        }
        return false;
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
        g.drawImage(knightImage, 0, 0, getWidth(), getHeight(), this);
    }
}

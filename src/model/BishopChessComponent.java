package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class BishopChessComponent extends ChessComponent {
    /**
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image BISHOP_WHITE;
    private static Image BISHOP_BLACK;
    private Image bishopImage;

    /**
     * 读取加载棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (BISHOP_WHITE == null) {
            BISHOP_WHITE = ImageIO.read(new File("./images/Chess.comTheme/bishop-white1.png"));//读取车的贴图信息
        }

        if (BISHOP_BLACK == null) {
            BISHOP_BLACK = ImageIO.read(new File("./images/Chess.comTheme/bishop-black1.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateBishopImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                bishopImage = BISHOP_WHITE;
            } else if (color == ChessColor.BLACK) {
                bishopImage = BISHOP_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BishopChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateBishopImage(color);
    }

    /**
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 棋子移动的合法性
     */

    @Override
    //以下记录了bishop的行棋规则
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();  //用于记录该棋子当前位置
        if (source.getX() + source.getY() == destination.getX() + destination.getY()) {
            for (int row = Math.min(source.getX(), destination.getX()) + 1, col = Math.max(source.getY(), destination.getY()) - 1; row < Math.max(source.getX(), destination.getX()); row++, col--) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else if (source.getX() - source.getY() == destination.getX() - destination.getY()) {
            for (int row = Math.min(source.getX(), destination.getX()) + 1, col = Math.min(source.getY(), destination.getY()) + 1; row < Math.max(source.getX(), destination.getX()); row++, col++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        } else {
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
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(bishopImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.LIGHT_GRAY);
            g.drawOval(0, 0, getWidth(), getHeight());
            g.drawOval(0, 0, getWidth(), getHeight());
            g.drawOval(1, 1, getWidth()-1, getHeight()-2);
            g.drawOval(1, 1, getWidth()-2, getHeight()-1);
            g.drawOval(1, 1, getWidth()-2, getHeight()-2);
            g.drawOval(2, 2, getWidth()-3, getHeight()-4);
            g.drawOval(2, 2, getWidth()-4, getHeight()-3);
            g.drawOval(2, 2, getWidth()-4, getHeight()-4);
            g.drawOval(3, 3, getWidth()-5, getHeight()-6);
            g.drawOval(3, 3, getWidth()-6, getHeight()-5);
            g.drawOval(3, 3, getWidth()-6, getHeight()-6);
        }
    }
}

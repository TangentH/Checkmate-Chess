package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class KingChessComponent extends ChessComponent {
    /**
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image KING_WHITE;
    private static Image KING_BLACK;
    private Image kingImage;
    private boolean kingCanCastle = true;//用于检查King是否移动过,默认为没有移动过，可以易位

    public boolean isKingCanCastle() {
        return kingCanCastle;
    }

    public void setKingCanCastle(boolean kingCanCastle) {
        this.kingCanCastle = kingCanCastle;
    }

    /**
     * 读取加载车棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (KING_WHITE == null) {
            KING_WHITE = ImageIO.read(new File("./images/Chess.comTheme/king-white1.png"));//读取车的贴图信息
        }

        if (KING_BLACK == null) {
            KING_BLACK = ImageIO.read(new File("./images/Chess.comTheme/king-black1.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateKingImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                kingImage = KING_WHITE;
            } else if (color == ChessColor.BLACK) {
                kingImage = KING_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KingChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateKingImage(color);
    }

    /**
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 车棋子移动的合法性
     */

    @Override
    //以下记录了king的行棋规则
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();  //用于记录该棋子当前位置
        if (source.getX() == destination.getX() && Math.abs(source.getY() - destination.getY()) == 1) {
            return true;
        } else if (source.getY() == destination.getY() && Math.abs(source.getX() - destination.getX()) == 1) {
            return true;
        } else if (source.getX() + source.getY() == destination.getX() + destination.getY() && Math.abs(source.getX() - destination.getX()) == 1 || source.getX() - source.getY() == destination.getX() - destination.getY() && Math.abs(source.getX() - destination.getX()) == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//在绘制了棋盘格的基础上，再添加上王的图片，这里应该有图层的思想
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
        g.drawImage(kingImage, 0, 0, getWidth(), getHeight(), this);//这里将王的图片绘制到棋盘上
//        g.setColor(Color.BLACK);

    }
}

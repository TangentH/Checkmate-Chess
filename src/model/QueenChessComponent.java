package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class QueenChessComponent extends ChessComponent {
    /**
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image QUEEN_WHITE;
    private static Image QUEEN_BLACK;
    private Image queenImage;

    public static Image getQueenWhite() {
        return QUEEN_WHITE;
    }

    public static Image getQueenBlack() {
        return QUEEN_BLACK;
    }

    /**
     * 读取加载棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (theme == 0) {
            QUEEN_WHITE = ImageIO.read(new File("./images/Chess.comTheme/queen-white1.png"));//读取车的贴图信息
        }
        if (theme == 1) {
            QUEEN_WHITE = ImageIO.read(new File("./images/DemoTheme/queen-white.png"));
        }

        if (theme == 0) {
            QUEEN_BLACK = ImageIO.read(new File("./images/Chess.comTheme/queen-black1.png"));
        }
        if (theme == 1) {
            QUEEN_BLACK = ImageIO.read(new File("./images/DemoTheme/queen-black.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateQueenImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                queenImage = QUEEN_WHITE;
            } else if (color == ChessColor.BLACK) {
                queenImage = QUEEN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public QueenChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size, char name) {
        super(chessboardPoint, location, color, listener, size, name);
        initiateQueenImage(color);
    }

    /**
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 棋子移动的合法性
     */

    @Override
    //以下记录了queen的行棋规则
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();  //用于记录该棋子当前位置
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
        } else if (source.getX() + source.getY() == destination.getX() + destination.getY()) {
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
        if (isSelected()) { // Highlights the model if selected.
            super.paintComponent(g);
            g.setColor(clicked);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.drawImage(queenImage, 0, 0, getWidth(), getHeight(), this);
        } else if (getCanBeCaptured()) {
            super.paintComponent(g);
            g.setColor(new Color(0, 0, 0, 30));
            g.fillOval(0, 0, getWidth(), getHeight());
            g.setColor(squareColor);
            g.fillOval(5, 5, 66, 66);
            g.drawImage(queenImage, 0, 0, getWidth(), getHeight(), this);
        } else if (isMoving) {
            //如果棋子在移动，就不用绘制棋盘格
            g.drawImage(queenImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            super.paintComponent(g);
            g.drawImage(queenImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

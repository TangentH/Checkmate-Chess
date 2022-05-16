package model;

import view.Chessboard;
import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示国际象棋里面的兵
 */
public class PawnChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有兵对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image PAWN_WHITE;
    private static Image PAWN_BLACK;

    /**
     * 兵棋子对象自身的图片，是上面两种中的一种
     */
    private Image pawnImage;

    /**
     * 读取加载兵棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (PAWN_WHITE == null) {
            PAWN_WHITE = ImageIO.read(new File("./images/Chess.comTheme/pawn-white1.png"));
        }

        if (PAWN_BLACK == null) {
            PAWN_BLACK = ImageIO.read(new File("./images/Chess.comTheme/pawn-black1.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定pawnImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                pawnImage = PAWN_WHITE;
            } else if (color == ChessColor.BLACK) {
                pawnImage = PAWN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PawnChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
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
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();  //用于记录该棋子当前位置

        int col = source.getY();

        if (chessColor == ChessColor.WHITE) {//白兵在起始行上，可以往前走1或2格
            if (source.getY() == destination.getY() && !(chessComponents[source.getX() - 1][col] instanceof EmptySlotComponent)) {//兵不能吃前方的棋子
                return false;
            } else {
                if (source.getX() == 6) {
                    if (source.getY() == destination.getY() && (destination.getX() == 4 || destination.getX() == 5) && chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent) {
                        for (int i = Math.min(source.getX(), destination.getX()) + 1;
                             i < Math.max(source.getX(), destination.getX()); i++) {
                            if (!(chessComponents[i][col] instanceof EmptySlotComponent)) {
                                return false;
                            }
                        }
                        return true;
                    }
                } else {//白兵不在起始行上，只能往前走1格
                    if (source.getY() == destination.getY() && source.getX() - destination.getX() == 1) {
                        for (int i = Math.min(source.getX(), destination.getX()) + 1;
                             i < Math.max(source.getX(), destination.getX()); i++) {
                            if (!(chessComponents[i][col] instanceof EmptySlotComponent)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                //白兵的特殊吃法：如果兵的斜进一格内有对方棋子，就可以吃掉它而占据该格。;
                if (source.getX() - destination.getX() == 1 && Math.abs(source.getY() - destination.getY()) == 1) {
                    if (!(chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent)) {
                        return true;
                    }
                    //return enPassant吃过路兵；
                }
                return false;
            }
        }

        if (chessColor == ChessColor.BLACK) {//黑兵在起始行上，可以往前走1或2格
            if (source.getY() == destination.getY() && !(chessComponents[source.getX() + 1][col] instanceof EmptySlotComponent)) {//兵不能吃前方的棋子
                return false;
            } else {
                if (source.getX() == 1) {
                    if (source.getY() == destination.getY() && (destination.getX() == 2 || destination.getX() == 3) && chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent) {
                        for (int i = Math.min(source.getX(), destination.getX()) + 1;
                             i < Math.max(source.getX(), destination.getX()); i++) {
                            if (!(chessComponents[i][col] instanceof EmptySlotComponent)) {
                                return false;
                            }
                        }
                        return true;
                    }
                } else {
                    if (source.getY() == destination.getY() && source.getX() - destination.getX() == -1) {
                        for (int i = Math.min(source.getX(), destination.getX()) + 1;
                             i < Math.max(source.getX(), destination.getX()); i++) {
                            if (!(chessComponents[i][col] instanceof EmptySlotComponent)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                //黑兵的特殊吃法：如果兵的斜进一格内有对方棋子，就可以吃掉它而占据该格。;
                if (source.getX() - destination.getX() == -1 && Math.abs(source.getY() - destination.getY()) == 1) {
                    if (!(chessComponents[destination.getX()][destination.getY()] instanceof EmptySlotComponent)) {
                        return true;
                    }
                    //return enPassant吃过路兵；
                }
                return false;
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
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(pawnImage, 0, 0, getWidth(), getHeight(), this);
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
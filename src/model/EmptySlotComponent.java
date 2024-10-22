package model;

import view.ChessboardPoint;
import controller.ClickController;

import java.awt.*;
import java.io.IOException;

/**
 * 这个类表示棋盘上的空位置
 */
public class EmptySlotComponent extends ChessComponent {

    public EmptySlotComponent(ChessboardPoint chessboardPoint, Point location, ClickController listener, int size, char name) {
        super(chessboardPoint, location, ChessColor.NONE, listener, size, name);
    }

    @Override
    public boolean canMoveTo(ChessComponent[][] chessboard, ChessboardPoint destination) {
        return false;
    }

    @Override
    public void loadResource() throws IOException {
        //No resource!
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getCanBeCaptured()) {
            g.setColor(new Color(0,0,0,30));
            g.fillOval(38 - 11, 38 - 11, 22, 22);
        }
    }
}

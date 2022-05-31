package controller;

//所有chessComponent共用同一个clickController
//clickController隶属于chessboard,将chessboard的clickController传入每一个chessComponent

import model.*;
import view.*;

import java.awt.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static model.ChessComponent.*;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first;
    private static int animationMode = 2;
    //animation mode: 0:disabled; 1:Dragon tail; 2:Sneak attack

    public static int getAnimationMode() {
        return animationMode;
    }

    public static void setAnimationMode(int animationMode) {
        ClickController.animationMode = animationMode;
    }

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) throws InterruptedException {
        //棋盘格被点击后的反应
        if (first == null) {    //如果现在没有棋子被选取
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();//情况变化后，一定要repaint chessComponent
                checkValidMovements(chessComponent);
                paintValidMovements();
            }
        } else {
            if (first == chessComponent) {      // 再次点击取消选取
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
                //要把原来选取的位置repaint
                clearValidMovements();
                chessboard.repaint();
            } //以下这个else if判断的是王车易位
            else if ((first.getChessColor() == chessComponent.getChessColor()) &&
                    (first instanceof RookChessComponent && chessComponent instanceof KingChessComponent
                            || first instanceof KingChessComponent && chessComponent instanceof RookChessComponent)
                    && checkCastle(first, chessComponent, chessboard.getChessComponents())) {
                /**判断棋手是否想进行王车易位
                 *
                 */
                if (first instanceof RookChessComponent) {
                    chessboard.castle((KingChessComponent) chessComponent, (RookChessComponent) first);
                    chessboard.swapColor();
                    first.setSelected(false);
                    first = null;//成功行棋后，自动将clickController的选定设为null
                } else {
                    chessboard.castle((KingChessComponent) first, (RookChessComponent) chessComponent);
                    chessboard.swapColor();//更换行棋方
                    first.setSelected(false);
                    first = null;//成功行棋后，自动将clickController的选定设为null
                }
                clearValidMovements();
                chessboard.repaint();
                chessboard.saveStep();
                System.out.println(chessboard.step);
            } else if (handleSecond(chessComponent)) {       //选取移动位置后的反应
                //repaint in swap chess method.
                first.setSelected(false);
                clearValidMovements();
                if (animationMode != 0) {
                    movingTo(chessComponent);//TODO:需要绘制动画时调用
                }
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();
                first = null;//成功行棋后，自动将clickController的选定设为null
                chessboard.repaint();
                //检查是否被将军,以及将死
                if (check() != ChessColor.NONE) { //将军
                    if (check() == ChessColor.WHITE) {  //如果白方被将军
                        if (!(intercept(ChessColor.WHITE) || run(ChessColor.WHITE) || capture(ChessColor.WHITE))) {  //被将死
                            ChessGameFrame.setInfoText("BLACK WIN!");  //TODO：结束游戏
                            ChessGameFrame.setStatus(1);
                            paintVictoryGrid();
                            Chessboard.chessGameFrame.playback.setVisible(true);
                        } else {  //没被将死
                            ChessGameFrame.setInfoText("   Check!");
                        }
                    } else {  //如果黑方被将军
                        if (!(intercept(ChessColor.BLACK) || run(ChessColor.BLACK) || capture(ChessColor.BLACK))) {  //被将死
                            ChessGameFrame.setInfoText("WHITE WIN!");  //TODO：结束游戏
                            ChessGameFrame.setStatus(2);
                            paintVictoryGrid();
                            Chessboard.chessGameFrame.playback.setVisible(true);
                        } else {  //没被将死
                            ChessGameFrame.setInfoText("   Check!");
                        }
                    }
                }
                chessboard.saveStep();
            } else if (chessComponent.getChessColor() == first.getChessColor()) {
                //用于简化选取流程的语句：只要选了同一方的棋子且不是王车易位，就可以自动更换选取的棋子
                clearValidMovements();
                chessboard.repaint();
                first.setSelected(false);
                chessComponent.setSelected(true);
                ChessComponent recordFirst = first;
                first = chessComponent;
                first.repaint();
                recordFirst.repaint();
                checkValidMovements(chessComponent);
                paintValidMovements();
            }
        }
    }


    public static void paintVictoryGrid() throws InterruptedException {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessComponent.chessComponents[i][j].paintImmediately(0,0,76,76);
                Thread.sleep(10);
            }
        }
    }

    //判断是否被将军（未被将军返回null）
    public static ChessColor check() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessComponent king = ChessComponent.chessComponents[i][j]; //遍历棋盘，找到王
                if (king instanceof KingChessComponent && king.getChessColor() == ChessColor.WHITE) {  //判断是否为白王
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {  //再次遍历棋盘，找到能吃王的棋子
                            ChessComponent chess = ChessComponent.chessComponents[k][l];
                            if (!(chess instanceof EmptySlotComponent) && king.getChessColor() != chess.getChessColor() && chess.canMoveTo(ChessComponent.chessComponents, king.getChessboardPoint())) {
                                return ChessColor.WHITE;
                            }
                        }
                    }
                } else if (king instanceof KingChessComponent && king.getChessColor() == ChessColor.BLACK) {  //判断是否为黑王
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {  //再次遍历棋盘，找到能吃王的棋子
                            ChessComponent chess = ChessComponent.chessComponents[k][l];
                            if (!(chess instanceof EmptySlotComponent) && king.getChessColor() != chess.getChessColor() && chess.canMoveTo(ChessComponent.chessComponents, king.getChessboardPoint())) {
                                return ChessColor.BLACK;
                            }
                        }
                    }
                }
            }
        }
        ChessGameFrame.setInfoText("   No info.");
        return ChessColor.NONE;
    }


    //以下为将死所用到的方法
    //判断是否可以避将(不考虑吃过路兵)
    public boolean run(ChessColor kingColor) {  //传入王的颜色
        int X = 0;
        int Y = 0;

        for (int i = 0; i < 8; i++) {  //遍历棋盘找到被将军的王
            for (int j = 0; j < 8; j++) {
                ChessComponent king = ChessComponent.chessComponents[i][j]; //遍历棋盘，找到王
                if (king instanceof KingChessComponent && king.getChessColor() == kingColor) {
                    X = i;
                    Y = j;
                    break;
                }
            }
        }

        if (getChessboard().getCurrentColor() == kingColor) {  //判断当前行棋方是否与被将的王相同(因为可能出现送将的情况)
            ChessComponent king = ChessComponent.chessComponents[X][Y];
            ChessboardPoint kPoint = king.getChessboardPoint();
            Point kLocation = king.getLocation();
            char kName = king.getChessName();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ChessComponent chess = ChessComponent.chessComponents[i][j];
                    ChessboardPoint cPoint = chess.getChessboardPoint();
                    Point cLocation = chess.getLocation();
                    if (kingColor != chess.getChessColor() && king.canMoveTo(ChessComponent.chessComponents, chess.getChessboardPoint())) { //遍历棋盘，找到被将的王能走的位置
                        //自动走棋，判断交换位置后，是否还会继续被将军
                        chessboard.remove(ChessComponent.chessComponents[i][j]);
                        chessboard.remove(ChessComponent.chessComponents[X][Y]);
                        ChessComponent.chessComponents[i][j] = new KingChessComponent(cPoint, cLocation, kingColor, chessboard.getClickController(), chessboard.getCHESS_SIZE(), kName);
                        ChessComponent.chessComponents[X][Y] = new EmptySlotComponent(kPoint, kLocation, chessboard.getClickController(), chessboard.getCHESS_SIZE(), '_');
                        chessboard.add(ChessComponent.chessComponents[i][j]);
                        chessboard.add(ChessComponent.chessComponents[X][Y]);
                        //判断逃跑之后，是否还会继续被将军
                        if (check() == kingColor) {  //仍会被将军，无法避将
                            //无论是否会继续被将军，都要还原棋盘
                            chessboard.remove(ChessComponent.chessComponents[i][j]);
                            chessboard.remove(ChessComponent.chessComponents[X][Y]);
                            ChessComponent.chessComponents[X][Y] = king;
                            ChessComponent.chessComponents[i][j] = chess;
                            chessboard.add(ChessComponent.chessComponents[i][j]);
                            chessboard.add(ChessComponent.chessComponents[X][Y]);
                        } else {  //不会被将军，避将成功
                            chessboard.remove(ChessComponent.chessComponents[i][j]);
                            chessboard.remove(ChessComponent.chessComponents[X][Y]);
                            ChessComponent.chessComponents[X][Y] = king;
                            ChessComponent.chessComponents[i][j] = chess;
                            chessboard.add(ChessComponent.chessComponents[i][j]);
                            chessboard.add(ChessComponent.chessComponents[X][Y]);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    //找出有几个棋子将军王的函数（如果有两个及以上棋子将军王，则王无法消将和垫将）
    public static ArrayList<ChessComponent> checkingPieces(ChessColor kingColor) {
        ArrayList<ChessComponent> list = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessComponent king = ChessComponent.chessComponents[i][j]; //遍历棋盘，找到王
                if (king instanceof KingChessComponent && king.getChessColor() == kingColor) {  //判断是否为白王
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {  //再次遍历棋盘，找到能吃王的棋子
                            ChessComponent chess = ChessComponent.chessComponents[k][l];
                            if (!(chess instanceof EmptySlotComponent) && king.getChessColor() != chess.getChessColor() && chess.canMoveTo(ChessComponent.chessComponents, king.getChessboardPoint())) {
                                list.add(chess);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }


    //判断是否可以消将(不考虑吃过路兵)
    public boolean capture(ChessColor kingColor) {
        if (checkingPieces(kingColor).size() > 1) {  //王不能同时被多个棋子将军
            return false;
        } else if (checkingPieces(kingColor).size() == 1) {
            ChessComponent checkChess = checkingPieces(kingColor).get(0);  //对方将军的棋子
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    ChessComponent chess = ChessComponent.chessComponents[i][j];
                    if (!(chess instanceof EmptySlotComponent) && !(chess instanceof KingChessComponent) && chess.getChessColor() == kingColor
                            && chess.getChessColor() == getChessboard().getCurrentColor() && chess.canMoveTo(ChessComponent.chessComponents, checkChess.getChessboardPoint())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //判断是否可以垫将
    public boolean intercept(ChessColor kingColor) {
        if (checkingPieces(kingColor).size() > 1) {  //王不能同时被多个棋子将军
            return false;
        } else if (checkingPieces(kingColor).size() == 1) {
            ChessComponent checkChess = checkingPieces(kingColor).get(0);  //对方将军的棋子
            if (checkChess instanceof KingChessComponent || checkChess instanceof KnightChessComponent || checkChess instanceof PawnChessComponent || kingColor != getChessboard().getCurrentColor()) {  //如果将军的棋子是马、兵、王，不能垫将
                return false;
            }
            int X = 0;
            int Y = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {  //找到被将军的王
                    ChessComponent king = ChessComponent.chessComponents[i][j];
                    if (king instanceof KingChessComponent && king.getChessColor() == kingColor) {
                        X = i;
                        Y = j;
                    }
                }
            }
            if (Math.abs(X - checkChess.getChessboardPoint().getX()) > 1 || Math.abs(Y - checkChess.getChessboardPoint().getY()) > 1) {  //将军的棋子不能在被将军的王的九宫格范围内
                if (X == checkChess.getChessboardPoint().getX()) {  //考虑同一行的情况
                    int c1 = Math.max(Y, checkChess.getChessboardPoint().getY());
                    int c2 = Math.min(Y, checkChess.getChessboardPoint().getY());

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {  //遍历棋盘，找到能移动到该行垫将的棋子
                            ChessComponent chess = ChessComponent.chessComponents[i][j];
                            if (!(chess instanceof EmptySlotComponent) && !(chess instanceof KingChessComponent) && chess.getChessColor() == kingColor) {
                                for (int k = c2 + 1; k < c1; k++) {
                                    if (chess.canMoveTo(ChessComponent.chessComponents, chessComponents[X][k].getChessboardPoint())) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if (Y == checkChess.getChessboardPoint().getY()) {  //考虑同一列的情况
                    int r1 = Math.max(X, checkChess.getChessboardPoint().getX());
                    int r2 = Math.min(X, checkChess.getChessboardPoint().getX());

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            ChessComponent chess = ChessComponent.chessComponents[i][j];
                            if (!(chess instanceof EmptySlotComponent) && !(chess instanceof KingChessComponent) && chess.getChessColor() == kingColor) {
                                for (int k = r2 + 1; k < r1; k++) {
                                    if (chess.canMoveTo(ChessComponent.chessComponents, chessComponents[k][Y].getChessboardPoint())) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else if (X + Y == checkChess.getChessboardPoint().getX() + checkChess.getChessboardPoint().getY() || X - Y == checkChess.getChessboardPoint().getX() - checkChess.getChessboardPoint().getY()) {  //考虑同一对角线的情况
                    int c1 = Math.max(Y, checkChess.getChessboardPoint().getY());
                    int c2 = Math.min(Y, checkChess.getChessboardPoint().getY());
                    int r1 = Math.max(X, checkChess.getChessboardPoint().getX());
                    int r2 = Math.min(X, checkChess.getChessboardPoint().getX());

                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            ChessComponent chess = ChessComponent.chessComponents[i][j];
                            if (!(chess instanceof EmptySlotComponent) && !(chess instanceof KingChessComponent) && chess.getChessColor() == kingColor) {
                                for (int k = c2 + 1; k < c1; k++) {
                                    for (int l = r2 + 1; l < r1; l++) {
                                        if ((X + Y == k + l || X - Y == l - k) && chess.canMoveTo(ChessComponent.chessComponents, chessComponents[l][k].getChessboardPoint())) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;

    }


    /**
     * @param chessComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(ChessComponent chessComponent) {
        return chessComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param chessComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(ChessComponent chessComponent) {
        //TODO：加入预判棋子能够到达的区域
        return chessComponent.getChessColor() != chessboard.getCurrentColor() &&
                first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint());
        //用于判断有没有行棋到当前棋子颜色的位置，以及该位置能否由该棋子抵达
        /**此处说明了enum类型的“相等”，就是数值上相等，而不需要是“引用地址”相等
         *
         */
    }

    //用于检查是否满足王车易位条件
    public boolean checkCastle(ChessComponent chess1, ChessComponent chess2, ChessComponent[][] chessComponents) {
        if (chess1 instanceof RookChessComponent) {
            if (((RookChessComponent) chess1).isRookCanCastle() && ((KingChessComponent) chess2).isKingCanCastle()) {
                for (int i = Math.min(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()) + 1;
                     i < Math.max(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()); i++) {
                    //X对应row,Y对应col,遍历两个棋子间的元素，判断是否都为空棋子
                    if (!(chessComponents[chess1.getChessboardPoint().getX()][i] instanceof EmptySlotComponent) || check() != ChessColor.NONE) {
                        return false;
                    }
                }
                for (int i = Math.min(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY() + 1);
                     i <= Math.max(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()); i++) {
                    //王经过或者到达的位置不能受其他棋子攻击
                    ChessComponent sChessComponent = ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i];
                    ChessboardPoint sPoint = ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i].getChessboardPoint();
                    Point sLocation = ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i].getLocation();

                    chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()]);
                    chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                    ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i] = new KingChessComponent(sPoint, sLocation, chess2.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), chess2.getChessName());
                    ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()] = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), '_');
                    chessboard.add(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                    chessboard.add(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()]);
                    if (check() != ChessColor.NONE){
                        chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                        chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()]);
                        ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()] = chess2;
                        ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i] = sChessComponent;
                        chessboard.add(chess2);
                        chessboard.add(sChessComponent);
                        chessboard.repaint();
                        return false;
                    }else {
                        chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                        chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()]);
                        ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][chess2.getChessboardPoint().getY()] = chess2;
                        ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i] = sChessComponent;
                        chessboard.add(chess2);
                        chessboard.add(sChessComponent);
                    }
                }
                chessboard.repaint();
                return true;
            }
        } else {
            if (((RookChessComponent) chess2).isRookCanCastle() && ((KingChessComponent) chess1).isKingCanCastle()) {
                for (int i = Math.min(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()) + 1;
                     i < Math.max(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()); i++) {//小心不要直接chess1.getX(),那个坐标是图形界面的坐标
                    if (!(chessComponents[chess1.getChessboardPoint().getX()][i] instanceof EmptySlotComponent) || check() != ChessColor.NONE) {
                        return false;
                    }
                }
                for (int i = Math.min(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()) + 1;
                     i <= Math.max(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()); i++) {//小心不要直接chess1.getX(),那个坐标是图形界面的坐标
                    //王经过或者到达的位置不能受其他棋子攻击
                    ChessComponent sChessComponent = ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i];
                    ChessboardPoint sPoint = ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i].getChessboardPoint();
                    Point sLocation = ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i].getLocation();

                    chessboard.remove(ChessComponent.chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()]);
                    chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                    ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i] = new KingChessComponent(sPoint, sLocation, chess2.getChessColor(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), chess2.getChessName());
                    ChessComponent.chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()] = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), chessboard.getClickController(), chessboard.getCHESS_SIZE(), '_');
                    chessboard.add(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                    chessboard.add(ChessComponent.chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()]);
                    if (check() != ChessColor.NONE){
                        chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                        chessboard.remove(ChessComponent.chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()]);
                        ChessComponent.chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()] = chess1;
                        ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i] = sChessComponent;
                        chessboard.add(chess1);
                        chessboard.add(sChessComponent);
                        chessboard.repaint();
                        return false;
                    }else {
                        chessboard.remove(ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i]);
                        chessboard.remove(ChessComponent.chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()]);
                        ChessComponent.chessComponents[chess1.getChessboardPoint().getX()][chess1.getChessboardPoint().getY()] = chess1;
                        ChessComponent.chessComponents[chess2.getChessboardPoint().getX()][i] = sChessComponent;
                        chessboard.add(chess1);
                        chessboard.add(sChessComponent);
                    }
                }
                chessboard.repaint();
                return true;
            }
        }
        chessboard.repaint();
        return false;//当发现rook和king因为移动过而不能易位时，会执行这句话
    }


    //用于检测合法落子点的方法
    public void checkValidMovements(ChessComponent chessComponent) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessComponent.canMoveTo(chessboard.getChessComponents(), new ChessboardPoint(i, j))
                        && chessboard.getChessComponents()[i][j].getChessColor() != chessComponent.getChessColor()) {
                    //遍历整个棋盘，找到可以移动到的棋子
                    chessboard.getChessComponents()[i][j].setCanBeCaptured(true);
                }
            }
        }
    }

    public void paintValidMovements() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessboard.getChessComponents()[i][j].getCanBeCaptured()) {
                    chessboard.getChessComponents()[i][j].repaint();
                }
            }
        }
    }

    public void clearValidMovements() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard.getChessComponents()[i][j].setCanBeCaptured(false);
            }
        }
    }

    /**
     * 以下方法只负责动画，动画完成后，棋子在坐标上会回到原处，但是窗口中的棋盘中则不会
     */
    public void movingTo(ChessComponent chessComponent) throws InterruptedException {
        first.setMoving(true);
        int firstX = first.getX();//first是clickController的chessComponent字段
        int firstY = first.getY();
        int secondX = chessComponent.getX();
        int secondY = chessComponent.getY();
        int n = 25;//n表示刷新次数
//        chessboard.setOpaque(false);
        first.setOpaque(true);
        for (int i = 1; i <= n; i++) {
            first.setLocation(firstX + (secondX - firstX) * i / n, firstY + (secondY - firstY) * i / n);
            //这句话可以将firs置于顶层，不会被其他组件覆盖
            if (animationMode == 2) {
                chessboard.paintImmediately(0, 0, 76 * 8, 76 * 8);//神出鬼没
            }
            if (animationMode == 1) {
                first.paintImmediately(0, 0, 76, 76);//神龙摆尾
            }
            //不能使用repaint,repaint好像不会立即被执行，是个多线程方法？
            Thread.sleep(50 / n);//总时长为50毫秒
        }
        first.setLocation(firstX, firstY);
        //将第一个棋子的状态返回到初始状态，避免（可能）swapLocation出bug
        //确保图层绘制不出问题（少了这句，棋子移动到终点会消失
        first.setMoving(false);
        first.setOpaque(false);
    }
}

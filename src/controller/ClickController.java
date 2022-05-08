package controller;

//所有chessComponent共用同一个clickController
//clickController隶属于chessboard,将chessboard的clickController传入每一个chessComponent

import model.ChessComponent;
import model.EmptySlotComponent;
import model.KingChessComponent;
import model.RookChessComponent;
import view.Chessboard;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) {
        //棋盘格被点击后的反应
        if (first == null) {    //如果现在没有棋子被选取
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();//情况变化后，一定要repaint chessComponent
            }
        } else {
            if (first == chessComponent) {      // 再次点击取消选取
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();//要把原来选取的位置repaint
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
                    chessboard.swapColor();
                    first.setSelected(false);
                    first = null;//成功行棋后，自动将clickController的选定设为null
                }
            } else if (handleSecond(chessComponent)) {       //选取移动位置后的反应
                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();
                first.setSelected(false);
                first = null;//成功行棋后，自动将clickController的选定设为null
            }
        }
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
                    if (!(chessComponents[chess1.getChessboardPoint().getX()][i] instanceof EmptySlotComponent)) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            if (((RookChessComponent) chess2).isRookCanCastle() && ((KingChessComponent) chess1).isKingCanCastle()) {
                for (int i = Math.min(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()) + 1;
                     i < Math.max(chess1.getChessboardPoint().getY(), chess2.getChessboardPoint().getY()); i++) {//小心不要直接chess1.getX(),那个坐标是图形界面的坐标
                    if (!(chessComponents[chess1.getChessboardPoint().getX()][i] instanceof EmptySlotComponent)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;//当发现rook和king因为移动过而不能易位时，会执行这句话
    }

    //FIXME:以下方法用于检查王车易位条件2和3，这个判断暂未加入到代码中
    public boolean castleWithoutCheckmate() {
        return true;
    }
}

package controller;

//所有chessComponent共用同一个clickController
//clickController隶属于chessboard,将chessboard的clickController传入每一个chessComponent

import model.*;
import view.Chessboard;
import view.ChessboardPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static model.ChessComponent.getChessboard;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) throws IOException {
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
            } else if (handleSecond(chessComponent)) {       //选取移动位置后的反应
                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();
                first.setSelected(false);
                first = null;//成功行棋后，自动将clickController的选定设为null
                clearValidMovements();
                chessboard.repaint();
                if (checkmate()){ //检查是否被将军
                    System.out.println("--------------------Checkmate!------------------------");
                }
            } else if (chessComponent.getChessColor() == first.getChessColor()) {
                //用于简化选取流程的语句：只要选了同一方的棋子且不是王车易位，就可以自动更换选取的棋子
                clearValidMovements();
                chessboard.repaint();

            }else if(chessComponent.getChessColor()==first.getChessColor()){
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

    //存档
    public void saveGame() throws IOException {
        File file = new File("D:\\Project\\ChessProject\\ChessDemo\\resource\\save2.txt");
        System.out.println(file.createNewFile());
        System.out.println("-----------------------");
        FileOutputStream fos = new FileOutputStream("D:\\Project\\ChessProject\\ChessDemo\\resource\\save2.txt");  //创建文件输出流对象
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fos.write(ChessComponent.chessComponents[i][j].getChessName()); //将棋盘转为字符写入文件
            }
            fos.write("\n".getBytes(StandardCharsets.UTF_8));  //换行符
        }

//        if (currentColor == ChessColor.WHITE){
//            fos.write("w".getBytes(StandardCharsets.UTF_8)); //行棋方为白方，写入w
//        }else{
//            fos.write("b".getBytes(StandardCharsets.UTF_8)); //行棋方为黑方，写入b
//        }
        fos.write("w".getBytes(StandardCharsets.UTF_8));

        fos.close(); //释放资源
    }

    //判断是否被将军的方法
    public static boolean checkmate(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessComponent king = ChessComponent.chessComponents[i][j]; //遍历棋盘，找到王
                if (king instanceof KingChessComponent){
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            ChessComponent chess = ChessComponent.chessComponents[k][l];
                            if (!(chess instanceof EmptySlotComponent) && king.getChessColor() != chess.getChessColor() && chess.canMoveTo(ChessComponent.chessComponents , king.getChessboardPoint()) && chess.getChessColor() != getChessboard().getCurrentColor()){
                                return true; //再次遍历棋盘，找到能将王的棋子
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
    public boolean WithoutCheckmate() {
        return true;
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
}

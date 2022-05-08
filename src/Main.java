import view.ChessGameFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGameFrame mainFrame = new ChessGameFrame(1000, 760);//这里调整的是打开窗体的大小
            mainFrame.setVisible(true);//必须是true才能显示棋盘
        });
    }
}

//test

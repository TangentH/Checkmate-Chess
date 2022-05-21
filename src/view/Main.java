package view;

import music.Bgm;
import view.Welcome;

import javax.swing.*;
import javax.xml.crypto.Data;

import music.*;

public class Main {
    public static Welcome welcomeFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            welcomeFrame = new Welcome(550, 760);
            welcomeFrame.setIconImage(new ImageIcon("./images/Icon.png").getImage());
            welcomeFrame.setVisible(true);
//            ChessGameFrame mainFrame = new ChessGameFrame(1000, 760);//这里调整的是打开窗体的大小
//            mainFrame.setVisible(true);//必须是true才能显示棋盘
           /**播放bgm*/
            Bgm bgm = new Bgm();
            bgm.start();
        });
    }
}

package controller;

import view.ChessGameFrame;
import view.Chessboard;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameController {
    private Chessboard chessboard;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    //获取路径，并读档
    public List<String> loadGameFromFile(String path) {
        try {
            List<String> chessData = Files.readAllLines(Path.of(path));
            if (chessboard.loadGame(chessData)) {
                System.out.println("Load game successfully!"); //TODO:存档成功/失败后，有窗口弹出提示
                JOptionPane.showMessageDialog(Chessboard.chessGameFrame, "Load game successfully!");
                return chessData;
            } else {
                System.out.println("Load game failed!");
                JOptionPane.showMessageDialog(Chessboard.chessGameFrame, "Load game failed!");
                return null;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(Chessboard.chessGameFrame, "Load game failed!");
//            e.printStackTrace();
        }
        System.out.println("Load game failed!");
        JOptionPane.showMessageDialog(Chessboard.chessGameFrame, "Load game failed!");
        return null;
    }

    //获取路径，并存档
    public void saveGameFromFile(String fileName) {
        try {
            ChessGameFrame.saveGame(fileName);
            System.out.println("Save game successfully!");  //TODO:读档成功后，有窗口弹出提示
            JOptionPane.showMessageDialog(Chessboard.chessGameFrame, "Save game successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(Chessboard.chessGameFrame, "Save game failed!");
//            e.printStackTrace();
        }
    }

}

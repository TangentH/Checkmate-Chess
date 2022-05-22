package music;

import view.Main;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
//————————————————
//       版权声明：本文为CSDN博主「指尖侠」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//     原文链接：https://blog.csdn.net/qq_44491991/article/details/105859396

public class Bgm extends Thread {
    public static boolean loop = true;

    public void run() {// 背景音乐播放
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("music/Bgm.wav"));    //绝对路径
            AudioFormat aif = ais.getFormat();
            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            FloatControl fc = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);
            // value可以用来设置音量，从0-2.0
            double value = 0.5;
            float dB = (float) (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
            fc.setValue(dB);
            int nByte = 0;
            final int SIZE = 1024 * 64;
            byte[] buffer = new byte[SIZE];
            while (nByte != -1) {
                nByte = ais.read(buffer, 0, SIZE);
                sdl.write(buffer, 0, nByte);
            }
            sdl.stop();

        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (loop) {
                Bgm bgm = new Bgm();
                Main.bgm = bgm;
                bgm.start();
            }
            /**循环播放，finally:在执行完try和catch后一定要执行的代码
             * */
        }
    }
}
package net.minecraft.client.main;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class VerifySkipper {
public boolean finishedwaiting = false;

    public VerifySkipper() {
        // 创建一个Key监听器
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // 按下Alt + Shift + Y时，将shouldVerify设置为false
                if (e.isShiftDown()) {
                    Main.shouldverify = false;
                    finishedwaiting = true;
                    System.out.println("Development Key Pressed. Skipping Verify...");
                }
                else {
                    Main.shouldverify = true;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // 不需要实现
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // 不需要实现
            }
        };

        // 创建一个定时器，每100毫秒检查一次是否已经过去4秒
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int elapsedTime = 0; // 记录已经过去的时间

            @Override
            public void run() {
                elapsedTime += 100; // 每次累加100毫秒
                if (elapsedTime >= 4000) { // 如果已经过去4秒
                    System.out.println("Start Initializing the Verification...");
                    timer.cancel(); // 停止定时器
                    finishedwaiting = true;
                }
            }
        }, 0, 100); // 从0开始，每100毫秒执行一次
    }

    public static void main(String[] args) {
        // 创建VerifySkipper对象以触发Key监听器和定时器
        new VerifySkipper();
    }
}
//ChatGPT I love U.
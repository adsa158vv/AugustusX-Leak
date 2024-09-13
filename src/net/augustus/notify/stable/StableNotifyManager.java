package net.augustus.notify.stable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

/**
 * @author Genius
 * @since 2024/5/12 12:35
 * IntelliJ IDEA
 */

public class StableNotifyManager {

    public static ArrayList<StableNotify> notifications = new ArrayList<>();

    public static void clean(StableNotify notify) {
        notifications.remove(notify);
    }

    public static void addNotification(StableNotify stableNotify) {
        notifications.add(stableNotify);
    }

    public static void renderNotifications() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float y = sr.getScaledHeight() - 30;
        for (StableNotify notification : notifications) {
            notification.render(y);
            y -= 45;
        }

    }



    public static void update() {
        ArrayList<StableNotify> temp = new ArrayList<>();

        for (StableNotify no : notifications){
            temp.add(no);
            no.update();
//            System.out.println(no.timer.delay(no.lastTime * 1000) + " " );
            if(no.timer != null) {
                if (no.timer.reached((long) (no.lastTime * 1000))) {
                    no.setBack = true;
//                    no.timer.reset();
                    if(no.x <= 0.1f) {
                        temp.remove(no);
                    }
                }
            }


        }
        notifications.clear();
        notifications.addAll(temp);
    }

}


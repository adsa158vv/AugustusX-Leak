package net.augustus.notify.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Genius
 * @since 2024/5/12 12:35
 * IntelliJ IDEA
 */

public class CustomNotifyManager {

    public static ArrayList<CustomNotify> notifications = new ArrayList<>();

    public static void clean(CustomNotify notify) {
        notifications.remove(notify);
    }

    public static void addNotification(CustomNotify customNotify) {
        notifications.add(customNotify);
    }

    public static void renderNotifications() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float y = sr.getScaledHeight() - 30;
        for (CustomNotify notification : notifications) {
            notification.render(y);
            y -= 45;
        }

    }



    public static void update() {
        ArrayList<CustomNotify> temp = new ArrayList<>();

        for (CustomNotify no : notifications){
            temp.add(no);
            no.update();
            if(no.timer != null) {
                if (no.timer.reached((long) (no.lastTime * 1000))) {
                    no.setBack = true;
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


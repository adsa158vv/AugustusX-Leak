package net.augustus.notify.double_;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Iterator;

public class DoubleNotificationManager {
    public static ArrayList<DoubleNotification> notifications = new ArrayList<DoubleNotification>();

    public static ArrayList<DoubleNotification> getCleanNotifications() {
        return notifications;
    }

    public static void addCleanNotification(DoubleNotification n) {
        notifications.add(n);
    }

    public static void renderCleanNotifications() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float y = sr.getScaledHeight() - 30;
        for (DoubleNotification notification : notifications) {
            notification.render(y);
            y -= 25;
        }

    }

    public static void update() {
        ArrayList<DoubleNotification> temp = new ArrayList<>();

        for (DoubleNotification no : notifications){
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
        for (DoubleNotification no : temp) {
            notifications.add(no);
        }
    }

    public static void shader() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        for (DoubleNotification notification : notifications) {
            notification.shader();
        }

    }
}
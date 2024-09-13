package net.augustus.notify.augx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Genius
 * @since 2024/5/1 12:37
 * IntelliJ IDEA
 */

public class AugXNotifyManager {

    public static ArrayList<AugXNotify> notifications = new ArrayList<>();

    public static void clean(AugXNotify notify) {
        notifications.remove(notify);
    }

    public static void addNotification(AugXNotify augXNotify) {
        notifications.add(augXNotify);
    }

    public static void renderNotifications() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float y = sr.getScaledHeight() - 30;
        Iterator<AugXNotify> it = notifications.iterator();
        while (it.hasNext()) {
            it.next().render(y);
            y -= 25;
        }

    }



    public static void update() {
        ArrayList<AugXNotify> temp = new ArrayList<>();

        for (AugXNotify no : notifications){
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

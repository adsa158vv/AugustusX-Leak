package net.augustus.notify;

import net.augustus.Augustus;
import net.augustus.events.EventNotify;
import net.augustus.notify.augx.AugXNotify;
import net.augustus.notify.augx.AugXNotifyManager;
import net.augustus.notify.custom.CustomNotify;
import net.augustus.notify.custom.CustomNotifyManager;
import net.augustus.notify.double_.DoubleNotification;
import net.augustus.notify.double_.DoubleNotificationManager;
import net.augustus.notify.reborn.CleanNotification;
import net.augustus.notify.reborn.CleanNotificationManager;
import net.augustus.notify.stable.StableNotify;
import net.augustus.notify.stable.StableNotifyManager;
import net.augustus.notify.xenza.Notification;
import net.augustus.notify.xenza.NotificationsManager;
import net.augustus.utils.EventHandler;
import net.augustus.utils.interfaces.MM;

public class GeneralNotifyManager implements MM {
    public static void addNotification(String content, NotificationType type) {
        EventNotify eventNotify = new EventNotify(content , type);
        EventHandler.call(eventNotify);
        if (eventNotify.isCancelled()) {
            return;
        }
        switch(mm.notifications.mode.getSelected()) {
            case "Xenza": {
                NotificationsManager.addNotification(new Notification(content, type));
                break;
            }
            case "AugustusX": {
                AugXNotifyManager.addNotification(new AugXNotify(content,type));
                break;
            }
            case "Rise5": {
                Augustus.getInstance().getRise5notifyManager().registerNotification(content, type);
                break;
            }
            case "New": {
                CleanNotificationManager.addCleanNotification(new CleanNotification(content, type));
                break;
            }
            case "Stable": {
                StableNotifyManager.addNotification(new StableNotify(content, type));
                break;
            }
            case "Custom": {
                CustomNotifyManager.addNotification(new CustomNotify(content, type));
                break;
            }
            case "Double": {
                DoubleNotificationManager.addCleanNotification(new DoubleNotification(content, type));
                break;
            }
        }
    }
}

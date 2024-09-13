package net.augustus.events;

import net.augustus.utils.PrePostUtil;

public class EventPostMotion extends Event {
    public EventPostMotion(){
        PrePostUtil.Pre = false;
        PrePostUtil.Post = true;
    }
}

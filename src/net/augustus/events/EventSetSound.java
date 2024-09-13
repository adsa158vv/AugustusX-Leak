package net.augustus.events;

import lombok.Getter;
import net.minecraft.client.audio.SoundCategory;

@Getter
public class EventSetSound extends Event {
    float value;
    SoundCategory soundCategory;
    public EventSetSound(SoundCategory soundCategory , float value) {
        this.soundCategory = soundCategory;
        this.value = value;
    }

}

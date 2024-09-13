package net.augustus.events;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
public class EventAttackEntity extends Event {

    private final boolean pre;
    private Entity target;

    public EventAttackEntity(final Entity entity, final boolean pre) {
        this.target = entity;
        this.pre = pre;
    }

    public boolean isPre() {
        return this.pre;
    }

    public boolean isPost() {
        return !this.pre;
    }

}

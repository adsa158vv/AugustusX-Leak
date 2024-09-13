package net.augustus.modules;

import lombok.Getter;
import net.augustus.utils.RandomUtil;


import java.awt.*;

@Getter
public enum Categorys {
    MOVEMENT,
    COMBAT,
    PLAYER,
    MISC,
    RENDER,
    WORLD,
    EXPLOIT,
    SPECIAL;


    public Color color;

    Categorys() {
        color = new Color(RandomUtil.nextInt(0, 255), RandomUtil.nextInt(0, 255), RandomUtil.nextInt(0, 255));

    }

}

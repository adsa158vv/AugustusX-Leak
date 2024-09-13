package net.augustus.modules.render;

import net.augustus.events.EventAttackEntity;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.notify.GeneralNotifyManager;
import net.augustus.notify.NotificationType;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.skid.tenacity.animations.ContinualAnimation;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Genius
 * @since 2024/4/19 19:43
 * IntelliJ IDEA
 */

public class KillEffects extends Module {

    private final StringValue killEffectValue = new StringValue(16280,"KillEffects", this, "", new String[] {"LightningBolt",
            "Flame",
            "Smoke",
            "Water",
            "Love",
            "Blood",
            "Squid",
            "Off"} );
    private final StringValue killSoundValue = new StringValue(7861,"KillSound", this, "Squid", new String[] {"Squid",
            "Off"});
    private final BooleanValue tipsKillsValue = new BooleanValue(4347,"TipsKills", this,false);

    private int kills = 0;
    private EntityLivingBase target;
    private EntitySquid squid;
    private double percent = 0.0;
    private final ContinualAnimation anim = new ContinualAnimation();

    public KillEffects() {
        super("KillEffects", new Color(255,255,255), Categorys.RENDER);
    }

    public double easeInOutCirc(double x) {
        return x < 0.5 ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * x, 2.0))) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * x + 2.0, 2.0)) + 1.0) / 2.0;
    }

    @Override
    public void onDisable() {
        if (((Boolean)this.tipsKillsValue.getBoolean())) {
            this.kills = 0;
        }
    }

    @Override
    public void onEnable() {
        if (((Boolean)this.tipsKillsValue.getBoolean())) {
            this.kills = 0;
        }
    }

    @EventTarget
    public void onWorld(EventWorld event) {
        if (((Boolean)this.tipsKillsValue.getBoolean())) {
            this.kills = 0;
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.killEffectValue.getSelected().equals("Squid") && this.squid != null) {
            if (KillEffects.mc.theWorld.loadedEntityList.contains(this.squid)) {
                if (this.percent < 1.0) {
                    this.percent += Math.random() * 0.048;
                }
                if (this.percent >= 1.0) {
                    this.percent = 0.0;
                    for (int i = 0; i <= 8; ++i) {
                        KillEffects.mc.effectRenderer.emitParticleAtEntity(this.squid, EnumParticleTypes.FLAME);
                    }
                    KillEffects.mc.theWorld.removeEntity(this.squid);
                    this.squid = null;
                    return;
                }
            } else {
                this.percent = 0.0;
            }
            double easeInOutCirc = this.easeInOutCirc(1.0 - this.percent);
            this.anim.animate((float)easeInOutCirc, 450);
            this.squid.setPositionAndUpdate(this.squid.posX, this.squid.posY + (double)this.anim.getOutput() * 0.9, this.squid.posZ);
        }
        if (this.squid != null && this.killEffectValue.getSelected().equals("Squid")) {
            this.squid.squidPitch = 0.0f;
            this.squid.prevSquidPitch = 0.0f;
            this.squid.squidYaw = 0.0f;
            this.squid.squidRotation = 90.0f;
        }
        if (this.target != null && this.target.getHealth() <= 0.0f && !KillEffects.mc.theWorld.loadedEntityList.contains(this.target)) {
            if (((Boolean)this.tipsKillsValue.getBoolean())) {
                ++this.kills;
                GeneralNotifyManager.addNotification("Killed " + this.kills + " Players.  ", NotificationType.ToggleOn);
                //NotificationManager.addNotification(NotificationType.SUCCESS, "Kills +1", "Killed " + this.kills + " Players.  ");
            }
            if (this.killSoundValue.getSelected().equals("Squid")) {
                this.playSound(SoundType.KILL, (mc.gameSettings.getSoundLevel(SoundCategory.MASTER) * mc.gameSettings.getSoundLevel(SoundCategory.ANIMALS)));
            }
            if (this.killEffectValue.getSelected().equals("LightningBolt")) {
                EntityLightningBolt entityLightningBolt = new EntityLightningBolt(KillEffects.mc.theWorld, this.target.posX, this.target.posY, this.target.posZ);
                KillEffects.mc.theWorld.addEntityToWorld((int)(-Math.random() * 100000.0), entityLightningBolt);
                KillEffects.mc.theWorld.playSound(KillEffects.mc.thePlayer.posX, KillEffects.mc.thePlayer.posY, KillEffects.mc.thePlayer.posZ, "ambient.weather.thunder", 1.0f, 1.0f, false);
                KillEffects.mc.theWorld.playSound(KillEffects.mc.thePlayer.posX, KillEffects.mc.thePlayer.posY, KillEffects.mc.thePlayer.posZ, "random.explode", 1.0f, 1.0f, false);
                for (int i = 0; i <= 8; ++i) {
                    KillEffects.mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.FLAME);
                }
                KillEffects.mc.theWorld.playSound(KillEffects.mc.thePlayer.posX, KillEffects.mc.thePlayer.posY, KillEffects.mc.thePlayer.posZ, "item.fireCharge.use", 1.0f, 1.0f, false);
            }
            if (this.killEffectValue.getSelected().equals("Squid")) {
                this.squid = new EntitySquid(KillEffects.mc.theWorld);
                KillEffects.mc.theWorld.addEntityToWorld(-847815, this.squid);
                this.squid.setPosition(this.target.posX, this.target.posY, this.target.posZ);

            }
            this.target = null;
        }
        if (this.target != null && !this.target.isDead) {
            switch (this.killEffectValue.getSelected()) {
                case "Flame": {
                    KillEffects.mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.FLAME);
                    this.target = null;
                    break;
                }
                case "Smoke": {
                    KillEffects.mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.SMOKE_LARGE);
                    this.target = null;
                    break;
                }
                case "Water": {
                    KillEffects.mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.WATER_DROP);
                    this.target = null;
                    break;
                }
                case "Love": {
                    KillEffects.mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.HEART);
                    KillEffects.mc.effectRenderer.emitParticleAtEntity(this.target, EnumParticleTypes.WATER_DROP);
                    this.target = null;
                    break;
                }
                case "Blood": {
                    for (int i = 0; i < 10; ++i) {
                        KillEffects.mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), this.target.posX, this.target.posY + (double)(this.target.height / 2.0f), this.target.posZ, this.target.motionX + (double) KillEffects.nextFloat(-0.5f, 0.5f), this.target.motionY + (double) KillEffects.nextFloat(-0.5f, 0.5f), this.target.motionZ + (double) KillEffects.nextFloat(-0.5f, 0.5f), Block.getStateId(Blocks.redstone_block.getDefaultState()));
                    }
                    this.target = null;
                }
            }
        }
    }

    public void playSound(SoundType st, float volume) {
        new Thread(() -> {
            try {
                AudioInputStream as = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(this.getClass().getResourceAsStream("/resources/sounds/" + st.getName()))));
                Clip clip = AudioSystem.getClip();
                clip.open(as);
                clip.start();
                FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volume);
                clip.start();
            }
            catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0f) {
            return startInclusive;
        }
        return (float)((double)startInclusive + (double)(endInclusive - startInclusive) * Math.random());
    }

    @EventTarget
    public void onAttack(EventAttackEntity event) {
        if (event.getTarget() != null) {
            this.target = (EntityLivingBase)event.getTarget();
        }
    }

    public static enum SoundType {
        KILL("kill.wav");

        final String music;

        private SoundType(String fileName) {
            this.music = fileName;
        }

        String getName() {
            return this.music;
        }
    }

}

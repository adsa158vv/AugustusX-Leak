// Decompiled with: CFR 0.152
// Class Version: 8
package net.augustus.modules.combat;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.augustus.events.EventReadPacket;
import net.augustus.events.EventTick;
import net.augustus.events.EventUpdate;
import net.augustus.events.EventWorld;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.settings.StringValue;
import net.augustus.utils.skid.vestige.LogUtil;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.world.WorldSettings;


public class AntiBot
        extends Module {
    public static List<EntityPlayer> bots = new ArrayList<>();
    private BooleanValue checkTab = new BooleanValue(6694, "Tab", this, true);
    public BooleanValue livingTime = new BooleanValue(7785, "LivingTime", this, true);
    public DoubleValue livingTime_Ticks = new DoubleValue(9998, "LivingTicks", this, 40, 0, 600, 0);
    private BooleanValue customNameTag = new BooleanValue(8637, "CustomNameTag", this, true);
    private BooleanValue entityID = new BooleanValue(16261, "EntityID", this, false);
    private BooleanValue sleep = new BooleanValue(5031, "Sleep", this, false);
    private BooleanValue noArmor = new BooleanValue(16122, "NoArmor", this, false);
    private BooleanValue height = new BooleanValue(6313, "Height", this, false);
    private BooleanValue ground = new BooleanValue(537, "Ground", this, false);
    private BooleanValue dead = new BooleanValue(386, "Dead", this, false);
    private BooleanValue health = new BooleanValue(16297, "Health", this, false);
    private BooleanValue matrix = new BooleanValue(13712,"Matrix",this,false);
    private BooleanValue spawnInCombat = new BooleanValue(4124,"SpawnInCombat",this,true);

    private BooleanValue uuid = new BooleanValue(10398,"UUID",this,false);
    public BooleanValue hytGetNames = new BooleanValue(5429, "HytGetName", this, false);
    public final BooleanValue tips = new BooleanValue(9544, "HytGetNameTips", this, true);
    public StringValue hytGetNameMode = new StringValue(12771, "HytGetNameMode", this, "HytBedWars4v4", new String[]{"HytBedWars4v4", "HytBedWars1v1", "HytBedWars16", "HytBedWars32"});
    private static final List<Integer> groundBotList = new ArrayList<Integer>();

    private static final List<String> playerName = new ArrayList<String>();
    private boolean wasAdded = false;
    private String name;
    private final ArrayList<Integer> removedBots = new ArrayList<Integer>();
    private final ArrayList<Integer> spawnInCBBots = new ArrayList<Integer>();

    public AntiBot() {
        super("AntiBot", Color.GREEN, Categorys.COMBAT);
    }

    @EventTarget
    public void onWorld(EventWorld eventWorld) {
        this.clearAll();
    }
    @Override
    public void onEnable() {
        super.onEnable();
        this.clearAll();
        wasAdded = false;
    }
    @Override
    public void onDisable() {
        super.onDisable();
        this.clearAll();
        wasAdded = false;
    }
    private void clearAll() {
        playerName.clear();
        bots.clear();
        wasAdded = false;
        removedBots.clear();
        spawnInCBBots.clear();
        name = "";
    }

    @EventTarget
    public void onRecv(EventReadPacket event) {
        Entity entity;
        if (AntiBot.mc.thePlayer == null || AntiBot.mc.theWorld == null) {
            return;
        }
        if (matrix.getBoolean()) {
            Packet<?> packet = event.getPacket();

            if (packet instanceof S41PacketServerDifficulty) {
                wasAdded = false;
            }

            if (packet instanceof S38PacketPlayerListItem) {
                S38PacketPlayerListItem packetListItem = (S38PacketPlayerListItem) packet;
                S38PacketPlayerListItem.AddPlayerData data = packetListItem.getPlayers().get(0);

                if (data.getProfile() != null && data.getProfile().getName() != null) {
                    name = data.getProfile().getName();

                    if (!wasAdded) {
                        wasAdded = name.equals(mc.thePlayer.getCommandSenderEntity().getName());
                    } else if (!mc.thePlayer.isSpectator() && !mc.thePlayer.capabilities.allowFlying &&
                            (data.getPing() != 0) &&
                            (data.getGameMode() != WorldSettings.GameType.NOT_SET)) {

                            event.setCancelled(true);

                    }
                }
            }
        }
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof S14PacketEntity && ((Boolean) ground.getBoolean()) && (entity = ((S14PacketEntity) event.getPacket()).getEntity(AntiBot.mc.theWorld)) instanceof EntityPlayer && ((S14PacketEntity) event.getPacket()).onGround && !groundBotList.contains(entity.getEntityId())) {
            groundBotList.add(entity.getEntityId());
        }
        if (((Boolean) hytGetNames.getBoolean()) && packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat) packet;
            if (s02PacketChat.getChatComponent().getUnformattedText().contains("获得胜利!") || s02PacketChat.getChatComponent().getUnformattedText().contains("游戏开始 ...")) {
                this.clearAll();
            }
            switch (hytGetNameMode.getSelected()) {
                case "HytBedWars4v4":
                case "HytBedWars1v1":
                case "HytBedWars32": {
                    String name;
                    Matcher matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?) 死了!)))").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if ((matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了")) && !(name = matcher.group(1).trim()).isEmpty()) {
                        playerName.add(name);
                        if (((Boolean) this.tips.getBoolean())) {
                            LogUtil.addChatMessage("§8[§c§lAntiBotTips§8] §c§dAddBot：" + name);
                        }
                        String finalName = name;
                        new Thread(() -> {
                            try {
                                Thread.sleep(6000L);
                                playerName.remove(finalName);
                                if (((Boolean) this.tips.getBoolean())) {
                                    LogUtil.addChatMessage("§8[§c§lAntiBotTips§8]§c§dRemovedBot：" + finalName);
                                }
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                    }
                    if ((!matcher2.find() || s02PacketChat.getChatComponent().getUnformattedText().contains(": 起床战争>>")) && s02PacketChat.getChatComponent().getUnformattedText().contains(": 杀死了") || (name = matcher2.group(1).trim()).isEmpty())
                        break;
                    playerName.add(name);
                    if (((Boolean) this.tips.getBoolean())) {
                        LogUtil.addChatMessage("§8[§c§lAntiBotTips§8] §c§dAddBot：" + name);
                    }
                    String finalName1 = name;
                    new Thread(() -> {
                        try {
                            Thread.sleep(6000L);
                            playerName.remove(finalName1);
                            if (((Boolean) this.tips.getBoolean())) {
                                LogUtil.addChatMessage("§8[§c§lAntiBotTips§8]§c§dRemovedBot：" + finalName1);
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                    break;
                }
                case "HytBedWars16": {
                    String name;
                    Matcher matcher = Pattern.compile("击败了 (.*?)!").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("玩家 (.*?)死了！").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if ((matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) && !(name = matcher.group(1).trim()).isEmpty()) {
                        playerName.add(name);
                        if (((Boolean) this.tips.getBoolean())) {
                            LogUtil.addChatMessage("§8[§c§lAntiBotTips§8] §c§dAddBot：" + name);
                        }
                        String finalName = name;
                        new Thread(() -> {
                            try {
                                Thread.sleep(10000L);
                                playerName.remove(finalName);
                                if (((Boolean) this.tips.getBoolean())) {
                                    LogUtil.addChatMessage("§8[§c§lAntiBotTips§8]§c§dRemovedBot：" + finalName);
                                }
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                    }
                    if ((!matcher2.find() || s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了")) && s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ") || (name = matcher2.group(1).trim()).isEmpty())
                        break;
                    playerName.add(name);
                    LogUtil.addChatMessage("§8[§c§lAntiBotTips§8] §c§dAddBot：" + name);
                    String finalName1 = name;
                    new Thread(() -> {
                        try {
                            Thread.sleep(10000L);
                            playerName.remove(finalName1);
                            LogUtil.addChatMessage("§8[§c§lAntiBotTips§8]§c§dRemovedBot：" + finalName1);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                    break;
                }
            }
        }
        if (packet instanceof S0CPacketSpawnPlayer) {
            if(KillAura.target != null && !removedBots.contains(((S0CPacketSpawnPlayer) packet).getEntityID())) {
                spawnInCBBots.add(((S0CPacketSpawnPlayer) packet).getEntityID());
            }
        } else if (packet instanceof S13PacketDestroyEntities) {
            Collection<? extends Integer> collection = Arrays.stream(((S13PacketDestroyEntities) packet).getEntityIDs()).boxed().collect(Collectors.toCollection(ArrayList::new));
            removedBots.addAll(collection);
        }
    }

    @EventTarget
    public void onEventTick(EventTick eventTick) {
        List<EntityPlayer> tab = getTabPlayerList();

        if (mc.thePlayer.ticksExisted > 110) {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (customNameTag.getBoolean() && entity instanceof EntityPlayer && entity != mc.thePlayer && Objects.equals(entity.getCustomNameTag(), "") && !bots.contains((EntityPlayer) entity)) {
                    bots.add((EntityPlayer) entity);
                }
            }
        }


        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                if (checkTab.getBoolean()) {
                    if (!tab.contains(entity))
                        bots.add((EntityPlayer) entity);
                }

                if (livingTime.getBoolean() && (double) entity.ticksExisted < this.livingTime_Ticks.getValue() && entity != mc.thePlayer && !bots.contains(entity)) {
                    bots.add((EntityPlayer) entity);
                }
            }
        }

    }
    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if (uuid.getBoolean()) {
            mc.theWorld.playerEntities.forEach(player -> {
                final NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
                if (info == null) {
                    bots.add(player);
                } else {
                    bots.remove(player);
                }
            });
        }
    }

    public static List<EntityPlayer> getTabPlayerList() {
        NetHandlerPlayClient var4 = (Minecraft.getMinecraft()).thePlayer.sendQueue;
        List<EntityPlayer> list = new ArrayList<>();
        List<NetworkPlayerInfo> players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(var4.getPlayerInfoMap());
        for (NetworkPlayerInfo o : players) {
            NetworkPlayerInfo info = o;
            if (info == null)
                continue;
            list.add((Minecraft.getMinecraft()).theWorld.getPlayerEntityByName(info.getGameProfile().getName()));
        }
        return list;
    }
    public static boolean isServerBot(Entity entity) {
        if (mm.antiBot.isToggled() && entity instanceof EntityPlayer) {
            if (bots.contains(entity)) {
                return true;
            }
            if (mm.antiBot.spawnInCombat.getBoolean() && mm.antiBot.spawnInCBBots.contains(entity.getEntityId())) {
                return true;
            }
            if (((Boolean)mm.antiBot.hytGetNames.getBoolean()) && playerName.contains(entity.getName())) {
                return true;
            }
            if (((Boolean)mm.antiBot.height.getBoolean()) && ((double)entity.height <= 0.5 || ((EntityPlayer)entity).isPlayerSleeping() || entity.ticksExisted < 80)) {
                return true;
            }
            if (((Boolean)mm.antiBot.dead.getBoolean()) && entity.isDead) {
                return true;
            }
            if (((Boolean)mm.antiBot.health.getBoolean()) && ((EntityPlayer)entity).getHealth() == 0.0f) {
                return true;
            }
            if (((Boolean)mm.antiBot.sleep.getBoolean()) && ((EntityPlayer)entity).isPlayerSleeping()) {
                return true;
            }
            if (((Boolean)mm.antiBot.entityID.getBoolean()) && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1)) {
                return true;
            }
            if (((Boolean)mm.antiBot.ground.getBoolean()) && !groundBotList.contains(entity.getEntityId())) {
                return true;
            }
            return (Boolean) mm.antiBot.noArmor.getBoolean() && ((EntityPlayer)entity).inventory.armorInventory[0] == null && ((EntityPlayer)entity).inventory.armorInventory[1] == null && ((EntityPlayer)entity).inventory.armorInventory[2] == null && ((EntityPlayer)entity).inventory.armorInventory[3] == null;
        }
        return false;
    }


}

package net.augustus.modules.misc;

import net.augustus.events.*;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.settings.BooleanValue;
import net.augustus.settings.DoubleValue;
import net.augustus.utils.TimeHelper;
import net.java.games.input.Mouse;
import net.lenni0451.eventapi.reflection.EventTarget;

import java.awt.*;


public class Fixes extends Module {
   public final BooleanValue mouseDelayFix = new BooleanValue(14015, "MouseDelayFix", this, true);
   public final BooleanValue realMouse = new BooleanValue(131,"RealMouse",this,true);
   public final BooleanValue hitDelayFix = new BooleanValue(11689, "HitDelayFix", this, false);
   public final BooleanValue memoryFix = new BooleanValue(9036, "MemoryFix", this, false);

   public final BooleanValue memoryFix_onWorld = new BooleanValue(14741, "MemoryFixOnWorld", this, false);
   public final BooleanValue memoryFix_onTime = new BooleanValue(6452, "MemoryFixOnTime", this, false);
   public final BooleanValue memoryFix_onMemoryAbove = new BooleanValue(11984, "MemoryFixOnMemAbove", this, false);
   public final DoubleValue memoryFix_onTime_Ticks= new DoubleValue(14037, "MemoryFixOTTicks", this,1200,0,12000,0 );
   public final DoubleValue memoryFix_onMemoryAbove_Max= new DoubleValue(15670, "MemoryFixOMAMax", this,3000,0,16000,0 );
   public final DoubleValue getMemoryFix_onMemoryAbove_Ticks = new DoubleValue(502, "MemoryFixOMAMinTicks", this, 60,0,1200,0);
   public final BooleanValue noJumpDelay = new BooleanValue(2240, "NoJumpDelay", this, false);

   public final TimeHelper timeHelper = new TimeHelper();



   public static Mouse mouse;

   // Delta for mouse
   public int dx = 0;
   public int dy = 0;
   private boolean threadRun = false;
   public volatile Thread realMouseThread = null;

   public Fixes() {
      super("Fixes", new Color(169, 66, 237), Categorys.MISC);
   }

   @EventTarget
   public void onWorld(EventWorld eventWorld) {
       gcTicks = 0;
       gcDelay = 0;
       if (memoryFix.getBoolean()) {
          System.gc();
       }

   }
   @Override
   public void onDisable() {
      super.onDisable();
      if (realMouseThread != null && realMouseThread.isAlive()) {
         threadRun = false;
         try {
            // 等待线程安全停止
            realMouseThread.join();
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
         // 重置线程引用
         realMouseThread = null;
      }
   }
   @Override
   public void onEnable() {
      super.onEnable();
      gcTicks = 0;
      gcDelay = 0;
      if (memoryFix.getBoolean()) {
         System.gc();
      }
      if (realMouse.getBoolean() && realMouseThread == null) {
         realMouseThread = new Thread(() -> {
            while (threadRun) {
               mouse.poll();
               dx += (int) mouse.getX().getPollData();
               dy += (int) mouse.getY().getPollData();
               try {
                  Thread.sleep(1);
               } catch (InterruptedException e) {
                  // 这里可以添加更多的清理工作
                  Thread.currentThread().interrupt(); // 重置中断状态
               }
            }
            // 线程退出时的清理工作
         });
         realMouseThread.setName("MouseFixThread");

         threadRun = true;
         realMouseThread.start();

      }
   }
   private int gcTicks = 0;
   private int gcDelay = 0;
   @EventTarget
   public void onEventTick(EventTick eventTick) {
      double memoryUsed = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
      if (memoryFix_onTime.getBoolean() && memoryFix.getBoolean()) {
         gcTicks++;
         if (gcTicks >= memoryFix_onTime_Ticks.getValue()) {
            System.gc();
            gcTicks = 0;
         }
      }
      if (memoryFix_onMemoryAbove.getBoolean() && memoryFix.getBoolean()) {
         gcDelay ++;
         if (memoryUsed > memoryFix_onMemoryAbove_Max.getValue()) {
            if (gcDelay > getMemoryFix_onMemoryAbove_Ticks.getValue()) {
               System.gc();
               gcDelay = 0;
            }
         }
      }
      if (noJumpDelay.getBoolean()) {
         mc.thePlayer.setJumpTicks(0);
      }

   }
   @EventTarget
   public void onEventRender2D(EventRender2D eventRender2D) {
      if (this.realMouse.getBoolean() && realMouseThread == null) {
         realMouseThread = new Thread(() -> {
            while (threadRun) {
               mouse.poll();
               dx += (int) mouse.getX().getPollData();
               dy += (int) mouse.getY().getPollData();
               try {
                  Thread.sleep(1);
               } catch (InterruptedException e) {
                  // 这里可以添加更多的清理工作
                  Thread.currentThread().interrupt(); // 重置中断状态
               }
            }
            // 线程退出时的清理工作
         });
         threadRun = true; // 确保先设置运行标志
         realMouseThread.setName("MouseFixThread"); // 线程命名应该在启动前
         realMouseThread.start();
      }
      if (realMouseThread != null && realMouseThread.isAlive() && !this.realMouse.getBoolean()) {
         threadRun = false; // 设置为false以停止线程
      }
   }



}




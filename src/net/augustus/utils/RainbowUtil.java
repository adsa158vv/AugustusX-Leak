package net.augustus.utils;

import java.awt.Color;

public class RainbowUtil {
   private float rainBowColor = 0.0F;
   private Color color = new Color(0, 0, 0);
   private long lastTime;

   public void updateRainbow(float speed, int alpha) {
      long currentTime = System.nanoTime() / 1000000;
      long deltaTime = currentTime - this.lastTime;
      this.lastTime = currentTime;

      // 确保 speed 是合理的值
      if (speed < 0.0F) {
         throw new IllegalArgumentException("Speed must be non-negative");
      }

      // 更新彩虹色
      if (speed > 0.0F) {
         this.rainBowColor += speed * ((float) deltaTime / 1000.0F); // 转换为每秒
      }

      // 确保 rainbowColor 在 0.0 到 1.0 之间
      this.rainBowColor = (this.rainBowColor > 1.0F) ? (float) (this.rainBowColor - Math.floor(this.rainBowColor)) : this.rainBowColor;

      try {
         Color hsbColor = Color.getHSBColor(this.rainBowColor, 1.0F, 1.0F);
         this.color = new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), alpha);
      } catch (IllegalArgumentException e) {
         // 如果 Color.getHSBColor 返回了一个非法的 RGB 值，记录错误并重置颜色
         System.err.println("Error while generating rainbow color: " + e.getMessage());
         // 重置颜色为默认值或取最接近的合法值
         this.color = new Color(0, 0, 0);
      }
   }

   public Color getColor() {
      return this.color;
   }
}

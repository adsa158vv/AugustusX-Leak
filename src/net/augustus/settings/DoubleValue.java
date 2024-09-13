package net.augustus.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.augustus.modules.Module;
import me.jDev.augustusx.files.parts.SettingPart;

@Setter
@Getter
public class DoubleValue extends Setting {
   @Expose
   @SerializedName("Value")
   private double value;
   private double minValue;
   private double maxValue;
   private int decimalPlaces;
   private boolean var1;
   private boolean var2;

   public DoubleValue(int id, String name, Module parent, double value, double minValue, double maxValue, int decimalPlaces) {
      super(id, name, parent);
      this.value = value;
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.decimalPlaces = decimalPlaces;
      sm.newSetting(this);
   }




   public double getValueneg1() {
      return this.value-1;
   }

   @Override
   public void readSetting(SettingPart setting) {
      super.readSetting(setting);
      this.setValue(setting.getValue());
   }

   @Override
   public void readConfigSetting(SettingPart setting) {
      super.readConfigSetting(setting);
      this.setValue(setting.getValue());
   }
}

package net.augustus.events;



import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.Sys;



@Setter
@Getter
public class EventRayTraceRange extends Event
{
    public double blockReachDistance;
    public double range;
    public double rayTraceRange;
    
    public EventRayTraceRange(double range) {
        try {
            if (Minecraft.getMinecraft().playerController != null && Minecraft.getMinecraft().thePlayer != null) {
                blockReachDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance();

            } else {
                blockReachDistance = 3.0f;
                System.err.println("Minecraft.getMinecraft().thePlayer = " + Minecraft.getMinecraft().thePlayer + " , Minecraft.getMinecraft().playerController = " + Minecraft.getMinecraft().playerController + " !");
            }

            this.range = range;
        }
        catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

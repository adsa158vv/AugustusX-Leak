package net.minecraft.client;

import static net.augustus.modules.exploit.FakeForge.ffgist;


public class ClientBrandRetriever {
   public static String getClientModName() {
      if (ffgist) {
         return "fml,forge";
      } else {
         return "vanilla";
      }
   }
}

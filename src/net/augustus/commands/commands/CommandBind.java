package net.augustus.commands.commands;

import net.augustus.Augustus;
import net.augustus.commands.Command;
import net.augustus.modules.Module;
import org.lwjgl.input.Keyboard;

public class CommandBind extends Command {
   public CommandBind() {
      super(".bind");
   }

   @Override
   public void commandAction(String[] message) {
      super.commandAction(message);
      if (message.length >= 3) {
         for(Module module : Augustus.getInstance().getModuleManager().getModules()) {
            if (message[1].equalsIgnoreCase(module.getName())) {
               for(int i = 0; i < 84; ++i) {
                  if (Keyboard.getKeyName(i).equalsIgnoreCase(message[2])) {
                     this.setKey(i, module, message[2].toUpperCase());
                     return;
                  }
               }
            }
         }
      }

      this.errorMessage();
   }

   private void setKey(int key, Module module, String keyName) {
      module.setKey(key);
      this.sendChat("§c" + module.getName() + " §7bound to §2" + keyName.toUpperCase());
   }

   @Override
   public void helpMessage() {
      this.sendChat("§c.bind§7 (Binds a module to a key)");
   }

   public void errorMessage() {
      this.sendChat("§7.bind [§cModule§7] [§2Key§7]");
   }
}

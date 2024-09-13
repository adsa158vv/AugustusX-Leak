package net.augustus.utils.shader;

import net.augustus.Augustus;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

public class BackgroundShaderUtil {
   private final ArrayList<ShaderUtil> shaders = new ArrayList<>();
   private final ArrayList<String> shaderNames = new ArrayList<>();
   private ShaderUtil currentShader;

   public BackgroundShaderUtil() {
      this.saveResource("shaders/abraxas.frag", "AugustusX", false);
      this.saveResource("shaders/galaxy.frag", "AugustusX", false);
      this.saveResource("shaders/galaxy2.frag", "AugustusX", false);
      this.saveResource("shaders/purplewater.frag", "AugustusX", false);
      this.saveResource("shaders/liquidbounce.frag", "AugustusX", false);
      this.saveResource("shaders/redwaterlike.frag", "AugustusX", false);
      this.saveResource("shaders/bluemetal.frag", "AugustusX", false);
      this.saveResource("shaders/metal.frag", "AugustusX", false);
      this.saveResource("shaders/snowstar.frag", "AugustusX", false);
      this.saveResource("shaders/spider.frag", "AugustusX", false);
      this.saveResource("shaders/trinity.frag", "AugustusX", false);
      this.saveResource("shaders/bluefog.frag", "AugustusX", false);
      this.saveResource("shaders/cloudtunnel.frag", "AugustusX", false);
      this.saveResource("shaders/cute.frag", "AugustusX", false);
      this.saveResource("shaders/idk.frag", "AugustusX", false);
      this.saveResource("ip_changer_fritzbox.vbs", "AugustusX", false);
      File folder = new File("AugustusX/shaders");
      File[] listOfFiles = folder.listFiles();

      for(File file : listOfFiles) {
         if (file.getName().contains(".")) {
            String[] s = file.getName().split("\\.");
            if (s.length > 1 && s[1].equals("frag")) {
               ShaderUtil shaderUtil = new ShaderUtil();
               char[] arr = s[0].toCharArray();
               arr[0] = Character.toUpperCase(arr[0]);
               shaderUtil.createBackgroundShader(file.getAbsolutePath(), new String(arr));
               this.shaders.add(shaderUtil);
               this.shaderNames.add(shaderUtil.getName());
            }
         }
      }

      String shaderName = Augustus.getInstance().getConverter().readBackground();

      for(ShaderUtil shader : this.shaders) {
         if (shader.getName().equalsIgnoreCase(shaderName)) {
            this.currentShader = shader;
            return;
         }
      }

      this.currentShader = this.shaders.get(0);
   }

   public void setCurrentShader(String name) {
      for(ShaderUtil shaderUtil : this.shaders) {
         if (shaderUtil.getName().equalsIgnoreCase(name)) {
            this.currentShader = shaderUtil;
            Augustus.getInstance().getConverter().saveBackground(shaderUtil);
            return;
         }
      }
   }

   private void saveResource(String resourcePath, String dataFolder, boolean replace) {
      if (resourcePath != null && !resourcePath.equals("")) {
         resourcePath = resourcePath.replace('\\', '/');
         InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
         if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in client");
         } else {
            File outFile = new File(dataFolder, resourcePath);
            int lastIndex = resourcePath.lastIndexOf(47);
            File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
            if (!outDir.exists()) {
               outDir.mkdirs();
            }

            try {
               if (!outFile.exists() || replace) {
                  OutputStream out = new FileOutputStream(outFile);
                  byte[] buf = new byte[1024];

                  int len;
                  while((len = in.read(buf)) > 0) {
                     out.write(buf, 0, len);
                  }

                  out.close();
                  in.close();
               }
            } catch (IOException var11) {
               System.err.println(Level.SEVERE + "Could not save " + outFile.getName() + " to " + outFile);
            }
         }
      } else {
         throw new IllegalArgumentException("ResourcePath cannot be null or empty");
      }
   }

   public ShaderUtil getCurrentShader() {
      return this.currentShader;
   }

   public ArrayList<ShaderUtil> getShaders() {
      return this.shaders;
   }

   public ArrayList<String> getShaderNames() {
      return this.shaderNames;
   }
}

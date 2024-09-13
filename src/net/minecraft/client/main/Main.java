package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.*;
import java.net.Proxy.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;


import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import javax.swing.*;
import javax.swing.Timer;

public class Main {
   private static boolean allowed = false;
   public static boolean shouldverify = false;
   public static String user = "User";
   public static Object VO=new Object();
   public static void main(String[] p_main_0_) throws InterruptedException {




         main2(p_main_0_);




   }
   public static void AugXisFree() {
      // 创建一个窗口实例
      JFrame frame = new JFrame("AugustusX is Free");
      frame.setSize(300, 200);
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 防止用户关闭窗口
      frame.setLocationRelativeTo(null);


      JLabel label = new JLabel("AugustusX is Free",SwingConstants.CENTER);
      label.setHorizontalTextPosition(SwingConstants.CENTER);
      label.setVerticalTextPosition(SwingConstants.CENTER);


      frame.add(label);

      // 创建一个计时器，3秒后执行关闭窗口的动作
      Timer timer = new Timer(5000, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // 关闭窗口
            frame.dispose();
            System.out.println("Remind that AugustusX is Free!");
         }
      });

      // 显示窗口
      frame.setVisible(true);

      // 启动计时器
      timer.start();
   }
   public static void main2(String[] args){
      System.setProperty("java.net.preferIPv4Stack", "true");
      OptionParser optionparser = new OptionParser();
      optionparser.allowsUnrecognizedOptions();
      optionparser.accepts("demo");
      optionparser.accepts("fullscreen");
      optionparser.accepts("checkGlErrors");
      OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
      OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
      OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
      OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
      OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
      OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
      OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
      OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
      OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
      OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
      OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
      OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
      OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
      OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
      OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
      OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
      OptionSpec<String> optionspec19 = optionparser.nonOptions();
      OptionSet optionset = optionparser.parse(args);
      List<String> list = optionset.valuesOf(optionspec19);
      if (!list.isEmpty()) {
         System.out.println("Completely ignored arguments: " + list);
      }

      String s = optionset.valueOf(optionspec5);
      Proxy proxy = Proxy.NO_PROXY;
      if (s != null) {
         try {
            proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, optionset.valueOf(optionspec6)));
         } catch (Exception var46) {
         }
      }

      final String s1 = optionset.valueOf(optionspec7);
      final String s2 = optionset.valueOf(optionspec8);
      if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
         Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(s1, s2.toCharArray());
            }
         });
      }

      int i = optionset.valueOf(optionspec13);
      int j = optionset.valueOf(optionspec14);
      boolean flag = optionset.has("fullscreen");
      boolean flag1 = optionset.has("checkGlErrors");
      boolean flag2 = optionset.has("demo");
      String s3 = optionset.valueOf(optionspec12);
      Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
      PropertyMap propertymap = gson.fromJson(optionset.valueOf(optionspec15), PropertyMap.class);
      PropertyMap propertymap1 = gson.fromJson(optionset.valueOf(optionspec16), PropertyMap.class);
      File file1 = optionset.valueOf(optionspec2);
      File file2 = optionset.has(optionspec3) ? optionset.valueOf(optionspec3) : new File(file1, "assets/");
      File file3 = optionset.has(optionspec4) ? optionset.valueOf(optionspec4) : new File(file1, "resourcepacks/");
      String s4 = optionset.has(optionspec10) ? optionspec10.value(optionset) : optionspec9.value(optionset);
      String s5 = optionset.has(optionspec17) ? optionspec17.value(optionset) : null;
      String s6 = optionset.valueOf(optionspec);
      Integer integer = optionset.valueOf(optionspec1);
      Session session = new Session(optionspec9.value(optionset), s4, optionspec11.value(optionset), optionspec18.value(optionset));
      GameConfiguration gameconfiguration = new GameConfiguration(
         new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy),
         new GameConfiguration.DisplayInformation(i, j, flag, flag1),
         new GameConfiguration.FolderInformation(file1, file3, file2, s5),
         new GameConfiguration.GameInformation(flag2, s3),
         new GameConfiguration.ServerInformation(s6, integer)
      );
      Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
         @Override
         public void run() {
            Minecraft.stopIntegratedServer();
         }
      });
      Thread.currentThread().setName("Client thread");
      new Minecraft(gameconfiguration).run();

   }

   private static boolean isNullOrEmpty(String str) {
      return str != null && !str.isEmpty();
   }



   public static void Verify(String username, String password) {
      user = username;
      String up = username+":"+password;
      System.out.println("Starting the Verification...");
      String userHWID = getHWID();
      try {

         // 发起请求获取允许的 HWID
         List<String> allowedHWIDs = getAllowedHWIDs();

          // 获取用户的 HWID


         // 遍历比对用户的 HWID 和允许的 HWID
         for (int i = 0; i <= allowedHWIDs.size(); i++) {
            String allowedHWID_user_password = allowedHWIDs.get(i);

            if (Objects.equals(getRegCode(up,userHWID), allowedHWID_user_password)) {
               allowed = true;
               break;
            }
         }



      } catch (Exception e) {
         //e.printStackTrace();
      }
      if (!allowed){
         // HWID 不符合检测为非法用户或未激活用户自动打印 HWID
         System.err.println("Verification Failed!");
         System.err.println("Username: "+username);
         System.err.println("Password: "+password);
         System.err.println("HWID: "+userHWID);
         System.err.println("RegisterCode: "+getRegCode(up,userHWID));
         System.err.println("Please Contact Your Administrator to Register!");
         showCopyableDialog(getRegCode(up,userHWID));
         System.exit(0);
      }
   }
   private static void showCopyableDialog(String regCode) {
      // 创建一个可复制文本的对话框
      JDialog dialog = new JDialog(null, "Verification Failed!", Dialog.ModalityType.APPLICATION_MODAL);
      dialog.setLayout(new BorderLayout());
      JTextField messageField = new JTextField(regCode);
      messageField.setEditable(false); // 设置为不可编辑但可选中文本
      messageField.setCaretPosition(0); // 将光标移动到文本开头
      messageField.selectAll(); // 选中所有文本

      // 添加复制按钮
      JButton copyButton = new JButton("Copy RegCode");
      copyButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // 将文本字段的内容复制到剪贴板
            messageField.selectAll();
            messageField.copy();
            JOptionPane.showMessageDialog(dialog, "RegCode Copied！");
            System.exit(0);
         }
      });

      // 添加关闭按钮
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            dialog.dispose(); // 关闭对话框
            System.exit(0);
         }
      });

      // 创建底部按钮面板
      JPanel buttonPanel = new JPanel();
      buttonPanel.add(copyButton);
      buttonPanel.add(closeButton);

      // 将文本字段和按钮面板添加到对话框中
      dialog.add(messageField, BorderLayout.CENTER);
      dialog.add(buttonPanel, BorderLayout.SOUTH);

      dialog.pack(); // 根据组件大小自动调整对话框大小
      dialog.setLocationRelativeTo(null); // 相对于当前窗口居中显示
      dialog.setVisible(true); // 显示对话框
   }
   public static String getRegCode(String up, String hwid)  {
      String cbI = up+hwid;
       MessageDigest md = null;
       try {
           md = MessageDigest.getInstance("MD5");
       } catch (NoSuchAlgorithmException e) {
           throw new RuntimeException(e);
       }
       md.update(cbI.getBytes());
      byte[] hash = md.digest();

      // 将哈希值转换为十六进制字符串作为HWID
      StringBuilder sb = new StringBuilder();
      for (byte b : hash) {
         sb.append(String.format("%02x", b));
      }
      return sb.toString();
   }

   public static String getHWID() {
      String hwid = "";
      try {

         // 获取系统信息并计算MD5哈希值
         String os = System.getProperty("os.name");
         String user = System.getProperty("user.name");
         String arch = System.getProperty("os.arch");


         String biosVendor = "idk";
         String biosVersion = "Eithcer";



         String combinedInfo = "cra78256g2c357fvqhvhvha8vd8nvpowba8t78v7ervbe7rvwe67vr6ew7t"+os +"javaversio"+"2c7kc6v2hgj"+"cgba646"+user+"4bcj4785"+ "114514" + arch + "191364c" + "cnuycwa536523kl5c5387chga7858donotfuckingcrack486ch87536vfvtwvwevw66623";
         MessageDigest md = MessageDigest.getInstance("MD5");
         md.update(combinedInfo.getBytes());
         byte[] hash = md.digest();

         // 将哈希值转换为十六进制字符串作为HWID
         StringBuilder sb = new StringBuilder();
         for (byte b : hash) {
            sb.append(String.format("%02x", b));
         }
         hwid = sb.toString();
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();





      }
      return hwid;
   }

   private static List<String> getAllowedHWIDs() throws Exception {
      URL url = new URL("https://gitee.com/gghhddffggg/augustus-x-verify/raw/master/index.html");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      if (conn.getResponseCode() != 200) {
         System.out.println("Unable to Connect Gitee. Please Check Your Internet Connection.");
         JOptionPane.showMessageDialog(null, "Unable to Connect Gitee. Please Check Your Internet Connection.");
         System.exit(0);
         return null;
      }



      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      List<String> allowedHWIDs = new ArrayList<>();
      String line;
      while ((line = reader.readLine()) != null) {
         allowedHWIDs.add(line);
      }

      reader.close();
      return allowedHWIDs;
   }


   public static <T> T[] concat(T[] first, T[] second)
   {
      T[] result = Arrays.copyOf(first, first.length + second.length);
      System.arraycopy(second, 0, result, first.length, second.length);
      return result;
   }



}

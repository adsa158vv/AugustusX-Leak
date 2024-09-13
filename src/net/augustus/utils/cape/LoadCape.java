package net.augustus.utils.cape;

import net.augustus.ui.GuiIngameHook;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Genius
 * @since 2024/5/1 14:17
 * IntelliJ IDEA
 */

public class LoadCape
{
    public static ResourceLocation getResourceLocation(String name) {
        Path dynamicDir = Paths.get("assets/minecraft/textures/dynamic");

        return new ResourceLocation("textures/dynamic/" + name + ".png");

    }

    public static String[] getCapes() {
        try {
            // 获取动态纹理目录路径
            Path dynamicDir = Paths.get(GuiIngameHook.class.getProtectionDomain().getCodeSource().getLocation().toURI()).resolve("assets/minecraft/textures/dynamic");
            if (Files.exists(dynamicDir)) {
                // 清除动态纹理目录中的所有图片文件
                Files.list(dynamicDir)
                        .filter(path -> path.toString().toLowerCase().endsWith(".png"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } else {
                Files.createDirectories(dynamicDir);
            }

            // 从jar包中复制图片文件到动态纹理目录
            Path dir = Paths.get("AugustusX/capes");
            if (Files.exists(dir)) {
                Files.list(dir)
                        .filter(path -> path.toString().toLowerCase().endsWith(".png"))
                        .forEach(path -> {
                            try (InputStream is = Files.newInputStream(path)) {
                                Path destination = dynamicDir.resolve(path.getFileName());
                                Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }

            // 返回动态纹理目录中的所有图片文件名（不含扩展名）
            return Files.list(dynamicDir)
                    .filter(path -> path.toString().toLowerCase().endsWith(".png"))
                    .map(path -> path.getFileName().toString().replace(".png", ""))
                    .toArray(String[]::new);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return new String[]{"Vanilla"};
        }
    }


}

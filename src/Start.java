import net.minecraft.client.main.Main;

import java.util.Arrays;

public class Start


{
    public static void main(String[] args) throws InterruptedException {
        Main.shouldverify = true;
        Main.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}","--username","AbitofWither"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        Main.shouldverify = true;
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}

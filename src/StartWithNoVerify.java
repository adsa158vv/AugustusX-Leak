import net.minecraft.client.main.Main;

import java.util.Arrays;

public class StartWithNoVerify

    //This is a Start File without Verify. ONLY FOR DEVELOPMENT!
{
    public static void main(String[] args) throws InterruptedException {
        Main.shouldverify = false;
        Main.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}","--username","AbitofWither"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        Main.shouldverify = false;
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}

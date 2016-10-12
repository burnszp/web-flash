package cn.enilu.flash.core.util;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ImageUtilTest {

    @Test
    public void testResize() {
        String from = "src/test/resources/avatar.jpg";
        if (new File("zzzhc-base-core").isDirectory()) {
            from = "zzzhc-base-core/" + from;
        }

        for (int wh[] : new int[][]{{100, 80}, {80, 100}}) {
            int width = wh[0];
            int height = wh[1];
            String to = width + "x" + height + ".png";
            ImageUtil.resize(from, to, width, height, "#fff");

            ImageUtil.IdentifyInfo info = ImageUtil.identify(to);
            assertEquals(width, info.getWidth());
            assertEquals(height, info.getHeight());
        }
    }
}

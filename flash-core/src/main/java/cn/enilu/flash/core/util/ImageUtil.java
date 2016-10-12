package cn.enilu.flash.core.util;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImageUtil {

    @SuppressWarnings("serial")
    public static class ImageConvertException extends RuntimeException {
        public ImageConvertException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @SuppressWarnings("serial")
    public static class ImageIdentifyException extends RuntimeException {
        public ImageIdentifyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class IdentifyInfo {
        private final String format;
        private final int width;
        private final int height;

        public IdentifyInfo(String format, int width, int height) {
            this.format = format;
            this.width = width;
            this.height = height;
        }

        public String getFormat() {
            return format;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public String toString() {
            return "IdentifyInfo{" +
                    "format='" + format + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    private static final Pattern IDENTIFY_INFO_PATTERN = Pattern.compile("^(\\d+)x(\\d+)\\s+(.*)$");

    public static final IdentifyInfo UNKNOWN_INFO = new IdentifyInfo("unknown", 0, 0);

    private ImageUtil() {
    }

    public static IdentifyInfo identify(String path) {
        IMOperation op = new IMOperation();
        op.format("%wx%h %m");
        op.addImage(path);

        IdentifyCmd cmd = new IdentifyCmd();
        try {
            ArrayListOutputConsumer consumer = new ArrayListOutputConsumer();
            cmd.setOutputConsumer(consumer);
            cmd.run(op);

            List<String> output = consumer.getOutput();
            if (output.isEmpty()) {
                return UNKNOWN_INFO;
            }

            Matcher m = IDENTIFY_INFO_PATTERN.matcher(output.get(0));
            if (m.find()) {
                String format = m.group(3);
                int width = Integer.parseInt(m.group(1));
                int height = Integer.parseInt(m.group(2));
                return new IdentifyInfo(format, width, height);
            }

            return UNKNOWN_INFO;
        } catch (Exception e) {
            throw new ImageIdentifyException("run identify failed", e);
        }
    }

    public static void resize(String from, String to, int width, int height) {
        IMOperation op = new IMOperation();
        op.addImage(from);
        op.resize(width, height);
        op.addImage(to);

        runConvert(op);
    }

    public static void resizeWithWhitePadding(String from, String to, int width, int height) {
        resize(from, to, width, height, "#FFFFFF", false);
    }

    public static void resize(String from, String to, int width, int height, String paddingColor) {
        resize(from, to, width, height, paddingColor, false);
    }

    public static void resize(String from, String to, int width, int height, String paddingColor, boolean transparent) {
        IMOperation op = new IMOperation();
        op.size(width, height).addImage("xc:" + paddingColor);

        op.addImage(from);
        op.resize(width, height);
        op.gravity("center");
        if (transparent) {
            op.transparent(paddingColor);//背景透明，只对png有效
        }

        op.composite();

        op.addImage(to);

        runConvert(op);
    }

    public static void crop(String from, String to, int x, int y, int width,
                            int height) {
        IMOperation op = new IMOperation();
        op.addImage(from);
        op.crop(width, height, x, y);
        op.addImage(to);

        runConvert(op);
    }

    public static void crop(String from, String to, int x, int y, int width,
                            int height, int resizeWidth, int resizeHeight) {
        IMOperation op = new IMOperation();
        op.addImage(from);
        op.crop(width, height, x, y);
        op.resize(resizeWidth, resizeHeight);
        op.addImage(to);

        runConvert(op);
    }

    private static void runConvert(IMOperation op) {
        ConvertCmd convert = new ConvertCmd();
        try {
            convert.run(op);
        } catch (Exception e) {
            throw new ImageConvertException("convert failed, op="
                    + op.toString(), e);
        }
    }

    public static void main(String[] args) {
        String from = "cctv1.jpg";
        /*for (int wh[] : new int[][]{{130, 120}, {120, 130}}) {
            int width = wh[0];
            int height = wh[1];
            String to = "cctv1-" + width + "x" + height + ".png";
            resize(from, to, width, height);
        }*/
        from = "/Users/wangyong/Documents/image/3.png";
        ImageUtil.IdentifyInfo info = ImageUtil.identify(from);
        System.out.println(info.getWidth());
    }

}

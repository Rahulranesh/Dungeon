import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/** Development-only generator for the original 16px Realmfall Thorn Crawler. */
public class create_thorn_crawler {
    private static final int ROOT_SHADOW = 0xFF211A16;
    private static final int BARK = 0xFF3A2920;
    private static final int BARK_MID = 0xFF604333;
    private static final int BARK_HIGHLIGHT = 0xFF80604A;
    private static final int VEG_SHADOW = 0xFF18351F;
    private static final int FOREST_GREEN = 0xFF285A32;
    private static final int MOSS = 0xFF4A7A42;
    private static final int LEAF = 0xFF6E9854;
    private static final int CORRUPTION = 0xFF38203F;
    private static final int VIOLET = 0xFF69456F;
    private static final int EYES = 0xFF86C7A1;

    private static void pixel(BufferedImage image, int frame, int x, int y, int color) {
        if (x >= 0 && x < 16 && y >= 0 && y < 16) image.setRGB(frame * 16 + x, y, color);
    }

    private static void rect(BufferedImage image, int frame, int x, int y, int w, int h, int color) {
        for (int yy = y; yy < y + h; yy++) for (int xx = x; xx < x + w; xx++) pixel(image, frame, xx, yy, color);
    }

    private static void crawler(BufferedImage image, int frame, int shift, boolean thrust, boolean collapse) {
        if (collapse) {
            rect(image, frame, 5, 11, 6, 2, ROOT_SHADOW);
            rect(image, frame, 7, 10, 3, 1, FOREST_GREEN);
            pixel(image, frame, 6, 13, MOSS);
            pixel(image, frame, 9, 13, CORRUPTION);
            return;
        }

        int left = 2 + shift;
        int right = thrust ? 15 : 13 + shift;
        rect(image, frame, left + 2, 7, Math.max(7, right - left - 2), 4, ROOT_SHADOW);
        rect(image, frame, left + 3, 6, Math.max(6, right - left - 4), 4, BARK);
        rect(image, frame, left + 5, 6, 3, 2, BARK_MID);
        pixel(image, frame, left + 6, 6, BARK_HIGHLIGHT);
        rect(image, frame, left + 2, 10, 8, 1, FOREST_GREEN);
        pixel(image, frame, left + 4, 9, MOSS);
        pixel(image, frame, left + 9, 8, LEAF);

        // Uneven thorns and vines form the back silhouette.
        pixel(image, frame, left + 3, 5, FOREST_GREEN);
        pixel(image, frame, left + 3, 4, MOSS);
        pixel(image, frame, left + 6, 5, BARK_MID);
        pixel(image, frame, left + 6, 3, BARK);
        pixel(image, frame, left + 7, 4, BARK_HIGHLIGHT);
        pixel(image, frame, left + 10, 5, FOREST_GREEN);
        pixel(image, frame, left + 10, 4, MOSS);
        pixel(image, frame, left + 11, 6, LEAF);

        // Root-like underside.
        pixel(image, frame, left + 1, 11, BARK);
        pixel(image, frame, left + 2, 12, BARK_MID);
        pixel(image, frame, left + 4, 11, VEG_SHADOW);
        pixel(image, frame, left + 5, 12, FOREST_GREEN);
        pixel(image, frame, left + 7, 11, BARK);
        pixel(image, frame, left + 8, 12, BARK_MID);
        pixel(image, frame, left + 10, 11, VEG_SHADOW);
        pixel(image, frame, left + 11, 12, FOREST_GREEN);

        // Eyes and corruption accents.
        pixel(image, frame, thrust ? 11 : left + 9, 8, EYES);
        pixel(image, frame, thrust ? 13 : left + 10, 8, EYES);
        pixel(image, frame, left + 5, 8, CORRUPTION);
        pixel(image, frame, left + 8, 9, VIOLET);
        if (thrust) {
            pixel(image, frame, 14, 7, BARK_HIGHLIGHT);
            pixel(image, frame, 15, 6, MOSS);
            pixel(image, frame, 14, 10, FOREST_GREEN);
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedImage image = new BufferedImage(224, 16, BufferedImage.TYPE_INT_ARGB);
        crawler(image, 0, 0, false, false);
        crawler(image, 1, 1, false, false);
        crawler(image, 2, 0, false, false);
        crawler(image, 3, 0, false, false);
        crawler(image, 4, 1, false, false);
        crawler(image, 5, 0, false, false);
        crawler(image, 6, 1, false, false);
        crawler(image, 7, 0, false, false);
        crawler(image, 8, 0, true, false);
        crawler(image, 9, 1, false, false);
        crawler(image, 10, 0, false, false);
        crawler(image, 11, 0, false, true);
        crawler(image, 12, 0, false, true);
        crawler(image, 13, 0, false, true);
        File output = new File("core/src/main/assets/sprites/thorn_crawler.png");
        ImageIO.write(image, "png", output);
        BufferedImage written = ImageIO.read(output);
        Set<Integer> colors = new HashSet<>();
        boolean transparent = false;
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 224; x++) {
                int pixel = written.getRGB(x, y);
                if (((pixel >>> 24) & 0xFF) == 0) transparent = true;
                else colors.add(pixel);
            }
        }
        for (int frame = 0; frame < 14; frame++) {
            boolean visible = false;
            for (int y = 0; y < 16; y++) for (int x = frame * 16; x < frame * 16 + 16; x++)
                visible |= ((written.getRGB(x, y) >>> 24) & 0xFF) != 0;
            if (!visible) throw new IllegalStateException("Empty frame " + frame);
        }
        System.out.println("dimensions=" + written.getWidth() + "x" + written.getHeight());
        System.out.println("frames=14");
        System.out.println("transparent_background=" + transparent);
        System.out.println("unique_nontransparent_colors=" + colors.size());
    }
}

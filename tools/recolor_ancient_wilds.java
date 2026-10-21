import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Development-only, deterministic recolor utility for Ancient Wilds. */
public class recolor_ancient_wilds {

    private record Rect(int x, int y, int w, int h, boolean grass) {
        boolean contains(int px, int py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }

    private static final List<Rect> ALLOWLIST = List.of(
            new Rect(0, 0, 16, 16, false),
            new Rect(16, 0, 16, 16, false),
            new Rect(96, 0, 16, 16, false),
            new Rect(112, 0, 16, 16, false),
            new Rect(192, 0, 16, 16, false), // FLOOR_ALT_2
            new Rect(32, 0, 16, 16, true),
            new Rect(128, 0, 16, 16, true),
            new Rect(32, 64, 16, 16, true),
            new Rect(48, 64, 16, 16, true),
            new Rect(80, 64, 16, 16, true),
            new Rect(96, 64, 16, 16, true),
            new Rect(32, 112, 16, 16, true),
            new Rect(48, 112, 16, 16, true),
            new Rect(80, 112, 16, 16, true),
            new Rect(96, 112, 16, 16, true),
            new Rect(32, 224, 16, 16, true),
            new Rect(48, 224, 16, 16, true),
            new Rect(80, 224, 16, 16, true),
            new Rect(96, 224, 16, 16, true)
    );

    private static boolean allowed(int x, int y) {
        return ALLOWLIST.stream().anyMatch(r -> r.contains(x, y));
    }

    private static boolean grass(int x, int y) {
        return ALLOWLIST.stream().anyMatch(r -> r.grass && r.contains(x, y));
    }

    private static int paletteValue(int value, int[] stops) {
        int segment = Math.min(2, value * 3 / 256);
        int start = segment * 255 / 3;
        int end = (segment + 1) * 255 / 3;
        int amount = (value - start) * 255 / (end - start);
        return stops[segment] + (stops[segment + 1] - stops[segment]) * amount / 255;
    }

    private static int textureOffset(int x, int y, boolean isGrass) {
        int hash = x * 374761393 + y * 668265263 + (isGrass ? 127 : 0);
        hash = (hash ^ (hash >>> 13)) * 1274126177;
        return (hash & 7) - 3;
    }

    private static int recolor(int argb, boolean isGrass, int x, int y) {
        int alpha = (argb >>> 24) & 0xFF;
        if (alpha == 0) return argb;

        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;
        int luminance = Math.round(0.299f * r + 0.587f * g + 0.114f * b);

        int[] red = isGrass ? new int[]{24, 40, 62, 91} : new int[]{38, 58, 82, 112};
        int[] green = isGrass ? new int[]{46, 72, 96, 126} : new int[]{34, 50, 70, 96};
        int[] blue = isGrass ? new int[]{25, 35, 47, 65} : new int[]{24, 31, 43, 61};
        int variation = textureOffset(x, y, isGrass);
        int nr = Math.max(0, Math.min(255, paletteValue(luminance, red) + variation));
        int ng = Math.max(0, Math.min(255, paletteValue(luminance, green) + variation));
        int nb = Math.max(0, Math.min(255, paletteValue(luminance, blue) + variation));
        return (alpha << 24) | (nr << 16) | (ng << 8) | nb;
    }

    public static void main(String[] args) throws Exception {
        File baseFile = new File("core/src/main/assets/environment/tiles_ancient_wilds_base.png");
        File outputFile = new File("core/src/main/assets/environment/tiles_ancient_wilds.png");
        BufferedImage base = ImageIO.read(baseFile);
        if (base.getWidth() != 256 || base.getHeight() != 256) throw new IllegalStateException("Unexpected dimensions");

        BufferedImage output = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        int changed = 0;
        int changedGround = 0;
        int changedGrass = 0;
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                int original = base.getRGB(x, y);
                int value = allowed(x, y) ? recolor(original, grass(x, y), x, y) : original;
                output.setRGB(x, y, value);
                if (value != original) {
                    changed++;
                    if (grass(x, y)) changedGrass++; else changedGround++;
                }
            }
        }

        if (!ImageIO.write(output, "png", outputFile)) throw new IllegalStateException("PNG writer unavailable");

        BufferedImage written = ImageIO.read(outputFile);
        int outsideChanges = 0;
        int alphaChanges = 0;
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                int before = base.getRGB(x, y);
                int after = written.getRGB(x, y);
                if (((before >>> 24) & 0xFF) != ((after >>> 24) & 0xFF)) alphaChanges++;
                if (!allowed(x, y) && before != after) outsideChanges++;
            }
        }
        if (outsideChanges != 0 || alphaChanges != 0) {
            Files.copy(baseFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            throw new IllegalStateException("Validation failed; output restored from backup");
        }
        System.out.println("dimensions=256x256");
        System.out.println("changed_pixels=" + changed);
        System.out.println("changed_ground_pixels=" + changedGround);
        System.out.println("changed_grass_pixels=" + changedGrass);
        System.out.println("outside_allowlist_changes=0");
        System.out.println("alpha_changes=0");
    }
}

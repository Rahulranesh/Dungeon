#!/usr/bin/env python3
"""Generate Google Play Store graphics for Solo Levelling Dungeon
from the game's own art assets.

Outputs (play-store-assets/):
  app-icon-512x512.png          - store icon
  feature-graphic-1024x500.png  - feature banner
  phone-screenshot-N-1080x1920.png (x5)
"""

import os
import random
from PIL import Image, ImageDraw, ImageEnhance, ImageFont, ImageFilter, ImageOps

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(ROOT, "core", "src", "main", "assets")
OUT = os.path.join(ROOT, "play-store-assets")
os.makedirs(OUT, exist_ok=True)

FONT_PATH = os.path.join(ASSETS, "fonts", "pixel_font.ttf")

GOLD = (233, 196, 106)
GOLD_DIM = (176, 146, 78)
INK = (232, 232, 240)
MUTED = (150, 150, 168)
BG = (10, 10, 15)


def font(size):
    return ImageFont.truetype(FONT_PATH, size)


def fit_font(text, max_w, start, min_size=18):
    size = start
    while size > min_size and font(size).getbbox(text)[2] > max_w:
        size -= 2
    return font(size)


def center_text(draw, text, cx, y, fnt, fill):
    w = fnt.getbbox(text)[2]
    draw.text((cx - w / 2, y), text, font=fnt, fill=fill)


def glow_text(base, text, cx, y, fnt, fill, glow, radius=10, strength=190):
    layer = Image.new("RGBA", base.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    w = fnt.getbbox(text)[2]
    d.text((cx - w / 2, y), text, font=fnt, fill=glow + (strength,))
    layer = layer.filter(ImageFilter.GaussianBlur(radius))
    base.alpha_composite(layer)
    d2 = ImageDraw.Draw(base)
    d2.text((cx - w / 2, y), text, font=fnt, fill=fill)


def vignette(img, strength=120):
    w, h = img.size
    mask = Image.new("L", (w, h), 0)
    md = ImageDraw.Draw(mask)
    md.ellipse([-w * 0.35, -h * 0.25, w * 1.35, h * 1.25], fill=255)
    mask = mask.filter(ImageFilter.GaussianBlur(min(w, h) // 8))
    black = Image.new("RGBA", (w, h), (0, 0, 0, 255))
    img.paste(black, (0, 0), Image.eval(mask, lambda v: int((255 - v) * strength / 255)))
    return img


def cover(img, tw, th):
    """Scale + center-crop to exactly tw x th."""
    w, h = img.size
    s = max(tw / w, th / h)
    img = img.resize((int(w * s + 0.5), int(h * s + 0.5)), Image.LANCZOS)
    w, h = img.size
    l = (w - tw) // 2
    t = (h - th) // 2
    return img.crop((l, t, l + tw, t + th))


def load_logo():
    """Crop the game wordmark out of interfaces/banners.png.
    Rows 15-87 hold SOLO / LEVELLING / DUNGEON; everything right of x=133
    is sparkle decoration and rows below 87 are stray underline pixels."""
    src = Image.open(os.path.join(ASSETS, "interfaces", "banners.png")).convert("RGBA")
    crop = src.crop((0, 15, 133, 88))
    bbox = crop.getchannel("A").getbbox()
    return crop.crop(bbox)


def load_avatars(count=6):
    """Extract the class avatars from the 256x32 avatar sheet (alpha-segmented)."""
    sheet = Image.open(os.path.join(ASSETS, "sprites", "avatars.png")).convert("RGBA")
    alpha = sheet.getchannel("A")
    w, h = sheet.size
    cols = [sum(1 for y in range(h) if alpha.getpixel((x, y)) > 20) for x in range(w)]
    runs, start = [], None
    for x, c in enumerate(cols + [0]):
        if c > 0 and start is None:
            start = x
        elif c == 0 and start is not None:
            runs.append((start, x - 1))
            start = None
    out = []
    for (x0, x1) in runs[:count]:
        fr = sheet.crop((x0, 0, x1 + 1, h))
        bbox = fr.getchannel("A").getbbox()
        out.append(fr.crop(bbox))
    return out


# ---------------------------------------------------------------- icon
def make_icon():
    """Generate an app icon from the game's warrior class avatar: the big
    pixel hero on a dark purple radial glow with a gold/purple rim. Writes
    the 512 store icon plus every launcher density, adaptive and monochrome
    layer (equivalent of flutter_launcher_icons for this project)."""
    S = 1024

    # background: dark radial purple glow + faint pixel dust
    bg = Image.new("RGBA", (S, S), (11, 8, 18, 255))
    grad = ImageOps.invert(Image.radial_gradient("L")).resize((S, S))
    glow_col = Image.new("RGBA", (S, S), (54, 33, 100, 255))
    bg.paste(glow_col, (0, 0), grad.point(lambda v: int(v * 0.9)))
    d0 = ImageDraw.Draw(bg)
    rnd = random.Random(11)
    for _ in range(70):
        x, y = rnd.randrange(S), rnd.randrange(S)
        alpha = rnd.randint(8, 42)
        col = rnd.choice([(122, 92, 255), (89, 217, 140), (233, 196, 106)])
        sz = rnd.choice([3, 4, 6])
        d0.rectangle([x, y, x + sz, y + sz], fill=col + (alpha,))
    bg = vignette(bg, 110)

    # warrior avatar, enlarged with crisp nearest-neighbour pixels
    art = load_avatars()[0]
    target_h = int(S * 0.58)
    scale = target_h / art.height
    tw, th = max(1, int(art.width * scale)), target_h
    sprite = art.resize((tw, th), Image.NEAREST)
    glyph_mask = sprite.getchannel("A")

    outline_mask = glyph_mask.filter(ImageFilter.MaxFilter(21))
    ow, oh = outline_mask.size

    # foreground layer (transparent) with purple halo + dark outline + hero
    fg = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    cx, cy = S // 2, S // 2

    halo = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    halo.paste((122, 84, 255, 200), (cx - ow // 2, cy - oh // 2), outline_mask)
    fg.alpha_composite(halo.filter(ImageFilter.GaussianBlur(26)))

    dark = Image.new("RGBA", (ow, oh), (22, 14, 34, 255))
    fg.paste(dark, (cx - ow // 2, cy - oh // 2), outline_mask)
    fg.alpha_composite(sprite, (cx - tw // 2, cy - th // 2))

    sd = ImageDraw.Draw(fg)
    right = cx + ow // 2 + 18
    for i, (sz, dy) in enumerate([(12, -110), (9, -60), (7, -10)]):
        sd.rectangle([right + i * 10, cy + dy, right + i * 10 + sz, cy + dy + sz],
                     fill=(89, 217, 140, 235))
    left = cx - ow // 2 - 30
    for i, (sz, dy) in enumerate([(8, -50), (6, -8)]):
        sd.rectangle([left - i * 9, cy + dy, left - i * 9 + sz, cy + dy + sz],
                     fill=(89, 217, 140, 200))

    # legacy square icon: backdrop + emblem + gold/purple rim
    square = bg.copy()
    square.alpha_composite(fg)
    rim = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    inset, radius = 24, 148
    ImageDraw.Draw(rim).rounded_rectangle([inset, inset, S - inset, S - inset],
                                          radius=radius, outline=(138, 92, 255, 210), width=18)
    square.alpha_composite(rim.filter(ImageFilter.GaussianBlur(11)))
    ImageDraw.Draw(square).rounded_rectangle(
        [inset + 3, inset + 3, S - inset - 3, S - inset - 3],
        radius=radius - 3, outline=(228, 188, 94, 235), width=7)

    square.resize((512, 512), Image.LANCZOS).convert("RGB").save(
        os.path.join(OUT, "app-icon-512x512.png"), "PNG", optimize=True)
    print("icon: 512x512")

    # white silhouette for themed (monochrome) icons
    mono_layer = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    mono_layer.paste(Image.new("RGBA", (tw, th), (255, 255, 255, 255)),
                     (cx - tw // 2, cy - th // 2), glyph_mask)

    densities = {"ldpi": 36, "mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
    adaptive = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}

    for base in (os.path.join(ROOT, "android", "res"),
                 os.path.join(ROOT, "android", "src", "main", "res")):
        for density, px in densities.items():
            d = os.path.join(base, f"mipmap-{density}")
            os.makedirs(d, exist_ok=True)
            square.resize((px, px), Image.LANCZOS).convert("RGB").save(
                os.path.join(d, "ic_launcher.png"), "PNG", optimize=True)
        for density, px in adaptive.items():
            d = os.path.join(base, f"mipmap-{density}")
            os.makedirs(d, exist_ok=True)
            fg.resize((px, px), Image.LANCZOS).save(
                os.path.join(d, "ic_launcher_foreground.png"), "PNG", optimize=True)
            bg.resize((px, px), Image.LANCZOS).convert("RGB").save(
                os.path.join(d, "ic_launcher_background.png"), "PNG", optimize=True)
            mono_layer.resize((px, px), Image.LANCZOS).save(
                os.path.join(d, "ic_launcher_monochrome.png"), "PNG", optimize=True)
        print(f"icons written to {os.path.relpath(base, ROOT)}")


# ------------------------------------------------------- feature graphic
def make_feature():
    W, H = 1024, 500
    bg = Image.open(os.path.join(ASSETS, "splashes", "rogue.jpg")).convert("RGB")
    img = cover(bg, W, H).convert("RGBA")

    # left-to-right darkening so the wordmark reads clearly
    grad = Image.new("L", (W, H), 0)
    gd = ImageDraw.Draw(grad)
    for x in range(W):
        gd.line([(x, 0), (x, H)], fill=int(200 * max(0.0, 1 - x / (W * 0.62))))
    black = Image.new("RGBA", (W, H), (5, 5, 9, 255))
    img.paste(black, (0, 0), grad)
    img = vignette(img, 90)

    logo = load_logo()
    lw = 400
    lh = int(logo.height * lw / logo.width)
    logo = logo.resize((lw, lh), Image.NEAREST)

    # soft drop shadow behind logo
    sh = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    sd = ImageDraw.Draw(sh)
    sd.bitmap((64 + 6, (H - lh) // 2 - 30 + 6), logo, fill=(0, 0, 0, 160))
    img.alpha_composite(sh.filter(ImageFilter.GaussianBlur(6)))
    img.alpha_composite(logo, (64, (H - lh) // 2 - 30))

    fnt = font(26)
    d = ImageDraw.Draw(img)
    d.text((68, (H - lh) // 2 + lh + 10), "TURN-BASED DUNGEON ROGUELIKE", font=fnt, fill=GOLD)

    # thin gold frame + corner ticks
    d.rectangle([10, 10, W - 11, H - 11], outline=GOLD_DIM + (140,), width=2)
    for (x, y, dx, dy) in [(10, 10, 1, 1), (W - 11, 10, -1, 1), (10, H - 11, 1, -1), (W - 11, H - 11, -1, -1)]:
        d.line([(x, y), (x + dx * 34, y)], fill=GOLD + (220,), width=4)
        d.line([(x, y), (x, y + dy * 34)], fill=GOLD + (220,), width=4)

    out = img.convert("RGB")
    out.save(os.path.join(OUT, "feature-graphic-1024x500.png"), "PNG", optimize=True)
    print("feature:", out.size)


# ----------------------------------------------------------- screenshots
SHOTS = [
    ("warrior.jpg", "DESCEND THE DUNGEON", "A turn-based dungeon roguelike RPG"),
    ("mage.jpg", "UNLEASH ARCANE POWER", "Master spells, wands and talents"),
    ("rogue.jpg", "LOOT. GEAR UP. SURVIVE.", "Every run is different"),
    ("huntress.jpg", "HUNT DEADLY BOSSES", "Face monstrous foes in the depths"),
    ("cleric.jpg", "PLAY THE NEW CLERIC", "Smite evil, mend wounds"),
]


def make_screenshots():
    W, H = 1080, 1920
    logo = load_logo()

    for i, (splash, head, sub) in enumerate(SHOTS, start=1):
        img = Image.new("RGBA", (W, H), BG + (255,))

        # subtle vertical gradient
        g = Image.new("L", (1, H))
        gd = ImageDraw.Draw(g)
        for y in range(H):
            gd.point((0, y), fill=int(26 + 14 * (y / H)))
        shade = Image.new("RGBA", (W, H), (18, 18, 28, 255))
        img.paste(shade, (0, 0), g.resize((W, H)))

        # wordmark
        lw = 520
        lh = int(logo.height * lw / logo.width)
        lg = logo.resize((lw, lh), Image.NEAREST)
        img.alpha_composite(lg, ((W - lw) // 2, 120))

        # art panel (full width, native aspect, subtle glow + gold rules)
        art = Image.open(os.path.join(ASSETS, "splashes", splash)).convert("RGB")
        art = art.resize((W, int(art.height * W / art.width)), Image.LANCZOS).convert("RGBA")
        ay = 470
        halo = Image.new("RGBA", (W, H), (0, 0, 0, 0))
        hd = ImageDraw.Draw(halo)
        hd.rectangle([-4, ay - 4, W + 4, ay + art.height + 4], fill=(90, 110, 200, 130))
        img.alpha_composite(halo.filter(ImageFilter.GaussianBlur(18)))
        img.alpha_composite(art, (0, ay))
        d = ImageDraw.Draw(img)
        d.rectangle([0, ay, W - 1, ay + art.height - 1], outline=(0, 0, 0, 200), width=2)
        d.line([(0, ay - 2), (W, ay - 2)], fill=GOLD_DIM + (200,), width=2)
        d.line([(0, ay + art.height + 1), (W, ay + art.height + 1)], fill=GOLD_DIM + (200,), width=2)

        # headline + subline
        f_head = fit_font(head, W - 150, 96, 40)
        glow_text(img, head, W // 2, 1120, f_head, INK, (120, 140, 255), radius=14)
        f_sub = fit_font(sub, W - 260, 44, 24)
        center_text(ImageDraw.Draw(img), sub, W // 2, 1235, f_sub, MUTED)

        # pixel class lineup from the in-game avatar sheet
        avatars = load_avatars()
        target_h = 116
        scaled = [a.resize((max(1, int(a.width * target_h / a.height)), target_h), Image.NEAREST) for a in avatars]
        gap = 22
        total_w = sum(a.width for a in scaled) + gap * (len(scaled) - 1)
        ax = (W - total_w) // 2
        for a in scaled:
            img.alpha_composite(a, (ax, 1382 + (target_h - a.height)))
            ax += a.width + gap

        # footer line
        f_foot = font(30)
        center_text(ImageDraw.Draw(img), "SOLO LEVELLING DUNGEON", W // 2, 1730, f_foot, GOLD)
        f_foot2 = font(22)
        center_text(ImageDraw.Draw(img), "ROGUELIKE RPG  /  SINGLE PLAYER", W // 2, 1785, f_foot2, MUTED)

        # corner ticks
        dd = ImageDraw.Draw(img)
        for (x, y, dx, dy) in [(36, 36, 1, 1), (W - 37, 36, -1, 1), (36, H - 37, 1, -1), (W - 37, H - 37, -1, -1)]:
            dd.line([(x, y), (x + dx * 40, y)], fill=GOLD_DIM + (140,), width=3)
            dd.line([(x, y), (x, y + dy * 40)], fill=GOLD_DIM + (140,), width=3)

        out = img.convert("RGB")
        path = os.path.join(OUT, f"phone-screenshot-{i}-1080x1920.png")
        out.save(path, "PNG", optimize=True)
        print(f"screenshot {i}:", out.size)


if __name__ == "__main__":
    make_icon()
    make_feature()
    make_screenshots()

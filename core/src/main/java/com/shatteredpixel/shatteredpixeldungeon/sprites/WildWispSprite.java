/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.watabou.noosa.Game;

/**
 * Animated sprite for the Wild Wisp featuring organic dual-harmonic floating,
 * emerald bioluminescence, and drifting nature motes.
 */
public class WildWispSprite extends CrystalWispSprite {

	private float floatBaseY = Float.NaN;
	private float sparkleTimer = 0f;

	public WildWispSprite() {
		super();
		// Enchanted forest emerald glow
		hardlight( 0.45f, 1.25f, 0.65f );
	}

	@Override
	protected int texOffset() {
		return 13; // Uses vibrant green wisp frames
	}

	@Override
	public int blood() {
		return 0xFF3DDA55;
	}

	@Override
	public void update() {
		super.update();

		if (!paused && curAnim != die) {
			if (Float.isNaN(floatBaseY)) floatBaseY = y;
			// Realistic organic harmonic floating
			float floatOffset = (float)Math.sin(Game.timeTotal * 3.4f) * 1.5f + (float)Math.cos(Game.timeTotal * 1.7f) * 0.6f;
			y = floatBaseY + floatOffset;
			shadowOffset = 0.2f - 0.5f * (floatOffset / 2.1f);

			// Periodic subtle emerald motes
			if (visible && (sparkleTimer -= Game.elapsed) <= 0) {
				sparkleTimer = 1.0f;
				Splash.at( center(), 0xFF3DDA55, 1 );
			}
		}
	}

	@Override
	public void die() {
		super.die();
		Splash.at( center(), blood(), 10 );
	}
}


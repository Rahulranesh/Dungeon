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
 * Custom animated sprite for the Corrupted Slime with fluid gelatinous oscillation,
 * realistic attack spring physics, and corrupted dark-violet hue.
 */
public class CorruptedSlimeSprite extends SlimeSprite {

	private float springX = 1f;
	private float springY = 1f;

	public CorruptedSlimeSprite() {
		super();
		// Corrupted dark violet hue with glowing luminescence
		hardlight( 0.65f, 0.35f, 0.95f );
	}

	@Override
	public int blood() {
		return 0xFF6A2082;
	}

	@Override
	public void attack( int cell ) {
		super.attack( cell );
		// Squashes low and springs forward into the target
		origin.set( width / 2f, height );
		springX = 1.30f;
		springY = 0.70f;
	}

	@Override
	public void update() {
		super.update();

		if (!paused && curAnim != die) {
			origin.set( width / 2f, height );
			// Smoothly damp attack/hit squash back to resting
			springX += (1f - springX) * Math.min(1f, Game.elapsed * 14f);
			springY += (1f - springY) * Math.min(1f, Game.elapsed * 14f);

			// Organic continuous jelly breathing/wobble
			float wobble = (float)Math.sin(Game.timeTotal * 5.0f) * 0.08f;
			scale.set( (1f + wobble) * springX, (1f - wobble) * springY );
		}
	}

	@Override
	public void die() {
		super.die();
		origin.set( width / 2f, height );
		scale.set( 1.35f, 0.5f );
		Splash.at( center(), blood(), 10 );
	}
}


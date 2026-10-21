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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;

/**
 * Animated sprite for the Thorn Crawler featuring predatory attack lunge physics
 * and dynamic recoiling.
 */
public class ThornCrawlerSprite extends MobSprite {

	public ThornCrawlerSprite() {
		super();

		texture( Assets.Sprites.THORN_CRAWLER );

		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		idle = new Animation( 5, true );
		idle.frames( frames, 0, 1, 2, 1 );

		run = new Animation( 16, true );
		run.frames( frames, 3, 4, 5, 6 );

		attack = new Animation( 14, false );
		attack.frames( frames, 7, 8, 9 );

		die = new Animation( 12, false );
		die.frames( frames, 10, 11, 12, 13 );

		play( idle );
	}

	@Override
	public int blood() {
		return 0xFF285A32;
	}

	@Override
	public void attack( int cell ) {
		super.attack( cell );
		// Predatory attack lunge: springs forward on strike
		origin.set( width / 2f, height );
		scale.set( 1.25f, 0.85f );
	}

	@Override
	public void update() {
		super.update();

		if (!paused && curAnim != die) {
			origin.set( width / 2f, height );
			// Smoothly relax lunge or hit recoil back to normal
			scale.x += (1f - scale.x) * Math.min(1f, Game.elapsed * 16f);
			scale.y += (1f - scale.y) * Math.min(1f, Game.elapsed * 16f);
		}
	}
}

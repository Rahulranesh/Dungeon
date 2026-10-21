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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WildWispSprite;
import com.watabou.utils.Random;

/**
 * A fragile magical spirit from the Ancient Wilds.
 */
public class WildWisp extends CrystalWisp {

	{
		spriteClass = WildWispSprite.class;

		HP = HT = 16;
		defenseSkill = 5;

		EXP = 3;
		maxLvl = 9;
	}

	public WildWisp() {
		super();
		// Explicitly assign WildWispSprite to override CrystalWisp's random color assignment
		spriteClass = WildWispSprite.class;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 3, 7 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 14;
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		if (sprite != null && sprite.visible) {
			Splash.at( sprite.center(), 0xFF3DDA55, 8 );
			CellEmitter.center( pos ).burst( LeafParticle.LEVEL_SPECIFIC, 5 );
		}
	}
}

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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CorruptedSlimeSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

/**
 * A stronger early-game variant of Slime for Realmfall.
 * Features caustic sludge attacks, rubbery damage resistance, and a dynamic death burst.
 */
public class CorruptedSlime extends Slime {

	{
		spriteClass = CorruptedSlimeSprite.class;
		HP = HT = 24;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 3, 6 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		// 20% chance to coat target in caustic ooze
		if (Random.Int(5) == 0) {
			Buff.affect( enemy, Ooze.class );
			if (enemy.sprite != null && enemy.sprite.visible) {
				Splash.at( enemy.sprite.center(), 0xFF6A2082, 5 );
				Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
			}
		}
		return damage;
	}

	@Override
	public void damage( int dmg, Object src ) {
		super.damage( dmg, src );
		// Spatters caustic droplets when taking substantial damage
		if (isAlive() && dmg >= 3 && sprite != null && sprite.visible) {
			Splash.at( sprite.center(), 0xFF6A2082, 4 );
		}
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		if (Dungeon.level != null && sprite != null && sprite.visible) {
			Splash.at( sprite.center(), 0xFF6A2082, 14 );
			Sample.INSTANCE.play( Assets.Sounds.PUFF );
		}
	}
}

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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThornCrawlerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

/**
 * A fast, aggressive melee creature from the Ancient Wilds.
 */
public class ThornCrawler extends Mob {

	{
		spriteClass = ThornCrawlerSprite.class;

		HP = HT = 20;
		defenseSkill = 5;
		baseSpeed = 2f;

		EXP = 3;
		maxLvl = 9;
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
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		if (enemy == Dungeon.hero && Random.Int(5) == 0) {
			Buff.affect( enemy, Roots.class, 2f );
			if (enemy.sprite != null && enemy.sprite.visible) {
				CellEmitter.bottom( enemy.pos ).burst( LeafParticle.GENERAL, 8 );
				Splash.at( enemy.sprite.center(), 0xFF285A32, 8 );
				Sample.INSTANCE.play( Assets.Sounds.PLANT );
			}
			GLog.w( Messages.get(ThornCrawler.class, "root_proc") );
		}
		return damage;
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		if (sprite != null && sprite.visible) {
			Splash.at( sprite.center(), 0xFF285A32, 10 );
			CellEmitter.center( pos ).burst( LeafParticle.LEVEL_SPECIFIC, 4 );
		}
	}
}

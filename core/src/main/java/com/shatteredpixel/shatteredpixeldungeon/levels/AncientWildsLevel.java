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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.SewerPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

/**
 * Solo Levelling Dungeon first region: The Ancient Wilds.
 * An overgrown subterranean forest reclaimed by primordial nature and corruption.
 */
public class AncientWildsLevel extends SewerLevel {

	{
		// High-contrast deep moss and mystical forest canopy lighting
		color1 = 0x244a30;
		color2 = 0x3a784c;
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_ANCIENT_WILDS;
	}

	@Override
	protected Painter painter() {
		return new SewerPainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 5)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.40f, 4)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	protected Class<?>[] trapClasses() {
		return Dungeon.depth == 1 ?
				new Class<?>[]{ WornDartTrap.class, GrippingTrap.class } :
				new Class<?>[]{
						GrippingTrap.class, ToxicTrap.class, OozeTrap.class, AlarmTrap.class,
						WornDartTrap.class, ChillingTrap.class, ShockingTrap.class,
						FlockTrap.class, SummoningTrap.class, TeleportationTrap.class, GatewayTrap.class
				};
	}

	@Override
	protected float[] trapChances() {
		return Dungeon.depth == 1 ?
				new float[]{ 3, 2 } :
				new float[]{
						4, 4, 3, 3,
						3, 2, 2,
						1, 1, 1, 1
				};
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		addWildVisuals(this, visuals);
		return visuals;
	}

	public static void addWildVisuals(Level level, Group group) {
		for (int i = 0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add(new WildSporeEmitter(i));
			}
		}
	}

	@Override
	public void destroy(int pos) {
		int terr = map[pos];
		if (terr == Terrain.REGION_DECO) {
			set(pos, Terrain.WATER);
			Splash.at(pos, 0xFF2E5D38, 10);
		} else if (terr == Terrain.REGION_DECO_ALT) {
			set(pos, Terrain.EMPTY_SP);
			Splash.at(pos, 0xFF2E5D38, 10);
		}
		// Skip SewerLevel.destroy barrel override so our custom splash applies
		RegularLevelSuperDestroy(pos);
	}

	private void RegularLevelSuperDestroy(int pos) {
		// Call RegularLevel's destroy behavior
		super.destroy(pos);
	}

	@Override
	public String tileName(int tile) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(AncientWildsLevel.class, "water_name");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(AncientWildsLevel.class, "region_deco_name");
			default:
				return super.tileName(tile);
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(AncientWildsLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(AncientWildsLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(AncientWildsLevel.class, "region_deco_desc");
			default:
				return super.tileDesc(tile);
		}
	}

	private static class WildSporeEmitter extends Emitter {

		private int pos;

		public WildSporeEmitter(int pos) {
			super();
			this.pos = pos;
			PointF p = DungeonTilemap.tileCenterToWorld(pos);
			pos(p.x, p.y, 8, 8);
			pour(LeafParticle.LEVEL_SPECIFIC, 3.5f);
		}

		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}
	}
}

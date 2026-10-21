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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuestGuide;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class QuestBanner extends Button {

	private NinePatch bg;
	private Image icon;
	private RenderedTextBlock text;

	private float updateTimer = 0;
	private String currentMsg = "";

	public QuestBanner() {
		super();
		setSize( 200, 16 );
		refreshObjective( true );
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		bg = Chrome.get( Chrome.Type.TOAST_TR );
		bg.hardlight( 0.0f, 0.2f, 0.35f );
		add( bg );

		icon = Icons.get( Icons.COMPASS );
		add( icon );

		text = PixelScene.renderTextBlock( 7 );
		text.hardlight( 0x00E5FF );
		add( text );
	}

	@Override
	protected void layout() {
		super.layout();

		if (bg != null) {
			bg.x = x;
			bg.y = y;
			bg.size( width, height );
		}

		if (icon != null) {
			icon.origin.set( 0, 0 );
			if (icon.height() > height - 4) {
				icon.scale.set( (height - 4f) / icon.height() );
			} else {
				icon.scale.set( 1f );
			}
			icon.x = x + 3;
			icon.y = y + (height - icon.height() * icon.scale.y) / 2f;
			PixelScene.align( icon );
		}

		if (text != null) {
			text.maxWidth( (int)(width - 24) );
			text.setPos( x + 20, y + (height - text.height()) / 2f );
			PixelScene.align( text );
		}
	}

	@Override
	public void update() {
		super.update();

		updateTimer -= Game.elapsed;
		if (updateTimer <= 0) {
			updateTimer = 0.2f;
			refreshObjective( false );
		}
	}

	@Override
	protected void onClick() {
		Sample.INSTANCE.play( Assets.Sounds.CLICK );
		GameScene.show( new WndQuestGuide() );
	}

	public void refreshObjective( boolean force ) {
		if (Dungeon.hero == null || !Dungeon.hero.isAlive() || Dungeon.level == null) {
			return;
		}

		String msg;
		Icons ic;
		int col;
		float bgR = 0f, bgG = 0.2f, bgB = 0.35f;

		// 1. Boss Level Check
		if (Dungeon.bossLevel()) {
			boolean bossAlive = false;
			for (Mob mob : Dungeon.level.mobs) {
				if (mob.alignment == Char.Alignment.ENEMY && mob.isAlive()) {
					bossAlive = true;
					break;
				}
			}
			if (bossAlive) {
				msg = "BOSS: Defeat the Beast to claim the Skeleton Key!";
				ic = Icons.SKULL;
				col = 0xFF5555;
				bgR = 0.45f; bgG = 0.05f; bgB = 0.05f;
			} else {
				msg = "VICTORY: Boss defeated! Collect key & descend.";
				ic = Icons.CHECKED;
				col = 0x44FFAA;
				bgR = 0.05f; bgG = 0.35f; bgB = 0.1f;
			}
		}
		// 2. Low Health Critical Warning
		else if (Dungeon.hero.HP <= Dungeon.hero.HT * 0.35f) {
			msg = "DANGER: Low HP (" + Dungeon.hero.HP + "/" + Dungeon.hero.HT + ")! Use healing or rest.";
			ic = Icons.WARNING;
			col = 0xFF4444;
			bgR = 0.5f; bgG = 0.1f; bgB = 0.1f;
		}
		// 3. Combat Check - Hostile in FOV
		else {
			Mob enemyInSight = null;
			for (Mob mob : Dungeon.level.mobs) {
				if (mob.alignment == Char.Alignment.ENEMY && mob.isAlive() && Dungeon.level.heroFOV[mob.pos]) {
					enemyInSight = mob;
					break;
				}
			}

			if (enemyInSight != null) {
				msg = "COMBAT: " + enemyInSight.name() + " spotted! Tap it to strike.";
				ic = Icons.TARGET;
				col = 0xFFAA33;
				bgR = 0.35f; bgG = 0.15f; bgB = 0.0f;
			}
			// 4. Standing on Exit Stairs
			else if (Dungeon.hero.pos == Dungeon.level.exit()) {
				msg = "STAIRS: Tap hero or action button to descend!";
				ic = Icons.STAIRS;
				col = 0x00FF88;
				bgR = 0.0f; bgG = 0.35f; bgB = 0.15f;
			}
			// 5. Exit Stairs Discovered / Mapped
			else if (Dungeon.level.visited[Dungeon.level.exit()] || Dungeon.level.mapped[Dungeon.level.exit()]) {
				msg = "STAIRS FOUND: Follow your compass to descend.";
				ic = Icons.STAIRS;
				col = 0x33FFBB;
				bgR = 0.0f; bgG = 0.25f; bgB = 0.2f;
			}
			// 6. Loot on current tile
			else if (Dungeon.level.heaps.get( Dungeon.hero.pos ) != null 
					&& Dungeon.level.heaps.get( Dungeon.hero.pos ).type != Heap.Type.FOR_SALE) {
				Heap h = Dungeon.level.heaps.get( Dungeon.hero.pos );
				msg = "LOOT: " + h.title() + " at your feet! Tap to pick up.";
				ic = Icons.INFO;
				col = 0xFFEE55;
				bgR = 0.25f; bgG = 0.25f; bgB = 0.05f;
			}
			// 7. Next to a closed door
			else if (isAdjacentToClosedDoor()) {
				msg = "DOOR: Tap adjacent door to open.";
				ic = Icons.INFO;
				col = 0x00E5FF;
				bgR = 0.0f; bgG = 0.2f; bgB = 0.35f;
			}
			// 8. Default Exploration State
			else {
				msg = "QUEST: Floor " + Dungeon.depth + " • Explore & locate descent stairs.";
				ic = Icons.COMPASS;
				col = 0x00E5FF;
				bgR = 0.0f; bgG = 0.15f; bgB = 0.3f;
			}
		}

		if (force || !msg.equals( currentMsg )) {
			currentMsg = msg;
			icon.copy( Icons.get( ic ) );
			text.text( msg );
			text.hardlight( col );
			bg.hardlight( bgR, bgG, bgB );
			layout();
		}
	}

	private boolean isAdjacentToClosedDoor() {
		if (Dungeon.level == null || Dungeon.hero == null) return false;
		for (int n : PathFinder.NEIGHBOURS8) {
			int cell = Dungeon.hero.pos + n;
			if (cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.map[cell] == Terrain.DOOR) {
				return true;
			}
		}
		return false;
	}
}


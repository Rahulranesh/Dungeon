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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;

public class WndQuestGuide extends Window {

	private static final int WIDTH_P = 142;
	private static final int WIDTH_L = 210;
	private static final int MARGIN  = 4;

	public WndQuestGuide() {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		if (Camera.main != null) {
			width = Math.min(width, (int)(Camera.main.width - 16));
		}

		IconTitle title = new IconTitle( Icons.get( Icons.COMPASS ), "EXPEDITION & TACTICS" );
		title.setRect( MARGIN, MARGIN, width - MARGIN * 2, 0 );
		add( title );

		int exit = Dungeon.level != null ? Dungeon.level.exit() : -1;
		boolean exitFound = exit >= 0 && (Dungeon.level.visited[exit] || Dungeon.level.mapped[exit]);

		String statusStr;
		if (Dungeon.bossLevel()) {
			statusStr = "_STATUS: BOSS BATTLE_\nDefeat the guardian to claim the Skeleton Key!";
		} else if (exitFound) {
			statusStr = "_STATUS: STAIRS LOCATED!_\nFollow your compass needle to reach the exit stairs.";
		} else {
			statusStr = "_STATUS: EXPLORING FLOOR " + Dungeon.depth + "_\nScout the chambers to find the descent stairs.";
		}

		String guideText = statusStr + "\n\n"
				+ "_COMBAT & SURVIVAL TACTICS:_\n"
				+ "• _Attack:_ Tap directly on adjacent monsters to strike.\n"
				+ "• _Dodge / Surprise Attack:_ Retreat through doors when chased. Strike as the enemy enters the doorway for a guaranteed Surprise Attack that cannot be dodged!\n"
				+ "• _Turn Advantage:_ Tap [Wait/Clock] when an enemy is 1 tile away—let them step into your range so YOU hit first!\n"
				+ "• _Choke Points:_ Always retreat to 1-tile hallways to avoid being surrounded.\n"
				+ "• _Compass:_ Rotating arrow on your avatar points to exit stairs.\n"
				+ "• _Healing:_ Tap Backpack below to drink Potions of Healing.";

		RenderedTextBlock text = PixelScene.renderTextBlock( guideText, 6 );
		text.maxWidth( width - MARGIN * 2 );
		text.setPos( MARGIN, title.bottom() + MARGIN );
		add( text );

		float btnW = (width - MARGIN * 3) / 2f;
		float btnH = 18;
		float btnY = text.bottom() + MARGIN * 2;

		RedButton btnTutorial = new RedButton( "TUTORIAL", 8 ) {
			@Override
			protected void onClick() {
				Sample.INSTANCE.play( Assets.Sounds.CLICK );
				hide();
				GameScene.show( new WndMissionBriefing() );
			}
		};
		btnTutorial.setRect( MARGIN, btnY, btnW, btnH );
		add( btnTutorial );

		RedButton btnClose = new RedButton( "RESUME", 8 ) {
			@Override
			protected void onClick() {
				Sample.INSTANCE.play( Assets.Sounds.CLICK );
				hide();
			}
		};
		btnClose.setRect( btnTutorial.right() + MARGIN, btnY, btnW, btnH );
		add( btnClose );

		int totalH = (int)(btnClose.bottom() + MARGIN);
		if (Camera.main != null) {
			totalH = Math.min(totalH, (int)(Camera.main.height - 16));
		}
		resize( width, totalH );
	}
}

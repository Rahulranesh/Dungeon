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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndQuestGuide extends Window {

	private static final int WIDTH_P = 140;
	private static final int WIDTH_L = 200;
	private static final int MARGIN  = 4;

	public WndQuestGuide() {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		IconTitle title = new IconTitle( Icons.get( Icons.COMPASS ), "EXPEDITION GUIDE" );
		title.setRect( MARGIN, MARGIN, width - MARGIN * 2, 0 );
		add( title );

		int exit = Dungeon.level != null ? Dungeon.level.exit() : -1;
		boolean exitFound = exit >= 0 && (Dungeon.level.visited[exit] || Dungeon.level.mapped[exit]);

		String statusStr;
		if (Dungeon.bossLevel()) {
			statusStr = "_STATUS: BOSS BATTLE_\nDefeat the guardian to claim the Skeleton Key!";
		} else if (exitFound) {
			statusStr = "_STATUS: STAIRS LOCATED!_\nFollow your compass to reach the descent stairs.";
		} else {
			statusStr = "_STATUS: EXPLORING FLOOR " + Dungeon.depth + "_\nScout the chambers to find the descent stairs.";
		}

		String guideText = statusStr + "\n\n"
				+ "_HOW TO PLAY:_\n"
				+ "• _Move:_ Tap any floor tile to walk\n"
				+ "• _Attack:_ Tap adjacent enemies to strike\n"
				+ "• _Doors:_ Tap closed doors to open\n"
				+ "• _Compass:_ The arrow on your avatar points toward the exit stairs at all times\n"
				+ "• _Items & Heal:_ Tap your Backpack below to use potions, food, and equip items\n"
				+ "• _Rest:_ Tap the Hourglass to pass turns; hold it to sleep and recover HP\n"
				+ "• _Search:_ Tap the Magnifying Glass to uncover hidden doors and traps";

		RenderedTextBlock text = PixelScene.renderTextBlock( guideText, 6 );
		text.maxWidth( width - MARGIN * 2 );
		text.setPos( MARGIN, title.bottom() + MARGIN );
		add( text );

		RedButton btnClose = new RedButton( "RESUME EXPEDITION" ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnClose.setRect( MARGIN, text.bottom() + MARGIN * 2, width - MARGIN * 2, 18 );
		add( btnClose );

		resize( width, (int)(btnClose.bottom() + MARGIN) );
	}
}


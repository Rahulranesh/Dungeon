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
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndMissionBriefing extends Window {

	private static final int WIDTH_P = 140;
	private static final int WIDTH_L = 200;
	private static final int MARGIN  = 4;

	public static boolean shownThisRun = false;

	public WndMissionBriefing() {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		IconTitle title = new IconTitle( HeroSprite.avatar( Dungeon.hero ), "ANCIENT WILDS: FLOOR 1" );
		title.setRect( MARGIN, MARGIN, width - MARGIN * 2, 0 );
		add( title );

		String briefing = "_WELCOME, ADVENTURER!_\n\n"
				+ "You have entered the corrupted depths of the _Ancient Wilds_.\n\n"
				+ "_YOUR MISSION:_\n"
				+ "1. _Explore_ the dungeon to find the _Descent Stairs_ to Floor 2.\n"
				+ "2. _Follow your Compass:_ The needle rotating on your avatar points toward the exit!\n"
				+ "3. _Tap to Attack:_ Strike monsters when they stand adjacent to you.\n"
				+ "4. _Use your Backpack:_ Open your bag below to drink healing potions, eat food, and equip weapons.\n\n"
				+ "_QUEST BANNER:_\n"
				+ "The banner at the top of the screen tracks your immediate goal. Tap it anytime for help!";

		RenderedTextBlock text = PixelScene.renderTextBlock( briefing, 6 );
		text.maxWidth( width - MARGIN * 2 );
		text.setPos( MARGIN, title.bottom() + MARGIN );
		add( text );

		RedButton btnStart = new RedButton( "BEGIN EXPEDITION" ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnStart.setRect( MARGIN, text.bottom() + MARGIN * 2, width - MARGIN * 2, 18 );
		add( btnStart );

		resize( width, (int)(btnStart.bottom() + MARGIN) );
	}
}


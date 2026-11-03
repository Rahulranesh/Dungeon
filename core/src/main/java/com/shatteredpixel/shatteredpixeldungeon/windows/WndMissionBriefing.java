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
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;

public class WndMissionBriefing extends Window {

	private static final int WIDTH_P = 142;
	private static final int WIDTH_L = 210;
	private static final int MARGIN  = 4;

	public static boolean shownThisRun = false;

	private static final int TOTAL_PAGES = 3;
	private int currentPage = 0;

	private final int windowWidth;
	private final IconTitle title;
	private final RenderedTextBlock text;
	private final RedButton btnLeft;
	private final RedButton btnRight;

	public WndMissionBriefing() {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		if (Camera.main != null) {
			width = Math.min(width, (int)(Camera.main.width - 16));
		}
		this.windowWidth = width;

		title = new IconTitle();
		title.setRect( MARGIN, MARGIN, width - MARGIN * 2, 0 );
		add( title );

		text = PixelScene.renderTextBlock( 6 );
		text.maxWidth( width - MARGIN * 2 );
		add( text );

		btnLeft = new RedButton( "SKIP", 8 ) {
			@Override
			protected void onClick() {
				Sample.INSTANCE.play( Assets.Sounds.CLICK );
				if (currentPage == 0) {
					hide();
				} else {
					currentPage--;
					renderPage();
				}
			}
		};
		add( btnLeft );

		btnRight = new RedButton( "NEXT >", 8 ) {
			@Override
			protected void onClick() {
				Sample.INSTANCE.play( Assets.Sounds.CLICK );
				if (currentPage < TOTAL_PAGES - 1) {
					currentPage++;
					renderPage();
				} else {
					hide();
				}
			}
		};
		add( btnRight );

		renderPage();
	}

	private void renderPage() {
		String body;
		switch (currentPage) {
			default:
			case 0:
				title.icon( Dungeon.hero != null ? HeroSprite.avatar( Dungeon.hero ) : Icons.get( Icons.INFO ) );
				title.label( "TUTORIAL 1/3: MOVEMENT" );
				body = "_HOW TO EXPLORE & MOVE:_\n\n"
						+ "• _Tap to Walk:_ Tap any floor tile to walk there. Your hero walks one step at a time.\n\n"
						+ "• _Turn-Based Dungeon:_ Every step you take spends 1 turn. Monsters only move when YOU move!\n\n"
						+ "• _Exit Compass Radar:_ Look at your hero! The rotating yellow arrow around your avatar _always points directly to the stairs_ to Floor 2!";
				btnLeft.text( "SKIP" );
				btnRight.text( "NEXT >" );
				break;

			case 1:
				title.icon( Icons.get( Icons.TARGET ) );
				title.label( "TUTORIAL 2/3: HOW TO ATTACK" );
				body = "_COMBAT & FIRST STRIKE:_\n\n"
						+ "• _Tap to Strike:_ When a monster is adjacent, _tap directly on the monster's body_ to attack with your weapon!\n\n"
						+ "• _First Strike Advantage:_ If an enemy is 1 step away, _do not walk into them_! Tap the _Wait (Clock)_ button below—let them step next to you so _YOU hit first_!\n\n"
						+ "• _Red Attack Tag:_ Whenever an enemy is within reach, a red sword icon slides out on the screen—tap it to strike quickly!";
				btnLeft.text( "< PREV" );
				btnRight.text( "NEXT >" );
				break;

			case 2:
				title.icon( Icons.get( Icons.SNAKE ) );
				title.label( "TUTORIAL 3/3: DODGE & SURVIVE" );
				body = "_DODGE & SURPRISE TACTICS:_\n\n"
						+ "• _Doorway Ambush (100% Hit):_ Evasive enemies (like Snakes) dodge normal attacks. _Retreat through a door!_ When they step into the doorway after you, strike! It triggers a _Surprise Attack_ that _cannot miss or be dodged_!\n\n"
						+ "• _Choke Points:_ Never fight in wide open rooms! Retreat into a 1-tile hallway so enemies must fight you 1-on-1 without surrounding you.\n\n"
						+ "• _Emergency Healing:_ If your health turns red, open your _Backpack_ below to drink a _Potion of Healing_!";
				btnLeft.text( "< PREV" );
				btnRight.text( "START!" );
				break;
		}

		title.layout();

		text.text( body, windowWidth - MARGIN * 2 );
		text.setPos( MARGIN, title.bottom() + MARGIN );
		PixelScene.align( text );

		float btnW = (windowWidth - MARGIN * 3) / 2f;
		float btnH = 18;
		float btnY = text.bottom() + MARGIN * 2;

		btnLeft.setRect( MARGIN, btnY, btnW, btnH );
		btnRight.setRect( btnLeft.right() + MARGIN, btnY, btnW, btnH );

		int totalH = (int)(btnRight.bottom() + MARGIN);
		if (Camera.main != null) {
			totalH = Math.min(totalH, (int)(Camera.main.height - 16));
		}
		resize( windowWidth, totalH );
	}
}

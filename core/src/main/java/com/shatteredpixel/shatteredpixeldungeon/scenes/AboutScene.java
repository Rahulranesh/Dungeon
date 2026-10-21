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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndLegal;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class AboutScene extends PixelScene {

	@Override
	public void create() {
		super.create();

		int w = Camera.main.width;
		int h = Camera.main.height;

		RectF insets = getCommonInsets();

		TitleBackground BG = new TitleBackground( w, h );
		add( BG );

		//darkens the arches
		add(new ColorBlock(w, h, 0x44000000));

		ScrollPane list = new ScrollPane( new Component() );
		add( list );

		Component content = list.content();
		content.clear();

		//*** Solo Levelling Dungeon / Sherlin Jenifer Credits ***

		CreditsBlock shpx = new CreditsBlock(true, Window.SHPX_COLOR,
				"Solo Levelling Dungeon",
				Icons.SHPX.get(),
				"Developed by: _Ranesh / BerryLoop_ and _Sherlin Jenifer_\nBased on Shattered Pixel Dungeon",
				"berryloopofficial@gmail.com",
				"mailto:berryloopofficial@gmail.com");

		StyledButton btnTerms = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "Terms of Use", 7){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene().addToFront(new WndLegal(WndLegal.Type.TERMS));
			}
		};

		StyledButton btnPrivacy = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "Privacy Policy", 7){
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.scene().addToFront(new WndLegal(WndLegal.Type.PRIVACY));
			}
		};

		float cardWidth = landscape() ? 150 : 130;
		float cardLeft = (w - cardWidth) / 2f;
		float btnW = (cardWidth - 4) / 2f;
		float btnH = 16;

		shpx.setRect(cardLeft, 0, cardWidth, 0);
		content.add(shpx);

		float totalCardH = shpx.height() + 8 + btnH;
		float cardTop = Math.max(insets.top + 16, (h - totalCardH) / 2f);

		shpx.setPos(cardLeft, cardTop);

		btnTerms.setRect(cardLeft, shpx.bottom() + 8, btnW, btnH);
		content.add(btnTerms);

		btnPrivacy.setRect(btnTerms.right() + 4, btnTerms.top(), btnW, btnH);
		content.add(btnPrivacy);

		content.setSize(w, Math.max(h, btnPrivacy.bottom() + insets.bottom + 16));

		list.setRect( 0, 0, w, h );
		list.scrollTo(0, 0);

		ExitButton btnExit = new ExitButton();
		int ofs = PixelScene.landscape() ? 0 : 4;
		btnExit.setPos( Camera.main.width - btnExit.width() - ofs, ofs );
		add( btnExit );

		//fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(TitleScene.class);
	}

	private void addLine( float y, Group content, float x, float width ){
		ColorBlock line = new ColorBlock(width, 1, 0xFF333333);
		line.x = x;
		line.y = y;
		content.add(line);
	}

	private static class CreditsBlock extends Component {

		boolean large;
		RenderedTextBlock title;
		Image avatar;
		Flare flare;
		RenderedTextBlock body;

		RenderedTextBlock link;
		ColorBlock linkUnderline;
		PointerArea linkButton;

		private CreditsBlock(boolean large, int highlight, String title, Image avatar, String body, String linkText, String linkUrl){
			super();

			this.large = large;

			if (title != null) {
				this.title = PixelScene.renderTextBlock(title, large ? 8 : 6);
				if (highlight != -1) this.title.hardlight(highlight);
				add(this.title);
			}

			if (avatar != null){
				this.avatar = avatar;
				add(this.avatar);
			}

			if (large && highlight != -1 && this.avatar != null){
				this.flare = new Flare( 7, 24 ).color( highlight, true ).show(this.avatar, 0);
				this.flare.angularSpeed = 20;
			}

			this.body = PixelScene.renderTextBlock(body, 6);
			if (highlight != -1) this.body.setHightlighting(true, highlight);
			if (large) this.body.align(RenderedTextBlock.CENTER_ALIGN);
			add(this.body);

			if (linkText != null && linkUrl != null){

				int color = 0xFFFFFFFF;
				if (highlight != -1) color = 0xFF000000 | highlight;
				this.linkUnderline = new ColorBlock(1, 1, color);
				add(this.linkUnderline);

				this.link = PixelScene.renderTextBlock(linkText, 6);
				if (highlight != -1) this.link.hardlight(highlight);
				add(this.link);

				linkButton = new PointerArea(0, 0, 0, 0){
					@Override
					protected void onClick( PointerEvent event ) {
						ShatteredPixelDungeon.platform.openURI( linkUrl );
					}
				};
				add(linkButton);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			float topY = top();

			if (large){
				if (title != null){
					title.maxWidth((int)width());
					title.setPos( x + (width() - title.width())/2f, topY);
					topY += title.height() + 2;
				}

				if (avatar != null){
					avatar.x = x + (width()-avatar.width())/2f;
					avatar.y = topY;
					PixelScene.align(avatar);
					if (flare != null){
						flare.point(avatar.center());
					}
					topY = avatar.y + avatar.height() + 3;
				}

				body.maxWidth((int)width());
				body.setPos( x + (width() - body.width())/2f, topY);
				topY += body.height() + 2;

				if (link != null){
					topY += 1;
					link.maxWidth((int)width());
					link.setPos( x + (width() - link.width())/2f, topY);
					topY += link.height() + 2;

					linkButton.x = link.left()-1;
					linkButton.y = link.top()-1;
					linkButton.width = link.width()+2;
					linkButton.height = link.height()+2;

					linkUnderline.size(link.width(), PixelScene.align(0.49f));
					linkUnderline.x = link.left();
					linkUnderline.y = link.bottom()+1;
				}

			} else {
				// Clean horizontal contributor card: Avatar on left, (Title -> Body -> Link) on right
				float textX = x;
				float textW = width();

				if (avatar != null){
					avatar.x = x + 2;
					avatar.y = topY + 2;
					PixelScene.align(avatar);
					textX = avatar.x + avatar.width() + 5;
					textW = Math.max(10, width() - (textX - x));
				}

				float curY = topY;
				if (title != null){
					title.maxWidth((int)textW);
					title.setPos(textX, curY);
					curY += title.height() + 1;
				}

				body.maxWidth((int)textW);
				body.setPos(textX, curY);
				curY += body.height() + 1;

				if (link != null){
					link.maxWidth((int)textW);
					link.setPos(textX, curY);
					curY += link.height() + 1;

					linkButton.x = link.left() - 1;
					linkButton.y = link.top() - 1;
					linkButton.width = link.width() + 2;
					linkButton.height = link.height() + 2;

					linkUnderline.size(link.width(), PixelScene.align(0.49f));
					linkUnderline.x = link.left();
					linkUnderline.y = link.bottom() + 1;
				}

				float avBottom = avatar != null ? avatar.y + avatar.height() : topY;
				topY = Math.max(curY, avBottom) + 2;
			}

			height = Math.max(height, topY - top());
		}
	}
}

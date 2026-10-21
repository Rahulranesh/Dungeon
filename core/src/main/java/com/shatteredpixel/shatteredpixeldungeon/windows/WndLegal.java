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

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ui.Component;

public class WndLegal extends Window {

	public enum Type {
		TERMS,
		PRIVACY
	}

	public static String APP_NAME       = "Solo Levelling Dungeon";
	public static String DEVELOPER_NAME = "Ranesh / BerryLoop and Sherlin Jenifer";
	public static String CONTACT_EMAIL  = "berryloopofficial@gmail.com";
	public static String PRIVACY_URL    = "https://dungeon-10f9f.web.app/privacy";
	public static String TERMS_URL      = "https://dungeon-10f9f.web.app/terms";

	private static final int WIDTH_P    = 130;
	private static final int WIDTH_L    = 200;
	private static final int HEIGHT_P   = 170;
	private static final int HEIGHT_L   = 135;

	private ScrollPane scrollPane;

	public WndLegal( final Type type ) {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;
		if (Camera.main != null) {
			width = Math.min(width, (int)(Camera.main.width - 16));
			height = Math.min(height, (int)(Camera.main.height - 16));
		}

		resize( width, height );

		String titleText = type == Type.PRIVACY ? "PRIVACY POLICY" : "TERMS OF USE";
		IconTitle title = new IconTitle( Icons.get( type == Type.PRIVACY ? Icons.INFO : Icons.NEWS ), titleText );
		title.setRect( 0, 0, width, 0 );
		add( title );

		final String targetUrl = type == Type.PRIVACY ? PRIVACY_URL : TERMS_URL;
		float btnH = 16;
		float btnY = height - btnH;
		float btnW = (width - 2) / 2f;

		RedButton btnBrowser = new RedButton( "BROWSER", 8 ) {
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.platform.openURI( targetUrl );
			}
		};
		btnBrowser.setRect( 0, btnY, btnW, btnH );
		add( btnBrowser );

		RedButton btnClose = new RedButton( "CLOSE", 8 ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnClose.setRect( btnBrowser.right() + 2, btnY, btnW, btnH );
		add( btnClose );

		scrollPane = new ScrollPane( new Component() );
		add( scrollPane );
		scrollPane.setRect( 0, title.bottom() + 2, width, btnY - title.bottom() - 4 );

		Component content = scrollPane.content();
		String body = type == Type.PRIVACY ? getPrivacyPolicyText() : getTermsOfUseText();
		RenderedTextBlock docText = PixelScene.renderTextBlock( body, 6 );
		docText.maxWidth( width - 4 );
		docText.setPos( 0, 0 );
		content.add( docText );
		content.setSize( width - 4, docText.bottom() + 4 );

		scrollPane.setSize( scrollPane.width(), scrollPane.height() );
		scrollPane.scrollTo( 0, 0 );
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		if (scrollPane != null) {
			scrollPane.setPos( scrollPane.left(), scrollPane.top() );
		}
	}

	private static String getTermsOfUseText() {
		return "_TERMS OF USE_\n"
				+ "Last Updated: 2026\n\n"
				+ "_1. Acceptance of Terms_\n"
				+ "By downloading, installing, or playing " + APP_NAME + " ('the Game'), you agree to be bound by these Terms of Use.\n\n"
				+ "_2. License Grant (GPLv3)_\n"
				+ APP_NAME + " is distributed under the GNU General Public License version 3 (GPLv3). You are free to run, study, and play the game under the applicable open-source license terms.\n\n"
				+ "_3. Intellectual Property_\n"
				+ "Custom branding, visual art modifications, audio assets, and the name " + APP_NAME + " are created and owned by " + DEVELOPER_NAME + ".\n\n"
				+ "_4. User Conduct_\n"
				+ "You agree not to use the Game for any unlawful purposes, distribute modified binaries claiming official endorsement, or attempt to compromise any companion game services.\n\n"
				+ "_5. Disclaimer of Warranties_\n"
				+ "The Game is provided 'AS IS' and 'AS AVAILABLE', without warranty of any kind, express or implied. The developers do not guarantee uninterrupted or error-free gameplay.\n\n"
				+ "_6. Limitation of Liability_\n"
				+ "In no event shall the authors or copyright holders be liable for any claim, damages, or other liability arising from your use of the Game.\n\n"
				+ "_7. Contact & Support_\n"
				+ "For questions regarding these Terms, contact us at:\n"
				+ "_" + CONTACT_EMAIL + "_";
	}

	private static String getPrivacyPolicyText() {
		return "_PRIVACY POLICY_\n"
				+ "Last Updated: 2026\n\n"
				+ "_1. Overview & Commitment_\n"
				+ APP_NAME + " is developed with a strict 'Privacy by Design' philosophy. We respect your digital privacy and do not monetize your personal information.\n\n"
				+ "_2. Data We Do NOT Collect_\n"
				+ "• No personal identification (names, emails, phone numbers)\n"
				+ "• No device geolocation or GPS data\n"
				+ "• No browsing habits or advertising IDs\n"
				+ "• No telemetry or third-party behavioral trackers\n\n"
				+ "_3. Local Storage on Device_\n"
				+ "All your game saves, high scores, dungeon badges, and settings are saved locally on your physical device. If you uninstall the game, your local data is deleted unless backed up.\n\n"
				+ "_4. Cloud Backups & OS Services_\n"
				+ "If you use optional platform backup services (e.g., Google Play Games, Apple iCloud, or Steam Cloud), your save files are synced directly through your platform provider. We have no access to your credentials or payment methods.\n\n"
				+ "_5. Crash Reports & Debug Info_\n"
				+ "If a fatal crash occurs, debug logs are stored locally. Logs are only sent to the developer if you voluntarily choose to send an email to report a bug.\n\n"
				+ "_6. Children's Privacy (COPPA & GDPR)_\n"
				+ "The Game does not target or knowingly collect data from children under 13 (or under 16 in the EU). There are zero behavioral advertisements or microtransaction trackers.\n\n"
				+ "_7. Contact Information_\n"
				+ "If you have questions or concerns about this Privacy Policy, please email:\n"
				+ "_" + CONTACT_EMAIL + "_";
	}
}


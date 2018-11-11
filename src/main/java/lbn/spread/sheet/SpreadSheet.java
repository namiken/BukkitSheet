package lbn.spread.sheet;

import java.net.MalformedURLException;
import java.net.URL;

public class SpreadSheet {
	// アプリケーション名 (任意)
	static final String APPLICATION_NAME = "lbn-webapp-spreadsheet";

	private static final String SPREADSHEETS_FEED_BASE = "https://spreadsheets.google.com/feeds/";

	private static final String SPREADSHEETS_FEED_FULL = SPREADSHEETS_FEED_BASE + "spreadsheets/private/full";

	static final URL SPREADSHEET_FEED_URL;

	public static String jsonKeyUrl = null;

	static {
		if (jsonKeyUrl == null) {
			jsonKeyUrl = "C:\\Users\\KENSUKE\\Desktop\\thelow 設定\\google key\\TheLow-key_namiken.json";
		}
	}

	static {
		try {
			SPREADSHEET_FEED_URL = new URL(SPREADSHEETS_FEED_FULL);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}

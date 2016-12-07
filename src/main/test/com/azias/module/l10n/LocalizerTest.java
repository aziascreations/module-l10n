package com.azias.module.l10n;

import java.io.IOException;

import com.azias.module.l10n.Localizer;

public class LocalizerTest {
	private static Localizer localizer;
	
	public static void main(String[] args) {
		localizer = new Localizer(true);
		
		try {
			localizer.init();
			localizer.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		localizer.dump();
	}
}

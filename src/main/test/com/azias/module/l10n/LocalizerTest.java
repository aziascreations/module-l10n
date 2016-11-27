package com.azias.module.l10n;

import java.io.IOException;

import com.azias.module.l10n.Localizer;
import com.google.gson.JsonSyntaxException;

public class LocalizerTest {
	private static Localizer localizer;
	
	public static void main(String[] args) {
		localizer = new Localizer(true);
		
		try {
			localizer.load();
		} catch (IOException | JsonSyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Available locales:");
		for(String i : localizer.getLocaleList()) {
			System.out.println(i);
		}
		
		try {
			System.out.println("Changing locale...");
			localizer.changeLocale("ru_RU");
		} catch (IOException | JsonSyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}/**/
		
		//System.out.println(localizer.localizeString("test.helloworld"));
		
		localizer.dump();
		
		//Temp test
		try {
			System.out.println("Changing locale...");
			//localizer.changeLocale("ru_RU");
			String a = localizer.localizeString("test.helloworld");
			for (int i = 0; i < a.length(); i++){
				char c = a.charAt(i);
				System.out.println(c+" -> "+(int)c);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

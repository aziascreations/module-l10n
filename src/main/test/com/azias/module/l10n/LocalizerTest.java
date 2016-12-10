package com.azias.module.l10n;

import java.io.IOException;

import com.azias.module.l10n.Localizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
		
		//localizer.dump();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String a = gson.toJson(localizer);
		System.out.println(a);
		
		/*localizer = new Localizer(false);
		try {
			localizer.init();
			localizer.load("test_TEST");
		} catch (IOException e) {
			e.printStackTrace();
		}
		localizer.dump();/**/
	}
}

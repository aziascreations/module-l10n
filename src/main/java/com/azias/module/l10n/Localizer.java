package com.azias.module.l10n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Localizer {
	protected ArrayList<String> locales;
	protected HashMap<String, String> lines;
	protected String localeFolder;
	protected String defaultLocale;
	protected JsonParser jsonParser;
	protected boolean loadCommonLocale;

	public Localizer() {
		this("./lang/", "en_US", false);
	}
	
	public Localizer(boolean loadCommonLocale) {
		this("./lang/", "en_US", loadCommonLocale);
	}
	
	public Localizer(String folder) {
		this(folder, "en_US", false);
	}
	
	public Localizer(String folder, String defaultLocale) {
		this(folder, defaultLocale, false);
	}
	
	public Localizer(String folder, String defaultLocale, boolean loadCommonLocale) {
		this.localeFolder = folder;
		this.defaultLocale = defaultLocale;
		this.loadCommonLocale = loadCommonLocale;
	}
	
	//TODO: Add common lang file.
	public boolean load() throws IOException {
		this.jsonParser = new JsonParser();
		
		ArrayList<File> localeFiles = Localizer.listFiles(this.localeFolder);
		this.locales = new ArrayList<String>();
		
		for(File file : localeFiles) {
			if(file.getName().matches("^[a-z0-9\\-]{2,8}_[A-Z0-9\\-]{2,8}(\\.lang)$"))
				this.locales.add(file.getName().substring(0, file.getName().length() - 5));
			else
				System.out.println("This is not a locale: "+ file.getName());
		}
		
		if(!locales.contains(this.defaultLocale))
			throw new FileNotFoundException("Unable to find the default lang file: "+this.defaultLocale);
		
		return this.changeLocale(this.defaultLocale);
	}
	
	public boolean changeLocale(String newLocale) throws IOException, JsonSyntaxException {
		//TODO: check if newLocale is available.
		this.lines = new HashMap<String, String>();

		if(this.loadCommonLocale) {
			File commonLangFile= new File(this.localeFolder+"common.lang");
			this.processLocaleFile(Files.toString(commonLangFile, Charsets.UTF_8));
		}
		
		if(!newLocale.equals(this.defaultLocale)) {
			File defaultLangFile= new File(this.localeFolder+this.defaultLocale+".lang");
			this.processLocaleFile(Files.toString(defaultLangFile, Charsets.UTF_8));
		}
		
		//TODO: Check if file exists - No, it will simply throw an IOException.
		File desiredLangFile= new File(this.localeFolder+newLocale+".lang");
		this.processLocaleFile(Files.toString(desiredLangFile, Charsets.UTF_8));
		return true;
	}
	
	protected boolean processLocaleFile(String par1) throws JsonSyntaxException {
		JsonObject jObj = (JsonObject)this.jsonParser.parse(par1);
		for(Entry<String, JsonElement> e : jObj.entrySet()) {
			if(!e.getKey().startsWith("_"))
				this.lines.put(e.getKey(), e.getValue().getAsString());
		}
		return true;
	}
	
	//Use the locales to give names to the locales.
	public ArrayList<String> getLocaleList() {
		return locales;
	}
	
	public String localizeString(String unlocalizedString, String... par2) {
		if(Strings.isNullOrEmpty(unlocalizedString))
			return "error.nulloremptystring";
		
		if(unlocalizedString.startsWith("!"))
			return unlocalizedString.substring(1);
		
		String localizedString = lines.get(unlocalizedString);
		
		if(Strings.isNullOrEmpty(localizedString))
			return unlocalizedString;
		
		return String.format(localizedString, (Object[])par2);
	}
	
	/**
	 * List all the files under a directory
	 * @param directoryName to be listed
	 */
	private static ArrayList<File> listFiles(String directoryName) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		ArrayList<File> files = new ArrayList<File>();
		for(File file : fList) {
			if(file != null) {
				if(file.isFile()) {
					files.add(file);
				}
			}
		}
		return files;
	}
	
	public void dump() {
		System.out.println("\"lines\" Dump:");
		for(Entry<String, String> entry : this.lines.entrySet()) {
			System.out.println(entry.getKey() + " -> "+ entry.getValue());
		}
	}
}

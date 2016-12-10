package com.azias.module.l10n;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Insert useful text here.
 * 
 * @author Herwin Bozet
 * @version 1.0.0
 * @since 09-12-2016
 */
public class Localizer {
	private final static Logger logger = LoggerFactory.getLogger(Localizer.class);
	protected ArrayList<String> availableLocales, localesFolder;
	protected HashMap<String, String> lines;
	protected String defaultLocale, currentLocale;
	protected JsonParser jsonParser;
	protected boolean loadCommonLocale;
	
	/**
	 * Constructs a Localizer object with default configuration.<br>
	 * The default configuration has the following settings:
	 * <ul>
	 * 	<li>The default locale folder is set to: "./lang/"</li>
	 * 	<li>The default locale is set to: "eng_US"</li>
	 * 	<li>The common locale won't be loaded.</li>
	 * </ul>
	 */
	public Localizer() {
		this("./lang/", "eng_US", false);
	}
	
	public Localizer(boolean loadCommonLocale) {
		this("./lang/", "eng_US", loadCommonLocale);
	}
	
	public Localizer(String folder) {
		this(folder, "eng_US", false);
	}
	
	public Localizer(String folder, boolean loadCommonLocale) {
		this(folder, "eng_US", loadCommonLocale);
	}
	
	public Localizer(String folder, String defaultLocale) {
		this(folder, defaultLocale, false);
	}
	
	public Localizer(String folder, String defaultLocale, boolean loadCommonLocale) {
		logger.debug("Instantiating Localizer with \"{}\", \"{}\" and \"{}\" as arguments.", folder, defaultLocale, loadCommonLocale);
		this.localesFolder = new ArrayList<String>();
		this.localesFolder.add(folder);
		this.defaultLocale = defaultLocale;
		this.loadCommonLocale = loadCommonLocale;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException - Thrown if [check the Exception]
	 */
	public boolean init() throws IOException {
		this.availableLocales = new ArrayList<String>();
		this.lines = new HashMap<String, String>();
		this.jsonParser = new JsonParser();
		
		//Check if the given locales folders are "valid".
		File[] a = new File[this.localesFolder.size()];
		for(int i=0; i<a.length; i++) {
			a[i] = new File(this.localesFolder.get(i));
			if(!a[i].exists()) {
				logger.error("The Localizer is unable to find {}", a[i].getPath());
				throw new IOException(a[i].getPath() + " doesn't exist.");
			}
			if(!a[i].isDirectory()) {
				logger.error("The Localizer can't use files as locales folder. ({})", a[i].getPath());
				throw new IOException(a[i].getPath() + " isn't a folder.");
			}
		}
		
		//List the available lang files.
		ArrayList<File> b;
		for(int i=0; i<a.length; i++) {
			b = Localizer.listFiles(a[i]);
			
			for(File file : b) {
				if(file.getName().matches("^[a-z0-9\\-]{2,8}_[A-Z0-9\\-]{2,8}(\\.lang)$")) {
					this.availableLocales.add(file.getName().substring(0, file.getName().length() - 5));
				}
			}
		}
		
		//Check if the default locale is available.
		if(!this.availableLocales.contains(this.defaultLocale)) {
			logger.warn("The Localizer was unable to find the default locale named {}.", this.availableLocales);
			//return false;
		}
		
		return true;
	}
	
	public boolean load() throws IOException {
		return this.load(this.defaultLocale);
	}
	
	public boolean load(String par1) throws IOException {
		this.currentLocale = par1;
		//This part is not optimized yet, I had to get it working asap.
		//The garbage collector isn't going to like this...
		if(this.loadCommonLocale) {
			for(int i=0; i<this.localesFolder.size(); i++) {
				File a = new File(this.localesFolder.get(i)+"common.lang");
				if(a.exists() && a.isFile()) {
					this.processLocaleFile(Files.toString(a, Charsets.UTF_8));
				}
			}
		}
		
		for(int i=0; i<this.localesFolder.size(); i++) {
			File a = new File(this.localesFolder.get(i)+this.defaultLocale+".lang");
			if(a.exists() && a.isFile()) {
				this.processLocaleFile(Files.toString(a, Charsets.UTF_8));
			}
		}

		if(!par1.equals(this.defaultLocale)) {
			for(int i=0; i<this.localesFolder.size(); i++) {
				File a = new File(this.localesFolder.get(i)+par1+".lang");
				if(a.exists() && a.isFile()) {
					this.processLocaleFile(Files.toString(a, Charsets.UTF_8));
				}
			}
		}
		
		logger.debug("Loaded {} line(s).", this.lines.size());
		return false;
	}
	
	protected boolean processLocaleFile(String fileContent) {
		try {
			JsonObject jObj = (JsonObject)this.jsonParser.parse(fileContent);
			for(Entry<String, JsonElement> e : jObj.entrySet()) {
				if(!e.getKey().startsWith("_"))
					this.lines.put(e.getKey(), e.getValue().getAsString());
			}
			return true;
		} catch(JsonSyntaxException jse) {
			jse.printStackTrace();
			//logger.error("Syntax error in json String.");
			return false;
		}
	}
	
	public boolean addSourceFolder(String path) {
		File newDirectory = new File(path);
		
		if(!newDirectory.exists() || !newDirectory.isDirectory())
			return false;
		
		if(this.localesFolder.contains(path))
			return false;
		
		return true;
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
	
	//Use the locales to give names to the locales. - What the fuck ?
	public ArrayList<String> getAvailableLocales() {
		return this.availableLocales;
	}

	public String getDefaultLocale() {
		return this.defaultLocale;
	}
	
	//The name is shit, but it works...
	public boolean getLoadCommonLocale() {
		return this.loadCommonLocale;
	}

	public boolean setDefaultLocale(String newDefaultLocale) {
		if(Strings.isNullOrEmpty(newDefaultLocale))
			return false;
		
		if(!newDefaultLocale.matches("^[a-z0-9\\-]{2,8}_[A-Z0-9\\-]{2,8}"))
			return false;
		
		if(this.availableLocales != null)
			if(!this.availableLocales.contains(newDefaultLocale))
				return false;
		
		this.defaultLocale = newDefaultLocale;
		return true;
	}
	
	public boolean setLoadCommonLocale(boolean par1) {
		this.loadCommonLocale = par1;
		return true;
	}
	
	/**
	 * Prints all the lines stored in the {@link Localizer} to the [debug logging thing].
	 */
	public void dump() {
		logger.debug("Localizer lines:");
		for(Entry<String, String> entry : this.lines.entrySet()) {
			logger.debug("{} -> {}", entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Removes all the lines currently stored in the {@link Localizer}.
	 */
	public void reset() {
		this.lines.clear();
	}
	
	/**
	 * Removes all the lines and locales folders currently stored in the {@link Localizer}.
	 * Note: It also removes the default locales folder given in the constructor.
	 */
	public void hardReset() {
		this.reset();
		this.localesFolder.clear();
	}
	
	private static ArrayList<File> listFiles(File folder) throws IOException {
		if(!folder.exists())
			throw new IOException("");
		
		if(!folder.isDirectory())
			throw new IOException("");
		
		File[] fList = folder.listFiles();
		ArrayList<File> files = new ArrayList<File>();
		
		if(fList==null)
			return files;
		
		for(File file : fList) {
			if(file != null) {
				if(file.isFile()) {
					files.add(file);
				}
			}
		}
		
		return files;
	}
}

package me.deleteme.polyglot.util;

import java.util.HashMap;

import com.optimaize.langdetect.i18n.LdLocale;

/**
 * Links {@link LdLocale} with language name, for use in language panels {@link me.deleteme.polyglot.gui.SepPane SepPane} <br>
 * 
 * Stored in {@link me.deleteme.polyglot.gui.AddEdit AddEdit}
 * 
 * If language code specified is comb or newl, assign locale as null
 * @author Dennis
 *
 */
public class LangPair{
	private String langName;
	private String langCode;
	private LdLocale locale;
	/**
	 * map langCode to langName
	 */
	private static HashMap<String,String> map;
	public static void readList(){
		map=new HashMap<>();
		for(String[]codename:LangList.list){
			map.put(codename[0], codename[1]);
		}
	}
	public static LangPair fromLdLocale(LdLocale loc){
		if(loc!=null){
			String code=loc.toString();
			return new LangPair(map.get(code),code);
		}
		else{
			return new LangPair("Empty Line","newl");
		}
	}
	/**
	 * creates LangPair
	 * @param name default name
	 * @param code language code
	 */
	public LangPair(String name,String code){
		if(code.equals("comb")|code.equals("newl")){
			langName=name;
			langCode=code;
			locale=null;
		}else{
			langName = name;
			langCode = code;
			locale = LdLocale.fromString(code);
		}
	}
	
	/**
	 * @return "langName, langCode" as a string representation of the pair. 
	 */
	@Override
	public String toString(){
		return langName+", "+langCode;
	}
	/**
	 * Something wrong with the default equals method so overridden
	 * the default returns false even when all the three are equal
	 */
	@Override
	public boolean equals(Object langPair){
		LangPair lp=(LangPair)langPair;
		boolean nameEq=this.langName.equals(lp.getName());
		boolean codeEq=this.langCode.equals(lp.getCode());
		boolean locEq =this.locale.equals(lp.getLocale());
		return nameEq&&codeEq&&locEq;
	}
	/**
	 * @return code of the language
	 */
	public String getCode(){
		return langCode;
	}
	
	/** 
	 * @return Default name of the language
	 */
	public String getName(){
		return langName;
	}
	
	/**
	 * @return locale of the LangPair
	 */
	public LdLocale getLocale(){
		return locale;
	}
}
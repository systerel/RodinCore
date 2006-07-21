package org.eventb.eventBKeyboard.internal.translators;

public class Symbol {
	private String combo;
	private String translation;
	
	public Symbol(String combo, String translation) {
		this.combo = combo;
		this.translation = translation;
	}
	
	public String getCombo() {return combo;}
	
	public String getTranslation() {return translation;}
	
	public String toString() {
		return "\"" + combo + "\" --> \"" + translation + "\"";
	}

}

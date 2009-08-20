package org.rodinp.internal.keyboard.translators;

public class Symbol {

	private final String combo;
	private final String translation;

	public Symbol(String combo, String translation) {
		this.combo = combo;
		this.translation = translation;
	}

	public String getCombo() {
		return combo;
	}

	public String getTranslation() {
		return translation;
	}

	@Override
	public String toString() {
		return "\"" + combo + "\" --> \"" + translation + "\"";
	}

}

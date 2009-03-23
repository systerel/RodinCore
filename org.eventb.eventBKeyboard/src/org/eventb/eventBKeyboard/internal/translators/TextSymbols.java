package org.eventb.eventBKeyboard.internal.translators;


public class TextSymbols extends AbstractSymbols {
	// Combos input which are "text".
	private static final String[] textCombo = { "NAT1", "NAT", "POW1", "POW",
			"INT", "INTER", "UNION", "or", "not", "true", "false", "circ", "oftype" };

	// The translation of the above "text" combos
	private static final String[] textTranslation = { "\u2115\u0031",
			"\u2115", "\u2119\u0031", "\u2119", "\u2124", "\u22c2", "\u22c3",
			"\u2228", "\u00ac", "\u22a4", "\u22a5", "\u2218", "\u2982" };


	@Override
	protected String[] getTranslation() {
		return textTranslation;
	}

	@Override
	protected String[] getCombo() {
		return textCombo;
	}
	
	public TextSymbols() {
		super();
	}
	
}

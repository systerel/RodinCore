package org.eventb.eventBKeyboard.internal.translators;


public class MathSymbols extends AbstractSymbols {
	// Combos input which are "math".
	private static final String[] mathCombo = { "|>>", "|>", "\\/", "/\\",
			"|->", "-->", "/<<:", "/<:", "/:", "<=>", "=>", "&", "!", "#",
			"/=", "<=", ">=", "<<:", "<:", "<<->>", "<<->", "<->>", "<->",
			">->>", "+->", ">+>", ">->", "+>>", "->>", "{}", "\\", "**", "<+",
			"><", "||", "~", "<<|", "<|", "%", "..", ".", "-", "*", "/", ":=",
			"::", ":|", ":", "|", ",," };

	// The translation of the above "math" combos
	private static final String[] mathTranslation = { "\u2a65", "\u25b7",
			"\u222a", "\u2229", "\u21a6", "\u2192", "\u2284", "\u2288",
			"\u2209", "\u21d4", "\u21d2", "\u2227", "\u2200", "\u2203",
			"\u2260", "\u2264", "\u2265", "\u2282", "\u2286", "\ue102",
			"\ue100", "\ue101", "\u2194", "\u2916", "\u21f8", "\u2914",
			"\u21a3", "\u2900", "\u21a0", "\u2205", "\u2216", "\u00d7",
			"\ue103", "\u2297", "\u2225", "\u223c", "\u2a64", "\u25c1",
			"\u03bb", "\u2025", "\u00b7", "\u2212", "\u2217", "\u00f7",
			"\u2254", ":\u2208", ":\u2223", "\u2208", "\u2223", "\u21a6" };

	@Override
	protected String[] getTranslation() {
		return mathTranslation;
	}

	@Override
	protected String[] getCombo() {
		return mathCombo;
	}
	
	public MathSymbols() {
		super();
	}
	
}

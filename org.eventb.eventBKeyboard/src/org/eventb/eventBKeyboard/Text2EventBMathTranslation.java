package org.eventb.eventBKeyboard;

public class Text2EventBMathTranslation {
	
	private static final String [] textCombo =
	{"NAT1", "NAT", "POW1", "POW", "INT", "INTER", "UNION", "or",
	"not", "true", "false", "circ"};

	private static final String [] textComboTranslation =
	{"\u2115\u0031", "\u2115", "\u2119\u0031", "\u2119", "\u2124", "\u22c2", "\u22c3", "\u2228",
	"\u00ac", "\u22a4", "\u22a5", "\u2218"};


	private static final String [] mathCombo =
	{"|>>", "|>", "\\/", "/\\", "|->", "-->", "/<<:", "/<:", "/:", "<=>", "=>", "&", "!", "#", "/=", "<=", ">=",
	"<<:", "<:", 
	"<<->>", "<<->", "<->>", "<->",
	">->>", "+->", ">+>", ">->", "+>>", "->>",
	"{}", "\\", "**",
	"<+", "><", "||", "~", "<<|", "<|", 
	"%", "..", ".", "-", "*", "/",
	":=", "::", ":|", ":", "|"};

	private static final String [] mathComboTranslation =
	{"\u2a65", "\u25b7", "\u222a", "\u2229", "\u21a6", "\u2192", "\u2284", "\u2288", "\u2209", "\u21d4", "\u21d2", "\u2227", "\u2200", "\u2203", "\u2260", "\u2264", "\u2265",
	"\u2282", "\u2286", 
	"\ue102", "\ue100", "\ue101", "\u2194",
	"\u2916", "\u21f8", "\u2914", "\u21a3", "\u2900", "\u21a0",
	"\u2205", "\u2216", "\u00d7",
	"\ue103", "\u2297", "\u2225", "\u223c", "\u2a64", "\u25c1",
	"\u03bb", "\u2025", "\u00b7", "\u2212", "\u2217", "\u00f7",
	"\u2254", ":\u2208", ":\u2223", "\u2208", "\u2223"};
	
	public static String translate(String str) {
		assert (mathCombo.length == mathComboTranslation.length);
		assert (textCombo.length == textComboTranslation.length);
//		System.out.println("Translate: " + str);

		// Math
		for (int i = 0; i < mathCombo.length; i++) {
			String test = mathCombo[i];
			int index = str.indexOf(test);
			if (index != -1) {
				return translate(str.substring(0, index)) + mathComboTranslation[i] + translate(str.substring(index + mathCombo[i].length()));
			}
		}

		// Text
		for (int i = 0; i < textCombo.length;i++) {
			String test = " " + textCombo[i] + " ";
			int index = (" " + str + " ").indexOf(test);
			if (index == 0) {
				return textComboTranslation[i] + translate(str.substring(textCombo[i].length()));
			}
			else if (index != -1) {
				return translate(str.substring(0, index)) + textComboTranslation[i] + translate(str.substring(index + textCombo[i].length()));
			}
		}
		
		return str;
	}
	
	
}

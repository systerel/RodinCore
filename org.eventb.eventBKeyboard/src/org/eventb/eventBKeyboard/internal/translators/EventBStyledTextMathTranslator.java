package org.eventb.eventBKeyboard.internal.translators;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.custom.StyledText;
import org.eventb.eventBKeyboard.IEventBStyledTextTranslator;
import org.eventb.internal.eventBKeyboard.KeyboardUtils;

public class EventBStyledTextMathTranslator implements
		IEventBStyledTextTranslator {

	private HashMap<String, Collection<Symbol>> symbols = null;

	private int maxSize = 0;

	public void translate(StyledText widget) {
		if (symbols == null) {
			MathSymbols mathSymbols = new MathSymbols();
			symbols = mathSymbols.getSymbols();
			maxSize = mathSymbols.getMaxSize();
		}
		String text = widget.getText();
		translate(widget, 0, text.length());
	}

	private void translate(StyledText widget, int beginIndex, int endIndex) {
		KeyboardUtils.debugMath("***************************************");
		KeyboardUtils.debugMath("Begin: " + beginIndex);
		KeyboardUtils.debugMath("End: " + endIndex);
		if (beginIndex == endIndex) {
			KeyboardUtils.debugMath("Here " + widget.getCaretOffset());
			return;
		}
		String text = widget.getText();
		int currentPos = widget.getCaretOffset();
		String subString = text.substring(beginIndex, endIndex);

		KeyboardUtils.debugMath("Process: \"" + text + "\"");
		KeyboardUtils.debugMath("Pos: " + currentPos);
		KeyboardUtils.debugMath("Substring: \"" + subString + "\"");

		int realIndex = 0;
		String test = null;
		String result = null;
		String key = "";
		int i = 0;
		for (i = maxSize; i > 0; i--) {
			boolean translated = false;
			key = AbstractSymbols.generateKey(i);

			Collection<Symbol> collection = symbols.get(key);
			if (collection != null) {
				for (Symbol symbol : collection) {
					test = symbol.getCombo();
					int index = subString.indexOf(test);

					if (index != -1) {
						result = symbol.getTranslation();

						realIndex = beginIndex + index;

						widget.setSelection(realIndex, realIndex
								+ test.length());
						KeyboardUtils.debugMath("Replace at pos " + realIndex
								+ " from \"" + test + "\" by \"" + result
								+ "\"");
						widget.insert(result);

						if (currentPos <= realIndex) { // Translate after
							// current pos
							widget.setSelection(currentPos);
							KeyboardUtils.debugMath("New pos: " + currentPos);
						}
						// Transate before current pos
						else if (realIndex + test.length() < currentPos) {
							widget.setSelection(currentPos - test.length()
									+ result.length());
							KeyboardUtils.debugMath("New pos: "
									+ (currentPos - test.length() + result
											.length()));
						}
						// Translate within the current pos
						else {
							widget.setSelection(realIndex + result.length());
							KeyboardUtils.debugMath("New pos: "
									+ (realIndex + result.length()));
						}
						translated = true;
						break;
					}
				}
				if (translated)
					break;
			}

		}

		if (i == 0)
			return;

		else {
			translate(widget, realIndex + result.length(), endIndex
					- test.length() + result.length());
			translate(widget, beginIndex, realIndex);
		}
		return;
	}

}


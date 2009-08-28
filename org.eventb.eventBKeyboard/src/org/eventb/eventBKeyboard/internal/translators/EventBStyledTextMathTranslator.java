package org.eventb.eventBKeyboard.internal.translators;

import org.eclipse.swt.custom.StyledText;
import org.eventb.eventBKeyboard.IEventBStyledTextTranslator;
import org.rodinp.internal.keyboard.translators.RodinKeyboardMathTranslator;

public class EventBStyledTextMathTranslator implements
		IEventBStyledTextTranslator {

	private final RodinKeyboardMathTranslator translator = new RodinKeyboardMathTranslator();

	public void translate(StyledText widget) {
		translator.translate(widget);
	}

}

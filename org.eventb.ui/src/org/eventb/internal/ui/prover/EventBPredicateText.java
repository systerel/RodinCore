package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

public class EventBPredicateText implements IPropertyChangeListener {

	StyledText text;

	ScrolledForm parent;

	private Collection<Point> indexes;

	private Point current;

	private int currSize;

	private Collection<Point> dirtyStates;
	
	public EventBPredicateText(FormToolkit toolkit, final ScrolledForm parent,
			String string, Collection<Point> indexes) {

		this.parent = parent;
		this.indexes = indexes;

		dirtyStates = new ArrayList<Point>();
		text = new StyledText(parent.getBody(), SWT.MULTI | SWT.FULL_SELECTION);
		text.setText(string);
		Font font = JFaceResources
		.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		text.setFont(font);
//		text.addModifyListener(new EventBTextModifyListener());
		text.pack();
		setStyle();

		
		JFaceResources.getFontRegistry().addListener(this);

		text.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				drawBoxes(event);
			}

		});

		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (current == null)
					return;
				dirtyStates.add(current);
				updateIndexes();
			}

		});

		text.addVerifyListener(new VerifyListener() {

			public void verifyText(VerifyEvent e) {
				checkModifiable(e);
			}

		});

	}

	protected void checkModifiable(VerifyEvent e) {
		e.doit = false;
		int offset = text.getCaretOffset();
		for (Point index : indexes) {
			if (index.x <= offset && index.y >= offset) {
				e.doit = true;
				current = index;
				currSize = text.getText().length();
				break;
			}
		}
	}

	protected void updateIndexes() {
		int offset = text.getText().length() - currSize;

		for (Point index : indexes) {
			if (index.x > current.y) {
				index.x = index.x + offset;
				index.y = index.y + offset;
			}
		}

		current.y = current.y + offset;
		
		setStyle();
		text.pack();
		parent.reflow(true);
	}

	private void drawBoxes(Event event) {
		ProverUIUtils.debugProverUI("Draw boxes");
		String contents = text.getText();
		int lineHeight = text.getLineHeight();
		final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);
		event.gc.setForeground(RED);
		for (Point index : indexes) {
			String str = contents.substring(index.x, index.y);
			int stringWidth = event.gc.stringExtent(str).x;
			Point topLeft = text.getLocationAtOffset(index.x);
			event.gc.drawRectangle(topLeft.x - 1, topLeft.y, stringWidth + 1,
					lineHeight - 1);
		}
	}

	private void setStyle() {
		final Color YELLOW = Display.getDefault().getSystemColor(
				SWT.COLOR_YELLOW);
		for (Point index : indexes) {
			StyleRange style = new StyleRange();
			style.start = index.x;
			style.length = index.y - index.x;
			if (dirtyStates.contains(index)) style.background = YELLOW;
			style.fontStyle = SWT.ITALIC;
			text.setStyleRange(style);
		}
	}

	public StyledText getMainTextWidget() {
		return text;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			Font font = JFaceResources
					.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			text.setFont(font);
			text.pack();
		}
	}

	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		text.dispose();
	}

	public String[] getResults() {
		String [] results = new String[indexes.size()];
		int i = 0;
		for (Point index : indexes) {
			String str = text.getText().substring(index.x, index.y);
			results[i] = str;
			i++;
		}
		
		return results;
	}
	
}

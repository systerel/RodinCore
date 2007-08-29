package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.eventBKeyboard.EventBStyledTextModifyListener;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.IEventBSharedColor;
import org.eventb.internal.ui.TacticPositionUI;
import org.eventb.internal.ui.proofcontrol.IProofControlPage;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

public class EventBPredicateText implements IPropertyChangeListener {

	// Constants for showing different cursors
	final Cursor handCursor;

	final Cursor arrowCursor;

	private IUserSupport us;

	private Predicate hyp;

	StyledText text;
	
	private Collection<Point> boxes;

	TacticHyperlinkManager manager;
	
	Point currentBox;

	private int currSize;

	private ProverUI proverUI;
	
	Collection<Point> dirtyStates;

	MouseDownListener mouseDownListener = new MouseDownListener();

	MouseMoveListener mouseMoveListener = new MouseMoveListener();

	MouseHoverListener mouseHoverListener = new MouseHoverListener();

	MouseExitListener mouseExitListener = new MouseExitListener();

	MouseEnterListener mouseEnterListener = new MouseEnterListener();

	public EventBPredicateText(FormToolkit toolkit, final Composite parent, ProverUI proverUI) {
		this.proverUI = proverUI;
		dirtyStates = new ArrayList<Point>();
		text = new StyledText(parent, SWT.MULTI | SWT.FULL_SELECTION);
		Font font = JFaceResources
				.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		text.setFont(font);
		handCursor = new Cursor(text.getDisplay(), SWT.CURSOR_HAND);
		arrowCursor = new Cursor(text.getDisplay(), SWT.CURSOR_ARROW);
		JFaceResources.getFontRegistry().addListener(this);
		text.addListener(SWT.MouseDown, mouseDownListener);
		text.addListener(SWT.MouseMove, mouseMoveListener);
		text.addListener(SWT.MouseHover, mouseHoverListener);
		text.addListener(SWT.MouseExit, mouseExitListener);
		text.addListener(SWT.MouseEnter, mouseEnterListener);
		manager = new TacticHyperlinkManager(text) {

			@Override
			protected void applyTactic(String tacticID, IPosition position) {
				EventBPredicateText.this.applyTactic(tacticID, position);
			}

			@Override
			protected void disableListeners() {
				if (!text.isDisposed()) {
					text.removeListener(SWT.MouseDown, mouseDownListener);
					text.removeListener(SWT.MouseMove, mouseMoveListener);
					text.removeListener(SWT.MouseHover, mouseHoverListener);
					text.removeListener(SWT.MouseExit, mouseExitListener);
					text.removeListener(SWT.MouseEnter, mouseEnterListener);
				}
			}

			@Override
			protected void enableListeners() {
				if (!text.isDisposed()) {
					text.addListener(SWT.MouseDown, mouseDownListener);
					text.addListener(SWT.MouseMove, mouseMoveListener);
					text.addListener(SWT.MouseHover, mouseHoverListener);
					text.addListener(SWT.MouseExit, mouseExitListener);
					text.addListener(SWT.MouseEnter, mouseEnterListener);
				}
			}
			
		};
	}

	// This must be called after initialisation
	public void setText(String string, IUserSupport us, Predicate hyp,
			Collection<Point> boxes, Collection<TacticPositionUI> links) {
		this.hyp = hyp;
		this.us = us;
		this.boxes = boxes;
		manager.setHyperlinks(links);
		text.setText(string);
		text.pack();
		text.addModifyListener(new EventBStyledTextModifyListener());

		text.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				drawBoxes(event);
			}

		});

		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (currentBox == null)
					return;
				dirtyStates.add(currentBox);
				updateIndexes();
			}

		});

		text.addVerifyListener(new VerifyListener() {

			public void verifyText(VerifyEvent e) {
				checkModifiable(e);
			}

		});
		setStyle();
	}

	protected void checkModifiable(VerifyEvent e) {
		e.doit = false;
		Point pt = new Point(e.start, e.end);

		// Make sure the selection is from left to right
		if (pt.x > pt.y) {
			pt = new Point(pt.y, pt.x);
		}

		// It is only modify-able if it is within one subrange and the
		for (Point index : boxes) {
			if (index.x > pt.x)
				continue;
			if (index.y < pt.y)
				continue;

			if (e.text.equals("")) { // deletion
				if (pt.x == pt.y - 1 && index.y == pt.x) // SWT.DEL at the
					// end
					continue;
				else if (pt.x == pt.y + 1 && index.x == pt.y) // SWT.BS at the
					// beginning
					continue;
			}

			e.doit = true;
			currentBox = index;
			currSize = text.getText().length();
			break;
		}
	}

	protected void updateIndexes() {
		int offset = text.getText().length() - currSize;

		for (Point box : boxes) {
			if (box.x > currentBox.y) {
				box.x = box.x + offset;
				box.y = box.y + offset;
			}
		}

		manager.updateHyperlinks(currentBox.y, offset);
		
		for (Point point : dirtyStates) {
			if (point.x > currentBox.y) {
				point.x = point.x + offset;
				point.y = point.y + offset;
			}
		}

		currentBox.y = currentBox.y + offset;

		setStyle();
		text.redraw();
	}

	void drawBoxes(Event event) {
		if (boxes == null)
			return;
		String contents = text.getText();
		int lineHeight = text.getLineHeight();
		event.gc.setForeground(EventBUIPlugin.getSharedColor().getColor(
				IEventBSharedColor.BOX_BORDER));
		for (Point index : boxes) {
			String str = contents.substring(index.x, index.y);
			int stringWidth = event.gc.stringExtent(str).x;
			Point topLeft = text.getLocationAtOffset(index.x);
			event.gc.drawRectangle(topLeft.x - 1, topLeft.y, stringWidth + 1,
					lineHeight - 1);
		}
	}

	private void setStyle() {
		if (boxes == null)
			return;
		for (Point index : boxes) {
			StyleRange style = new StyleRange();
			style.start = index.x;
			style.length = index.y - index.x;
			if (dirtyStates.contains(index))
				style.background = EventBUIPlugin.getSharedColor().getColor(
						IEventBSharedColor.DIRTY_STATE);
			style.fontStyle = SWT.ITALIC;
			text.setStyleRange(style);
		}

		manager.setHyperlinkStyle();
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
		if (boxes == null)
			return new String[0];
		String[] results = new String[boxes.size()];
		int i = 0;
		for (Point index : boxes) {
			String str = text.getText().substring(index.x, index.y);
			results[i] = str;
			i++;
		}

		return results;
	}

	class MouseDownListener implements Listener {

		public void handleEvent(Event e) {
			manager.mouseDown(new Point(e.x, e.y));
		}

	}

	class MouseMoveListener implements Listener {

		public void handleEvent(Event e) {
			manager.disposeMenu();
			Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}
	}

	class MouseEnterListener implements Listener {

		public void handleEvent(Event e) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Enter ");
			Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}
	}

	class MouseHoverListener implements Listener {

		public void handleEvent(Event e) {
			manager.showToolTip(new Point(e.x, e.y));
		}
	}

	class MouseExitListener implements Listener {

		public void handleEvent(Event event) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Exit ");
			manager.disposeMenu();
			manager.disableCurrentLink();
		}

	}

	void applyTactic(String tacticID, IPosition position) {
		assert (hyp != null);
		TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		Set<Predicate> hypSet = new HashSet<Predicate>();
		hypSet.add(hyp);
		String[] inputs = this.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				ProverUIUtils.debug("Input: \"" + input + "\"");

		IProofControlPage proofControlPage = this.proverUI.getProofControl();
		String globalInput = proofControlPage.getInput();
		
		ITacticProvider provider = tacticUIRegistry.getTacticProvider(tacticID);
		if (provider != null)
			try {
				us.applyTacticToHypotheses(provider.getTactic(us.getCurrentPO()
						.getCurrentNode(), hyp, position, inputs, globalInput),
						hypSet, true, new NullProgressMonitor());
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
			IProofCommand command = tacticUIRegistry.getProofCommand(tacticID,
					TacticUIRegistry.TARGET_HYPOTHESIS);
			if (command != null) {
				try {
					command.apply(us, hyp, inputs, new NullProgressMonitor());
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

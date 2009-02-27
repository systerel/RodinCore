/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - changed double click behavior
 *******************************************************************************/
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
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.DoubleClickStyledTextListener;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.TacticPositionUI;
import org.eventb.internal.ui.proofcontrol.IProofControlPage;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

public class EventBPredicateText implements IPropertyChangeListener {
	private final static int MARGIN = 5;

	// Constants for showing different cursors
	final Cursor handCursor;

	final Cursor arrowCursor;

	private IUserSupport us;

	private Predicate hyp;

	StyledText styledText;
	
	IEventBInputText [] boxes;
	
	int [] offsets;

	TacticHyperlinkManager manager;
	
	private ProverUI proverUI;
	
	Collection<Point> dirtyStates;

	MouseDownListener mouseDownListener = new MouseDownListener();

	MouseMoveListener mouseMoveListener = new MouseMoveListener();

	MouseHoverListener mouseHoverListener = new MouseHoverListener();

	MouseExitListener mouseExitListener = new MouseExitListener();

	MouseEnterListener mouseEnterListener = new MouseEnterListener();

	private ScrolledForm scrolledForm;

	boolean isGoal;
	
	public EventBPredicateText(boolean isGoal, FormToolkit toolkit, final Composite parent, ProverUI proverUI, ScrolledForm scrolledForm) {
		this.isGoal = isGoal;
		this.proverUI = proverUI;
		this.scrolledForm = scrolledForm;
		dirtyStates = new ArrayList<Point>();
		styledText = new StyledText(parent, SWT.MULTI | SWT.FULL_SELECTION);
		styledText.addMouseListener(new DoubleClickStyledTextListener(styledText));
		Font font = JFaceResources
				.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		styledText.setFont(font);
		handCursor = new Cursor(styledText.getDisplay(), SWT.CURSOR_HAND);
		arrowCursor = new Cursor(styledText.getDisplay(), SWT.CURSOR_ARROW);
		JFaceResources.getFontRegistry().addListener(this);
		styledText.addListener(SWT.MouseDown, mouseDownListener);
		styledText.addListener(SWT.MouseMove, mouseMoveListener);
		styledText.addListener(SWT.MouseHover, mouseHoverListener);
		styledText.addListener(SWT.MouseExit, mouseExitListener);
		styledText.addListener(SWT.MouseEnter, mouseEnterListener);
		styledText.setEditable(false);
		manager = new TacticHyperlinkManager(styledText) {

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
			int [] positions, Collection<TacticPositionUI> links) {
		this.hyp = hyp;
		this.us = us;
		styledText.setText(string);
		this.offsets = positions;
		createTextBoxes();
		manager.setHyperlinks(links);
		// reposition widgets on paint event and draw a box around the widgets.
		styledText.addPaintObjectListener(new PaintObjectListener() {
			public void paintObject(PaintObjectEvent event) {
				event.gc.setForeground(EventBSharedColor.getSystemColor(
						SWT.COLOR_RED));
				StyleRange style = event.style;
				int start = style.start;
				for (int i = 0; i < offsets.length; i++) {
					int offset = offsets[i];
					if (start == offset) {
						Text text = boxes[i].getTextWidget();
						Point pt = text.getSize();
						int x = event.x + MARGIN;
						int y = event.y + event.ascent - 2*pt.y/3;
						text.setLocation(x, y);
						Rectangle bounds = text.getBounds();
						event.gc.drawRectangle(bounds.x - 1, bounds.y - 1,
								bounds.width + 1, bounds.height + 1);
						break;
					}
				}
			}
		});

		//		styledText.pack();
//		styledText.addModifyListener(new EventBStyledTextModifyListener());

		setStyle();
	}

	private void createTextBoxes() {
		if (offsets == null)
			return;
		boxes = new IEventBInputText[offsets.length];
		for (int i = 0; i < offsets.length; ++i) {
			final Text text = new Text(styledText, SWT.MULTI);
			final int offset = offsets[i];
			text.setText("     ");
			boxes[i] = new EventBMath(text);
			text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW));
			
			text.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					resizeControl(text, offset);
				}
				
			});
			resizeControl(text, offset);
			 
		}
	}

	protected void resizeControl(Text text, int offset) {
		StyleRange style = new StyleRange ();
		style.start = offset;
		style.length = 1;
		text.pack();
		Rectangle rect = text.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.width + 2 * MARGIN);
		styledText.setStyleRange(style);
		scrolledForm.reflow(true);
//		IFormPage page = proverUI.getActivePageInstance();
//		if (page != null && page instanceof ProofsPage) {
//			((ProofsPage) page).autoLayout();
//		}
	}

	private void setStyle() {
		manager.setHyperlinkStyle();
	}

	public StyledText getMainTextWidget() {
		return styledText;
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
			styledText.setFont(font);
			styledText.pack();
		}
	}

	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		if (boxes != null) {
			for (IEventBInputText box : boxes) {
				box.dispose();
			}
		}
		styledText.dispose();
	}

	public String[] getResults() {
		if (boxes == null)
			return new String[0];
		String[] results = new String[boxes.length];
		int i = 0;
		for (IEventBInputText box : boxes) {
			results[i] = box.getTextWidget().getText();
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
				Predicate pred = null;
				if (!isGoal)
					pred = hyp;
				us.applyTacticToHypotheses(provider.getTactic(us.getCurrentPO()
						.getCurrentNode(), pred, position, inputs, globalInput),
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

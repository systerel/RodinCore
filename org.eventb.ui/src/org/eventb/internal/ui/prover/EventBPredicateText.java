/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - changed double click behavior
 *     Systerel - fixed menu bug
 *     ETH Zurich - adapted to org.rodinp.keyboard
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     Systerel - added support for autocompletion 
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.DoubleClickStyledTextListener;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.eventb.internal.ui.proofcontrol.IProofControlPage;
import org.eventb.ui.prover.ITacticApplication;
import org.rodinp.keyboard.preferences.PreferenceConstants;

public class EventBPredicateText implements IPropertyChangeListener {
	private final static int MARGIN = 5;

	// Constants for showing different cursors
	private IUserSupport us;

	// Represents current predicate (hypothesis or goal)
	private Predicate hyp;

	private final StyledText styledText;
	
	IEventBInputText [] boxes;
	
	int [] offsets;

	final TacticHyperlinkManager manager;
	
	private final ProverUI proverUI;
	
	final MouseDownListener mouseDownListener = new MouseDownListener();

	final MouseMoveListener mouseMoveListener = new MouseMoveListener();

	final MouseHoverListener mouseHoverListener = new MouseHoverListener();

	final MouseExitListener mouseExitListener = new MouseExitListener();

	final MouseEnterListener mouseEnterListener = new MouseEnterListener();

	private ScrolledForm scrolledForm;

	boolean isGoal;
	
	public EventBPredicateText(boolean isGoal, FormToolkit toolkit,
			final Composite parent, ProverUI proverUI, ScrolledForm scrolledForm) {
		this.isGoal = isGoal;
		this.proverUI = proverUI;
		this.scrolledForm = scrolledForm;
		this.styledText = new StyledText(parent, SWT.MULTI | SWT.FULL_SELECTION);
		styledText.addMouseListener(new DoubleClickStyledTextListener(styledText));
		final Font font = JFaceResources
				.getFont(PreferenceConstants.RODIN_MATH_FONT);
		JFaceResources.getFontRegistry().addListener(this);
		styledText.setFont(font);
		styledText.setEditable(false);
		this.manager = new TacticHyperlinkManager(styledText) {

			@Override
			protected void applyTactic(ITacticApplication application) {
				EventBPredicateText.this.applyTactic(application);
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
		manager.enableListeners();
	}

	// This must be called after initialisation
	public void setText(String string, IUserSupport us, Predicate hyp,
			int [] positions, Map<Point, List<ITacticApplication>> links) {
		this.hyp = hyp;
		this.us = us;
		styledText.setText(string);
		this.offsets = positions;
		createTextBoxes();
		manager.setHyperlinks(links);
		// reposition widgets on paint event and draw a box around the widgets.
		styledText.addPaintObjectListener(new PaintObjectListener() {
			@Override
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

		setStyle();
	}

	private void createTextBoxes() {
		if (offsets == null)
			return;
		this.boxes = new IEventBInputText[offsets.length];
		for (int i = 0; i < offsets.length; ++i) {
			final Text text = new Text(styledText, SWT.SINGLE);
			final int offset = offsets[i];
			text.setText("     ");
			boxes[i] = new EventBMath(text);
			text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW));
			ContentProposalFactory.makeContentProposal(text, us);
			
			text.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					resizeControl(text, offset);
				}
				
			});
			resizeControl(text, offset);
			 
		}
	}

	protected void resizeControl(Text text, int offset) {
		final StyleRange style = new StyleRange ();
		style.start = offset;
		style.length = 1;
		text.pack();
		final Rectangle rect = text.getBounds();
		final int ascent = 2 * rect.height / 3;
		final int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.width + 2 * MARGIN);
		styledText.setStyleRange(style);
		scrolledForm.reflow(true);
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
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.RODIN_MATH_FONT)) {
			Font font = JFaceResources
					.getFont(PreferenceConstants.RODIN_MATH_FONT);
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
		manager.disposeMenu();
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

	// Note: problems with mouse event management prevent from using interfaces
	// MouseListener and MouseTrackListener
		
	class MouseDownListener implements Listener {

		@Override
		public void handleEvent(Event e) {
			manager.mouseDown(new Point(e.x, e.y));
		}

	}

	class MouseMoveListener implements Listener {

		@Override
		public void handleEvent(Event e) {
			manager.hideMenu();
			final Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}
	}

	class MouseEnterListener implements Listener {

		@Override
		public void handleEvent(Event e) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Enter ");
			final Point location = new Point(e.x, e.y);
			manager.setMousePosition(location);
		}
	}

	class MouseHoverListener implements Listener {

		@Override
		public void handleEvent(Event e) {
			manager.showToolTip(new Point(e.x, e.y));
		}
	}

	class MouseExitListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils.debug("Exit ");
			manager.hideMenu();
			manager.disableCurrentLink();
		}

	}


	void applyTactic(ITacticApplication application) {
		assert (hyp != null);
		final Set<Predicate> hypSet = Collections.singleton(hyp);
		final String[] inputs = this.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				ProverUIUtils.debug("Input: \"" + input + "\"");
		 
		final IProofControlPage proofControl = proverUI.getProofControl();
		//fixed bug #3137151
		final String globalInput = proofControl == null ? "" : proofControl.getInput();
		
		final boolean skipPostTactic = TacticUIRegistry.getDefault()
				.isSkipPostTactic(application.getTacticID());
		ProverUIUtils.applyTactic(application.getTactic(inputs, globalInput),
				us, hypSet, skipPostTactic, new NullProgressMonitor());
	}

}

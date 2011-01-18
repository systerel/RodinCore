/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
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
 *     Systerel - refactored according to the use of StyledText component
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static java.util.Collections.emptyMap;
import static org.eclipse.swt.SWT.COLOR_RED;
import static org.eclipse.swt.SWT.COLOR_YELLOW;
import static org.eventb.internal.ui.prover.PredicateUtil.prettyPrint;
import static org.eventb.internal.ui.prover.ProverUIUtils.getHyperlinks;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.eventb.ui.prover.ITacticApplication;

public class EventBPredicateText {

	private static final int MARGIN = 2;

	protected static final Color RED = EventBSharedColor
			.getSystemColor(COLOR_RED);
	protected static final Color YELLOW = EventBSharedColor
			.getSystemColor(COLOR_YELLOW);

	private static final int MAX_LENGTH = 30;

	protected final StyledText styledText;

	// The yellow boxes to insert some input
	protected IEventBInputText[] boxes;

	// The offset of each box on a given line
	protected int[] offsets;

	// The offset of the parent styledText before this predicate is appended
	protected int textOffset;

	private final TacticHyperlinkManager manager;

	private final HypothesisRow hypothesisRow;

	private PaintObjectListener paintObjListener;

	// Constants for showing different cursors
	private IUserSupport us;

	// Tells if hyperlinks should be enabled
	private final boolean enable;

	public EventBPredicateText(HypothesisRow hypothesisRow, boolean isGoal,
			boolean enable, ProverUI proverUI) {
		this.hypothesisRow = hypothesisRow;
		this.styledText = hypothesisRow.styledText;
		this.manager = hypothesisRow.manager;
		this.enable = enable;
	}

	// This must be called after initialisation
	public void append(String parsedString, IUserSupport userSupport,
			Predicate hyp, Predicate parsedPredicate) {
		this.us = userSupport;
		final Map<Point, List<ITacticApplication>> links = getLinks(
				parsedString, hyp);
		manager.putAssociation(links.keySet(), hypothesisRow);
		manager.setHyperlinks(links, hypothesisRow);
		textOffset = styledText.getCharCount();

		final String disPredStr = getDisplayablePredicateString(parsedString, parsedPredicate);
		styledText.append(disPredStr);
		

		createTextBoxes();
		// reposition widgets on paint event and draw a box around the widgets.
		paintObjListener = new PaintObjectListener() {
			@Override
			public void paintObject(PaintObjectEvent event) {
				event.gc.setForeground(RED);
				final StyleRange style = event.style;
				int start = style.start;
				for (int i = 0; i < offsets.length; i++) {
					int offset = offsets[i] + textOffset;
					if (start == offset) {
						final Text text = boxes[i].getTextWidget();
						final Point textSize = text.getSize();
						final int x = event.x + MARGIN;
						final int y = event.y + event.ascent - 2 * textSize.y / 3;
						text.setLocation(x, y);
						final Rectangle bounds = text.getBounds();
						event.gc.drawRectangle(bounds.x - 1, bounds.y - 1,
								bounds.width + 1, bounds.height + 1);
						break;
					}
				}
			}
		};
		styledText.addPaintObjectListener(paintObjListener);
		setStyle();
	}
	
	private String getDisplayablePredicateString(String predicateStr,
			Predicate parsedPredicate) {
		if (enable && parsedPredicate.getTag() == Formula.FORALL) {
			final String space = " ";
			final QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPredicate;
			final StringBuilder stb = new StringBuilder();
			stb.append("\u2200 ");
			final BoundIdentDecl[] idents = qpred.getBoundIdentDecls();
			offsets = new int[idents.length];
			int i = 0;
			for (BoundIdentDecl ident : idents) {
				final SourceLocation loc = ident.getSourceLocation();
				final String image = predicateStr.substring(loc.getStart(),
						loc.getEnd() + 1);
				stb.append(space);
				stb.append(image);
				stb.append(space);
				int x = stb.length();
				stb.append(space);
				offsets[i] = x;

				if (++i == idents.length) {
					stb.append("\u00b7\n");
				} else {
					stb.append(", ");
				}
			}
			stb.append(prettyPrint(MAX_LENGTH, predicateStr,
					qpred.getPredicate()));
			return stb.toString();
		}
		offsets = new int[0];
		return prettyPrint(MAX_LENGTH, predicateStr, parsedPredicate);
	}

	private Map<Point, List<ITacticApplication>> getLinks(String predicateStr,
			Predicate hypothesis) {
		if (enable) {
			return getHyperlinks(manager, us, true, predicateStr,
					hypothesis);
		}
		return emptyMap();
	}

	protected void createTextBoxes() {
		if (offsets == null)
			return;
		this.boxes = new IEventBInputText[offsets.length];
		for (int i = 0; i < offsets.length; ++i) {
			final Text text = new Text(styledText, SWT.SINGLE);
			final int offset = offsets[i] + textOffset;
			text.setText("     ");
			boxes[i] = new EventBMath(text);
			text.setBackground(YELLOW);
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
		final StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		text.pack();
		final Rectangle rect = text.getBounds();
		final int ascent = 2 * rect.height / 3;
		final int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.width + 2 * MARGIN);
		text.setLocation(styledText.getLocationAtOffset(offset));
		styledText.setStyleRange(style);
	}

	private void setStyle() {
		manager.setHyperlinkStyle();
	}

	public void dispose() {
		if (paintObjListener != null && !styledText.isDisposed())
			styledText.removePaintObjectListener(paintObjListener);
		if (boxes != null) {
			for (IEventBInputText box : boxes) {
				box.dispose();
			}
		}
		manager.disposeMenu();
	}

	public String[] getResults() {
		if (boxes == null)
			return new String[0];
		final String[] results = new String[boxes.length];
		int i = 0;
		for (IEventBInputText box : boxes) {
			results[i] = box.getTextWidget().getText();
			i++;
		}
		return results;
	}

}

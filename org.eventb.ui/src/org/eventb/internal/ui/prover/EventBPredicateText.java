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

	
	private String predicateText;
	
	// The yellow boxes to insert some input
	protected IEventBInputText[] boxes;

	// The offset of each box on a given line
	protected int[] offsets;

	private final PredicateRow predicateRow;

	// Constants for showing different cursors
	private IUserSupport us;

	// Tells if hyperlinks should be enabled
	private final boolean enable;
	
	protected boolean boxesDrawn;

	private Map<Point, List<ITacticApplication>> links;

	private Predicate pred;

	public EventBPredicateText(PredicateRow predicateRow, boolean isGoal,
			boolean enable, ProverUI proverUI) {
		this.predicateRow = predicateRow;
		this.enable = enable;
		this.boxesDrawn = false;
	}
	
	public void load(String parsedString, IUserSupport userSupport,
			Predicate predicate, Predicate parsedPredicate) {
		us = userSupport;
		pred = predicate;
		predicateText = getPrettyPrintedString(parsedString, parsedPredicate);
	}
	
	
	public void append(TacticHyperlinkManager manager, boolean odd) {
		links = getLinks(predicateText, pred, manager);
		manager.setHyperlinks(links, predicateRow);
		manager.putAssociation(links.keySet(), predicateRow);
		manager.addPaintObjectListener(createPaintListener(manager));
		final int startOffset = manager.getCurrentOffset();
		manager.appendText(predicateText);
		final int endOffset = manager.getCurrentOffset();
		manager.addBackgroundPainter(odd, startOffset, endOffset);
	}

	// creates a paintObjectListener that repositions widgets on paint event and
	// draw a red box around the
	// yellow input widgets
	private PaintObjectListener createPaintListener(final TacticHyperlinkManager manager) {
		final int textOffset = manager.getCurrentOffset();
		return new PaintObjectListener() {
			@Override
			public void paintObject(PaintObjectEvent event) {
				if (!boxesDrawn) {
					createTextBoxes(manager, textOffset);
					boxesDrawn = true;
				}
				event.gc.setForeground(RED);
				final StyleRange style = event.style;
				int start = style.start;
				for (int i = 0; i < offsets.length; i++) {
					int offset = offsets[i] + textOffset;
					if (start == offset) {
						final Text text = boxes[i].getTextWidget();
						final Point textSize = text.getSize();
						final int x = event.x + MARGIN;
						final int y = event.y + event.ascent - 2 * textSize.y
								/ 3;
						text.setLocation(x, y);
						final Rectangle bounds = text.getBounds();
						event.gc.drawRectangle(bounds.x - 1, bounds.y - 1,
								bounds.width + 1, bounds.height + 1);
						break;
					}
				}
			}
		};
	}
	
	private String getPrettyPrintedString(String predicateStr,
			Predicate parsedPredicate) {
		final int nbTabsFromLeft = predicateRow.getNbTabsFromLeft();
		final StringBuilder stb = new StringBuilder();
		if (enable && parsedPredicate.getTag() == Formula.FORALL) {
			final String space = " ";
			final QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPredicate;
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
				stb.append(space);
				if (++i == idents.length) {
					stb.append("\u00b7\n");
				} else {
					stb.append(", ");
				}
			}
			ProverUIUtils.appendTabs(stb, nbTabsFromLeft);
			stb.append(prettyPrint(MAX_LENGTH, predicateStr,
					qpred.getPredicate(), nbTabsFromLeft));
			stb.append("\n");
			return stb.toString();
		}
		offsets = new int[0];
		stb.append(prettyPrint(MAX_LENGTH, predicateStr, parsedPredicate, nbTabsFromLeft));
		stb.append("\n");
		return stb.toString();
	}

	private Map<Point, List<ITacticApplication>> getLinks(String predicateStr,
			Predicate predicate, TacticHyperlinkManager manager) {
		if (enable) {
			return getHyperlinks(manager, us, true, predicateStr,
					predicate);
		}
		return emptyMap();
	}

	protected void createTextBoxes(TacticHyperlinkManager manager, int textOffset) {
		if (offsets == null)
			return;
		this.boxes = new IEventBInputText[offsets.length];
		for (int i = 0; i < offsets.length; ++i) {
			final StyledText parent = manager.getText();
			final Text text = new Text(parent, SWT.SINGLE);
			final int offset = offsets[i] + textOffset;
			text.setText("     ");
			boxes[i] = new EventBMath(text);
			text.setBackground(YELLOW);
			ContentProposalFactory.makeContentProposal(text, us);
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					
					createOrRelocateInputBoxes(parent, text, offset);
				}
			});
			createOrRelocateInputBoxes(parent, text, offset);
		}
	}

	protected void createOrRelocateInputBoxes(StyledText parent, Text text,
			int offset) {
		final StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		text.pack();
		final Rectangle rect = text.getBounds();
		final int ascent = 2 * rect.height / 3;
		final int descent = rect.height - ascent;
		style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
				rect.width + 2 * MARGIN);
		text.setLocation(parent.getLocationAtOffset(offset));
		parent.setStyleRange(style);
	}

	// It's this one that was setting the styles for each predicate.
	// Now the manager should do it
//	private void setStyle() {
//		manager.setHyperlinkStyle();
//	}

	public void dispose() {
		if (boxes != null) {
			for (IEventBInputText box : boxes) {
				box.dispose();
			}
		}
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

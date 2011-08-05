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
import static org.eventb.internal.ui.prover.PredicateUtil.prettyPrint;
import static org.eventb.internal.ui.prover.ProverUIUtils.getHyperlinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IUserSupport;
import org.eventb.ui.prover.ITacticApplication;

public class EventBPredicateText {

	private static final int MAX_LENGTH = 30;
	
	private String predicateText;
	
	// The holders for the yellow boxes
	protected List<YellowBoxHolder> textControls;

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

	private final boolean isGoal;

	private final YellowBoxMaker yellowBoxMaker;

	public EventBPredicateText(PredicateRow predicateRow, boolean isGoal,
			boolean enable, ProverUI proverUI, YellowBoxMaker yellowBoxMaker) {
		this.predicateRow = predicateRow;
		this.isGoal = isGoal;
		this.enable = enable;
		this.yellowBoxMaker = yellowBoxMaker;
		this.boxesDrawn = false;
	}
	
	public void load(String parsedString, IUserSupport userSupport,
			Predicate predicate, Predicate parsedPredicate) {
		us = userSupport;
		pred = predicate;
		predicateText = getPrettyPrintedString(parsedString, parsedPredicate);
	}
	
	
	public void append(TacticHyperlinkManager manager, boolean odd) {
		final int startOffset = manager.getCurrentOffset();
		if (enable) {
			links = getLinks(predicateText, pred, manager);
			manager.setHyperlinks(links, predicateRow);
			manager.putAssociation(links.keySet(), predicateRow);
			createTextBoxes(manager, startOffset);
		}
		manager.appendText(predicateText);
		final int endOffset = manager.getCurrentOffset();
		manager.addBackgroundPainter(odd, startOffset, endOffset);
	}
	
	public void attach() {
		if (enable) {
			for (ControlHolder c : textControls) {
				c.attach(true);
			}
		}
	}
	
	private String getPrettyPrintedString(String predicateStr,
			Predicate parsedPredicate) {
		final int nbTabsFromLeft = predicateRow.getNbTabsFromLeft();
		final StringBuilder stb = new StringBuilder();
		final int tag = parsedPredicate.getTag();
		if (enable && (!isGoal && tag == Formula.FORALL)
				|| (isGoal && tag == Formula.EXISTS)) {
			final String space = " ";
			final QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPredicate;
			if (tag == Formula.EXISTS)
				stb.append("\u2203");
			if (tag == Formula.FORALL)
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
		} else {
			offsets = new int[0];
			stb.append(prettyPrint(MAX_LENGTH, predicateStr, parsedPredicate,
					nbTabsFromLeft));
		}
		stb.append("\n");
		return stb.toString();
	}

	private Map<Point, List<ITacticApplication>> getLinks(String predicateStr,
			Predicate predicate, TacticHyperlinkManager manager) {
		if (enable) {
			return getHyperlinks(manager, us, !isGoal, predicateStr, predicate);
		}
		return emptyMap();
	}

	protected void createTextBoxes(TacticHyperlinkManager manager,
			int textOffset) {
		if (offsets == null)
			return;
		this.textControls = new ArrayList<YellowBoxHolder>(offsets.length);
		for (int i = 0; i < offsets.length; ++i) {
			final int offset = offsets[i] + textOffset;
			textControls.add(i, new YellowBoxHolder(predicateRow,
					yellowBoxMaker, offset, true));
		}
	}

	public void dispose() {
		if (textControls != null) {
			for (YellowBoxHolder h : textControls) {
				h.remove();
			}
		}
	}

	public String[] getResults() {
		if (textControls == null)
			return new String[0];
		final String[] results = new String[offsets.length];
		int i = 0;
		for (YellowBoxHolder holder : textControls) {
			results[i] = holder.getInputString();
			i++;
		}
		return results;
	}
	
}

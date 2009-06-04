/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - mathematical language V2
 *     Systerel - added dispose listener to hypothesis composite
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.TacticPositionUI;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.proofcontrol.IProofControlPage;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A class to create a row containing a hypothesis and the set of proof
 *         buttons which is applicable to the hypothesis
 */
public class HypothesisRow {

	private static final FormulaFactory formulaFactory = FormulaFactory
			.getDefault();

	// Set of composites and button.
	private Button checkBox;

	private Composite buttonComposite;

	private Composite hypothesisComposite;

	private ProverUI proverUI;
	
	EventBPredicateText hypothesisText;

	// The UserSupport associated with this instance of the editor.
	IUserSupport userSupport;

	// The hypothesis contains in this row.
	Predicate hyp;

	// This should be varied when the user resize.
	private int max_length = 30;

	private Color background;

	private boolean enable;

	private String actualString;

	private Predicate parsedPred;

	private FormToolkit toolkit;

	SelectionListener listener;
	
	private ScrolledForm scrolledForm;
	
	private Collection<ImageHyperlink> hyperlinks;
	
	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	/**
	 * Constructor.
	 * 
	 * @param toolkit
	 *            The Form Toolkit to create this row
	 * @param parent
	 *            The composite parent
	 */
	public HypothesisRow(FormToolkit toolkit, Composite parent, Predicate hyp,
			IUserSupport userSupport, boolean odd,
			boolean enable,
			SelectionListener listener, ProverUI proverUI, ScrolledForm scrolledForm) {
		GridData gd;
		this.hyp = hyp;
		this.scrolledForm = scrolledForm;
		this.listener = listener;
		this.userSupport = userSupport;
		this.enable = enable;
		this.proverUI = proverUI;

		this.toolkit = toolkit;
		// FIXME why twice the same color?
		if (odd)
			background = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
		else
			background = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);

		checkBox = toolkit.createButton(parent, "", SWT.CHECK);
		if (ProverUIUtils.DEBUG) {
			checkBox.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		}
		else {
			checkBox.setBackground(background);
		}
		checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false));
		checkBox.setEnabled(enable);
		checkBox.addSelectionListener(listener);
		
		buttonComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 3;

		buttonComposite.setLayout(layout);
		if (ProverUIUtils.DEBUG) {
			buttonComposite.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		}
		else {
			buttonComposite.setBackground(background);
		}
		buttonComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, false, false));
		hyperlinks = new ArrayList<ImageHyperlink>();
		createImageHyperlinks(buttonComposite);

		hypothesisComposite = toolkit.createComposite(parent);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		hypothesisComposite.setLayoutData(gd);
		if (ProverUIUtils.DEBUG) {
			hypothesisComposite.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		}
		else {
			hypothesisComposite.setBackground(background);		
		}
		hypothesisComposite.setLayout(new GridLayout());
		EventBEditorUtils.changeFocusWhenDispose(hypothesisComposite,
				scrolledForm.getParent());

		Predicate pred = hyp;
		actualString = pred.toString();
		IParseResult parseResult = formulaFactory.parsePredicate(actualString, V2, null);
		assert !parseResult.hasProblem();
		parsedPred = parseResult.getParsedPredicate();

		createHypothesisText();

	}

	public void createHypothesisText() {
		if (hypothesisText != null)
			hypothesisText.dispose();
		hypothesisText = new EventBPredicateText(false, toolkit, hypothesisComposite,
				proverUI, scrolledForm);
		StyledText textWidget = hypothesisText.getMainTextWidget();
		textWidget.setBackground(background);
		textWidget.setLayoutData(new GridData(GridData.FILL_BOTH));

		// int borderWidth =
		// hypothesisText.getMainTextWidget().getBorderWidth();
		// hypothesisText.getMainTextWidget().setText(" ");
		// hypothesisComposite.pack(true);
		// int textWidth = hypothesisText.getMainTextWidget().getSize().x;
		//
		// Rectangle rec = hypothesisComposite.getBounds();
		// Point size = hypothesisComposite.getSize();
		// int compositeWidth = hypothesisComposite.getClientArea().width;
		// if (textWidth != 0) {
		// max_length = (compositeWidth - borderWidth) / textWidth;
		// } else
		// max_length = 30;

		if (enable && parsedPred instanceof QuantifiedPredicate
				&& parsedPred.getTag() == Formula.FORALL) {
			QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPred;
			
			String string = "\u2200 ";
			BoundIdentDecl[] idents = qpred.getBoundIdentDecls();
			int [] indexes = new int[idents.length];

			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = actualString.substring(loc.getStart(), loc
						.getEnd() + 1);
				if (ProverUIUtils.DEBUG)
					ProverUIUtils.debug("Ident: " + image);
				string += " " + image + " ";
				int x = string.length();
				string += " ";
				indexes[i] = x;

				if (++i == idents.length) {
					string += "\u00b7\n";
				} else {
					string += ", ";
				}
			}
			String str = PredicateUtil.prettyPrint(max_length, actualString,
					qpred.getPredicate());
			// SourceLocation loc = qpred.getPredicate().getSourceLocation();
			// String str = actualString.substring(loc.getStart(),
			// loc.getEnd());

			string += str;

			// Reparsed the String,
			// Get the list of applicable tactic
			// For each tactic, get the applicable positions

			IParseResult parseResult = formulaFactory.parsePredicate(string, V2, null);
			assert !parseResult.hasProblem();
			Predicate parsedStr = parseResult.getParsedPredicate();

			Map<Point, TacticPositionUI> links = new HashMap<Point, TacticPositionUI>();

			final TacticUIRegistry tacticUIRegistry = TacticUIRegistry
					.getDefault();

			String[] tactics = tacticUIRegistry.getApplicableToHypothesis(
					userSupport, hyp);

			for (final String tacticID : tactics) {
				List<IPosition> positions = tacticUIRegistry
						.getApplicableToHypothesisPositions(tacticID,
								userSupport, hyp);
				if (positions.size() == 0)
					continue;
				for (final IPosition position : positions) {
					Point pt = tacticUIRegistry.getOperatorPosition(tacticID,
							parsedStr, string, position);
					// Point pt = ProverUIUtils.getOperatorPosition(parsedStr,
					// position);
					TacticPositionUI tacticPositionUI = links.get(pt);
					if (tacticPositionUI == null) {
						tacticPositionUI = new TacticPositionUI(pt);
						links.put(pt, tacticPositionUI);
					}
					tacticPositionUI.addTacticPosition(tacticID, position);

					// runnables.add(new Runnable() {
					// public void run() {
					// applyTactic(tacticID, node, position);
					// }
					// });
				}
			}
			hypothesisText.setText(string, userSupport, hyp, indexes, links.values());
		} else {
			String str = PredicateUtil.prettyPrint(max_length, actualString,
					parsedPred);

			IParseResult parseResult = formulaFactory.parsePredicate(str, V2, null);
			assert !parseResult.hasProblem();
			Predicate parsedStr = parseResult.getParsedPredicate();

			int [] indexes = new int[0];

			Map<Point, TacticPositionUI> links = new HashMap<Point, TacticPositionUI>();

			if (enable) {
				final TacticUIRegistry tacticUIRegistry = TacticUIRegistry
						.getDefault();

				String[] tactics = tacticUIRegistry.getApplicableToHypothesis(
						userSupport, hyp);

				for (final String tacticID : tactics) {
					List<IPosition> positions = tacticUIRegistry
							.getApplicableToHypothesisPositions(tacticID,
									userSupport, hyp);
					if (positions.size() == 0)
						continue;
					for (IPosition position : positions) {
						Point pt = tacticUIRegistry.getOperatorPosition(
								tacticID, parsedStr, str, position);
						TacticPositionUI tacticPositionUI = links.get(pt);
						if (tacticPositionUI == null) {
							tacticPositionUI = new TacticPositionUI(pt);
							links.put(pt, tacticPositionUI);
						}
						tacticPositionUI.addTacticPosition(tacticID, position);

						// runnables.add(new Runnable() {
						// public void run() {
						// applyTactic(tacticID, node, position);
						// }
						// });
					}
				}
			}
			hypothesisText.setText(str, userSupport, hyp, indexes, links.values());
		}
		toolkit.paintBordersFor(hypothesisComposite);
	}

	/*
	 * Creating a null hyperlink
	 */
	private void createNullHyperlinks() {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Create Null Image");
		ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
				SWT.CENTER);
		hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		toolkit.adapt(hyperlink, true, true);
		hyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_NULL));
		hyperlink.setBackground(background);
		hyperlink.setEnabled(false);
		hyperlinks.add(hyperlink);
		return;
	}

	/**
	 * Utility methods to create image hyperlinks for applicable tactics.
	 * <p>
	 * 
	 */
	private void createImageHyperlinks(Composite parent) {
		final IProofTreeNode node = userSupport.getCurrentPO().getCurrentNode();

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] tactics = tacticUIRegistry.getApplicableToHypothesis(
				userSupport, hyp);

		if (tactics.length == 0) {
			createNullHyperlinks();
			return;
		}

		for (final String tacticID : tactics) {
			List<IPosition> positions = tacticUIRegistry
					.getApplicableToHypothesisPositions(tacticID, userSupport,
							hyp);
			if (positions.size() != 0)
				continue;

			ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
					SWT.CENTER);
			hyperlink
					.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, true));
			toolkit.adapt(hyperlink, true, true);
			hyperlink.setImage(tacticUIRegistry.getIcon(tacticID));
			hyperlink
					.addHyperlinkListener(new HyperlinkListener(tacticID, node));
			hyperlink.setBackground(background);
			hyperlink.setToolTipText(tacticUIRegistry.getTip(tacticID));
			hyperlink.setEnabled(enable);
			hyperlinks.add(hyperlink);
		}

		return;
	}

	/**
	 * Utility method to dispose the compsites and check boxes.
	 */
	public void dispose() {
//		if (hypothesisText != null)
//			hypothesisText.dispose();

		if (!checkBox.isDisposed()) {
			checkBox.removeSelectionListener(listener);
			checkBox.dispose();
		}
		for (ImageHyperlink hyperlink : hyperlinks)
			hyperlink.dispose();
		buttonComposite.dispose();
		hypothesisComposite.dispose();
	}

	/**
	 * Return if the hypothesis is selected or not.
	 * <p>
	 * 
	 * @return <code>true</code> if the row is selected, and
	 *         <code>false</code> otherwise
	 */
	public boolean isSelected() {
		return checkBox.getSelection();
	}

	/**
	 * Get the contained hypothesis.
	 * <p>
	 * 
	 * @return the hypothesis corresponding to this row
	 */
	public Predicate getHypothesis() {
		return hyp;
	}

	void applyTactic(String tacticID, IProofTreeNode node, IPosition position) {
		TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		Set<Predicate> hypSet = new HashSet<Predicate>();
		hypSet.add(hyp);
		String[] inputs = hypothesisText.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				ProverUIUtils.debug("Input: \"" + input + "\"");

		IProofControlPage proofControlPage = this.proverUI.getProofControl();
		String globalInput = proofControlPage.getInput();

		ITacticProvider provider = tacticUIRegistry.getTacticProvider(tacticID);
		if (provider != null)
			try {
				userSupport.applyTacticToHypotheses(provider.getTactic(node,
						hyp, position, inputs, globalInput), hypSet, true,
						new NullProgressMonitor());
			} catch (RodinDBException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		else {
			IProofCommand command = tacticUIRegistry.getProofCommand(tacticID,
					TacticUIRegistry.TARGET_HYPOTHESIS);
			if (command != null) {
				try {
					command.apply(userSupport, hyp, inputs,
							new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	class HyperlinkListener implements IHyperlinkListener {
		private String tacticID;

		private IProofTreeNode node;

		public HyperlinkListener(String tacticID, IProofTreeNode node) {
			this.node = node;
			this.tacticID = tacticID;
		}

		public void linkEntered(HyperlinkEvent e) {
			return;
		}

		public void linkExited(HyperlinkEvent e) {
			return;
		}

		public void linkActivated(HyperlinkEvent e) {
			applyTactic(tacticID, node, null);
		}

	}

	public void setSelected(boolean selected) {
		checkBox.setSelection(selected);
	}

}
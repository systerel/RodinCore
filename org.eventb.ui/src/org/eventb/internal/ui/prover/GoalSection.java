/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.TacticPositionUI;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class implements the goal section in the Prover UI Editor.
 */
public class GoalSection extends SectionPart {

	// Title and description.
	private static final String SECTION_TITLE = "Goal";

	private static final String SECTION_DESCRIPTION = "The current goal";

	private static final FormulaFactory formulaFactory = FormulaFactory
			.getDefault();

	FormPage page;

	private FormToolkit toolkit;

	private ScrolledForm scrolledForm;

	private Composite buttonComposite;

	private ScrolledForm goalComposite;

	EventBPredicateText goalText;

	private Predicate parsedPred;

	private String actualString;

	private int max_length = 30;

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param page
	 *            The page that contain this section
	 * @param parent
	 *            the composite parent of the section
	 * @param style
	 *            style to create this section
	 */
	public GoalSection(FormPage page, Composite parent, int style) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		toolkit = page.getManagedForm().getToolkit();
		createClient(getSection());
	}

	/**
	 * Creating the client of the section.
	 * <p>
	 * 
	 * @param section
	 *            the section that used as the parent of the client
	 */
	public void createClient(Section section) {
		section.setText(SECTION_TITLE);
		section.setDescription(SECTION_DESCRIPTION);
		scrolledForm = toolkit.createScrolledForm(section);

		Composite comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 3;
		comp.setLayout(layout);
		section.setClient(scrolledForm);
		toolkit.paintBordersFor(scrolledForm);

		IUserSupport userSupport = ((ProverUI) ((ProofsPage) this.page)
				.getEditor()).getUserSupport();
		IProofState ps = userSupport.getCurrentPO();
		if (ps != null) {
			setGoal(ps.getCurrentNode());
		} else
			setGoal(null);
	}

	/**
	 * Set the current goal
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node.
	 */
	public void setGoal(IProofTreeNode node) {
		if (buttonComposite != null)
			buttonComposite.dispose();
		if (goalComposite != null)
			goalComposite.dispose();

		Composite comp = scrolledForm.getBody();

		buttonComposite = toolkit.createComposite(comp);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 5;

		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));

		goalComposite = toolkit.createScrolledForm(comp);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		goalComposite.setLayoutData(gd);
		goalComposite.getBody().setLayout(new FillLayout());

		IUserSupport userSupport = ((ProverUI) ((ProofsPage) this.page)
				.getEditor()).getUserSupport();

		if (node == null)
			createNullHyperlinks();
		else if (node.isOpen())
			createHyperlinks(userSupport, true);
		else
			createHyperlinks(userSupport, false);

		createGoalText(node);

		scrolledForm.reflow(true);

		return;
	}

	public void createGoalText(final IProofTreeNode node) {
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		if (goalText != null)
			goalText.dispose();
		goalText = new EventBPredicateText(toolkit, goalComposite);
		final StyledText styledText = goalText.getMainTextWidget();
		// styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// true));

		// int borderWidth = styledText.getBorderWidth();
		// styledText.setText(" ");
		// goalComposite.pack(true);
		// int textWidth = styledText.getSize().x;
		//
		// Rectangle rec = goalComposite.getBounds();
		// Point size = goalComposite.getSize();
		// int compositeWidth = goalComposite.getClientArea().width;
		// if (textWidth != 0) {
		// max_length = (compositeWidth - borderWidth) / textWidth;
		// } else
		// max_length = 30;

		if (node == null) {
			IUserSupport userSupport = ((ProverUI) ((ProofsPage) this.page)
					.getEditor()).getUserSupport();
			goalText.setText("No current goal", userSupport, node.getSequent().goal(), null, null);
			styledText.setBackground(color);
		} else {
			Predicate goal = node.getSequent().goal();
			actualString = goal.toString();
			IParseResult parseResult = formulaFactory
					.parsePredicate(actualString);
			assert parseResult.isSuccess();
			parsedPred = parseResult.getParsedPredicate();

			if (node != null && node.isOpen()
					&& parsedPred instanceof QuantifiedPredicate
					&& parsedPred.getTag() == Formula.EXISTS) {
				QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPred;
				Collection<Point> indexes = new ArrayList<Point>();

				String string = "\u2203 ";
				BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

				int i = 0;
				for (BoundIdentDecl ident : idents) {
					SourceLocation loc = ident.getSourceLocation();
					String image = actualString.substring(loc.getStart(), loc
							.getEnd() + 1);
					// ProverUIUtils.debugProverUI("Ident: " + image);
					string += " " + image + " ";
					int x = string.length();
					string += "      ";
					int y = string.length();
					indexes.add(new Point(x, y));

					if (++i == idents.length) {
						string += "\u00b7\n";
					} else {
						string += ", ";
					}
				}
				String str = PredicateUtil.prettyPrint(max_length,
						actualString, qpred.getPredicate());
				// SourceLocation loc =
				// qpred.getPredicate().getSourceLocation();
				// String str = actualString.substring(loc.getStart(), loc
				// .getEnd() + 1);
				string += str;

				IParseResult parsedResult = formulaFactory.parsePredicate(string);
				assert parsedResult.isSuccess();
				Predicate parsedStr = parsedResult.getParsedPredicate();

				Map<Point, TacticPositionUI> links = new HashMap<Point, TacticPositionUI>();

				final TacticUIRegistry tacticUIRegistry = TacticUIRegistry
						.getDefault();
				IUserSupport userSupport = ((ProverUI) ((ProofsPage) this.page)
						.getEditor()).getUserSupport();

				String[] tactics = tacticUIRegistry
						.getApplicableToGoal(userSupport);

				for (final String tacticID : tactics) {
					List<IPosition> positions = tacticUIRegistry
							.getApplicableToGoalPositions(tacticID, userSupport);
					if (positions.size() == 0)
						continue;
					for (final IPosition position : positions) {
						Formula subFormula = parsedStr.getSubFormula(position);
						Point pt = ProverUIUtils
								.getOperatorPosition(subFormula);
						TacticPositionUI tacticPositionUI = links.get(pt);
						if (tacticPositionUI == null) {
							tacticPositionUI = new TacticPositionUI();
							links.put(pt, tacticPositionUI);
						}
						tacticPositionUI.addTacticPosition(tacticID, position);
					}
				}

				goalText.setText(string, userSupport, node.getSequent().goal(), indexes, links);
			} else {
				String str = PredicateUtil.prettyPrint(max_length,
						actualString, parsedPred);

				Collection<Point> indexes = new ArrayList<Point>();

				IParseResult parsedResult = formulaFactory.parsePredicate(str);
				assert parsedResult.isSuccess();
				Predicate parsedStr = parsedResult.getParsedPredicate();

				Map<Point, TacticPositionUI> links = new HashMap<Point, TacticPositionUI>();
				IUserSupport userSupport = ((ProverUI) ((ProofsPage) this.page)
						.getEditor()).getUserSupport();

				final TacticUIRegistry tacticUIRegistry = TacticUIRegistry
						.getDefault();
				String[] tactics = tacticUIRegistry
						.getApplicableToGoal(userSupport);

				for (final String tacticID : tactics) {
					List<IPosition> positions = tacticUIRegistry
							.getApplicableToGoalPositions(tacticID, userSupport);
					if (positions.size() == 0)
						continue;
					for (final IPosition position : positions) {
						Formula subFormula = parsedStr.getSubFormula(position);
						Point pt = ProverUIUtils
								.getOperatorPosition(subFormula);
						TacticPositionUI tacticPositionUI = links.get(pt);
						if (tacticPositionUI == null) {
							tacticPositionUI = new TacticPositionUI();
							links.put(pt, tacticPositionUI);
						}
						tacticPositionUI.addTacticPosition(tacticID, position);
					}
				}

				goalText.setText(str, userSupport, node.getSequent().goal(), indexes, links);

				if (!node.isOpen()) {
					styledText.setBackground(color);
				}
			}

		}
		toolkit.paintBordersFor(goalComposite);

		// DragSource source = new DragSource(styledText, DND.DROP_COPY
		// | DND.DROP_MOVE);
		// source.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		// source.addDragListener(new DragSourceAdapter() {
		// Point selection;
		//
		// public void dragStart(DragSourceEvent e) {
		// selection = goalText.getMainTextWidget().getSelection();
		// e.doit = selection.x != selection.y;
		// }
		//
		// public void dragSetData(DragSourceEvent e) {
		// e.data = goalText.getMainTextWidget().getText(selection.x,
		// selection.y - 1);
		// }
		//
		// public void dragFinished(DragSourceEvent e) {
		// if (e.detail == DND.DROP_MOVE) {
		// goalText.getMainTextWidget().replaceTextRange(selection.x,
		// selection.y - selection.x, "");
		// }
		// selection = null;
		// }
		// });

		// styledText.addListener(SWT.MouseDown, new Listener() {
		// public void handleEvent(Event e) {
		// Point location = new Point(e.x, e.y);
		// Point maxLocation = styledText.getLocationAtOffset(styledText
		// .getCharCount());
		// int maxOffset = styledText.getCharCount();
		// if (location.y >= maxLocation.y + styledText.getLineHeight()) {
		// styledText.setCaretOffset(maxOffset);
		// return;
		// }
		// int startLineOffset = styledText.getOffsetAtLocation(new Point(0,
		// location.y));
		// int line = styledText.getLineAtOffset(startLineOffset);
		// Point pt = styledText.getSelection();
		// ProverUIUtils.debugProverUI("Selection: " + pt.x + ", " + pt.y);
		// if (line == styledText.getLineCount() - 1) {
		// if (location.x > maxLocation.x) {
		// styledText.setCaretOffset(maxOffset);
		// } else {
		// int offset = styledText.getOffsetAtLocation(location);
		// // styledText.setCaretOffset(offset);
		// if (pt.x <= offset && offset <= pt.y) {
		// ProverUIUtils.debugProverUI("Drag: " + offset);
		// }
		// else {
		// ProverUIUtils.debugProverUI("Select " + offset);
		// }
		// }
		// return;
		// }
		//				
		//				
		//				
		// int startNextLineOffset = styledText.getOffsetAtLine(line + 1);
		// Point lineEnd = styledText
		// .getLocationAtOffset(startNextLineOffset - 1);
		// if (location.x > lineEnd.x) {
		// // styledText.setCaretOffset(startNextLineOffset - 1);
		// } else {
		// int offset = styledText.getOffsetAtLocation(location);
		// // styledText.setCaretOffset(offset);
		// if (pt.x <= offset && offset <= pt.y) {
		// ProverUIUtils.debugProverUI("Drag: " + offset);
		// }
		// else {
		// ProverUIUtils.debugProverUI("Select " + offset);
		// }
		// }
		// }
		// });

		// source.addDragListener(new DragSourceListener() {
		// Point selection;
		//
		// public void dragStart(DragSourceEvent event) {
		// ProverUIUtils.debugProverUI("Start dragging: ");
		// selection = styledText.getSelection();
		// event.doit = selection.x != selection.y;
		// }
		//
		// public void dragSetData(DragSourceEvent event) {
		// ProverUIUtils.debugProverUI("Set Data: ");
		// event.data = styledText.getText(selection.x, selection.y - 1);
		//
		// }
		//
		// public void dragFinished(DragSourceEvent event) {
		// ProverUIUtils.debugProverUI("Finish dragging ");
		//
		// }
		//
		// });

	}

	@Override
	public void dispose() {
		goalText.dispose();
		super.dispose();
	}

	private void createNullHyperlinks() {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Create Null Image");
		ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
				SWT.CENTER);
		hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		toolkit.adapt(hyperlink, true, true);
		hyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_NULL));
		hyperlink.setEnabled(false);
		return;
	}

	/**
	 * Utility methods to create hyperlinks for applicable tactics.
	 * <p>
	 * 
	 */
	private void createHyperlinks(final IUserSupport us, boolean enable) {

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] tactics = tacticUIRegistry.getApplicableToGoal(us);

		if (tactics.length == 0) {
			createNullHyperlinks();
		}

		for (final String tacticID : tactics) {

			List<IPosition> positions = tacticUIRegistry
					.getApplicableToGoalPositions(tacticID, us);
			
			if (positions.size() != 0)
				continue;

			ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
					SWT.CENTER);
			hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
					false));
			toolkit.adapt(hyperlink, true, true);
			hyperlink.setImage(tacticUIRegistry.getIcon(tacticID));

			hyperlink.addHyperlinkListener(new IHyperlinkListener() {

				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				public void linkExited(HyperlinkEvent e) {
					return;
				}

				public void linkActivated(HyperlinkEvent e) {
					IProofTreeNode node = us.getCurrentPO()
					.getCurrentNode();

					applyTactic(tacticID, node, null);
				}

			});
			hyperlink.setToolTipText(tacticUIRegistry.getTip(tacticID));
			hyperlink.setEnabled(enable);
		}

		return;
	}

	void applyTactic(String tacticID, IProofTreeNode node, IPosition position) {
		TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] inputs = goalText.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				ProverUIUtils.debug("Input: \"" + input + "\"");

		IUserSupport userSupport = ((ProverUI) ((ProofsPage) this.page)
				.getEditor()).getUserSupport();
		ITacticProvider provider = tacticUIRegistry.getTacticProvider(tacticID);
		if (provider != null)
			try {
				userSupport.applyTactic(provider.getTactic(node, null,
						position, inputs), new NullProgressMonitor());
			} catch (RodinDBException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		else {
			IProofCommand command = tacticUIRegistry.getProofCommand(tacticID,
					TacticUIRegistry.TARGET_HYPOTHESIS);
			if (command != null) {
				try {
					command.apply(userSupport, null, inputs,
							new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}
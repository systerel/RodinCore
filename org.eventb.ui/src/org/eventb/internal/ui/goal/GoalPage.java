/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *     Systerel - used EventBSharedColor
 *     Systerel - mathematical language V2
 ******************************************************************************/
package org.eventb.internal.ui.goal;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.TacticPositionUI;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.internal.ui.prover.EventBPredicateText;
import org.eventb.internal.ui.prover.PredicateUtil;
import org.eventb.internal.ui.prover.ProofStatusLineManager;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.TacticUIRegistry;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Goal 'page'.
 */
public class GoalPage extends Page implements IGoalPage {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	protected IUserSupport userSupport;

	private FormToolkit toolkit;

	ScrolledForm scrolledForm;

	private Composite buttonComposite;

	private Composite goalComposite;

	EventBPredicateText goalText;

	private Predicate parsedPred;

	private String actualString;

	private int max_length = 30;

	private ProofStatusLineManager statusManager;

	private ProverUI proverUI;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this Goal Page.
	 */
	public GoalPage(ProverUI proverUI, IUserSupport userSupport) {
		super();
		this.proverUI = proverUI;
		this.userSupport = userSupport;
		USM.addChangeListener(this);
	}

	@Override
	public void dispose() {
		USM.removeChangeListener(this);
		super.dispose();
	}

	@Override
	public void createControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		scrolledForm = toolkit.createScrolledForm(parent);

		Composite comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);
		toolkit.paintBordersFor(scrolledForm);

		IProofState ps = userSupport.getCurrentPO();
		if (ps != null) {
			setGoal(ps.getCurrentNode());
		} else
			setGoal(null);
		
		contributeToActionBars();
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
		layout.numColumns = 3;

		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));

		goalComposite = toolkit.createComposite(comp);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		goalComposite.setLayoutData(gd);
		goalComposite.setLayout(new FillLayout());

		if (node == null)
			createNullHyperlinks();
		else if (node.isOpen())
			createImageHyperlinks(true);
		else
			createImageHyperlinks(false);

		createGoalText(node);

		scrolledForm.reflow(true);

		return;
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
	private void createImageHyperlinks(boolean enable) {

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] tactics = tacticUIRegistry.getApplicableToGoal(userSupport);

		if (tactics.length == 0) {
			createNullHyperlinks();
		}

		for (final String tacticID : tactics) {

			List<IPosition> positions = tacticUIRegistry
					.getApplicableToGoalPositions(tacticID, userSupport);

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
					IProofTreeNode node = userSupport.getCurrentPO().getCurrentNode();
					applyTactic(tacticID, node, null);
				}

			});
			hyperlink.setToolTipText(tacticUIRegistry.getTip(tacticID));
			hyperlink.setEnabled(enable);
		}

		return;
	}

	private void createGoalText(final IProofTreeNode node) {
		Color color = EventBSharedColor.getSystemColor(SWT.COLOR_GRAY);
		if (goalText != null)
			goalText.dispose();
		goalText = new EventBPredicateText(true, toolkit, goalComposite, proverUI, scrolledForm);
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
			goalText.setText("No current goal", userSupport, null, null, new ArrayList<TacticPositionUI>());
			styledText.setBackground(color);
		} else {
			Predicate goal = node.getSequent().goal();
			String tmpString = goal.toString();
			IParseResult parseResult = ff.parsePredicate(tmpString, V2, null);
			assert !parseResult.hasProblem();
			Predicate tmpPred = parseResult.getParsedPredicate();

			int [] indexes = new int[0];

			if (node.isOpen() && tmpPred instanceof QuantifiedPredicate
					&& tmpPred.getTag() == Formula.EXISTS) {
				indexes = getIndexesString(tmpPred, tmpString);
			} else {
				actualString = PredicateUtil.prettyPrint(max_length, tmpString,
						tmpPred);
			}
			IParseResult parsedResult = ff.parsePredicate(actualString, V2, null);
			assert !parsedResult.hasProblem();
			parsedPred = parsedResult.getParsedPredicate();

			Collection<TacticPositionUI> links = new ArrayList<TacticPositionUI>();
			if (node.isOpen()) {
				links = getHyperlinks();
			}
			goalText.setText(actualString, userSupport, node.getSequent().goal(),
					indexes, links);

			if (!node.isOpen()) {
				styledText.setBackground(color);
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

	void applyTactic(String tacticID, IProofTreeNode node, IPosition position) {
		TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] inputs = goalText.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				ProverUIUtils.debug("Input: \"" + input + "\"");

		ITacticProvider provider = tacticUIRegistry.getTacticProvider(tacticID);
		String globalInput = this.proverUI.getProofControl().getInput();
		if (provider != null)
			try {
				userSupport.applyTactic(
						provider.getTactic(node, null,
						position, inputs, globalInput), true,
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
					command.apply(userSupport, null, inputs, new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	private Collection<TacticPositionUI> getHyperlinks() {
		Map<Point, TacticPositionUI> links = new HashMap<Point, TacticPositionUI>();

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();

		String[] tactics = tacticUIRegistry.getApplicableToGoal(userSupport);

		for (final String tacticID : tactics) {
			List<IPosition> positions = tacticUIRegistry
					.getApplicableToGoalPositions(tacticID, userSupport);
			if (positions.size() == 0)
				continue;
			for (final IPosition position : positions) {
				Point pt = tacticUIRegistry.getOperatorPosition(tacticID,
						parsedPred, actualString, position);
				TacticPositionUI tacticPositionUI = links.get(pt);
				if (tacticPositionUI == null) {
					tacticPositionUI = new TacticPositionUI(pt);
					links.put(pt, tacticPositionUI);
				}
				tacticPositionUI.addTacticPosition(tacticID, position);
			}
		}
		return links.values();
	}
	
	private int [] getIndexesString(Predicate pred,
			String sourceString) {
		QuantifiedPredicate qpred = (QuantifiedPredicate) pred;

		actualString = "\u2203 ";
		BoundIdentDecl[] idents = qpred.getBoundIdentDecls();
		int [] offsets = new int[idents.length];

		int i = 0;
		for (BoundIdentDecl ident : idents) {
			SourceLocation loc = ident.getSourceLocation();
			String image = sourceString.substring(loc.getStart(),
					loc.getEnd() + 1);
			// ProverUIUtils.debugProverUI("Ident: " + image);
			actualString += " " + image + " ";
			int offset = actualString.length();
			actualString += "\uFFFC";
			offsets[i] = offset;

			if (++i == idents.length) {
				actualString += "\u00b7\n";
			} else {
				actualString += ", ";
			}
		}
		actualString += PredicateUtil.prettyPrint(max_length, sourceString,
				qpred.getPredicate());
		return offsets;
	}

	/**
	 * Setup the action bars
	 */
	private void contributeToActionBars() {
		IActionBars bars = getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Fill the local pull down.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new Separator());
	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Fill the local toolbar.
	 * <p>
	 * 
	 * @param manager
	 *            the toolbar manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		// Do nothing
	}

	@Override
	public Control getControl() {
		return scrolledForm;
	}

	@Override
	public void setFocus() {
		scrolledForm.setFocus();
	}

	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		if (GoalUtils.DEBUG)
			GoalUtils.debug("Begin User Support Manager Changed");

		// Do nothing if the page is disposed.
		final Control control = this.getControl();
		if (control.isDisposed())
			return;

		// Trying to get the changes for the current user support.
		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		// Do nothing if there is no change for this current user support.
		if (affectedUserSupport == null)
			return;

		// If the user support has been removed, do nothing. This will be handle
		// by the main proof editor.
		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED) {
			return; // Do nothing
		}

		// This case should NOT happened.
		if (kind == IUserSupportDelta.ADDED) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = control.getDisplay();
		
		display.syncExec(new Runnable() {
			public void run() {
				if (control.isDisposed())
					return;
				
				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();

					// Set the information if it has been changed.
					if ((flags & IUserSupportDelta.F_INFORMATION) != 0) {
						setInformation(affectedUserSupport.getInformation());
					}

					if ((flags & IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed, reupdate the page
						IProofState ps = userSupport.getCurrentPO();
						if (ps != null) {
							setGoal(ps.getCurrentNode());
						} else {
							setGoal(null);
						}
						scrolledForm.reflow(true);
						return;
					} 
					
					if ((flags & IUserSupportDelta.F_STATE) != 0) {
						// If the changes occurs in some proof states.	
						IProofState proofState = userSupport.getCurrentPO();
						// Trying to get the change for the current proof state. 
						final IProofStateDelta affectedProofState = ProverUIUtils
								.getProofStateDelta(affectedUserSupport,
										proofState);
						if (affectedProofState != null) {
							// If there are some changes
							int psKind = affectedProofState.getKind();

							if (psKind == IProofStateDelta.ADDED) {
								// This case should not happened
								if (ProofControlUtils.DEBUG)
									ProofControlUtils
											.debug("Error: Delta said that the proof state is added");
								return;
							}

							if (psKind == IProofStateDelta.REMOVED) {
								// Do nothing in this case, this will be handled
								// by the main proof editor.
								return;
							}
							
							if (psKind == IProofStateDelta.CHANGED) {
								// If there are some changes to the proof state.
								int psFlags = affectedProofState.getFlags();

								if ((psFlags & IProofStateDelta.F_NODE) != 0
										|| (psFlags & IProofStateDelta.F_PROOFTREE) != 0) {
									setGoal(proofState.getCurrentNode());
								}
								scrolledForm.reflow(true);
							}
						}
					}
				}
			}
		});

		if (GoalUtils.DEBUG)
			GoalUtils.debug("End User Support Manager Changed");
	}
	
	void setInformation(final IUserSupportInformation[] information) {
		if (statusManager == null) {
			statusManager = new ProofStatusLineManager(this.getSite()
					.getActionBars());
		}
		statusManager.setProofInformation(information);
	}

}

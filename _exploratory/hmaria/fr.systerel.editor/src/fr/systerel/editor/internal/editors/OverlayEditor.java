/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.internal.ui.EventBUtils.getFormulaFactory;
import static org.eventb.internal.ui.autocompletion.ContentProposalFactory.getProposalProvider;
import static org.eventb.internal.ui.autocompletion.ContentProposalFactory.makeContentProposal;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.swt.IFocusService;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IExpressionElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.autocompletion.EventBContentProposalAdapter;
import org.eventb.internal.ui.autocompletion.ProposalProvider;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.keyboard.RodinKeyboardPlugin;

import fr.systerel.editor.internal.actions.StyledTextEditAction;
import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.documentModel.RodinTextStream;
import fr.systerel.editor.internal.operations.AtomicOperation;
import fr.systerel.editor.internal.operations.History;
import fr.systerel.editor.internal.operations.OperationFactory;
import fr.systerel.editor.internal.presentation.IRodinColorConstant;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.RodinConfiguration.ContentType;

/**
 * This class manages the little text field that is used to edit an element.
 */
@SuppressWarnings("restriction")
public class OverlayEditor implements IAnnotationModelListener,
		IAnnotationModelListenerExtension, ExtendedModifyListener,
		VerifyKeyListener, IMenuListener {

	public static final String EDITOR_TEXT_ID = RodinEditor.EDITOR_ID
	+ ".editorText";
	private static final int MARGIN = 4;

	private static class ContentProposalManager {
		private final ProposalProvider provider;
		private final EventBContentProposalAdapter contentProposal;

		public ContentProposalManager(StyledText text, IInternalElement root) {
			final FormulaFactory factory = getFormulaFactory(root);
			provider = getProposalProvider(null, factory);
			contentProposal = makeContentProposal(text, provider);
		}
		
		public boolean isProposalPopupOpen() {
			return contentProposal.isProposalPopupOpen();
		}
		
		public void setCompletionLocation(Interval inter) {
			final IInternalElement element = inter.getElement().getElement();
			
			// FIXME null for predicate and assignment
			IAttributeType attributeType = inter.getContentType()
					.getAttributeType();
			if (attributeType == null) {
				// return; FIXME
				if (element instanceof IPredicateElement)
					attributeType = EventBAttributes.PREDICATE_ATTRIBUTE;
				else if (element instanceof IAssignmentElement)
					attributeType = EventBAttributes.ASSIGNMENT_ATTRIBUTE;
				else
					return;
			}
			final IAttributeLocation location = RodinCore.getInternalLocation(
					element, attributeType);
			provider.setLocation(location);
		}

	}
	
	private final ProjectionViewer viewer;
	private final DocumentMapper mapper;
	private final StyledText parent;
	private final RodinEditor editor;
	private final ITextViewer textViewer;
	
	private final ModifyListener eventBTranslator = RodinKeyboardPlugin
			.getDefault().createRodinModifyListener();

	private final ContentProposalManager contentProposal;
	
	private StyledText editorText;
	private Interval interval;
	private ArrayList<IAction> editActions = new ArrayList<IAction>();
	private Point editorPos;
	private Menu fTextContextMenu;

	public OverlayEditor(StyledText parent, DocumentMapper mapper,
			ProjectionViewer viewer, RodinEditor editor) {
		this.viewer = viewer;
		this.mapper = mapper;
		this.parent = parent;
		this.editor = editor;
		textViewer = new TextViewer(parent, SWT.NONE);
		textViewer.getTextWidget().setBackground(IRodinColorConstant.BG_COLOR);
		setupEditorText();
		contentProposal = new ContentProposalManager(editorText, mapper
				.getRoot().getElement());
	}

	private void setupEditorText() {
		editorText = textViewer.getTextWidget();
		editorText.addVerifyKeyListener(this);
		editorText.setFont(parent.getFont());

		parent.pack();	
		final Point oldsize = parent.getSize();
		parent.setSize(oldsize);
		
		editorText.setVisible(false);
		editorText.addExtendedModifyListener(this);			

		createMenu();
		createEditActions();
		// the focus tracker is used to activate the handlers, when the widget
		// has focus.
		final IFocusService focusService = (IFocusService) editor.getSite()
				.getService(IFocusService.class);
		focusService.addFocusTracker(editorText, EDITOR_TEXT_ID);
	}

	protected IDocument createDocument(String text) {
		IDocument doc = new Document();
		doc.set(text);
		return doc;
	}

	//TODO Check for command based replacement ?
	private void createMenu() {
		 final String id = "editorTextMenu";
		 final MenuManager manager = new MenuManager(id, id);
		 manager.setRemoveAllWhenShown(true);
		 manager.addMenuListener(this);
		 fTextContextMenu = manager.createContextMenu(editorText);
		 editorText.setMenu(fTextContextMenu);
	}

	public void showAtOffset(int offset) {
		// if the overlay editor is currently shown,
		// save the content and show at the new location.
		if (editorText.isVisible() && interval != null
				&& !interval.contains(offset)) {
			saveAndExit();
		}
		final Interval inter = mapper.findEditableInterval(viewer
				.widgetOffset2ModelOffset(offset));
		if (inter == null) {
			return;
		}
		if (inter.getElement().isImplicit())
			return;
		final int pos = offset - inter.getOffset();
		
		interval = inter;
		if (!editorText.isVisible()) {
			if (inter != null) {
				final ContentType contentType = inter.getContentType();
				if (!contentType.isAttributeContentType()) {
					showEditorText(inter, pos);					
				} else {
					handleAttributeContentType(inter, contentType);
				}
			}
		}
	}

	private void handleAttributeContentType(final Interval inter,
			final ContentType contentType) {
		if (!contentType.isBooleanAttributeType()) {
			final IAttributeManipulation attManip = inter
					.getAttributeManipulation();
			if (attManip != null) {
				showTipMenu(inter);
			}
			return;
		}
		changeBooleanValue(inter);
	}

	private void changeBooleanValue(Interval inter) {
		final IRodinElement element = inter.getRodinElement();
		final IAttributeManipulation manip = inter.getAttributeManipulation();
		try {
			final String value = manip.getValue(element, null);
			final String[] possibleValues = manip.getPossibleValues(element,
					null);
			String toSet = null;
			for (String v : possibleValues) {
				if (v.equals(value)) {
					continue;
				}
				toSet = v;
			}
			if (toSet != null)
				updateAttributeValueWithManipulation(inter.getElement(), manip,
						toSet);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private void showEditorText(Interval inter, int pos) {
		contentProposal.setCompletionLocation(inter);
		setEventBTranslation(inter);
		final int start = viewer.modelOffset2WidgetOffset(inter.getOffset());
		final int end = start + inter.getLength();
		final int clickOffset = start + pos;
		final String text;
		if (inter.getLength() > 0) {
			final String extracted = parent.getText(start, end - 1);
			final int level = inter.getIndentation();
			final boolean multiLine = inter.isMultiLine();
			final boolean addWhiteSpace = inter.isAddWhiteSpace();
			text = RodinTextStream.deprocessMulti(level, multiLine,
					addWhiteSpace, extracted);
		} else {
			text = "";
		}
		final Point beginPt = (parent.getLocationAtOffset(start));
		textViewer.setDocument(createDocument(text));
		final Rectangle bounds = parent.getTextBounds(start, end);
		editorText.setCaretOffset(clickOffset - start);
		editorText.setSize(bounds.width + MARGIN, bounds.height);
		editorText.setLocation(beginPt.x - 2, beginPt.y); // -2 to place on text
		editorText.setFont(parent.getFont());
		editorText.setVisible(true);
		editorText.setFocus();
	}

	private void showTipMenu(final Interval inter) {
		final Menu tipMenu = new Menu(parent);
		final String[] possibleValues = inter.getPossibleValues();
		if (possibleValues == null) {
			return;
		}
		for (final String value : possibleValues) {
			final MenuItem item = new MenuItem(tipMenu, SWT.PUSH);
			item.setText(value);
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent se) {
					widgetSelected(se);
				}

				@Override
				public void widgetSelected(SelectionEvent se) {
					final ILElement element = inter.getElement();
					final IAttributeManipulation attManip = inter
							.getAttributeManipulation();
					updateAttributeValueWithManipulation(element, attManip, value);
				}
			});
		}
		final Point loc = parent.getLocationAtOffset(inter.getOffset());
		final Point mapped = parent.getDisplay().map(parent, null, loc);
		tipMenu.setLocation(mapped);
		tipMenu.setVisible(true);
	}
	
	public void updateAttributeValueWithManipulation(ILElement element,
			IAttributeManipulation manip, String value) {
		final IInternalElement ielement = element.getElement();
		final String oldValue;
		try {
			if (manip.hasValue(ielement, null)) {
				oldValue = manip.getValue(ielement, null);
			} else {
				oldValue = null;
			}
			if (value.equals(oldValue)) {
				return;
			}
			final AtomicOperation op = OperationFactory.changeAttribute(manip,
					ielement, value);
			History.getInstance().addOperation(op);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	
	public void abortEditing() {
		editorText.removeModifyListener(eventBTranslator);
		editorText.setVisible(false);
		editorPos = null;
		interval = null;
	}

	/**
	 * Updates the current interval displayed text.
	 * @throws RodinDBException 
	 */
	public void updateModelAfterChanges() {
		if (interval == null) {
			return;
		}
		final ContentType contentType = interval.getContentType();
		final ILElement element = interval.getElement();
		final IInternalElement ielement = element.getElement();
		final String original = editorText.getText();
		// force translation
		final String text = RodinKeyboardPlugin.getDefault()
				.translate(original);
		if (ielement instanceof IIdentifierElement
				&& contentType.equals(RodinConfiguration.IDENTIFIER_TYPE)) {
			updateAttribute(ielement, IDENTIFIER_ATTRIBUTE, text);
		}
		if (ielement instanceof ILabeledElement
				&& contentType.equals(RodinConfiguration.LABEL_TYPE)
				|| contentType.equals(RodinConfiguration.BOLD_LABEL_TYPE)) {
			updateAttribute(ielement, LABEL_ATTRIBUTE, text);
		}
		if (ielement instanceof IExpressionElement
				&& contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
			updateAttribute(ielement, EXPRESSION_ATTRIBUTE, text);
		}
		if (ielement instanceof IPredicateElement
				&& contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
			updateAttribute(ielement, PREDICATE_ATTRIBUTE, text);
		}
		if (ielement instanceof IAssignmentElement
				&& contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
			updateAttribute(ielement, ASSIGNMENT_ATTRIBUTE, text);
		}
		if (ielement instanceof ICommentedElement
				&& contentType.equals(RodinConfiguration.COMMENT_TYPE)) {
			updateAttribute(ielement, COMMENT_ATTRIBUTE, text);
		}
	}
	
	public void updateAttribute(IInternalElement element, IAttributeType type,
			String textValue) {
		final IAttributeValue.String newValue;
		if (type == IDENTIFIER_ATTRIBUTE) {
			newValue = IDENTIFIER_ATTRIBUTE.makeValue(textValue);
		} else if (type == LABEL_ATTRIBUTE) {
			newValue = LABEL_ATTRIBUTE.makeValue(textValue);
		} else if (type == EXPRESSION_ATTRIBUTE) {
			newValue = EXPRESSION_ATTRIBUTE.makeValue(textValue);
		} else if (type == PREDICATE_ATTRIBUTE) {
			newValue = PREDICATE_ATTRIBUTE.makeValue(textValue);
		} else if (type == ASSIGNMENT_ATTRIBUTE) {
			newValue = ASSIGNMENT_ATTRIBUTE.makeValue(textValue);
		} else if (type == COMMENT_ATTRIBUTE) {
			newValue = COMMENT_ATTRIBUTE.makeValue(textValue);
		} else {
			newValue = null;
		}
		try {
			if (!element.hasAttribute(type)
					|| !element.getAttributeValue(type).equals(newValue)) {
				final AtomicOperation op = OperationFactory.changeAttribute(
						element, newValue);
				History.getInstance().addOperation(op);
			}
		} catch (RodinDBException e) {
			System.err
					.println("Problems occured when updating the database after attribute edition"
							+ e.getMessage());
		}
	}

	public void modelChanged(IAnnotationModel model) {
		// do nothing
	}

	public void modelChanged(AnnotationModelEvent event) {
		// react to folding of the editor

		if (event.getChangedAnnotations().length > 0 && editorText.isVisible()) {
			// adjust the location of the editor
			if (viewer.modelOffset2WidgetOffset(interval.getOffset()) > 0) {
				//editorText.setLocation(editorText.getLocation().x,
				//parent.getLocationAtOffset(viewer.modelOffset2WidgetOffset(interval.getOffset())).y);
			} else {
				// if the interval that is currently being edited is hidden from
				// view abort the editing
				abortEditing();
			}
		}
	}

	/**
	 * Resizes the editorText widget according to the text modifications.
	 */
	public void modifyText(ExtendedModifyEvent event) {
		final String text = editorText.getText();
		mapper.synchronizeInterval(interval, text);
		final int offset = interval.getOffset();
		final int end = interval.getLastIndex();
		final int height = getHeight(parent.getText(offset, end));
		editorText.setRedraw(false);
		if (!editorText.getText().isEmpty()) {
			final Rectangle textBounds = editorText.getTextBounds(0,
					editorText.getCharCount() - 1);
			editorText.setSize(Math.max(MARGIN, textBounds.width + MARGIN), height);
		}
		if (editorPos == null) {
			editorPos = parent.getLocationAtOffset(offset);
		}
		editorText.setLocation(editorPos);
		editorText.setRedraw(true);
	}

	private int getHeight(final String extracted) {
		final String regex = "(\r\n)|(\n)|(\r)";
		final Pattern pattern = Pattern.compile(regex);
		final String[] split = pattern.split(extracted);
		final int height = split.length * editorText.getLineHeight();
		return height;
	}

	public void verifyKey(VerifyEvent event) {
		if (contentProposal.isProposalPopupOpen()) {
			// do not add the return to the text
			event.doit = false;
			return;
		}
		if ((event.stateMask == SWT.NONE) && event.character == SWT.CR) {
			// do not add the return to the text
			event.doit = false;
			saveAndExit();
		}
		if (event.character == SWT.ESC) {
			abortEditing();
		}

	}

	/**
	 * 
	 * @return the interval that this editor is currently editing or
	 *         <code>null</code>, if the editor is not visible currently.
	 */
	public Interval getInterval() {
		return interval;
	}

	public void menuAboutToShow(IMenuManager manager) {

		for (IAction action : editActions) {
			if (action.getActionDefinitionId().equals(
					IWorkbenchCommandConstants.EDIT_COPY)
					|| action.getActionDefinitionId().equals(
							IWorkbenchCommandConstants.EDIT_CUT)) {
				action.setEnabled(editorText.getSelectionCount() > 0);
			}
			if (action.getActionDefinitionId().equals(
					IWorkbenchCommandConstants.EDIT_PASTE)) {
				// TODO: disable, if nothing to paste.
			}
			manager.add(action);
		}
	}

	public void createEditActions() {
		IAction action;
		action = new StyledTextEditAction(editorText, ST.COPY);
		action.setText("Copy");
		action.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);
		editActions.add(action);

		action = new StyledTextEditAction(editorText, ST.PASTE);
		action.setText("Paste");
		action.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);
		editActions.add(action);

		action = new StyledTextEditAction(editorText, ST.CUT);
		action.setText("Cut");
		action.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_CUT);
		editActions.add(action);
	}

	public void saveAndExit() {
		updateModelAfterChanges();
		abortEditing();
	}

	private void setEventBTranslation(Interval interval) {
		// TODO use attribute type
//		final IAttributeType attributeType = interval.getContentType()
//				.getAttributeType();
//		final boolean enable = (attributeType == EventBAttributes.PREDICATE_ATTRIBUTE || attributeType == EventBAttributes.ASSIGNMENT_ATTRIBUTE);
		
		// or better: add isMath() to IAttributeManipulation
		final IInternalElement element = interval.getElement().getElement();
		final boolean enable = (element instanceof IPredicateElement || element instanceof IAssignmentElement)
				&& interval.getContentType().equals(
						RodinConfiguration.CONTENT_TYPE);
		if (enable) {
			editorText.addModifyListener(eventBTranslator);
		}
	}

}
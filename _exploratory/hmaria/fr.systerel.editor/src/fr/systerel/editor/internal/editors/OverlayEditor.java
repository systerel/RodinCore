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

import static fr.systerel.editor.internal.editors.EditPos.computeEnd;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.COMMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.internal.ui.EventBUtils.getFormulaFactory;
import static org.eventb.internal.ui.autocompletion.ContentProposalFactory.getProposalProvider;
import static org.eventb.internal.ui.autocompletion.ContentProposalFactory.makeContentProposal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
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
import fr.systerel.editor.internal.actions.operations.AtomicOperation;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.actions.operations.RodinEditorHistory;
import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.documentModel.RodinTextStream;
import fr.systerel.editor.internal.presentation.IRodinColorConstant;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.RodinConfiguration.AttributeContentType;
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
	private static final int MARGIN = 15;

	private static enum EditType {
		TEXT {
			@Override
			public void doEdit(OverlayEditor editor, Interval interval,
					int pos) {
				editor.showEditorText(interval, pos);
			}
		}, BOOL {
			@Override
			public void doEdit(OverlayEditor editor, Interval interval,
					int pos) {
				editor.changeBooleanValue(interval);
			}
		}, MULTI {
			@Override
			public void doEdit(OverlayEditor editor, Interval interval,
					int pos) {
				editor.showTipMenu(interval);				
			}
		}, NONE {
			@Override
			public void doEdit(OverlayEditor editor, Interval interval,
					int pos) {
				// do nothing
			}
		};
		
		public abstract void doEdit(OverlayEditor editor, Interval interval, int pos);
		
		public static void handleEdit(OverlayEditor editor, Interval inter, int pos) {
			final EditType editType = computeEditType(inter);
			editType.doEdit(editor, inter, pos);
		}
		
		private static EditType computeEditType(Interval inter) {
			final ContentType contentType = inter.getContentType();
			if (!(contentType instanceof AttributeContentType)) {
				return EditType.NONE;
			}
			final IAttributeType attType = ((AttributeContentType) contentType)
					.getAttributeType();
			if (attType instanceof IAttributeType.Boolean) {
				return EditType.BOOL;
			} else if (inter.getPossibleValues() != null) {
				return EditType.MULTI;
			} else {
				return EditType.TEXT;
			}
		}

	}
	
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
			
			final ContentType contentType = inter.getContentType();
			IAttributeType attributeType = null;
			if (contentType instanceof AttributeContentType) {
				// FIXME null for predicate and assignment
				attributeType = ((AttributeContentType) contentType)
					.getAttributeType();
			}
			if (attributeType == null) {
				// return; FIXME temporary fix
				if (element instanceof IPredicateElement)
					attributeType = EventBAttributes.PREDICATE_ATTRIBUTE;
				else if (element instanceof IAssignmentElement)
					attributeType = EventBAttributes.ASSIGNMENT_ATTRIBUTE;
				else if (element instanceof IExpressionElement)
					attributeType = EventBAttributes.EXPRESSION_ATTRIBUTE;
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
	
	private static boolean modifyingText = false;

	private final ModifyListener eventBTranslator = RodinKeyboardPlugin
			.getDefault().createRodinModifyListener();

	private final ContentProposalManager contentProposal;
	
	private StyledText editorText;
	private Interval interval;
	private Map<Integer, IAction> editActions = new HashMap<Integer, IAction>();
	private Menu fTextContextMenu;
	
	/** A backup of the text contained on the opening of the editor. */
	private String originalText = "";

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
		
		interval = inter;
		if (!editorText.isVisible() && inter != null) {
			final int pos = editorToOverlayOffset(offset);
			EditType.handleEdit(this, inter, pos);
		}
	}
	
	private int editorToOverlayOffset(int offset) {
		final int startOffset = interval.getOffset();
		final int startLine = parent.getLineAtOffset(startOffset);
		final int targetLine = parent.getLineAtOffset(offset);
		final int overlayLine = targetLine - startLine;
		final int indentation = interval.getIndentation();
		return offset - startOffset - overlayLine * indentation;
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
		if (!inter.getContentType().getName().contains("comment"))
			setEventBTranslation(inter);
		final int start = viewer.modelOffset2WidgetOffset(inter.getOffset());
		final int end = computeEnd(start, inter.getLength());
		final String text;
		if (inter.getLength() > 0) {
			final String extracted = parent.getText(start, end);
			final int level = inter.getIndentation();
			final boolean multiLine = inter.isMultiLine();
			final boolean addWhiteSpace = inter.isAddWhiteSpace();
			text = RodinTextStream.deprocessMulti(level, multiLine,
					addWhiteSpace, extracted);
		} else {
			text = "";
		}
		originalText = text;
		final Point beginPt = (parent.getLocationAtOffset(start));
		textViewer.setDocument(createDocument(text));
		
		editorText.setCaretOffset(pos);
		editorText.setSize(editorText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		editorText.setLocation(beginPt.x - 2, beginPt.y); // -2 to place on text
		editorText.setFont(parent.getFont());
		editorText.setVisible(true);
		editorText.setFocus();
	}

	private void showTipMenu(final Interval inter) {
		final String[] possibleValues = inter.getPossibleValues();
		if (possibleValues == null) {
			return;
		}
		final Menu tipMenu = new Menu(parent);
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
			RodinEditorHistory.getInstance().addOperation(op);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	
	public void abortEdition() {
		if (!originalText.isEmpty())
			editorText.setText(originalText);
		quitEdition();
	}
	
	public void quitEdition() {
		editorText.removeModifyListener(eventBTranslator);
		setVisible(false);
		final int newEditorOffset = overlayToEditorOffset();
		parent.setCaretOffset(newEditorOffset);
		interval = null;
	}
	
	private int overlayToEditorOffset() {
		final int startOffset = interval.getOffset();
		final int overlayOffset = editorText.getCaretOffset();
		final int line = editorText.getLineAtOffset(overlayOffset);
		final int indentation = interval.getIndentation();
		return startOffset + overlayOffset + line * indentation;
	}
	
	public boolean isActive() {
		return editorText.isVisible();
	}
	
	public void setVisible(final boolean visible) {
		editorText.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				editorText.setVisible(visible);
			}
		});
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
		final String text;
		if (!contentType.equals(RodinConfiguration.COMMENT_TYPE)) {
			// force translation
			text = RodinKeyboardPlugin.getDefault().translate(original);
		} else {
			text = original;
		}
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
	
	public void updateAttribute(IInternalElement element, IAttributeType.String type,
			String textValue) {
		final IAttributeValue.String newValue = type.makeValue(textValue);
		try {
			if (!element.hasAttribute(type)
					|| !element.getAttributeValue(type).equals(newValue)) {
				final AtomicOperation op = OperationFactory.changeAttribute(
						element, newValue);
				RodinEditorHistory.getInstance().addOperation(op);
			}
		} catch (RodinDBException e) {
			System.err
					.println("Problems occured when updating the database after attribute edition"
							+ e.getMessage());
		}
	}

	@Override
	public void modelChanged(IAnnotationModel model) {
		// do nothing
	}

	@Override
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
				quitEdition();
			}
		}
	}

	/**
	 * Resizes the editorText widget according to the text modifications when
	 * the user edits the contents of this overlay editor.
	 */
	@Override
	public void modifyText(ExtendedModifyEvent event) {
		try {
			modifyingText = true;
		final String text = editorText.getText();
		modifyText(event, text, editorText, parent, mapper, interval, true);
		} finally {
			modifyingText = false;
		}
	}


	private static void modifyText(ExtendedModifyEvent event, String text,
			StyledText target, StyledText parent, DocumentMapper mapper,
			Interval inter, boolean redraw) {
//		mapper.synchronizeInterval(inter, text);
		final int offset = inter.getOffset();
		final int end = inter.getLastIndex();
		final int height = getHeight(parent.getText(offset, end), target);
		if (!redraw)
			return;
		target.setRedraw(false);
		if (!target.getText().isEmpty()) {
			final Rectangle textBounds = target.getTextBounds(0,
					target.getCharCount() - 1);
			target.setSize(Math.max(MARGIN, textBounds.width + MARGIN), height);
		}
		final Point editionPosition = parent.getLocationAtOffset(offset);
		target.setLocation(editionPosition);
		target.setRedraw(true);
	}

	public void refreshOverlayContents(DocumentEvent event) {
		final int offset = event.getOffset();
		if (isActive() && !modifyingText && interval.contains(offset)) {
			mapper.synchronizeIntervalWithoutModifyingDocument(interval, event);
			final int carPosBckp = editorText.getCaretOffset();
			editorText.setText(event.getText());
			final int edTextLength = editorText.getText().length();
			if (carPosBckp < edTextLength)
				editorText.setCaretOffset(carPosBckp);
			else
				editorText.setCaretOffset(edTextLength);
		}
	}
	
	private static int getHeight(final String extracted, StyledText reference) {
		final String regex = "(\r\n)|(\n)|(\r)";
		final Pattern pattern = Pattern.compile(regex);
		final String[] split = pattern.split(extracted);
		final int height = split.length * reference.getLineHeight();
		return height;
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		if (contentProposal.isProposalPopupOpen() && event.character == SWT.CR) {
			// do not add the return to the text
			event.doit = false;
			return;
		}
		if ((event.stateMask == SWT.NONE) && event.character == SWT.CR) {
			// do not add the return to the text
			event.doit = false;
			saveAndExit();
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

	@Override
	public void menuAboutToShow(IMenuManager manager) {
		for (IAction action : editActions.values()) {
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
		editActions.put(ST.COPY, action);

		action = new StyledTextEditAction(editorText, ST.PASTE);
		action.setText("Paste");
		action.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);
		editActions.put(ST.PASTE, action);

		action = new StyledTextEditAction(editorText, ST.CUT);
		action.setText("Cut");
		action.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_CUT);
		editActions.put(ST.CUT, action);
	}
	
	public IAction getOverlayAction(int actionConstant) {
		return editActions.get(actionConstant);
	}

	public void saveAndExit() {
		updateModelAfterChanges();
		quitEdition();
	}

	private void setEventBTranslation(Interval interval) {
		// TODO use attribute type
//		final IAttributeType attributeType = interval.getContentType()
//				.getAttributeType();
//		final boolean enable = (attributeType == EventBAttributes.PREDICATE_ATTRIBUTE || attributeType == EventBAttributes.ASSIGNMENT_ATTRIBUTE);
		
		// or better: add isMath() to IAttributeManipulation
		final IInternalElement element = interval.getElement().getElement();
		final boolean enable = (element instanceof IPredicateElement
				|| element instanceof IAssignmentElement || element instanceof IExpressionElement)
				&& interval.getContentType().equals(
						RodinConfiguration.CONTENT_TYPE);
		if (enable) {
			editorText.addModifyListener(eventBTranslator);
		}
	}

}
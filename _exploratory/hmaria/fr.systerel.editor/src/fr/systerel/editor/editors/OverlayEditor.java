/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.editor.editors;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eventb.core.IAssignmentElement;
import org.eventb.core.ICommentedElement;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.keyboard.RodinKeyboardPlugin;

import fr.systerel.editor.actions.StyledTextEditAction;
import fr.systerel.editor.contentAssist.RodinContentAssistProcessor;
import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.Interval;


/**
 * This class manages the little text field that is used to edit an element.
 */
public class OverlayEditor implements IAnnotationModelListener, IAnnotationModelListenerExtension, 
										ExtendedModifyListener, VerifyKeyListener, IMenuListener, PaintListener{
	private StyledText editorText;
	private DocumentMapper mapper;
	private StyledText parent;
	private ProjectionViewer viewer;
	private Interval interval;
	private static final int DEFAULT_WIDTH = 300;
	private ITextViewer textViewer;
	private IContentAssistant contentAssistant;
	private RodinEditor editor;
	private Menu fTextContextMenu;
	private ArrayList<IAction> editActions = new ArrayList<IAction>();
	private ModifyListener eventBTranslator;
	
	//counts the lines that were added to the underlying (parent) styled text
	private int addedLines = 0;
	
	public static final String EDITOR_TEXT_ID = RodinEditor.EDITOR_ID +".editorText";


	public OverlayEditor(StyledText parent, DocumentMapper mapper, ProjectionViewer viewer, RodinEditor editor) {
		this.viewer = viewer;
		this.mapper = mapper;
		this.parent = parent;
		this.editor = editor;
		
		textViewer = new TextViewer(parent, SWT.BORDER |SWT.V_SCROLL);
		contentAssistant = getContentAssistant();
		contentAssistant.install(textViewer);
		eventBTranslator = RodinKeyboardPlugin.getDefault().getRodinModifyListener();
		
		setupEditorText();
	}


	private void setupEditorText() {
		editorText = textViewer.getTextWidget();

		editorText.addVerifyKeyListener(this);

		editorText.setFont(parent.getFont());
		Point oldsize = parent.getSize();
		parent.pack();
		parent.setSize(oldsize);
		editorText.setVisible(false);
		editorText.addExtendedModifyListener(this);
		parent.addPaintListener(this);
		editorText.addModifyListener(eventBTranslator);

		createMenu();
		createEditActions();
	
		
		//the focus tracker is used to activate the handlers, when the widget has focus.
		IFocusService focusService =(IFocusService) editor.getSite().getService(IFocusService.class);
		focusService.addFocusTracker(editorText, EDITOR_TEXT_ID);
	}


	private void createMenu() {
		String id = "editorTextMenu";
		MenuManager manager= new MenuManager(id, id);
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(this);
		fTextContextMenu= manager.createContextMenu(editorText);

		editorText.setMenu(fTextContextMenu);
	}

	
	public void showAtOffset(int offset){
		Interval inter = mapper.findEditableInterval(viewer.widgetOffset2ModelOffset(offset));
		int pos = 0;
		if (inter != null) {
			pos = offset - inter.getOffset();
		}
		// if the overlay editor is currently shown,
		// save the content and show at the new location.
		if (editorText.isVisible()) {
			saveAndExit();
		}
		
		if (!editorText.isVisible()) {
			String text;
			if (inter != null) {
				setEventBTranslation(inter);
				int start = viewer.modelOffset2WidgetOffset(inter.getOffset());
				offset = start + pos;
				if (inter.getLength() > 0) {
					text = parent.getText(start, start +inter.getLength()-1);
				} else {
					text = "";
				}
				Point location = (parent.getLocationAtOffset(start));
				
				Point endPoint = new Point(findMaxWidth(start, start +inter.getLength()), parent.getLocationAtOffset(start +inter.getLength()).y);
				Point size = new Point(endPoint.x - location.x, endPoint.y - (location.y) +parent.getLineHeight());
				
				textViewer.setDocument(createDocument(text));
				editorText.setCaretOffset(offset - start);
				resizeTo(size.x, size.y);
				editorText.setFont(parent.getFont());
				setToLocation(location.x, location.y);
				editorText.setVisible(true);
				editorText.setFocus();
			}			
		}
		interval = inter;
	}
	
	/**
	 * Sets the editor text to a given location. The y-value will get slightly
	 * corrected. The y value should correspond to the start of a line in the
	 * underlying editor
	 * 
	 * @param x,
	 *            the x-value of the new location
	 * @param y,
	 *            the y-value of the new location
	 */
	public void setToLocation(int x, int y) {
		editorText.setLocation(x, y-2);
	}

	/**
	 * Resizes the editor. A margin will be added to the given width and height.
	 * 
	 * @param width
	 * @param height
	 */
	public void resizeTo(int width, int height) {
		int w = Math.max(width +5 +editorText.getVerticalBar().getSize().x, DEFAULT_WIDTH);
		int h = Math.max(height, editorText.getLineHeight() ) +4;
		
		adaptEditorLines(h);
		
		editorText.setSize(w, h);
	}

	/**
	 * Adds or removes lines in the underlying editor in order not to cover up
	 * its content with the overlay editor.
	 * 
	 * @param new_height
	 *            The new height of the editor
	 */
	private void adaptEditorLines(int new_height) {
		//need to add lines?
		if (new_height > editorText.getSize().y && new_height > editorText.getLineHeight()+4) {
			Point location = new Point(0, editorText.getLocation().y +editorText.getSize().y);
			int offset = parent.getOffsetAtLocation(location);
			int line = parent.getLineAtOffset(offset);
			int start = parent.getOffsetAtLine(line);
			parent.replaceTextRange(start, 0, System.getProperty("line.separator"));
			addedLines++;
		//need to remove lines?
		} else if (new_height < editorText.getSize().y && addedLines > 0 ) {
			Point location = new Point(0, editorText.getLocation().y +editorText.getSize().y);
			int offset = parent.getOffsetAtLocation(location);
			int line = parent.getLineAtOffset(offset);
			int start = parent.getOffsetAtLine(line-1);
			int end = parent.getOffsetAtLine(line);
			parent.replaceTextRange(start, end-start, "");
			addedLines--;
		}
	}
	
	public void abortEditing() {
		editorText.setVisible(false);
		interval = null;
		addedLines = 0;
		
	}

	
	public void addChangeToDatabase() {
		if (interval != null) {
			//TODO: check if there really are changes. if not, do nothing.
			IRodinElement element = interval.getElement();
			String contentType= interval.getContentType();
			String text = removeWhiteSpaces(editorText.getText());
			if (element instanceof IIdentifierElement && contentType.equals(RodinConfiguration.IDENTIFIER_TYPE)) {
				try {
					((IIdentifierElement) element).setIdentifierString(text, null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (element instanceof ILabeledElement && contentType.equals(RodinConfiguration.IDENTIFIER_TYPE)) {
				try {
					((ILabeledElement) element).setLabel(text, null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (element instanceof IPredicateElement && contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
				try {
					((IPredicateElement) element).setPredicateString(text, null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (element instanceof IAssignmentElement && contentType.equals(RodinConfiguration.CONTENT_TYPE)) {
				try {
					((IAssignmentElement) element).setAssignmentString(text, null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (element instanceof ICommentedElement && contentType.equals(RodinConfiguration.COMMENT_TYPE)) {
				try {
					((ICommentedElement) element).setComment(text, null);
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	/**
	 * Removes whitespaces at beginning and end of a text.
	 * 
	 * @return the text with the whitespaces removed
	 */
	public String removeWhiteSpaces(String text) {
		
		int first_pos = -1;
		int last_pos = -1;
		int i = 0;
		for (char ch : text.toCharArray()) {
			if (first_pos == -1 && !isWhitespace(ch)) {
				first_pos = i;
			}
			if (last_pos == -1 && isWhitespace(ch)){
				last_pos = i;
			}
			if (last_pos != -1 && !isWhitespace(ch)){
				last_pos = -1;
			}
			i++;
		}
		first_pos = Math.max(first_pos, 0);
		last_pos = (last_pos == -1 ) ? (text.length()) : last_pos;
		return (text.substring(first_pos, last_pos));
	}
	
	protected boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

	
	/**
	 * finds the maximum with of the the text in the parent widget within a
	 * given range.
	 * 
	 * @param start
	 * @param end
	 * @return the maximum with in pixels.
	 */
	protected int findMaxWidth(int start, int end){
		int firstLine = parent.getLineAtOffset(start);
		int lastLine = parent.getLineAtOffset(end);
		int max = 0;
		for (int i = firstLine; i <= lastLine; i++) {
			max = Math.max(parent.getLocationAtOffset(parent.getOffsetAtLine(i+1)-1).x, max);
		}
		
		return max;
	}
	
	protected IDocument createDocument(String text) {
		IDocument doc = new Document();
		doc.set(text);
		return doc;
	}




	public void modelChanged(IAnnotationModel model) {
		// do nothing
	}


	public void modelChanged(AnnotationModelEvent event) {
		// react to folding of the editor
		
		if (event.getChangedAnnotations().length >0 && editorText.isVisible()) {
			//adjust the location of the editor
			if (viewer.modelOffset2WidgetOffset(interval.getOffset()) > 0) {
//				setToLocation(editorText.getLocation().x, parent.getLocationAtOffset(viewer.modelOffset2WidgetOffset(interval.getOffset())).y);
			} else {
				// if the interval that is currently being edited is hidden from view
				// abort the editing
				abortEditing();
			}
		}
	}


	/**
	 * Resizes the editorText widget according to the text modifications.
	 */
	public void modifyText(ExtendedModifyEvent event) {
		int max = 0;
		for (int i = 0; i < editorText.getLineCount()-1; i++) {
			int offset = editorText.getOffsetAtLine(i +1)-1;
			max = Math.max(max, editorText.getLocationAtOffset(offset).x);
		}
		//last line
		max = Math.max(max, editorText.getLocationAtOffset(editorText.getCharCount()).x);
		int height = editorText.getLineCount() * editorText.getLineHeight();
		resizeTo(max, height);
		
	}
	
	public IContentAssistant getContentAssistant() {

		ContentAssistant assistant= new ContentAssistant();
//		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		assistant.setContentAssistProcessor(new RodinContentAssistProcessor(mapper, this), Document.DEFAULT_CONTENT_TYPE);

		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);

		return assistant;
	}


	public void verifyKey(VerifyEvent event) {
		if ((event.stateMask == SWT.NONE)  && event.character == SWT.CR) {
			//do not add the return to the text
			event.doit = false;
			saveAndExit();
		}
		if (event.character == SWT.ESC) {
			abortEditing();
		}
		
		
		if ((event.stateMask & SWT.CTRL) != 0 && event.character == '\u0020') {
			contentAssistant.showPossibleCompletions();
		}
			
	}

	/**
	 *
	 * @return the interval that this editor is currently editing
	 * or <code>null</code>, if the editor is not visible currently.
	 */
	public Interval getInterval() {
		return interval;
	}


	public void menuAboutToShow(IMenuManager manager) {
		
		for (IAction action : editActions) {
			if (action.getActionDefinitionId().equals(IWorkbenchActionDefinitionIds.COPY)
					|| action.getActionDefinitionId().equals(IWorkbenchActionDefinitionIds.CUT)) {
				action.setEnabled(editorText.getSelectionCount() > 0);
			}
			if (action.getActionDefinitionId().equals(IWorkbenchActionDefinitionIds.PASTE)) {
				//TODO: disable, if nothing to paste.
			}
			manager.add(action);
			
		}
	}
	
	public void createEditActions() {
		IAction action;
		action= new StyledTextEditAction(editorText, ST.COPY);
		action.setText("Copy");
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);
		editActions.add(action);

		action= new StyledTextEditAction(editorText, ST.PASTE);
		action.setText("Paste");
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);
		editActions.add(action);

		action= new StyledTextEditAction(editorText, ST.CUT);
		action.setText("Cut");
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.CUT);
		editActions.add(action);
		
	}
	
	public void saveAndExit() {
		addChangeToDatabase();
		abortEditing();
	}



	public void paintControl(PaintEvent e) {
		if (interval != null) {
			if (viewer.modelOffset2WidgetOffset(interval.getOffset()) > 0) {
				setToLocation(editorText.getLocation().x, parent.getLocationAtOffset(viewer.modelOffset2WidgetOffset(interval.getOffset())).y);
			} 
		}
		
	}

	public void setEventBTranslation(Interval interval) {
		boolean enable = (interval.getElement() instanceof IPredicateElement ||
				interval.getElement() instanceof IAssignmentElement ) 
				&& interval.getContentType().equals(RodinConfiguration.CONTENT_TYPE);
		RodinKeyboardPlugin.getDefault().enableRodinModifyListener(enable);
		
	}
	
	

	
}
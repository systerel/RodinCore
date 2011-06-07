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
package fr.systerel.editor.editors;

import java.util.HashMap;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.operations.OperationHistoryActionHandler;
import org.eclipse.ui.texteditor.IAbstractTextEditorHelpContextIds;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.keyboard.preferences.PreferenceConstants;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.actions.HistoryAction;
import fr.systerel.editor.actions.HistoryAction.Redo;
import fr.systerel.editor.actions.HistoryAction.Undo;
import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.documentModel.MarkerAnnotationPosition;
import fr.systerel.editor.documentModel.RodinDocumentProvider;
import fr.systerel.editor.operations.OperationFactory;
import fr.systerel.editor.operations.RodinFileUndoContext;
import fr.systerel.editor.presentation.ColorManager;
import fr.systerel.editor.presentation.RodinConfiguration;

public class RodinEditor extends TextEditor {

	public static final String EDITOR_ID = EditorPlugin.PLUGIN_ID
			+ ".editors.RodinEditor";
	public static final String EDITOR_SCOPE = EditorPlugin.PLUGIN_ID
			+ ".contexts.rodinEditorScope";

	private final ColorManager colorManager = new ColorManager();
	private final DocumentMapper mapper = new DocumentMapper();
	private final RodinDocumentProvider documentProvider;

	private StyledText styledText;
	private ProjectionSupport projectionSupport;
	private ProjectionAnnotationModel projectionAnnotationModel;
	private OverlayEditor overlayEditor;
	private IAnnotationModel visualAnnotationModel;
	private Annotation[] oldPojectionAnnotations = new Annotation[0];
	private Annotation[] oldMarkers = new Annotation[0];

	private ProjectionViewer viewer;
	private IElementStateListener stateListener;
	private CursorManager cursorManager;
	private DNDManager dndManager;

	private IUndoContext  undoContext;
	private SelectionController selController;

	// private Menu fTextContextMenu;

	public RodinEditor() {
		super();
		setEditorContextMenuId(EDITOR_ID);
		setSourceViewerConfiguration(new RodinConfiguration(colorManager,
				mapper));
		documentProvider = new RodinDocumentProvider(mapper, this);
		setDocumentProvider(documentProvider);
		stateListener = EditorElementStateListener.getNewListener(this,
				documentProvider);
		documentProvider.addElementStateListener(stateListener);
	}

	public void dispose() {
		close(false);
		colorManager.dispose();
		if (stateListener != null)
			documentProvider.removeElementStateListener(stateListener);
		documentProvider.unloadResource();
		super.dispose();
	}

	public Composite getTextComposite() {
		return styledText;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		viewer = (ProjectionViewer) getSourceViewer();
		projectionSupport = new ProjectionSupport(viewer,
				getAnnotationAccess(), getSharedColors());
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
		projectionAnnotationModel = viewer.getProjectionAnnotationModel();
		visualAnnotationModel = viewer.getVisualAnnotationModel();
		styledText = viewer.getTextWidget();

		overlayEditor = new OverlayEditor(styledText, mapper, viewer, this);
		projectionAnnotationModel.addAnnotationModelListener(overlayEditor);
		selController = new SelectionController(styledText, mapper, viewer,
				overlayEditor);
		styledText.addMouseListener(selController);
		styledText.addVerifyKeyListener(selController);
		styledText.addTraverseListener(selController);
		dndManager = new DNDManager(selController, styledText, mapper,
				documentProvider);
		dndManager.install();
		
		cursorManager = new CursorManager(this, viewer);
		styledText.addMouseMoveListener(cursorManager);

		final Font font = JFaceResources
				.getFont(PreferenceConstants.RODIN_MATH_FONT);
		styledText.setFont(font);

		updateFoldingStructure();
		updateMarkerStructure();
		final IEventBRoot inputRoot = documentProvider.getInputRoot();
		setTitleImage(inputRoot);

		viewer.setUndoManager(new RodinUndoManager((RodinFileUndoContext) getUndoContext()));
		
		
		// Activate Event-B Editor Context
		final IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		
		if (inputRoot instanceof IMachineRoot) {
			contextService.activateContext(EditorPlugin.PLUGIN_ID
					+ ".contexts.rodinEditorMachineScope");
		} else if (inputRoot instanceof IContextRoot) {
			contextService.activateContext(EditorPlugin.PLUGIN_ID
					+ ".contexts.rodinEditorContextScope");
		}
		contextService.activateContext(EditorPlugin.PLUGIN_ID
				+ ".contexts.rodinEditorDefaultScope");
	}
	
	/**
	 * It is mandatory to remove the actions so that the commands contributed
	 * through extension points are taken into account.
	 */
	@Override
	protected void createActions() {
		super.createActions();
		removeAction(ActionFactory.CUT.getId());
		removeAction(ActionFactory.COPY.getId());
		removeAction(ActionFactory.PASTE.getId());
		removeAction(ActionFactory.DELETE.getId());
		removeAction(ITextEditorActionConstants.SHIFT_RIGHT);
		removeAction(ITextEditorActionConstants.SHIFT_LEFT);
		removeAction(ITextEditorActionConstants.MOVE_LINE_DOWN);
		removeAction(ITextEditorActionConstants.MOVE_LINE_UP);
	}

	private void removeAction(String actionId) {
		if (actionId == null)
			return;
		setAction(actionId, null);
	}
	
	private IUndoContext getUndoContext() {
		final IInternalElement inputRoot = getInputRoot();
		if (inputRoot != null) {
			undoContext = OperationFactory.getRodinFileUndoContext(inputRoot);
		}
		return undoContext;
	}

	@Override
	protected void createUndoRedoActions() {
		if (getUndoContext() != null) {
			// Create the undo action
			final IWorkbenchWindow ww = getEditorSite().getWorkbenchWindow();
			final Undo undoAction = new HistoryAction.Undo(ww);
			PlatformUI
					.getWorkbench()
					.getHelpSystem()
					.setHelp(undoAction,
							IAbstractTextEditorHelpContextIds.UNDO_ACTION);
			undoAction
					.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_UNDO);
			registerUndoRedoAction(ITextEditorActionConstants.UNDO, undoAction);
			undoAction.setEnabled(false);

			// Create the redo action.
			final Redo redoAction = new HistoryAction.Redo(ww);
			PlatformUI
					.getWorkbench()
					.getHelpSystem()
					.setHelp(redoAction,
							IAbstractTextEditorHelpContextIds.REDO_ACTION);
			redoAction
					.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_REDO);
			registerUndoRedoAction(ITextEditorActionConstants.REDO, redoAction);
			redoAction.setEnabled(false);
		}
	}

	/**
	 * Pushed down from AbstractTextEditor
	 */
	private void registerUndoRedoAction(String actionId, HistoryAction action) {
		IAction oldAction = getAction(actionId);
		if (oldAction instanceof OperationHistoryActionHandler)
			((OperationHistoryActionHandler) oldAction).dispose();
		if (action == null)
			return;
		setAction(actionId, action);
		final IActionBars actionBars = getEditorSite().getActionBars();
		if (actionBars != null)
			actionBars.setGlobalActionHandler(actionId, action);
	}

	private void setTitleImage(IEventBRoot inputRoot) {
		final IInternalElementType<?> rootType = inputRoot.getElementType();
		String img = null;
		if (rootType == IMachineRoot.ELEMENT_TYPE) {
			img = IEventBSharedImages.IMG_MACHINE;
		} else if (rootType == IContextRoot.ELEMENT_TYPE) {
			img = IEventBSharedImages.IMG_CONTEXT;
		}
		if (img != null) {
			final ImageRegistry imgReg = EventBUIPlugin.getDefault()
					.getImageRegistry();
			setTitleImage(imgReg.get(img));
		}
	}

	/**
	 * Creates a projection viewer to allow folding
	 */
	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		final ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);

		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	/**
	 * Replaces the old folding structure with the current one.
	 */
	public void updateFoldingStructure() {
		for (Annotation a : oldPojectionAnnotations) {
			projectionAnnotationModel.removeAnnotation(a);
		}
		final Position[] positions = mapper.getFoldingPositions();
		final Annotation[] annotations = mapper.getFoldingAnnotations();
		Assert.isLegal(annotations.length == positions.length);
		// TODO use AnnotationModel.replaceAnnotations(Annotation[], Map)
		for (int i = 0; i < positions.length; i++) {
			projectionAnnotationModel.addAnnotation(annotations[i],
					positions[i]);
		}
		oldPojectionAnnotations = annotations;
	}

	/**
	 * Replaces the old marker structure with this new one.
	 */
	public void updateMarkerStructure() {
		final MarkerAnnotationPosition[] markers = documentProvider
				.getMarkerAnnotations();
		final Annotation[] annotations = new Annotation[markers.length];
		// this will hold the new annotations along
		// with their corresponding positions
		final HashMap<Annotation, Position> newAnnotations = new HashMap<Annotation, Position>();
		int i = 0;
		for (Annotation annotation : oldMarkers) {
			visualAnnotationModel.removeAnnotation(annotation);
		}
		for (MarkerAnnotationPosition marker : markers) {
			annotations[i] = marker.getAnnotation();
			newAnnotations.put(marker.getAnnotation(), marker.getPosition());
			visualAnnotationModel.addAnnotation(marker.getAnnotation(),
					marker.getPosition());
			i++;
		}

		oldMarkers = annotations;

	}

	/**
	 * Sets the selection. If the selection is a <code>IRodinElement</code> the
	 * corresponding area in the editor is highlighted
	 */
	protected void doSetSelection(ISelection selection) {
		super.doSetSelection(selection);
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			final Interval interval = mapper
					.findInterval((IRodinElement) ((IStructuredSelection) selection)
							.getFirstElement());
			if (interval != null) {
				setHighlightRange(interval.getOffset(), interval.getLength(),
						true);
			}
		}
	}
	
	public void resync(final IProgressMonitor monitor) {
		if (styledText != null && !styledText.isDisposed()) {
			final Display display = styledText.getDisplay();
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (styledText.isDisposed()) {
						return;
					}
					final int currentOffset = getCurrentOffset();
					final ILElement[] sel = selController.getSelectedElements();
					documentProvider.synchronizeRoot(monitor);
					selectAndReveal(currentOffset, 0);
					selController.selectItems(sel);
				}
			});
		}
	}

	public DocumentMapper getDocumentMapper() {
		return mapper;
	}

	public int getCurrentOffset() {
		return styledText.getCaretOffset();
	}

	public IInternalElement getInputRoot() {
		return documentProvider.getInputRoot();
	}
	
	public IDocument getDocument() {
		return documentProvider.getDocument();
	}
	
	public SelectionController getSelectionController() {
		return selController;
	}

}

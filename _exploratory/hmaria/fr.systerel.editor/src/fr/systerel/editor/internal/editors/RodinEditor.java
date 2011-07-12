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
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.api.itf.ILFile;
import org.rodinp.keyboard.preferences.PreferenceConstants;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.actions.HistoryAction;
import fr.systerel.editor.internal.actions.HistoryAction.Redo;
import fr.systerel.editor.internal.actions.HistoryAction.Undo;
import fr.systerel.editor.internal.actions.operations.OperationFactory;
import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.documentModel.RodinDocumentProvider;
import fr.systerel.editor.internal.presentation.ColorManager;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.updaters.ProblemMarkerAnnotationsUpdater;

public class RodinEditor extends TextEditor {

	public static boolean DEBUG;
	
	public static final String EDITOR_ID = EditorPlugin.PLUGIN_ID
			+ ".editors.RodinEditor";
	public static final String EDITOR_SCOPE = EditorPlugin.PLUGIN_ID
			+ ".contexts.rodinEditorScope";

	private final RodinDocumentProvider documentProvider;
	private ColorManager colorManager;
	private DocumentMapper mapper;
	private RodinConfiguration rodinViewerConfiguration;
	
	private IElementStateListener stateListener;
	private CursorManager cursorManager;
	private DNDManager dndManager;
	private IUndoContext  undoContext;
	
	/** The overlay editor to edit elements and attributes */
	private OverlayEditor overlayEditor;

	/** The source viewer on which projection for folding is enabled */
	private ProjectionViewer viewer;
	/** The support for folding on the viewer */
	private ProjectionSupport projectionSupport;
	/** The annotation model containing folding annotations */
	private ProjectionAnnotationModel projectionAnnotationModel;
	/** The basic annotations currently carried by the source viewer */
	private Annotation[] oldPojectionAnnotations = new Annotation[0];
	/** The graphical text component carried by the viewer */
	private StyledText styledText;
	/** A controller for selection on the styled text */
	private SelectionController selController;
	/** The viewer's model of basic annotations (e.g. problem annotations) */
	private IAnnotationModel annotationModel;
	/** An updater for problem annotations which listens to the resource changes */
	private ProblemMarkerAnnotationsUpdater markerAnnotationsUpdater;
	/** A listener to update overlay editor's contents in case of indirect typing modification (e.g. undo-redo) */
	private OverlayBackModificationUpdater overlayUpdater;

	public RodinEditor() {
		setEditorContextMenuId(EDITOR_ID);
		documentProvider = new RodinDocumentProvider(mapper, this);
		setDocumentProvider(documentProvider);
		stateListener = EditorElementStateListener.getNewListener(this,
				documentProvider);
		documentProvider.addElementStateListener(stateListener);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		activateAppropriateContext();
		viewer = (ProjectionViewer) getSourceViewer();
		projectionSupport = new ProjectionSupport(viewer,
				getAnnotationAccess(), getSharedColors());
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
		projectionAnnotationModel = viewer.getProjectionAnnotationModel();
		annotationModel = viewer.getAnnotationModel();
		if (markerAnnotationsUpdater == null)
			markerAnnotationsUpdater = new ProblemMarkerAnnotationsUpdater(
					this, annotationModel);
	
		styledText = viewer.getTextWidget();
	
		overlayEditor = new OverlayEditor(styledText, mapper, viewer, this);
		overlayUpdater = new OverlayBackModificationUpdater(overlayEditor);
		getDocument().addDocumentListener(overlayUpdater);
		
		projectionAnnotationModel.addAnnotationModelListener(overlayEditor);
		selController = new SelectionController(styledText, mapper, viewer,
				overlayEditor);
		styledText.addMouseListener(selController);
		styledText.addVerifyKeyListener(selController);
		dndManager = new DNDManager(selController, styledText, mapper,
				documentProvider);
		dndManager.install();
	
		cursorManager = new CursorManager(this, viewer);
		styledText.addMouseMoveListener(cursorManager);
	
		final Font font = JFaceResources
				.getFont(PreferenceConstants.RODIN_MATH_FONT);
		styledText.setFont(font);
	
		updateFoldingStructure();
		markerAnnotationsUpdater.initializeMarkersAnnotations();
	
		setTitleImageAndPartName();
	}

	private void setTitleImageAndPartName() {
		final IEventBRoot inputRoot = documentProvider.getInputRoot();
		final IInternalElementType<?> rootType = inputRoot.getElementType();
		String img = null;
		setPartName(inputRoot.getComponentName());
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

	@Override
	public void dispose() {
		close(false);
		colorManager.dispose();
		markerAnnotationsUpdater.dispose();
		if (stateListener != null) {
			documentProvider.removeElementStateListener(stateListener);
		}
		getDocument().removeDocumentListener(overlayUpdater);
		documentProvider.unloadResource();
		super.dispose();
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		colorManager = new ColorManager();
		mapper = new DocumentMapper();
		rodinViewerConfiguration = new RodinConfiguration(colorManager, mapper);
		setSourceViewerConfiguration(rodinViewerConfiguration);
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

	private void activateAppropriateContext() {
		// Activate Event-B Editor Context
		final IContextService contextService = (IContextService) getSite()
		.getService(IContextService.class);
		final IInternalElement inputRoot = documentProvider.getInputRoot();
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
		removeAction(ActionFactory.REFRESH.getId());
		removeAction(ITextEditorActionConstants.SHIFT_RIGHT);
		removeAction(ITextEditorActionConstants.SHIFT_LEFT);
		removeAction(ITextEditorActionConstants.MOVE_LINE_DOWN);
		removeAction(ITextEditorActionConstants.MOVE_LINE_UP);
		removeAction(ITextEditorActionDefinitionIds.SELECT_LINE_UP);
		removeAction(ITextEditorActionDefinitionIds.SELECT_LINE_DOWN);
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

	/**
	 * Pushed down from AbstractTextEditor
	 */
	private void registerUndoRedoAction(String actionId, HistoryAction action) {
		final IAction oldAction = getAction(actionId);
		if (oldAction instanceof OperationHistoryActionHandler)
			((OperationHistoryActionHandler) oldAction).dispose();
		if (action == null)
			return;
		setAction(actionId, action);
		final IActionBars actionBars = getEditorSite().getActionBars();
		if (actionBars != null)
			actionBars.setGlobalActionHandler(actionId, action);
	}

	/**
	 * Sets the selection. If the selection is a <code>IRodinElement</code> the
	 * corresponding area in the editor is highlighted
	 */
	@Override
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

	/**
	 * Aborts the current overlay edition. The modification are not saved. This
	 * has no effect if the overlay is inactive.
	 */
	public void abordEdition() {
		if (overlayEditor.isActive()) {
			overlayEditor.abortEdition();
		}
	}

	public int getCurrentOffset() {
		return styledText.getCaretOffset();
	}

	public DocumentMapper getDocumentMapper() {
		return mapper;
	}
	
	@Override
	public RodinDocumentProvider getDocumentProvider() {
		return documentProvider;
	}

	public IDocument getDocument() {
		return documentProvider.getDocument();
	}

	public IInternalElement getInputRoot() {
		return documentProvider.getInputRoot();
	}
	
	/** Returns the registered action or <code>null</code> if not found */
	public IAction getOverlayEditorAction(int actionConstant) {
		return overlayEditor.getOverlayAction(actionConstant);
	}

	public ILFile getResource() {
		return documentProvider.getResource();
	}
	
	public SelectionController getSelectionController() {
		return selController;
	}

	public StyledText getStyledText() {
		return styledText;
	}

	/** Tells if the overlay is currently visible as the user is editing */
	public boolean isOverlayActive() {
		return overlayEditor.isActive();
	}

	/** Saves the current changes and quit edition. */
	public void quitAndSaveEdition() {
		if (overlayEditor.isActive())
			overlayEditor.updateModelAfterChanges();
	}

	/**
	 * Refreshes the editor and avoids making the document dirty if the
	 * parameter <code>silent</code> is <code>true</code>. Indeed, this
	 * parameter represents the fact that nothing shall have changed (i.e. the
	 * user can not save it, as it is not supposed to be anything to save,
	 * typically in case of the "refresh" of the editor). Note: this is
	 * necessary, as the resynchronisation will make the underlying document
	 * change, even if there is no change in the rodin database.
	 */
	public void resync(final IProgressMonitor monitor, final boolean silent) {
		if (styledText != null && !styledText.isDisposed()) {
			final int currentOffset = getCurrentOffset();
			final int topIndex = styledText.getTopIndex();
			final ILElement[] selection = selController.getSelectedElements();
			final Display display = styledText.getDisplay();
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (styledText.isDisposed()) {
						return;
					}
					final long start = System.currentTimeMillis();
					if (DEBUG)
						System.out.println("\\ Start refreshing Rodin Editor.");
					documentProvider.synchronizeRoot(monitor, silent);
					updateFoldingStructure();
					updateMarkerStructure();
					styledText.setTopIndex(topIndex);
					styledText.setCaretOffset(currentOffset);
					selController.selectItems(selection);
					if (DEBUG) {
						System.out
								.println("\\ Finished refreshing Rodin Editor.");
						final long time = System.currentTimeMillis() - start;
						System.out.println("\\ Elapsed time : " + time + "ms.");
					}
				}
			});
		}
	}

	public void reveal(EditPos pos) {
		selectAndReveal(pos.getOffset(), 0, pos.getOffset(), pos.getLength());
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
	 * Recalculates the old marker structure.
	 */
	public void updateMarkerStructure() {
		markerAnnotationsUpdater.recalculateAnnotations();
	}

}

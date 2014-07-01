/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.editors;

import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.TOGGLE_OVERWRITE;
import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.swt.IFocusService;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILFile;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.actions.operations.EditorActionTarget;
import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.documentModel.RodinDocumentProvider;
import fr.systerel.editor.internal.presentation.RodinConfiguration;
import fr.systerel.editor.internal.presentation.updaters.ProblemMarkerAnnotationsUpdater;

public class RodinEditor extends TextEditor {

	public static boolean DEBUG;
	
	public static final String EDITOR_ID = EditorPlugin.PLUGIN_ID
			+ ".editors.RodinEditor";
	public static final String EDITOR_SCOPE = EditorPlugin.PLUGIN_ID
			+ ".contexts.rodinEditorScope";

	private final RodinDocumentProvider documentProvider;
	private DocumentMapper mapper;
	private RodinConfiguration rodinViewerConfiguration;
	
	private DNDManager dndManager;
	
	/** The overlay editor to edit elements and attributes */
	private OverlayEditor overlayEditor;

	/** The source viewer on which projection for folding is enabled */
	private ProjectionViewer viewer;
	/** The font used by the underlying viewer */
	private Font font;
	/** The graphical text component carried by the viewer */
	private StyledText styledText;
	
	///** The support for folding on the viewer */
	//private ProjectionSupport projectionSupport;
	///** The annotation model containing folding annotations */
	//private ProjectionAnnotationModel projectionAnnotationModel;
	///** The basic annotations currently carried by the source viewer */
	//private Annotation[] oldPojectionAnnotations = new Annotation[0];

	/** A controller for selection on the styled text */
	private SelectionController selController;
	/** An updater for problem annotations which listens to the resource changes */
	private ProblemMarkerAnnotationsUpdater markerAnnotationsUpdater;

	private IContextActivation specificContext;

	private IContextActivation defaultContext;

	private ContextMenuSimplifier contextMenuSimplifier;

	private EditorActionTarget editorActionTarget;

	private HistoryActionUpdater historyActionUpdater;

	private ShowImplicitElementStateManager showImplicitStateManager;
	
	public RodinEditor() {
		setEditorContextMenuId(EDITOR_ID);
		documentProvider = new RodinDocumentProvider(mapper, this);
		setDocumentProvider(documentProvider);
		showImplicitStateManager = new ShowImplicitElementStateManager(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		activateRodinEditorContext();
		viewer = (ProjectionViewer) getSourceViewer();
//		projectionSupport = new ProjectionSupport(viewer,
//				getAnnotationAccess(), getSharedColors());
//		projectionSupport.install();
//		viewer.doOperation(ProjectionViewer.TOGGLE);
//		projectionAnnotationModel = viewer.getProjectionAnnotationModel();
		if (markerAnnotationsUpdater == null)
			markerAnnotationsUpdater = new ProblemMarkerAnnotationsUpdater(this);
	
		styledText = viewer.getTextWidget();
		font = JFaceResources.getFont(RODIN_MATH_FONT);
		styledText.setFont(font);
	
		overlayEditor = new OverlayEditor(styledText, mapper, viewer, this);
		editorActionTarget = new EditorActionTarget(this);
		
		selController = new SelectionController(styledText, mapper, viewer,
				overlayEditor);
		getSite().setSelectionProvider(selController);
		selController.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelectionDependentActions();
			}
		});
		styledText.addMouseListener(selController);
		styledText.addVerifyKeyListener(selController);
		dndManager = new DNDManager(selController, styledText, mapper,
				documentProvider);
		dndManager.install();
		makeWideCaret();
		
		markerAnnotationsUpdater.initializeMarkersAnnotations();
		getDocument().addDocumentListener(markerAnnotationsUpdater);
	
		setTitleImageAndPartName();
		contextMenuSimplifier = ContextMenuSimplifier.startSimplifying(styledText.getMenu());
		// the focus tracker is used to activate the handlers, when the widget
		// has focus and the editor is not in overlay mode.
		final IFocusService focusService = (IFocusService) getSite()
				.getService(IFocusService.class);
		focusService.addFocusTracker(styledText, EDITOR_ID);
		viewer.setEditable(false);
		styledText.setEditable(false);
		
		historyActionUpdater =  new HistoryActionUpdater(this);
		historyActionUpdater.startListening();
		showImplicitStateManager.register();
	}
	
	/**
	 * Set a global action handler for a undo/redo action and add the given part
	 * listener.
	 * */
	private void setGlobalHistoryActions(String actionId) {
		final IAction action = getAction(actionId);
		setAction(actionId, action);
		final IActionBars actionBars= getEditorSite().getActionBars();
		if (actionBars != null)
			actionBars.setGlobalActionHandler(actionId, action);
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		// Deactivate unused actions
		setAction(ITextEditorActionConstants.SMART_ENTER, null);
		setAction(ITextEditorActionConstants.SMART_ENTER_INVERSE, null);
		setAction(ITextEditorActionConstants.MOVE_LINE_UP, null);
		setAction(ITextEditorActionConstants.MOVE_LINE_DOWN, null);
		setAction(ITextEditorActionConstants.SHIFT_LEFT, null);
		setAction(ITextEditorActionConstants.SHIFT_RIGHT, null);
		setAction(ITextEditorActionConstants.DELETE_LINE, null);
	}

	/**
	 * Let the caret be wider by using the 'insert overwrite' caret of the basic
	 * text editor. It was chosen to not create a custom caret, but rather reuse
	 * the action of enabling the overwrite mode, which changes the form of the
	 * caret (i.e. what is expected here). This is indeed allowed as the Rodin
	 * Editor presents a non-editable text.
	 */
	private void makeWideCaret() {
		final IAction action = getAction(TOGGLE_OVERWRITE);
		final ActionHandler actionHandler = new ActionHandler(action);
		try {
			actionHandler.execute(new ExecutionEvent());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		if (historyActionUpdater!=null) {
			historyActionUpdater.finishListening();
		}
		RodinEditorUtils.flushHistory(this);
		close(false);
		if (contextMenuSimplifier != null) {
			contextMenuSimplifier.finishSimplifying();
		}
		if (markerAnnotationsUpdater != null) {
			getDocument().removeDocumentListener(markerAnnotationsUpdater);
			markerAnnotationsUpdater.dispose();
		}
		if (showImplicitStateManager != null) {
			showImplicitStateManager.unregister();
		}
		documentProvider.unloadResource();
		deactivateRodinEditorContext();
		super.dispose();
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		mapper = new DocumentMapper();
		rodinViewerConfiguration = new RodinConfiguration(this);
		setSourceViewerConfiguration(rodinViewerConfiguration);
	}
	
	@Override
	protected void initializeDragAndDrop(ISourceViewer viewer) {
		// Removing the drag and drop initialization done above by the
		// AbstractTextEditor
	}
	
	/**
	 * Tells the callers that the editor is editable even if its main text
	 * component is not.
	 */
	@Override
	public boolean isEditorInputModifiable() {
		return true;
	}

	/**
	 * Tells the callers that the editor is editable even if its main text
	 * component is not.
	 */
	@Override
	public boolean isEditable() {
		return true;
	}

	/**
	 * Updates the state of the given editor input such as read-only flag.
	 */
	@Override
	protected void updateState(IEditorInput input) {
		// Do nothing. This editor viewer is read-only and that shall not change
	}
	
	@Override
	protected void validateState(IEditorInput input) {
		// Do nothing. This editor viewer is read-only and that shall not change
	}
	
	/**
	 * Let editor actions be redirected (programmatically) to the right target
	 * depending on the current mode (i.e. in overlay mode or not). Overriding
	 * these actions at the handler level is not possible.
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ITextOperationTarget.class.equals(adapter)) {
			return getEditorActionTarget();
		}
		return super.getAdapter(adapter);
	}
	
	
	private Object getEditorActionTarget() {
		if (editorActionTarget == null)
			editorActionTarget = new EditorActionTarget(this);
		return editorActionTarget;
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

	public void activateRodinEditorContext() {
		// Activate Event-B Editor Context
		final IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		final IInternalElement inputRoot = documentProvider.getInputRoot();
		if (inputRoot instanceof IMachineRoot) {
			specificContext = contextService
					.activateContext(EditorPlugin.PLUGIN_ID
							+ ".contexts.rodinEditorMachineScope");
		} else if (inputRoot instanceof IContextRoot) {
			specificContext = contextService
					.activateContext(EditorPlugin.PLUGIN_ID
							+ ".contexts.rodinEditorContextScope");
		}
		defaultContext = contextService.activateContext(EditorPlugin.PLUGIN_ID
				+ ".contexts.rodinEditorDefaultScope");
	}
	
	public void deactivateRodinEditorContext() {
		final IContextService contextService = (IContextService) getSite()
				.getService(IContextService.class);
		if (specificContext != null)
			contextService.deactivateContext(specificContext);
		if (defaultContext != null)
			contextService.deactivateContext(defaultContext);
	}

	/**
	 * Deactivating the undo manager on the source viewer.
	 * Retarget UNDO/REDO actions using TextEditorOperation actions.
	 */
	@Override
	protected void createUndoRedoActions() {
		final ISourceViewer sourceViewer = getSourceViewer();
		sourceViewer.setUndoManager(null);
		super.createUndoRedoActions();
		setGlobalHistoryActions();
	}

	private void setGlobalHistoryActions() {
		setGlobalHistoryActions(ITextEditorActionConstants.UNDO);
		setGlobalHistoryActions(ITextEditorActionConstants.REDO);
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
	 * <p>
	 * This method can be called from outside the UI thred.
	 * </p>
	 */
	public void abordEdition() {
		if (styledText != null && !styledText.isDisposed()) {
			final Display display = styledText.getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					if (overlayEditor.isActive()) {
						overlayEditor.abortEdition(true);
					}

				};
			});
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

	public OverlayEditor getOverlayEditor() {
		return overlayEditor;
	}

	public IDocument getDocument() {
		return documentProvider.getDocument();
	}

	public IEventBRoot getInputRoot() {
		return documentProvider.getInputRoot();
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
		return overlayEditor != null && overlayEditor.isActive();
	}
	
	/**
	 * Updates a single action if it implements {@link IUpdate}.
	 *
	 * @param actionId
	 *            the id of the action to update
	 */
	public void updateAction(String actionId) {
		final IAction action = getAction(actionId);
		if (action instanceof IUpdate) {
			((IUpdate) action).update();
		}
	}
	
	/**
	 * Refreshes the enablement of editor selection dependent actions. This
	 * update is mandatory to redirect actions to the right implementation
	 * depending on the editor selection.
	 */
	public void updateSelectionDependentActions() {
		super.updateSelectionDependentActions();
	}
	
	/**
	 * Refreshes the enablement of editor property dependent actions. This
	 * update is mandatory to redirect actions (especially UNDO and REDO
	 * actions) to the right implementation depending on the editor selection.
	 */
	public void updatePropertyDependentActions() {
		super.updatePropertyDependentActions();
	}
	
	public void reveal(EditPos pos) {
		selectAndReveal(pos.getOffset(), 0, pos.getOffset(), pos.getLength());
	}

	public SourceViewer getViewer() {
		return viewer;
	}

	public void toggleShowImplicitElements() {
		showImplicitStateManager.toggle();
	}

	public boolean isShowImplicitElements() {
		return showImplicitStateManager.getValue();
	}
	
//	/**
//	 * Replaces the old folding structure with the current one.
//	 */
//	public void updateFoldingStructure() {
//		for (Annotation a : oldPojectionAnnotations) {
//			projectionAnnotationModel.removeAnnotation(a);
//		}
//		final Position[] positions = mapper.getFoldingPositions();
//		final Annotation[] annotations = mapper.getFoldingAnnotations();
//		Assert.isLegal(annotations.length == positions.length);
//		// TODO use AnnotationModel.replaceAnnotations(Annotation[], Map)
//		for (int i = 0; i < positions.length; i++) {
//			projectionAnnotationModel.addAnnotation(annotations[i],
//					positions[i]);
//		}
//		oldPojectionAnnotations = annotations;
//	}

//	/**
//	 * Recalculates the old marker structure.
//	 */
//	public void updateMarkerStructure() {
//		markerAnnotationsUpdater.recalculateAnnotations();
//	}

}

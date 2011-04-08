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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.keyboard.preferences.PreferenceConstants;

import fr.systerel.editor.actions.DeleteAction;
import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.documentModel.MarkerAnnotationPosition;
import fr.systerel.editor.documentModel.RodinDocumentProvider;
import fr.systerel.editor.presentation.ColorManager;
import fr.systerel.editor.presentation.RodinConfiguration;

public class RodinEditor extends TextEditor {

	public static final String EDITOR_ID = "fr.systerel.editor.editors.RodinEditor";

	private ColorManager colorManager;
	private StyledText styledText;
	private DocumentMapper mapper = new DocumentMapper();
	private ProjectionSupport projectionSupport;
	private ProjectionAnnotationModel annotationModel;
	private OverlayEditor overlayEditor;
	private IAnnotationModel visualAnnotationModel;
	private Annotation[] oldAnnotations;
	private Annotation[] oldMarkers = new Annotation[0];
	private RodinDocumentProvider documentProvider;

	public RodinEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new RodinConfiguration(colorManager,
				mapper));
		documentProvider = new RodinDocumentProvider(mapper, this);
		setDocumentProvider(documentProvider);
		setElementStateListener();
	}

	protected void createActions() {
		super.createActions();
		IAction action = new DeleteAction(this);
		action.setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		action.setText("Delete");
		setAction(ITextEditorActionConstants.DELETE, action);
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// obtain a foldable editor
		ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
		projectionSupport = new ProjectionSupport(viewer,
				getAnnotationAccess(), getSharedColors());
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
		annotationModel = viewer.getProjectionAnnotationModel();
		visualAnnotationModel = viewer.getVisualAnnotationModel();

		styledText = getSourceViewer().getTextWidget();

		overlayEditor = new OverlayEditor(styledText, mapper, viewer, this);
		annotationModel.addAnnotationModelListener(overlayEditor);
		SelectionController controller = new SelectionController(styledText,
				mapper, viewer, overlayEditor);
		styledText.addMouseListener(controller);
		styledText.addVerifyKeyListener(controller);
		styledText.addTraverseListener(controller);
		Font font = JFaceResources.getFont(PreferenceConstants.RODIN_MATH_FONT);
		styledText.setFont(font);
		// TODO
		ButtonManager buttonManager = new ButtonManager(mapper, styledText,
				viewer, this);
		buttonManager.createButtons();

		updateFoldingStructure(documentProvider.getFoldingRegions());
		updateMarkerStructure(documentProvider.getMarkerAnnotations());
		
		setTitleImage(documentProvider.getInputRoot());
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
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);

		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	/**
	 * Replaces the old folding structure with this new one.
	 * 
	 * @param positions
	 *            The new positions
	 */
	public void updateFoldingStructure(Position[] positions) {

		positions = mapper.getFoldingPositions();
		
		// Annotation[] annotations = new Annotation[positions.length];
		ProjectionAnnotation[] annotations = mapper.getFoldingAnnotations();

		// this will hold the new annotations along
		// with their corresponding positions
		HashMap<Annotation, Position> newAnnotations = new HashMap<Annotation, Position>();
		boolean collapsed = false;

		int i = 0;
		for (Position position : positions) {
			// ProjectionAnnotation annotation = new
			// ProjectionAnnotation(collapsed);

			newAnnotations.put(annotations[i], new Position(position.offset,
					position.length));

			// annotations[i]=annotation;
			i++;
		}

		//annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);

		oldAnnotations = annotations;
	}

	/**
	 * Replaces the old marker structure with this new one.
	 * 
	 * @param markers
	 *            The new markers
	 */
	public void updateMarkerStructure(MarkerAnnotationPosition[] markers) {
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

	private void setElementStateListener() {
		documentProvider.addElementStateListener(new IElementStateListener() {

			public void elementContentAboutToBeReplaced(Object element) {
				// do nothing
			}

			public void elementContentReplaced(Object element) {
				documentProvider.setCanSaveDocument(documentProvider
						.getEditorInput());
				updateFoldingStructure(documentProvider.getFoldingRegions());
				updateMarkerStructure(documentProvider.getMarkerAnnotations());

			}

			public void elementDeleted(Object element) {
				// do nothing
			}

			public void elementDirtyStateChanged(Object element, boolean isDirty) {
				// do nothing
			}

			public void elementMoved(Object originalElement, Object movedElement) {
				// do nothing
			}
			
		});
	}

	public DocumentMapper getDocumentMapper() {
		return mapper;
	}
	
	
	
}

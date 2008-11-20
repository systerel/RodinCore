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

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.internal.win32.DOCINFO;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.documentModel.MarkerAnnotationPosition;
import fr.systerel.editor.documentModel.RodinDocumentProvider;

public class RodinEditor extends TextEditor implements IElementChangedListener {

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
	private int topIndex;
	public static final String EDITOR_ID = "fr.systerel.editor.editors.RodinEditor";


	public RodinEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new RodinConfiguration(colorManager, mapper));
		documentProvider = new RodinDocumentProvider(mapper, this);
		setDocumentProvider(documentProvider);
		setElementStateListener();
		RodinCore.addElementChangedListener(this);
		
	}

	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
		
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		//obtain a foldable editor
		ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
		projectionSupport = new ProjectionSupport(viewer,getAnnotationAccess(),getSharedColors());
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
		annotationModel = viewer.getProjectionAnnotationModel();
		visualAnnotationModel = viewer.getVisualAnnotationModel();
		
		styledText = getSourceViewer().getTextWidget();
		
		overlayEditor = new OverlayEditor(styledText, mapper, viewer, this);
		annotationModel.addAnnotationModelListener(overlayEditor);
		SelectionController controller = new SelectionController(styledText, mapper, viewer, overlayEditor);
//		styledText.addSelectionListener(controller);
		styledText.addKeyListener(controller);
//		styledText.addVerifyListener(controller);
		styledText.addMouseListener(controller);
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		styledText.setFont(font);
			
		updateFoldingStructure(documentProvider.getFoldingRegions());
		updateMarkerStructure(documentProvider.getMarkerAnnotations());
	}
	
	
	/**
	 * Creates a projection viewer to allow folding
	 */
    protected ISourceViewer createSourceViewer(Composite parent,
            IVerticalRuler ruler, int styles)
    {
        ISourceViewer viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
   
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
	public void updateFoldingStructure(Position[] positions){
		Annotation[] annotations = new Annotation[positions.length];
		
		//this will hold the new annotations along
		//with their corresponding positions
		HashMap<Annotation, Position> newAnnotations = new HashMap<Annotation, Position>();
		
		int i = 0;
		for(Position position : positions){
			ProjectionAnnotation annotation = new ProjectionAnnotation(false);
			
			newAnnotations.put(annotation, position);
			
			annotations[i]=annotation;
			i++;
		}
		
		annotationModel.modifyAnnotations(oldAnnotations,newAnnotations,null);
		
		oldAnnotations=annotations;
	}
	
	
	/**
	 * Replaces the old marker structure with this new one.
	 * 
	 * @param markers
	 *            The new markers
	 */
	public void updateMarkerStructure(MarkerAnnotationPosition[] markers){
		Annotation[] annotations = new Annotation[markers.length];
		
		//this will hold the new annotations along
		//with their corresponding positions
		HashMap<Annotation, Position> newAnnotations = new HashMap<Annotation, Position>();
		
		int i = 0;
		
		for (Annotation annotation : oldMarkers) {
			visualAnnotationModel.removeAnnotation(annotation);
		}
		
		for(MarkerAnnotationPosition marker : markers){
			annotations[i] = marker.getAnnotation();
			newAnnotations.put(marker.getAnnotation(), marker.getPosition());
			visualAnnotationModel.addAnnotation(marker.getAnnotation(), marker.getPosition());
			i++;
		}
		
		oldMarkers=annotations;
		
	}
	

	/**
	 * Sets the selection. If the selection is a <code>IRodinElement</code>
	 * the corresponding area in the editor is highlighted
	 */
	protected void doSetSelection(ISelection selection) {
		super.doSetSelection(selection);
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Interval interval = mapper.findInterval((IRodinElement) ((IStructuredSelection)selection).getFirstElement());
			if (interval != null) {
				setHighlightRange(interval.getOffset(), interval.getLength(), true);
			}
		}
	}
	
	/**
	 * Reacts to changes in the databases.
	 * The editor is refreshed.
	 */
	public void elementChanged(ElementChangedEvent event) {
		DeltaProcessor proc = new DeltaProcessor(event.getDelta(), documentProvider.getInputRoot());
		if (proc.isMustRefresh()) {
			performRevert();
		} else if (proc.isMustRefreshMarkers()) {
			updateMarkerStructure(documentProvider.getMarkerAnnotations());
		}
		
	}

	private void setElementStateListener() {
		documentProvider.addElementStateListener(new IElementStateListener() {

			@Override
			public void elementContentAboutToBeReplaced(Object element) {
				// do nothing
			}

			@Override
			public void elementContentReplaced(Object element) {
//				setHighlightRange(topIndex, 0, false);
				
				documentProvider.setCanSaveDocument(documentProvider.getEditorInput());
				updateFoldingStructure(documentProvider.getFoldingRegions());
				updateMarkerStructure(documentProvider.getMarkerAnnotations());
				
			}

			@Override
			public void elementDeleted(Object element) {
				// do nothing
			}

			@Override
			public void elementDirtyStateChanged(Object element, boolean isDirty) {
				// do nothing
			}

			@Override
			public void elementMoved(Object originalElement, Object movedElement) {
				// do nothing
			}
			
		});
	}
	
	
	
}

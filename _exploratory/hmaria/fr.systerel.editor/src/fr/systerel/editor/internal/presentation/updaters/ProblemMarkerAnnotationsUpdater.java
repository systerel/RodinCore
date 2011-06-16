/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.presentation.updaters;

import static fr.systerel.editor.internal.presentation.updaters.IEditorMarkerConstants.FORMULA_CHAR_END;
import static fr.systerel.editor.internal.presentation.updaters.IEditorMarkerConstants.FORMULA_CHAR_START;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.RodinEditor;

public class ProblemMarkerAnnotationsUpdater {

	/** The workspace. */
	private IWorkspace workspace;
	/** The resource. */
	private IResource resource;
	/** The associated Rodin Editor */
	private final RodinEditor editor;
	/** The annotation model to keep coherent */
	private final IAnnotationModel annotationModel;
	/** The map of marker and annotations */
	private Map<IMarker, Annotation> directAnnotationAccess = new HashMap<IMarker, Annotation>();

	private ResourceChangeListener listener = new ResourceChangeListener();
	
	private boolean isUpdating = false;
	
	/**
	 * Internal resource change listener.
	 */
	class ResourceChangeListener implements IResourceChangeListener {
	
		public void resourceChanged(IResourceChangeEvent e) {
			final IResourceDelta delta = e.getDelta();
			if (delta != null && resource != null) {
				IResourceDelta child = delta.findMember(resource.getFullPath());
				if (child != null) {
					update(child.getMarkerDeltas());
				}
			}
		}

	}

	private void update(IMarkerDelta[] markerDeltas) {
		for (IMarkerDelta d : markerDeltas) {
			if (d.getKind() == IResourceDelta.REMOVED) {
				recalculateAnnotations();	
				break;
			}
		}
	}

	private void removeMarkerAnnotations() {
		((IAnnotationModelExtension) annotationModel)
				.replaceAnnotations(
						(Annotation[]) directAnnotationAccess.values().toArray(
								new Annotation[directAnnotationAccess.values()
										.size()]), null);
		directAnnotationAccess.clear();
	}
	
	/**
	 * Creates an annotation for the given marker and adds it to this model.
	 * 
	 * @param marker
	 *            the marker
	 */
	protected final void addMarkerAnnotation(IMarker marker) {
		final Point p = findPoint(marker);
		final Annotation annotation = createMarkerAnnotation(marker);
		final Position finalPos = updateMarkerPosition(marker, p);
		if (annotation != null) {
			annotationModel.addAnnotation(annotation, finalPos);
			directAnnotationAccess.put(marker, annotation);
		}
		displayInfo(marker, "ADD");
	}

	private void displayInfo(IMarker marker, String context) {
		try {
			System.out.println("=============" + context + "===========");
			System.out.println("*********MARKERSTART***"
					+ marker.getAttribute(IMarker.CHAR_START));
			System.out.println("*********MARKEREND*****"
					+ marker.getAttribute(IMarker.CHAR_END));
			System.out.println("*****MARKERMESSAGE*****"
					+ marker.getAttribute(IMarker.MESSAGE));
			System.out.println("*********ID**************"+ marker.getId());
			System.out.println("=======================================");
		} catch (CoreException e) {
			System.out.println("FAILED TO GET MARKER INFO");
		}
	}

	private Position updateMarkerPosition(IMarker marker, Point p) {
		final IDocument document = editor.getDocument();
		try {
			if (p != null) {
				final int charStart = p.x;
				int lineNumber = document.getLineOfOffset(charStart) + 1;
				final int charEnd = p.y;
				updateMarkerInfo(marker, lineNumber, charStart, charEnd);
				return DocumentMapper.toPosition(p);
			} else {
				final IInternalElement inputRoot = editor.getInputRoot();
				final DocumentMapper mapper = editor.getDocumentMapper();
				final EditorElement rootEditorElement = mapper
						.findEditorElement(inputRoot);
				final int offset = rootEditorElement.getOffset();
				final int length = rootEditorElement.getLength();
				
				final Position pos = new Position(offset, length);
				updateMarkerInfo(marker, document.getLineOfOffset(offset) + 1,
						offset, offset + length - 1);
				return pos;
			}
		} catch (BadLocationException e) {
			// ignore failure
		}
		return DocumentMapper.toPosition(p);
	}
	
	private void updateMarkerInfo(IMarker marker, int lineNumber,
			int charStart, int charEnd) {
		try {
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			marker.setAttribute(IMarker.CHAR_START, charStart);
			marker.setAttribute(IMarker.CHAR_END, charEnd);
		} catch (CoreException e) {
			// ignore failure
		}
	}

	private Annotation createMarkerAnnotation(IMarker marker) {
		return new SimpleMarkerAnnotation(marker);
	}

	public ProblemMarkerAnnotationsUpdater(RodinEditor rodinEditor,
			IAnnotationModel annotationModel) {
		this.editor = rodinEditor;
		this.resource = editor.getInputRoot().getResource();
		assert (resource != null);
		this.workspace = resource.getWorkspace();
		this.annotationModel = annotationModel;
		workspace.addResourceChangeListener(listener);
	}
	
	

	public void initializeMarkersAnnotations() {
		final IInternalElement inputRoot = editor.getInputRoot();
		if (!(inputRoot instanceof IEventBRoot)) {
			return;
		}
		final IResource file = ((IEventBRoot) inputRoot).getResource();
		try {
			final IMarker[] markers = file.findMarkers(
					RodinMarkerUtil.RODIN_PROBLEM_MARKER, true,
					IResource.DEPTH_INFINITE);
			for (IMarker marker : markers) {
				addMarkerAnnotation(marker);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finds the point in the document for a given marker.
	 * 
	 * @param marker
	 * @return the point of the element corresponding to the marker inside
	 *         the document.
	 */
	private Point findPoint(IMarker marker) {
		final IRodinElement element = RodinMarkerUtil.getElement(marker);
		final DocumentMapper documentMapper = editor.getDocumentMapper();
		final EditorElement eElement = documentMapper.findEditorElement(element);
		if (eElement == null) {
			// not an internal location
			return null;
		}
		final IAttributeType attr = RodinMarkerUtil.getAttributeType(marker);
		if (attr == null) {
			// not an attribute location
			return documentMapper.getEnclosingPoint(eElement);
		}
		final Interval interval = eElement.getInterval(attr);
		if (interval == null) {
			return null;
		}
		final int charStart = RodinMarkerUtil.getCharStart(marker);
		final int charEnd = RodinMarkerUtil.getCharEnd(marker);
		if (charStart < 0 || charEnd < 0) {
			// not an attribute substring location
			return new Point(interval.getOffset(), interval.getLength());
		}
		
		return getSubstringPosition(marker, interval, charStart, charEnd);
	}

	private Point getSubstringPosition(IMarker marker, Interval interval,
			int charStart, int charEnd) {
		int fStart = marker.getAttribute(FORMULA_CHAR_START, -1);
		int fEnd = marker.getAttribute(FORMULA_CHAR_END, -1);
		if (fStart < 0 || fEnd < 0) {
			// first access, standard start and end are formula based
			fStart = charStart;
			fEnd = charEnd;
			// store for future use
			// standard start and end will become editor based
			try {
				marker.setAttribute(FORMULA_CHAR_START, fStart);
				marker.setAttribute(FORMULA_CHAR_END, fEnd);
			} catch (CoreException e) {
				// ignore failure
			}
		}
		final int offset = interval.getOffset();
		final int pStart = offset + fStart;
		final int pEnd = offset + fEnd;
		return new Point(pStart, pEnd);
	}

	public void recalculateAnnotations() {
		if (isUpdating)
			return;
		try {
			isUpdating = true;
			editor.getSite().getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					removeMarkerAnnotations();
					initializeMarkersAnnotations();
				}
			});
		} finally {
			isUpdating = false;
		}
	}

}

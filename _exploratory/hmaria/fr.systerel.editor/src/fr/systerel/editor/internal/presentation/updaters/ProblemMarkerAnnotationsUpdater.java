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

import static fr.systerel.editor.internal.editors.EditPos.isValidStartEnd;
import static fr.systerel.editor.internal.editors.EditPos.newPosOffLen;
import static fr.systerel.editor.internal.editors.EditPos.newPosStartEnd;
import static fr.systerel.editor.internal.presentation.updaters.IEditorMarkerConstants.FORMULA_BASED;
import static fr.systerel.editor.internal.presentation.updaters.IEditorMarkerConstants.FORMULA_CHAR_END;
import static fr.systerel.editor.internal.presentation.updaters.IEditorMarkerConstants.FORMULA_CHAR_START;

import java.util.Iterator;

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
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;

import fr.systerel.editor.internal.documentModel.DocumentMapper;
import fr.systerel.editor.internal.documentModel.EditorElement;
import fr.systerel.editor.internal.documentModel.Interval;
import fr.systerel.editor.internal.editors.EditPos;
import fr.systerel.editor.internal.editors.RodinEditor;
import fr.systerel.editor.internal.presentation.RodinProblemAnnotation;

public class ProblemMarkerAnnotationsUpdater {
	
	/** Tracing debug option */
	public static boolean DEBUG;
	
	private final static String BASIC_TEXT_ANNOTATION_ERROR_TYPE = "org.eclipse.ui.workbench.texteditor.error";
	private final static String BASIC_TEXT_ANNOTATION_WARNING_TYPE = "org.eclipse.ui.workbench.texteditor.warning";
	
	/** The workspace. */
	private IWorkspace workspace;
	/** The resource. */
	private IResource resource;
	/** The associated Rodin Editor */
	private final RodinEditor editor;
	/** The annotation model to keep coherent */
	private final IAnnotationModel annotationModel;
	/** The map of marker and annotations */
	private ResourceChangeListener listener = new ResourceChangeListener();
	
	/**
	 * Internal resource change listener.
	 */
	class ResourceChangeListener implements IResourceChangeListener {
	
		@Override
		public void resourceChanged(IResourceChangeEvent e) {
			final IResourceDelta delta = e.getDelta();
			if (delta != null && resource != null) {
				final IResourceDelta child = delta.findMember(resource
						.getFullPath());
				if (child != null) {
					update(child.getMarkerDeltas());
				}
			}
		}

	}

	private void update(IMarkerDelta[] markerDeltas) {
		for (IMarkerDelta d : markerDeltas) {
			if (d.getKind() == IResourceDelta.REMOVED
					|| d.getKind() == IResourceDelta.ADDED) {
				recalculateAnnotations();
				break;
			}
		}
	}

	private void removeMarkerAnnotations() {
		final Iterator<?> itr = annotationModel.getAnnotationIterator();
		while (itr.hasNext()) {
			final Object next = itr.next();
			/*
			 * FIXME this is a workaround to remove the basic (i.e. formula
			 * based) warning and error annotations to be printed as they are
			 * put in the annotationModel, but we don't want them.
			 * */ 
			if (next instanceof MarkerAnnotation) {
				final String type = ((MarkerAnnotation) next).getType();
				if (type.equals(BASIC_TEXT_ANNOTATION_ERROR_TYPE) || type.equals(BASIC_TEXT_ANNOTATION_WARNING_TYPE)) {
					annotationModel.removeAnnotation((Annotation) next);
				}
			}
			// we remove all the Rodin Problem annotations 
			if (next instanceof RodinProblemAnnotation) {
				annotationModel.removeAnnotation((Annotation) next);
			}	
		}
	}
	
	/**
	 * Creates an annotation for the given marker and adds it to this model.
	 * 
	 * @param marker
	 *            the marker
	 */
	protected final void addMarkerAnnotation(IMarker marker) {
		final EditPos p = findPoint(marker);
		final Annotation annotation = createMarkerAnnotation(marker);
		final EditPos finalPos = updateMarkerPosition(marker, p);
		if (annotation != null && finalPos != null) {
			annotationModel.addAnnotation(annotation, finalPos.toPosition());
		}
		if (DEBUG)
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

	private EditPos updateMarkerPosition(IMarker marker, EditPos p) {
		final IDocument document = editor.getDocument();
		try {
			if (p != null) {
				final int lineNumber = document.getLineOfOffset(p.getStart()) + 1;
				updateMarkerInfo(marker, lineNumber, p);
				return p;
			} else {
				final IInternalElement inputRoot = editor.getInputRoot();
				final DocumentMapper mapper = editor.getDocumentMapper();
				final EditorElement rootEditorElement = mapper
						.findEditorElement(inputRoot);
				if (rootEditorElement == null)
					return p;
				final Interval interval = rootEditorElement
						.getInterval(EventBAttributes.LABEL_ATTRIBUTE);
				final EditPos pos;
				if (interval == null) {
					pos = rootEditorElement.getPos();
				} else {
					pos = newPosOffLen(interval.getOffset(),
							interval.getLength());
				}
				updateMarkerInfo(marker,
						document.getLineOfOffset(pos.getOffset()) + 1, pos);
				return pos;
			}
		} catch (BadLocationException e) {
			// ignore failure
		}
		return p;
	}
	
	private void updateMarkerInfo(IMarker marker, int lineNumber,
			EditPos pos) {
		try {
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			marker.setAttribute(IMarker.CHAR_START, pos.getStart());
			// IMarker.CHAR_END claims it is exclusive
			// but it behaves inclusively (like EditPos)
			marker.setAttribute(IMarker.CHAR_END, pos.getEnd());
		} catch (CoreException e) {
			// ignore failure
		}
	}

	private Annotation createMarkerAnnotation(IMarker marker) {
		return new RodinProblemAnnotation(marker);
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
		removeMarkerAnnotations();
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
				final IRodinElement elem = RodinMarkerUtil.getElement(marker);
				if (!elem.exists()) {
					marker.delete();
				}
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
	private EditPos findPoint(IMarker marker) {
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
			return eElement.getPos();
		}
		final Interval interval = eElement.getInterval(attr);
		if (interval == null) {
			return null;
		}
		final int charStart = RodinMarkerUtil.getCharStart(marker);
		// char end is exclusive with Rodin markers
		final int charEnd = RodinMarkerUtil.getCharEnd(marker);
		
		if (!isFormulaBasedMarkerSet(marker)) {
			if (!isValidStartEnd(charStart, charEnd, false)) {
				setFormulaBased(marker, false);
				// not an attribute substring location
				return newPosOffLen(interval.getOffset(), interval.getLength());
			}
			setFormulaBased(marker, true);
		}
		if (isFormulaBasedMarker(marker)) {
			if (isFormulaStartEndSet(marker)) {
				int fStart = marker.getAttribute(FORMULA_CHAR_START, -1);
				int fEnd = marker.getAttribute(FORMULA_CHAR_END, -1);
				return getNewSubstringPosition(marker, interval, fStart, fEnd);
			}
			return getSubstringPosition(marker, interval, charStart, charEnd);				
		}
		return newPosOffLen(interval.getOffset(), interval.getLength());
	}

	private static void setFormulaBased(IMarker marker, boolean value) {
		try {
			marker.setAttribute(FORMULA_BASED, value);
		} catch (CoreException e) {
			// ignore
		}
	}
	
	private static boolean isFormulaBasedMarkerSet(IMarker marker) {
		try {
			return marker.getAttribute(FORMULA_BASED) != null;
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}
	
	private static boolean isFormulaBasedMarker(IMarker marker) {
		return marker.getAttribute(FORMULA_BASED, false);
	}

	private static boolean isFormulaStartEndSet(IMarker marker) {
		final boolean b = marker.getAttribute(FORMULA_CHAR_START, -1) >= 0;
		return b;
	}

	private EditPos getSubstringPosition(IMarker marker, Interval interval,
			int charStart, int charEnd) {
		int fStart = marker.getAttribute(FORMULA_CHAR_START, -1);
		int fEnd = marker.getAttribute(FORMULA_CHAR_END, -1);
		if (fStart < 0 || fEnd < 0) {
			// first access, standard start and end are formula based
			fStart = charStart;
			fEnd = charEnd - 1;
			// store for future use
			// standard start and end will become editor based
			try {
				marker.setAttribute(FORMULA_CHAR_START, fStart);
				marker.setAttribute(FORMULA_CHAR_END, fEnd);
			} catch (CoreException e) {
				// ignore failure
			}
		}
		return getNewSubstringPosition(marker, interval, fStart, fEnd);
	}
	
	private static EditPos getNewSubstringPosition(IMarker marker,
			Interval interval, int fStart, int fEnd) {
		final int offset = interval.getOffset();
		final int pStart = offset + fStart;
		final int pEnd = offset + fEnd;
		return newPosStartEnd(pStart, pEnd);
	}

	public void recalculateAnnotations() {
		editor.getSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				initializeMarkersAnnotations();
			}
		});
	}

	public void dispose() {
		workspace.removeResourceChangeListener(listener);
	}

}

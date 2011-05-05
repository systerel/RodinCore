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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;
import org.rodinp.core.emf.lightcore.sync.SynchroUtils;
import org.rodinp.keyboard.preferences.PreferenceConstants;

import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.EditorElement;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.documentModel.MarkerAnnotationPosition;
import fr.systerel.editor.documentModel.RodinDocumentProvider;
import fr.systerel.editor.presentation.ColorManager;
import fr.systerel.editor.presentation.RodinConfiguration;

public class RodinEditor extends TextEditor {

	public static final String EDITOR_ID = "fr.systerel.editor.editors.RodinEditor";

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
		final SelectionController controller = new SelectionController(
				styledText, mapper, viewer, overlayEditor);
		styledText.addMouseListener(controller);
		styledText.addVerifyKeyListener(controller);
		styledText.addTraverseListener(controller);
		setupDND(controller);

		cursorManager = new CursorManager(this, viewer);
		styledText.addMouseMoveListener(cursorManager);

		final Font font = JFaceResources
				.getFont(PreferenceConstants.RODIN_MATH_FONT);
		styledText.setFont(font);

		// ButtonManager buttonManager = new ButtonManager(mapper, styledText,
		// viewer, this);
		// buttonManager.createButtons();

		// TODO Folding is broken REPAIR
		// updateFoldingStructure(documentProvider.getFoldingRegions());
		updateMarkerStructure(documentProvider.getMarkerAnnotations());
		setTitleImage(documentProvider.getInputRoot());
	}

	@SuppressWarnings("restriction")
	private void setupDND(final SelectionController controller) {
		styledText.setDragDetect(false);
		// remove standard DND
		styledText.setData(DND.DRAG_SOURCE_KEY, null);
		styledText.setData(DND.DROP_TARGET_KEY, null);

		final DragSource source = new DragSource(styledText, DND.DROP_COPY
				| DND.DROP_MOVE);
		source.setTransfer(new Transfer[] { RodinHandleTransfer.getInstance() });
		source.addDragListener(new DragSourceAdapter() {

			public void dragStart(DragSourceEvent e) {
				e.doit = controller.getSelectedElement() != null;
				System.out.println("drag start " + e.doit);
			}

			public void dragSetData(DragSourceEvent e) {
				final IInternalElement element = controller
						.getSelectedElement().getElement();
				e.data = new IRodinElement[] {element};
				System.out.println("set data " + e.data);
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				System.out.println("drag finished");
			}
			
		});
		final DropTarget target = new DropTarget(styledText, DND.DROP_DEFAULT
				| DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		target.setTransfer(new Transfer[] { RodinHandleTransfer.getInstance() });
		target.addDropListener(new DropTargetAdapter() {
			public void dragEnter(DropTargetEvent e) {
				System.out.println("drag enter" + e);
				if (e.detail == DND.DROP_DEFAULT)
					e.detail = DND.DROP_COPY;
			}

			public void dragOperationChanged(DropTargetEvent e) {
				System.out.println("drag operation changed " + e);
				if (e.detail == DND.DROP_DEFAULT)
					e.detail = DND.DROP_COPY;
			}

			public void drop(DropTargetEvent e) {
				System.out.println("drop " + e);
				final Point loc = styledText.toControl(e.x, e.y);
				System.out.println(loc);
				final int offset = controller.getOffset(loc);
				System.out.println(offset);
				final IRodinElement[] elements = (IRodinElement[]) e.data;
				System.out.println("¡¡¡ DROP !!! " + elements[0]);
				processDrop(elements, offset);
			}

			private void processDrop(IRodinElement[] elements, int offset) {
				for (IRodinElement element : elements) {
					final ILElement lElement = SynchroUtils.findElement(
							element, mapper.getRoot());
					final ILElement selectionParent = lElement.getParent();
					if (selectionParent == null)
						return; // cannot move root
					final int oldPos = selectionParent
							.getChildPosition(lElement);
					final int newPos = findInsertPos(offset, lElement,
							oldPos);
					if (newPos == INVALID_POS)
						return;
					assert oldPos >= 0;
					selectionParent.moveChild(newPos, oldPos);
				}
				try {
					documentProvider.doSynchronize(mapper.getRoot(), null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private static final int INVALID_POS = -1;

			private int findInsertPos(int offset, ILElement selection,
					int oldPos) {
				final ILElement selectionParent = selection.getParent();
				final ILElement before = findSiblingBefore(offset, selection);
				if (before != null) {
					final int posBefore = selectionParent.getChildPosition(before);
					assert posBefore >= 0;
					if (posBefore < oldPos) {
						return posBefore + 1;
					} else {
						return posBefore;
					}
				}
				// try sibling after
				final ILElement after = findSiblingAfter(offset, selection);
				if (after != null) {
					final int posAfter = selectionParent.getChildPosition(after);
					assert posAfter >= 0;
					if (oldPos < posAfter) {
						return posAfter - 1;
					} else {
						return posAfter;
					}
				}
				return INVALID_POS;
			}

			private ILElement findSiblingBefore(int offset, ILElement selection) {
				final Interval intervalBefore = mapper
						.findEditableIntervalBefore(offset);
				if (intervalBefore == null)
					return null;
				return findSiblingAt(intervalBefore.getOffset(), selection);
			}

			private ILElement findSiblingAfter(int offset, ILElement selection) {
				final Interval intervalAfter = mapper.findEditableIntervalAfter(offset);
				if (intervalAfter == null)
					return null;
				return findSiblingAt(intervalAfter.getLastIndex(), selection);
			}

			private ILElement findSiblingAt(int offset, ILElement selection) {
				final EditorElement item = mapper.findItemContaining(offset);
				if (item == null)
					return null;
				final ILElement sibling = findDirectChild(item.getLightElement(),
						selection.getParent());
				if (sibling == null)
					return null;
				if (sameType(sibling, selection)) {
					return sibling;
				} else {
					return null;
				}
			}

			private boolean sameType(ILElement el1, ILElement el2) {
				return el1.getElementType() == el2.getElementType();
			}

			private ILElement findDirectChild(ILElement descendant, ILElement parent) {
				final ILElement descParent = descendant.getParent();
				if (descParent == null) { // parent of root
					return null;
				}
				if (descParent.equals(parent)) {
					return descendant;
				}
				return findDirectChild(descParent, parent);
			}
		});

		// TODO customize DragSourceEffect, DropTargetEffect
	}

	@Override
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		// TODO Auto-generated method stub
		super.editorContextMenuAboutToShow(menu);
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
	 * Replaces the old folding structure with this new one.
	 * 
	 * @param positions
	 *            The new positions
	 */
	public void updateFoldingStructure(Position[] positions) {
		for (Annotation a : oldPojectionAnnotations) {
			projectionAnnotationModel.removeAnnotation(a);
		}
		final Annotation[] annotations = documentProvider
				.getFoldingAnnotations();
		Assert.isLegal(annotations.length == positions.length);
		for (int i = 0; i < positions.length; i++) {
			projectionAnnotationModel.addAnnotation(annotations[i],
					positions[i]);
		}
		oldPojectionAnnotations = annotations;
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

	public DocumentMapper getDocumentMapper() {
		return mapper;
	}

	public int getCurrentOffset() {
		return styledText.getCaretOffset();
	}

}

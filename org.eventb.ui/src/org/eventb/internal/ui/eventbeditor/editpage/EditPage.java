/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added "show borders" preference
 *     Systerel - used EventBSharedColor and EventBPreferenceStore
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - removed focus listener of Hyper Link
 *     Systerel - separation of file and root element
 *     Systerel - used ElementDescRegistry
 *     Systerel - optimized tree traversal
 *     Systerel - fixed expanding
 *     Systerel - fixed Hyperlink.setImage() calls
 *     Systerel - refactored using IElementRelationship
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.editpage;

import static org.eventb.internal.ui.EventBUtils.setHyperlinkImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.internal.ui.preferences.EventBPreferenceStore;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Edit Page in Event-B editor.
 *         Basically, this contains several {@link ISectionComposite}.
 */
public class EditPage extends EventBEditorPage implements
		IElementChangedListener, IResourceChangeListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID + ".edit"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_edit_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_edit_tabTitle;

	// The next two variables maintain a link to the sections embedded in this
	// page. The list gives the order of the sections, while the map allows
	// direct access to a section, based on the type of the elements it
	// contains.
	List<ISectionComposite> sectionComps;
	Map<IElementType<?>, ISectionComposite> mapComps;

	// Set of all sections for which prefix marker must be refreshed
	Set<ISectionComposite> toRefreshPrefixMarker;
	
	// The main scrolled form
	ScrolledForm form;
	
	private IEditComposite[] rootComps;

	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public EditPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public void initialize(FormEditor editor) {
		super.initialize(editor);
		((IEventBEditor<?>) editor).addElementChangedListener(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		// Store the reference to the main scrolled form.
		form = managedForm.getForm();
		
		// The body of the main scrolled form has a grid layout (by default has
		// only one column).
		final Composite body = form.getBody();
		final GridLayout gLayout = new GridLayout();
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		body.setLayout(gLayout);

		// Try to set the background color if in debug mode.
		if (EventBEditorUtils.DEBUG) {
			body
					.setBackground(EventBSharedColor
							.getSystemColor(SWT.COLOR_BLUE));
		}
		
		// Create the top declaration. 
		createDeclaration(body);

		// Create the different section composites.
		createSections(body);
		
		// Refresh the main scrolled form.
		form.reflow(true);
	}

	/**
	 * Search the first occurrence of the given element and return the previous
	 * element
	 * 
	 * @param array
	 *            an IInternalElement array
	 */
	private IInternalElement getPreviousElement(IInternalElement[] array,
			IInternalElement element) {
		IInternalElement previous = null;
		for (int i = 0; i < array.length; ++i) {
			if (array[i].equals(element))
				break;
			previous = array[i];
		}
		return previous;
	}

	/**
	 * Search the first occurrence of the given element and return the next
	 * element
	 * 
	 * @param array
	 *            an IInternalElement array
	 */
	private IInternalElement getNextElement(IInternalElement[] array,
			IInternalElement element) {
		IInternalElement[] revert = getReverse(array);
		return getPreviousElement(revert, element);
	}

	/**
	 * Utility method for getting the reverse array of a given array of
	 * IInternalElement
	 * 
	 * @param array
	 *            an IInternalElement array to revert
	 */
	private IInternalElement[] getReverse(IInternalElement[] array) {
		IInternalElement[] revert = new IInternalElement[array.length];
		for (int i = 0; i < array.length; ++i) {
			int revertPos = array.length - 1 - i;
			revert[revertPos] = array[i];
		}
		return revert;
	}

	private Object[] getCurrentSelection(){
		if (currentSelection instanceof StructuredSelection) {
			StructuredSelection ssel = (StructuredSelection) currentSelection;
			return ssel.toArray();
		}else{
			return new Object[0];
		}
	}
	
	/**
	 * Utility method for moving elements up and down. An important assumption
	 * here is that the current selection contain the list of consecutive
	 * elements of the same type and has the same parent.
	 * 
	 * @param type 
	 *            the type of the elements to be moved.
	 * @param up
	 *            <code>true</code> for moving up, <code>false</code> for
	 *            moving down.
	 */
	protected void move(final IInternalElementType<?> type, final boolean up) {

		final Object[] elements = getCurrentSelection();
		if (elements.length == 0) {
			return;
		}

		final IInternalElement firstElement = (IInternalElement) elements[0];
		final IInternalElementType<?> firstType = firstElement.getElementType();
		if (firstType != type)
			return;
		final IRodinElement parent = firstElement.getParent();
		final IInternalElement lastElement = (IInternalElement) elements[elements.length - 1];
		
		if (parent != null && parent instanceof IInternalElement) {
			IInternalElement iparent = (IInternalElement) parent;

			try {
				final IInternalElement[] children = iparent
						.getChildrenOfType(firstType);

				assert (children.length > 0);

				final IInternalElement previous = getPreviousElement(children,
						firstElement);
				final IInternalElement next = getNextElement(children,
						lastElement);

				EventBEditorUtils.handle(up, firstElement, previous, next);

			} catch (RodinDBException e) {
				UIUtils.log(e, "Cannot move the current selection");
			}
		}
	}

	/**
	 * Utility method for creating the declaration part of this Edit page.
	 * 
	 * @param parent
	 *            the composite parent.
	 */
	private void createDeclaration(Composite parent) {
		final FormToolkit toolkit = this.getManagedForm().getToolkit();
		final Composite comp = toolkit.createComposite(parent);
		final boolean borderEnabled = EventBPreferenceStore
				.getBooleanPreference(PreferenceConstants.P_BORDER_ENABLE);
		
		if (EventBEditorUtils.DEBUG) {
			comp
					.setBackground(EventBSharedColor
							.getSystemColor(SWT.COLOR_CYAN));
		}
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp.setLayout(new GridLayout(5, false));
		
		// Expand/Collapse all button
		final IHyperlinkListener expandAllListener = new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				assert sectionComps != null;
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.setExpandNoReflow(true, true);
				}
				form.reflow(true);
			}
			@Override
			public void linkEntered(HyperlinkEvent e) {
				// Do nothing
			}
			@Override
			public void linkExited(HyperlinkEvent e) {
				// Do nothing
			}
		};
		final IHyperlinkListener collapseAllListener = new IHyperlinkListener() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				assert sectionComps != null;
				for (ISectionComposite sectionComp : sectionComps) {
					sectionComp.setExpandNoReflow(false, true);
				}
				form.reflow(true);
			}
			@Override
			public void linkEntered(HyperlinkEvent e) {
				// Do nothing
			}
			@Override
			public void linkExited(HyperlinkEvent e) {
				// Do nothing
			}
		};
		createHyperLink(toolkit, comp, expandAllListener, IEventBSharedImages.IMG_EXPAND_ALL);
		createHyperLink(toolkit, comp, collapseAllListener, IEventBSharedImages.IMG_COLLAPSE_ALL);

		final FormText widget = toolkit.createFormText(comp, true);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		widget.setLayoutData(gd);
		new EventBFormText(widget);
		final IInternalElement rodinInput = getRodinInput();
		final String declaration = ElementDescRegistry.getInstance().getPrefix(
				rodinInput.getElementType());

		final String text = "<form><li style=\"text\" bindent = \"-20\"><b>"
				+ declaration + "</b> " + rodinInput.getElementName()
				+ "</li></form>";
		widget.setText(text, true, true);

		createRootAttrs(comp, toolkit);

		if (borderEnabled) {
			toolkit.paintBordersFor(comp);
		}
	}
	
	private static void removeFocusListener(ImageHyperlink hyperlink) {
		for (Listener l : hyperlink.getListeners(SWT.FocusIn)) {
			hyperlink.removeListener(SWT.FocusIn, l);
		}
	}
	
	private static void createHyperLink(FormToolkit toolkit, final Composite comp,
			IHyperlinkListener listener, String image) {
		final ImageHyperlink hyperlinkExpand =
				toolkit.createImageHyperlink(comp, SWT.TOP);
		// to fix bug 2420471
		removeFocusListener(hyperlinkExpand);
		setHyperlinkImage(hyperlinkExpand, EventBImage.getImage(image));
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		hyperlinkExpand.setLayoutData(gd);
		hyperlinkExpand.addHyperlinkListener(listener);
	}

	/**
	 * Utility method for creating different sections composite. The information
	 * about the section composites is read from the element relationship UI
	 * Spec. registry.
	 * 
	 * @param parent
	 *            the composite parent of the section composites.
	 */
	private void createSections(final Composite parent) {
		final FormToolkit toolkit = this.getManagedForm().getToolkit();
		createSectionComps(parent, toolkit);
	}

	private void createRootAttrs(Composite parent, FormToolkit toolkit) {
		final IEventBEditor<?> editor = (IEventBEditor<?>) this.getEditor();
		final IInternalElement rodinRoot = getRodinInput();
		rootComps = DescRegistryReader.createAttributeComposites(form,
				rodinRoot, parent, editor, toolkit);
	}

	private void createSectionComps(Composite parent, FormToolkit toolkit) {
		final ElementDescRegistry registry = ElementDescRegistry.getInstance();
		final IInternalElement rodinInput = getRodinInput();
		
		// Get the list of possible element type depending on the type (e.g.
		// IMachineFile or IContextFile) of the input file.
		final IElementRelationship[] childRelationships = registry
				.getChildRelationships(rodinInput.getElementType());
		// Create the section composite corresponding with each relationship.
		sectionComps = new ArrayList<ISectionComposite>(
				childRelationships.length);
		mapComps = new HashMap<IElementType<?>, ISectionComposite>();
		for (final IElementRelationship rel : childRelationships) {
			// Create the section composite
			final SectionComposite sectionComp = new SectionComposite(this, toolkit,
					form, parent, rodinInput, rel, 0);
			sectionComps.add(sectionComp);
			mapComps.put(rel.getChildType(), sectionComp);
		}

	}

	// This is related to how the page is refreshed.
	
	// The set of elements that has changed.
	Set<IRodinElement> isChanged;

	// The set of elements that has been removed.
	Set<IRodinElement> isRemoved;

	// The set of elements that has been added.
	Set<IRodinElement> isAdded;

	// The set of pairs between element and the type of the children that has
	// changed.
	Set<Pair<IRodinElement, IElementType<?>>> childrenHasChanged;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	@Override
	public void elementChanged(ElementChangedEvent event) {
		// Record the starting time.
		long beforeTime = System.currentTimeMillis();
		
		// Reset the information collected for the changed event.
		// TODO: What about concurrency?
		isChanged = new HashSet<IRodinElement>();
		isRemoved = new HashSet<IRodinElement>();
		isAdded = new HashSet<IRodinElement>();
		childrenHasChanged = new HashSet<Pair<IRodinElement, IElementType<?>>>();
		
		// Process the input changed event.
		processDelta(event.getDelta());
		
		// Refresh the page according to the collected information.
		postRefresh();

		// Record the end time.
		long afterTime = System.currentTimeMillis();
		
		// Measure the duration for refreshing the page. 
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Duration: " + (afterTime - beforeTime)
					+ " ms");
	}

	/**
	 * Utility method for updating comment of the file.
	 */
	void updateRootAttrs() {
		for (IEditComposite editComp: rootComps) {
			editComp.refresh(true);
		}
		if (form != null) {
			internalPack(form.getBody());
		}
	}
	
	/**
	 * Utility method for packing the composite to the preferred size.
	 * 
	 * @param c
	 *            the composite to be packed.
	 */
	void internalPack(Composite c) {
		if (c.equals(form.getBody())) {
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Full resize");
			form.reflow(true);			
		}
		final Rectangle bounds = c.getBounds();
		final Point preferredSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x > bounds.width || preferredSize.y > bounds.height) {
			internalPack(c.getParent());
		} else {
			c.layout(true);
			c.setBounds(bounds);
		}
	}

	/**
	 * Utility method for refreshing the page according to a collected
	 * information from the delta.
	 */
	private void postRefresh() {
		if (form == null || form.isDisposed())
			return;

		final Display display = form.getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				// Do not redraw the page.
				form.getBody().setRedraw(false);

				// Process the removed element
				for (IRodinElement element : isRemoved) {
					final ISectionComposite comp = getCompositeTowards(element);
					if (comp != null) {
						comp.elementRemoved(element);
					}
					if (isSelected(element)) {
						setEditorSelection(new StructuredSelection());
					}
				}

				// Process the added element
				for (IRodinElement element : isAdded) {
					final ISectionComposite comp = getCompositeTowards(element);
					if (comp != null) {
						comp.elementAdded(element);
					}
				}

				// Process the changed element
				for (IRodinElement element : isChanged) {
					final ISectionComposite comp = getCompositeTowards(element);
					if (comp != null) {
						comp.refresh(element);
					}
					if (element.isRoot()) {
						updateRootAttrs();
					}
				}
			
				// Process the elements that changed order last.
				for (Pair<IRodinElement, IElementType<?>> pair : childrenHasChanged) {
					final IRodinElement parent = pair.getFirst();
					final IElementType<?> type = pair.getSecond();
					if (parent == null)
						continue;
					final ISectionComposite comp;
					if (parent.equals(getRodinInput())) {
						comp = mapComps.get(type);
					} else {
						comp = getCompositeTowards(parent);
					}
					if (comp != null) {
						comp.childrenChanged(parent, type);
					}
				}

				// Redraw the page.
				form.getBody().setRedraw(true);
			}

		});
	}

	/**
	 * Utility method to check if an element is currently selected within the
	 * page.
	 * 
	 * @param element
	 *            a Rodin element.
	 * @return <code>true</code> if the element is selected. Return
	 *         <code>false<code> otherwise.
	 */
	protected boolean isSelected(IRodinElement element) {
		if (currentSelection instanceof StructuredSelection) {
			final StructuredSelection ssel = (StructuredSelection) currentSelection;
			for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
				if (it.next().equals(element))
					return true;
			}
		}
		return false;
	}

	/**
	 * Process the delta to collect the information about changes.
	 * 
	 * @param delta
	 *            a Rodin Element Delta.
	 */
	void processDelta(IRodinElementDelta delta) {
		final IRodinElement element = delta.getElement();
		int kind = delta.getKind();
		if (element instanceof IRodinFile && kind == IRodinElementDelta.CHANGED) {
			for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
				processDelta(subDelta);
			}
			return;
		}

		if (kind == IRodinElementDelta.ADDED) {
			isAdded.add(element);
			childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
					element.getParent(), element.getElementType()));
			return;
		}
		if (kind == IRodinElementDelta.REMOVED) {
			isRemoved.add(element);
			childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
					element.getParent(), element.getElementType()));
			return;
		} else { // kind == CHANGED
			int flags = delta.getFlags();
			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("REORDERED");
				childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
						element.getParent(), element.getElementType()));
				return;
			} 
			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				for (IRodinElementDelta subDelta : delta.getAffectedChildren()) {
					processDelta(subDelta);
				}
			}
			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				isChanged.add(element);
				childrenHasChanged.add(new Pair<IRodinElement, IElementType<?>>(
						element.getParent(), element.getElementType()));
			}
			return;
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#dispose()
	 */
	@Override
	public void dispose() {
		final IEventBEditor<?> editor = this.getEventBEditor();
		editor.removeElementChangedListener(this);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	// The maintain the current selection.
	private ISelection currentSelection = new StructuredSelection();

	// This should be called from Event-B Editor only.
	public void setSelection(ISelection selection) {
		// TODO Should try to compare the selection first ?
		// De-select the current selection
		deselect(currentSelection);

		final List<IRodinElement> elements = new ArrayList<IRodinElement>();
		
		if (selection instanceof StructuredSelection) {
			for (Iterator<?> it = ((StructuredSelection) selection).iterator(); it
					.hasNext();) {
				final Object obj = it.next();
				if (obj instanceof IRodinElement) {
					if (select((IRodinElement) obj, true)) {
						elements.add((IRodinElement) obj);
					}
				}
			}
		}

		// Create a new selection with the set of selected elements.
		currentSelection = new StructuredSelection(elements);			
	}

	// The last selected element (from ButtonComposite).
	private IRodinElement lastSelectedElement;

	// This is the callback from ButtonComposite for selecting an element with
	// the input specifies if Shift is pressed.
	public void selectionChanges(IRodinElement element, boolean shiftPressed) {
		long beginTime = System.currentTimeMillis();
		if (currentSelection instanceof StructuredSelection
				&& ((StructuredSelection) currentSelection).size() == 1
				&& ((StructuredSelection) currentSelection).getFirstElement()
						.equals(element)) {
			setEditorSelection(new StructuredSelection());
			return;

		} else {
			if (shiftPressed
					&& lastSelectedElement != null
					&& element.getParent().equals(
							lastSelectedElement.getParent())
					&& element.getElementType().equals(
							lastSelectedElement.getElementType())) {
				selectRange(lastSelectedElement, element);
			} else {
				lastSelectedElement = element;
				setEditorSelection(new StructuredSelection(element));
			}
		}
		long afterTime = System.currentTimeMillis();
		if (EventBEditorUtils.DEBUG) {
			EventBEditorUtils.debug("Duration " + (afterTime - beginTime)
					+ " ms");
		}
	}

	void setEditorSelection(ISelection selection) {
		IEventBEditor<?> eventBEditor = this.getEventBEditor();
		eventBEditor.getSite().getSelectionProvider().setSelection(selection);
	}

	private void selectRange(IRodinElement firstElement,
			IRodinElement secondElement) {
		assert firstElement.getParent().equals(secondElement.getParent());
		assert firstElement.getElementType().equals(
				secondElement.getElementType());
		final IRodinElement parent = firstElement.getParent();
		final IElementType<? extends IRodinElement> type = firstElement
				.getElementType();
		assert parent instanceof IInternalElement;
		try {
			IRodinElement[] children = ((IInternalElement) parent)
					.getChildrenOfType(type);
			boolean found = false;
			final List<IRodinElement> selected = new ArrayList<IRodinElement>();
			for (IRodinElement child : children) {
				if (child.equals(firstElement) || child.equals(secondElement)) {
					selected.add(child);
					if (found)
						break;
					else
						found = true;
				} else {
					if (found) {
						selected.add(child);
					}
				}
			}
			setEditorSelection(new StructuredSelection(selected));
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deselect(ISelection ssel) {
		
		if (ssel instanceof StructuredSelection) {
			for (Iterator<?> it = ((StructuredSelection) ssel).iterator(); it
					.hasNext();) {
				final Object obj = it.next();
				if (obj instanceof IRodinElement) {
					select((IRodinElement) obj, false);
				}
			}
		}
	}

	public boolean select(IRodinElement element, boolean select) {
		final ISectionComposite comp = getCompositeTowards(element);
		if (comp != null) {
			return comp.select(element, select);
		}
		return false;
	}

	public void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd) {
		final ISectionComposite comp = getCompositeTowards(element);
		if (comp != null) {
			comp.edit(element, attributeType, charStart, charEnd);
		}
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		final Map<IRodinElement, Set<IAttributeType>> map = new HashMap<IRodinElement, Set<IAttributeType>>();
		final IFile file = getRodinInput().getResource();
		final IMarkerDelta[] rodinProblemMarkerDeltas = event.findMarkerDeltas(
			RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);
		for (IMarkerDelta delta : rodinProblemMarkerDeltas) {
			final IResource resource = delta.getResource();
			if (file.equals(resource)) {
				if (EventBEditorUtils.DEBUG) {
					printRodinMarkerDelta(delta);
				}
				final IRodinElement element = RodinMarkerUtil.getElement(delta);
				if (element == null)
					continue;
				final IAttributeType attributeType = RodinMarkerUtil
						.getAttributeType(delta);
				addToMap(map, element, attributeType);
			}
		}
		if (EventBEditorUtils.DEBUG) {
			printMarkers(map);
		}
		resourceChangedRefresh(map);
		
	}

	private void printMarkers(Map<IRodinElement, Set<IAttributeType>> markers) {
		EventBEditorUtils.debug(markers.toString());
	}

	private void addToMap(Map<IRodinElement, Set<IAttributeType>> markers,
			IRodinElement element, IAttributeType attributeType) {
		Set<IAttributeType> list = markers.get(element);
		if (list == null) {
			list = new HashSet<IAttributeType>();
			if (attributeType != null)
				list.add(attributeType);
			markers.put(element, list);
		}
		else if (list.size() != 0 && attributeType != null) {
			list.add(attributeType);
		}
	}

	private void printRodinMarkerDelta(IMarkerDelta delta) {
		EventBEditorUtils.debug("******");
		int kind = delta.getKind();
		if (kind == IResourceDelta.ADDED) {
			EventBEditorUtils.debug("Marker added");
		} else if (kind == IResourceDelta.REMOVED) {
			EventBEditorUtils.debug("Marker removed");
		} else if (kind == IResourceDelta.CHANGED) {
			EventBEditorUtils.debug("Marker changed");
		}
		final Map<?,?> attributes = delta.getAttributes();
		final Set<?> keySet = attributes.keySet();
		for (Object key : keySet) {
			EventBEditorUtils.debug(key.toString() + " --> "
					+ attributes.get(key).toString());
		}
	}

	private void resourceChangedRefresh(final Map<IRodinElement, Set<IAttributeType>> map) {
		final Display display = this.getSite().getShell().getDisplay();
		if (display.isDisposed()) {
			return;
		}
		try {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					toRefreshPrefixMarker = new HashSet<ISectionComposite>();
					for (Entry<IRodinElement, Set<IAttributeType>> entry : map
							.entrySet()) {
						final IRodinElement key = entry.getKey();
						final Set<IAttributeType> set = entry.getValue();
						final ISectionComposite comp = getCompositeTowards(key);
						if (comp != null) {
							addToRefreshPrefixMarker(comp);
							comp.refresh(key, set);
						}
					}
					refreshPrefixMarker();
				}
			});
		} catch (SWTException e) {
			if (e.code == SWT.ERROR_DEVICE_DISPOSED) {
				// do not refresh
				return;
			}
			throw e;
		}
	}

	void refreshPrefixMarker() {
		for (ISectionComposite comp : toRefreshPrefixMarker) {
			comp.refreshPrefixMarker();
		}
	}

	public void addToRefreshPrefixMarker(ISectionComposite section) {
		toRefreshPrefixMarker.add(section);
	}
	
	protected ISectionComposite getCompositeTowards(IRodinElement element) {
		final IRodinElement child = EventBEditorUtils.getChildTowards(
				getRodinInput(), element);
		if (child == null || mapComps == null)
			return null;
		final IElementType<?> type = child.getElementType();
		return mapComps.get(type);
	}

	protected IInternalElement getRodinInput() {
		final IEventBEditor<?> editor = (IEventBEditor<?>) this.getEditor();
		return editor.getRodinInput();
	}
}

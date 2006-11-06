package org.eventb.ui.eventbeditor;

import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.eventbeditor.IStatusChangedListener;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public interface IEventBEditor extends IEditorPart {

	/**
	 * Add an element to be new.
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 */
	public abstract void addNewElement(IRodinElement element);

	/**
	 * Add a new status changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            a status changed listener
	 */
	public abstract void addStatusListener(IStatusChangedListener listener);

	/**
	 * Remove a status changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            a status changed listener
	 */
	public abstract void removeStatusListener(IStatusChangedListener listener);

	/**
	 * Check if an element is new (unsaved).
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @return <code>true</code> if the element is new (unsaved)
	 */
	public abstract boolean isNewElement(IRodinElement element);

	/**
	 * Add a new element changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            an element changed listener
	 */
	public abstract void addElementChangedListener(
			IElementChangedListener listener);

	/**
	 * Remove an element changed listener.
	 * <p>
	 * 
	 * @param listener
	 *            an element changed listener
	 */
	public abstract void removeElementChangedListener(
			IElementChangedListener listener);

	/**
	 * Set the selection in the editor.
	 * <p>
	 * 
	 * @param ssel
	 *            the current selecting element. It can be an IRodinElement or a
	 *            TreeNode (from the ProjectExplorer).
	 */
	public abstract void edit(Object ssel);

	public abstract void setSelection(IInternalElement element);

	public abstract IRodinFile getRodinInput();


}
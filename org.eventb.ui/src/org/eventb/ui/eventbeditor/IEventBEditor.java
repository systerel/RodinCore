package org.eventb.ui.eventbeditor;

import org.eventb.internal.ui.eventbeditor.IStatusChangedListener;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public interface IEventBEditor {

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
	 * Checking if a Rodin element is "original" (created automatically, but is
	 * not modified).
	 * <p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @return <code>true</code> if the element has default created values.
	 *         <code>false</code> otherwise.
	 */
	// private boolean isOriginal(IRodinElement element) {
	// if (element instanceof IGuard) {
	// try {
	// if (((IGuard) element).getContents().equals(
	// EventBUIPlugin.GRD_DEFAULT)) {
	// return true;
	// }
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// if (element instanceof IAction) {
	// try {
	// if (((IAction) element).getContents().equals(
	// EventBUIPlugin.SUB_DEFAULT)) {
	// return true;
	// }
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return false;
	// }
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

}
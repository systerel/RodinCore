/*******************************************************************************
 * Copyright (c) 2008, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.List;

import org.eventb.ui.eventbeditor.EventBEditorPage;

/**
 * @author htson
 *         <p>
 *         The interface for the editor page registry. Through this registry,
 *         clients can create different pages for Event-B Editor (either for
 *         machine or context).
 */
public interface IEditorPagesRegistry {

	/**
	 * Create an editor page corresponding to the given page id and editor id.
	 * 
	 * @param editorID
	 *            an editor ID, it must be either
	 *            {EventBMachineEditor.#EDITOR_ID} or
	 *            {EventBContextEditor.#EDITOR_ID}.
	 * @param pageID
	 *            a page id.
	 * @return an editor page corresponding to the input page id for the given
	 *         editor. Return <code>null</code> if the editor ID is invalid or
	 *         the page ID is invalid or there is some problem in creating the
	 *         page.
	 */
	public abstract EventBEditorPage createPage(String editorID, String pageID);

	/**
	 * Create all editor pages for the given editor ID.
	 * 
	 * @param editorID
	 *            an editor ID, it must be either
	 *            {EventBMachineEditor.#EDITOR_ID} or
	 *            {EventBContextEditor.#EDITOR_ID}.
	 * @return a list of editor pages for the editor with the given id. Return
	 *         an empty list if the editor ID is invalid. All the instances of
	 *         the pages are NOT <code>null</code>.
	 */
	public abstract EventBEditorPage[] createAllPages(String editorID);

	/**
	 * Check if the page id is a valid page for an editor with the given editor
	 * id.
	 * 
	 * @param editorID
	 *            an editor ID, it must be either
	 *            {EventBMachineEditor.#EDITOR_ID} or
	 *            {EventBContextEditor.#EDITOR_ID}.
	 * @param pageID
	 *            a page id
	 * @return <code>true</code> if the page id is valid page for editor with
	 *         given input editor id. Return <code>false</code> otherwise,
	 *         including if the editor id is invalid.
	 */
	public abstract boolean isValid(String editorID, String pageID);

	/**
	 * Get the name of the editor page corresponding to the given page id and
	 * editor id.
	 * 
	 * @param editorID
	 *            an editor ID, it must be either
	 *            {EventBMachineEditor.#EDITOR_ID} or
	 *            {EventBContextEditor.#EDITOR_ID}.
	 * @param pageID
	 *            a page id
	 * @return the name of the corresponding page to the input page id and the
	 *         editor id. Return <code>null</code> if the page id or the
	 *         editor id is invalid. If the page does not have a name, its id
	 *         will be returned.
	 */
	public abstract String getPageName(String editorID, String pageID);

	/**
	 * Return the list of all page IDs registered for the given editor.
	 * 
	 * @param editorID
	 *            an editor ID, it must be either
	 *            {EventBMachineEditor.#EDITOR_ID} or
	 *            {EventBContextEditor.#EDITOR_ID}.
	 * @return the list of page IDs for given editor. Return an empty list if
	 *         the editor ID is invalid.
	 */
	public abstract List<String> getAllPageIDs(String editorID);

	/**
	 * Return the list of default page IDs for given editor.
	 * 
	 * @param editorID
	 *            an editor ID, it must be either
	 *            {EventBMachineEditor.#EDITOR_ID} or
	 *            {EventBContextEditor.#EDITOR_ID}.
	 * @return the list of default page IDs for given editor. Return an empty
	 *         list if the editor ID is invalid.
	 */
	public abstract List<String> getDefaultPageIDs(String editorID);

}
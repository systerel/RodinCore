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

package fr.systerel.editor.contentAssist;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IContextRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.EditorUtils;
import fr.systerel.editor.documentModel.DocumentMapper;
import fr.systerel.editor.documentModel.Interval;
import fr.systerel.editor.editors.OverlayEditor;

/**
 */
public class CompletionCalculator {

	private static final String[] NO_STRING = new String[0];

	private DocumentMapper documentMapper;
	private OverlayEditor overlayEditor;

	public CompletionCalculator(DocumentMapper documentMapper, OverlayEditor overlayEditor) {
		this.documentMapper = documentMapper;
		this.overlayEditor = overlayEditor;
	}
	
	public String[] calculateCompletions(int offset) {
		final Interval interval = overlayEditor.getInterval();

		if (interval == null) {
			return NO_STRING;
		}
		final ILElement element = interval.getElement();
		if (element == null) {
			return NO_STRING;
		}
		final IInternalElement rElement = element.getElement();

		//		FIXME use standard completion
//		final IAttributeType attributeType = interval.getAttributeType();
//		final IAttributeLocation location = RodinCore.getInternalLocation(rElement, attributeType);
//		return EventBPlugin.getProposals(location, true);

		final List<String> result = new ArrayList<String>();
		if (element instanceof IRefinesMachine) {
			IMachineRoot[] identifiers = getMachines(rElement);
			for (IMachineRoot id : identifiers) {
				result.add(id.getComponentName());
			}
			return result.toArray(new String[result.size()]);
		}
		if (element instanceof ISeesContext) {
			IContextRoot[] identifiers = getContexts(rElement);
			for (IContextRoot id : identifiers) {
				result.add(id.getComponentName());
			}
			return result.toArray(new String[result.size()]);
		}
		IIdentifierElement[] identifiers = getVariablesAndConstants();
		for (IIdentifierElement id : identifiers) {
			try {
				if (id.hasIdentifierString()) {
					result.add(id.getIdentifierString());
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	protected IIdentifierElement[] getVariablesAndConstants() {
		ArrayList<IIdentifierElement> result = new ArrayList<IIdentifierElement>();
		final List<ILElement> variables = documentMapper.getRoot()
				.getChildrenOfType(IVariable.ELEMENT_TYPE);
		for (ILElement v : variables) {
			result.add((IIdentifierElement) v.getElement());
		}
		// TODO: add variables and constants from seen and refined machines.
		return result.toArray(new IIdentifierElement[result.size()]);
	}
	
	protected IMachineRoot[] getMachines(IRodinElement element) {
		try {
			return EditorUtils.getMachineRootChildren(element.getRodinProject());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		
		return new IMachineRoot[0];
	}

	protected IContextRoot[] getContexts(IRodinElement element) {
		try {
			return EditorUtils.getContextRootChildren(element.getRodinProject());
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		
		return new IContextRoot[0];
	}
	
	
}

/*******************************************************************************
 * Copyright (c) 2008, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions.operations;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.Pair;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;


// TODO changer, utiliser les operation de base

/* operation pour recreer un element. Uitliser lors du delete */
public class CreateIdenticalElement extends OperationLeaf {

	final IInternalElement element;

	private IAttributeType[] attributeTypes;

	final ArrayList<Pair<IAttributeType.Boolean, Boolean>> listBoolean;
	final ArrayList<Pair<IAttributeType.Handle, IRodinElement>> listHandle;
	final ArrayList<Pair<IAttributeType.Integer, Integer>> listInteger;
	final ArrayList<Pair<IAttributeType.Long, Long>> listLong;
	final ArrayList<Pair<IAttributeType.String, String>> listString;

	public CreateIdenticalElement(IInternalElement element) {
		super("CreateElementLeaf");
		this.element = element;
		listBoolean = new ArrayList<Pair<IAttributeType.Boolean, Boolean>>();
		listHandle = new ArrayList<Pair<IAttributeType.Handle, IRodinElement>>();
		listInteger = new ArrayList<Pair<IAttributeType.Integer, Integer>>();
		listLong = new ArrayList<Pair<IAttributeType.Long, Long>>();
		listString = new ArrayList<Pair<IAttributeType.String, String>>();
		
		
		try {
			attributeTypes = element.getAttributeTypes();
			for (IAttributeType type : attributeTypes) {
				getAttributes(type);
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		if (!element.exists()) {
			element.create(null, monitor);
		}
		setBooleanAttributes(listBoolean, monitor);
		setHandleAttributes(listHandle, monitor);
		setIntegerAttributes(listInteger, monitor);
		setLongAttributes(listLong, monitor);
		setStringAttributes(listString, monitor);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		doExecute(monitor, info);
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		element.delete(true, monitor);
	}

	private void getAttributes(IAttributeType type) throws RodinDBException {
		if (type instanceof IAttributeType.String) {
			final IAttributeType.String attrType = (IAttributeType.String) type;
			final String value = element.getAttributeValue(attrType);
			listString.add(new Pair<IAttributeType.String, String>(attrType,
					value));
		} else if (type instanceof IAttributeType.Boolean) {
			final IAttributeType.Boolean attrType = (IAttributeType.Boolean) type;
			final Boolean value = new Boolean(element
					.getAttributeValue(attrType));
			listBoolean.add(new Pair<IAttributeType.Boolean, Boolean>(attrType,
					value));
		} else if (type instanceof IAttributeType.Handle) {
			final IAttributeType.Handle attrType = (IAttributeType.Handle) type;
			final IRodinElement value = element.getAttributeValue(attrType);
			listHandle.add(new Pair<IAttributeType.Handle, IRodinElement>(
					attrType, value));
		} else if (type instanceof IAttributeType.Integer) {
			final IAttributeType.Integer attrType = (IAttributeType.Integer) type;
			final Integer value = new Integer(element
					.getAttributeValue(attrType));
			listInteger.add(new Pair<IAttributeType.Integer, Integer>(attrType,
					value));
		} else if (type instanceof IAttributeType.Long) {
			final IAttributeType.Long attrType = (IAttributeType.Long) type;
			final Long value = new Long(element.getAttributeValue(attrType));
			listLong.add(new Pair<IAttributeType.Long, Long>(attrType, value));
		}
	}

	private void setBooleanAttributes(
			ArrayList<Pair<IAttributeType.Boolean, Boolean>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Boolean, Boolean> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	private void setHandleAttributes(
			ArrayList<Pair<IAttributeType.Handle, IRodinElement>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Handle, IRodinElement> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	private void setIntegerAttributes(
			ArrayList<Pair<IAttributeType.Integer, Integer>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Integer, Integer> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	private void setLongAttributes(
			ArrayList<Pair<IAttributeType.Long, Long>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Long, Long> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	private void setStringAttributes(
			ArrayList<Pair<IAttributeType.String, String>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.String, String> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	@Override
	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub
		
	}

}

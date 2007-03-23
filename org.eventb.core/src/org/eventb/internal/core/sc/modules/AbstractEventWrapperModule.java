/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.internal.core.sc.AbstractEventInfo;
import org.eventb.internal.core.sc.AbstractEventTable;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class AbstractEventWrapperModule extends LabeledElementModule {

	protected static class AbstractEventWrapper {
		private final AbstractEventInfo info;
		
		public AbstractEventWrapper(AbstractEventInfo info) {
			this.info = info;
			refineError = false;
			implicitRefinedInfo = null;
			splitInfos = new LinkedList<IEventSymbolInfo>();
			mergeInfos = new LinkedList<IEventSymbolInfo>();
		}

		private IEventSymbolInfo implicitRefinedInfo;

		private boolean refineError;

		private List<IEventSymbolInfo> splitInfos;

		private List<IEventSymbolInfo> mergeInfos;

		/**
		 * returns whether the event corresponding to this abstract event info is refined by some event.
		 * 
		 * @return whether the event corresponding to this abstract event info is refined by some event
		 */
		public boolean isRefined() throws CoreException {
			return implicitRefinedInfo != null || splitInfos.size() != 0 || mergeInfos.size() != 0;
		}
		
		public void setRefineError(boolean value) throws CoreException {
			refineError = value;
		}

		/**
		 * Returns the refine error of this abstract event info.
		 * 
		 * @return the refine error of this abstract event info
		 */
		public boolean hasRefineError() throws CoreException {
			return refineError;
		}

		public void addMergeSymbolInfo(IEventSymbolInfo symbolInfo)
				throws CoreException {
			mergeInfos.add(symbolInfo);
		}

		public void addSplitSymbolInfo(IEventSymbolInfo symbolInfo)
				throws CoreException {
			splitInfos.add(symbolInfo);
		}

		/**
		 * Returns the event symbol infos that merge refine the abstract event corresponding
		 * to this abstract event info.
		 * <p>
		 * <b>Attention:</b>
		 * Reliable information about refinement relationships can only be detrmined via the 
		 * refinining, i.e. concrete, event. If <code>hasRefineError()</code> is <code>true</code>,
		 * the refinement information available in this abstract event info will be inconsistent.
		 * </p>
		 * 
		 * @return the event symbol infos that merge refine the abstract event corresponding
		 * to this abstract event info
		 */
		public List<IEventSymbolInfo> getMergeSymbolInfos()
				throws CoreException {
			return mergeInfos;
		}

		/**
		 * Returns the event symbol infos that split refine the abstract event corresponding
		 * to this abstract event info. This should usually be a list with exactly one element.
		 * See, however, the remark below.
		 * <p>
		 * <b>Attention:</b>
		 * Reliable information about refinement relationships can only be detrmined via the 
		 * refinining, i.e. concrete, event. If <code>hasRefineError()</code> is <code>true</code>,
		 * the refinement information available in this abstract event info will be inconsistent.
		 * </p>
		 * 
		 * @return the event symbol infos that merge refine the abstract event corresponding
		 * to this abstract event info
		 */
		public List<IEventSymbolInfo> getSplitSymbolInfos()
				throws CoreException {
			return splitInfos;
		}

		public void setImplicit(IEventSymbolInfo eventSymbolInfo)
				throws CoreException {
			implicitRefinedInfo = eventSymbolInfo;
		}

		/**
		 * Returns the event symbol info the event that implicitly refines the event corresponding
		 * to this abstract event info, or <code>null</code> if the abstract event is not implicitly
		 * refined.
		 * <p>
		 * Implicit refinement refers to refined initialisations and inherited events that are 
		 * <b>not</b> allowed to have <b>explicit</b> refinement clauses. Hence, they are referred
		 * to as implicitly refined here. 
		 * </p>
		 * <b>Attention:</b>
		 * Reliable information about refinement relationships can only be detrmined via the 
		 * refinining, i.e. concrete, event. If <code>hasRefineError()</code> is <code>true</code>,
		 * the refinement information available in this abstract event info will be inconsistent.
		 * </p>
		 * 
		 * @return the event symbol info the event that implicitly refines the event corresponding
		 * to this abstract event info, or <code>null</code> if the abstract event is not implicitly
		 * refined
		 */
		public IEventSymbolInfo getImplicit() throws CoreException {
			return implicitRefinedInfo;
		}

		public AbstractEventInfo getInfo() {
			return info;
		}

	}
	
	private AbstractEventWrapper[] wrappers;
	private AbstractEventTable abstractEventTable;
	
	private void initWrappers() throws CoreException {
		List<AbstractEventInfo> infos = abstractEventTable.getAbstractEventInfos();
		int size = infos.size();
		wrappers = new AbstractEventWrapper[size];
		for (int i=0; i<size; i++) {
			wrappers[i] = new AbstractEventWrapper(infos.get(i));
		}
	}
	
	protected AbstractEventWrapper getAbstractEventWrapper(String label) throws CoreException {
		int index = abstractEventTable.getIndexForLabel(label);
		if (index == -1)
			return null;
		else
			return getWrappers()[index];
	}
	
	protected void issueRefinementErrorMarker(IEventSymbolInfo symbolInfo) throws CoreException {
		if (!symbolInfo.hasError())
			createProblemMarker(
					symbolInfo.getSourceElement(), 
					GraphProblem.EventRefinementError);
		symbolInfo.setError();
	}
	
	protected AbstractEventWrapper getAbstractEventWrapperForLabel(
			IEventSymbolInfo symbolInfo, 
			String label, 
			IInternalElement element,
			IAttributeType attributeType) throws CoreException {
		AbstractEventWrapper abstractEventWrapper = getAbstractEventWrapper(label);

		if (abstractEventWrapper == null) {
			if (attributeType == null)
				createProblemMarker(
						element,
						GraphProblem.AbstractEventNotFoundError,
						label);
			else
				createProblemMarker(
						element,
						attributeType,
						GraphProblem.AbstractEventNotFoundError,
						label);
			abstractEventWrapper = null;
			createProblemMarker(
					element,
					attributeType,
					GraphProblem.EventRefinementError);
			issueRefinementErrorMarker(symbolInfo);
		}
		return abstractEventWrapper;
	}

	protected AbstractEventWrapper[] getWrappers() throws CoreException {
		if (wrappers == null)
			initWrappers();
		return wrappers;
	}

	@Override
	public void initModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		abstractEventTable =
			(AbstractEventTable) repository.getState(IAbstractEventTable.STATE_TYPE);
				
	}

	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		abstractEventTable = null;
	}
	
}

/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * @since 1.0
 */
public class SinglePredInput implements IReasonerInput, ITranslatableReasonerInput {
	
	private static final String SERIALIZATION_KEY = "pred";

	private Predicate predicate;
	private String error;
	
	public SinglePredInput(String predString, ITypeEnvironment typeEnv){
		
		final FormulaFactory ff = typeEnv.getFormulaFactory();
		predicate = DLib.parsePredicate(ff, predString);
		if (predicate == null)
		{
			error = "Parse error for predicate: "+ predString;
			return;
		}
		if (! Lib.typeCheckClosed(predicate,typeEnv)){
			error = "Type check failed for Predicate: "+predicate;
			predicate = null;
			return;
		}
		error = null;
	}
	
	public SinglePredInput(Predicate predicate){
		assert predicate != null;
		this.predicate = predicate;
		this.error = null;
	}
	
	public final boolean hasError(){
		return (error != null);
	}
	
	/**
	 * @return Returns the error.
	 */
	public final String getError() {
		return error;
	}

	/**
	 * @return Returns the predicate.
	 */
	public final Predicate getPredicate() {
		return predicate;
	}

	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		assert ! hasError();
		assert predicate != null;
		writer.putPredicates(SERIALIZATION_KEY, predicate);
	}
	
	public SinglePredInput(IReasonerInputReader reader) throws SerializeException {
		final Predicate[] preds = reader.getPredicates(SERIALIZATION_KEY);
		if (preds.length != 1) {
			throw new SerializeException(
					new IllegalStateException("Expected exactly one predicate")
			);
		}
		predicate = preds[0];
	}

	public void applyHints(ReplayHints hints) {
		predicate = hints.applyHints(predicate);
		
	}

	/**
	 * @since 3.0
	 */
	@Override
	public IReasonerInput translate(FormulaFactory factory) {
		if (predicate == null) {
			return this;
		}
		return new SinglePredInput(predicate.translate(factory));
	}

	/**
	 * @since 3.0
	 */
	@Override
	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
		final ITypeEnvironmentBuilder typeEnv = factory
				.makeTypeEnvironment();
		if (predicate != null) {
			typeEnv.addAll(predicate.getFreeIdentifiers());
		}
		return typeEnv;
	}

}

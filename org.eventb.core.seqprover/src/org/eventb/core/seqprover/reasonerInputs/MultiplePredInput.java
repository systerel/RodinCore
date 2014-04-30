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

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner.Input;

/**
 * @since 1.0
 */
public class MultiplePredInput implements IReasonerInput, ITranslatableReasonerInput {
	
	private static final String SERIALIZATION_KEY = "preds";

	private Predicate[] predicates;
	private String error;
		
	public MultiplePredInput(Predicate[] predicates){
		this.predicates = predicates;
		if (this.predicates != null)
			this.error = null;
		else
			this.error = "Predicates uninitialised";
	}

	public MultiplePredInput(Set<Predicate> predicates){
		this(predicates.toArray(new Predicate[predicates.size()]));
	}
		
	@Override
	public final boolean hasError(){
		return (error != null);
	}
	
	/**
	 * @return Returns the error.
	 */
	@Override
	public final String getError() {
		return error;
	}

	/**
	 * @return Returns the predicate.
	 */
	public final Predicate[] getPredicates() {
		return predicates;
	}
	
	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		assert ! hasError();
		assert predicates != null;
		writer.putPredicates(SERIALIZATION_KEY, predicates);
	}

	public MultiplePredInput(IReasonerInputReader reader) throws SerializeException {
		predicates = reader.getPredicates(SERIALIZATION_KEY);
		error = null;
	}

	@Override
	public void applyHints(ReplayHints hints) {
		for (int i = 0; i < predicates.length; i++) {
			predicates[i] = hints.applyHints(predicates[i]);
		}
		
	}
	
	/**
	 * @since 3.0
	 */
	@Override
	public IReasonerInput translate(FormulaFactory factory) {
		final Predicate[] trPreds = new Predicate[predicates.length];
		for (int i = 0; i < predicates.length; i++) {
			trPreds[i] = predicates[i].translate(factory);
		}
		return new Input(trPreds);
	}

	/**
	 * @since 3.0
	 */
	@Override
	public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
		final ITypeEnvironmentBuilder typeEnv = factory
				.makeTypeEnvironment();
		for (int i = 0; i < predicates.length; i++) {
			typeEnv.addAll(predicates[i].getFreeIdentifiers());
		}
		return typeEnv;
	}

}

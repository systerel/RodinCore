/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - adapted to XProver v2 API
 *******************************************************************************/
package org.eventb.internal.pp;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.xprover.XProverCall2;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.core.seqprover.xprover.XProverReasoner2;

/**
 * Implementation of {@link XProverReasoner2} for PP.
 *
 * @author François Terrier
 *
 */
public class PPReasoner extends XProverReasoner2 implements IVersionedReasoner {

	public static final String REASONER_ID = "org.eventb.pp.pp";
	
	public static final int VERSION = 1;

	public static boolean DEBUG = false;
	private static void debug(String msg) {
		System.out.println(msg);
	}
	
	@Override
	public XProverCall2 newProverCall(IReasonerInput input,
			ISimpleSequent sequent, IProofMonitor pm) {
		if (PPReasoner.DEBUG) PPReasoner.constructTest(sequent);
		
		return new PPProverCall((XProverInput)input,sequent,pm);
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}


	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new PPInput(reader);
	}

	public static void constructTest(ISimpleSequent sequent) {
		final StringBuilder builder = new StringBuilder();
		builder.append("doTest(\n");
		appendTypeEnvironment(builder, sequent.getTypeEnvironment());
		builder.append(", mSet(\n");
		String sep = "\t";
		Predicate goal = null;
		for (ITrackedPredicate predicate : sequent.getPredicates()) {
			builder.append(sep);
			sep = ",\n\t";
			if (predicate.isHypothesis()) {
				appendString(builder, predicate.getPredicate());
			} else {
				goal = predicate.getPredicate();
			}
		}
		builder.append("\n), ");
		appendString(builder, goal);
		builder.append(", true);\n");
		debug(builder.toString());
	}

	private static void appendTypeEnvironment(StringBuilder builder,
			ITypeEnvironment typeEnv) {
		builder.append("mList(\n");
		String sep = "\t";
		final IIterator iterator = typeEnv.getIterator();
		while(iterator.hasNext()) {
			iterator.advance();
			builder.append(sep);
			sep = ",\n\t";
			appendString(builder, iterator.getName());
			builder.append(", ");
			appendString(builder, iterator.getType());
		}
		builder.append("\n)");
	}

	private static void appendString(StringBuilder builder, Object obj) {
		builder.append("\"");
		builder.append(obj);
		builder.append("\"");
	}

	@Override
	public int getVersion() {
		return VERSION;
	}

}

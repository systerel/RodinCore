/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.xprover.XProverCall;
import org.eventb.core.seqprover.xprover.XProverInput;
import org.eventb.core.seqprover.xprover.XProverReasoner;

/**
 * Implementation of {@link XProverReasoner} for PP.
 *
 * @author François Terrier
 *
 */
public class PPReasoner extends XProverReasoner implements IVersionedReasoner {

	public static final String REASONER_ID = "org.eventb.pp.pp";
	
	public static final int VERSION = 1;

	public static boolean DEBUG = false;
	private static void debug(String msg) {
		System.out.println(msg);
	}
	
	@Override
	public XProverCall newProverCall(IReasonerInput input, Iterable<Predicate> hypotheses, Predicate goal, IProofMonitor pm) {
		if (PPReasoner.DEBUG) PPReasoner.constructTest(hypotheses, goal);
		
		return new PPProverCall((XProverInput)input,hypotheses,goal,pm);
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

	public static void constructTest(Iterable<Predicate> hypotheses,
			Predicate goal) {
		final StringBuilder builder = new StringBuilder();
		builder.append("doTest(\n");
		appendTypeEnvironment(builder, hypotheses, goal);
		builder.append(", mSet(\n");
		String sep = "\t";
		for (Predicate predicate : hypotheses) {
			builder.append(sep);
			sep = ",\n\t";
			appendString(builder, predicate);
		}
		builder.append("\n), ");
		appendString(builder, goal);
		builder.append(", true);\n");
		debug(builder.toString());
	}

	private static void appendTypeEnvironment(StringBuilder builder,
			Iterable<Predicate> hypotheses, Predicate goal) {
		builder.append("mList(\n");
		final FreeIdentifier[] idents = collectFreeIdentifiers(hypotheses, goal);
		String sep = "\t";
		for (final FreeIdentifier ident : idents) {
			builder.append(sep);
			sep = ",\n\t";
			appendString(builder, ident.getName());
			builder.append(", ");
			appendString(builder, ident.getType());
		}
		builder.append("\n)");
	}

	private static FreeIdentifier[] collectFreeIdentifiers(
			Iterable<Predicate> hypotheses, Predicate goal) {
		final Set<FreeIdentifier> idents = new HashSet<FreeIdentifier>();
		for (Predicate predicate : hypotheses) {
			idents.addAll(Arrays.asList(predicate.getFreeIdentifiers()));
		}
		idents.addAll(Arrays.asList(goal.getFreeIdentifiers()));
		final FreeIdentifier[] result = new FreeIdentifier[idents.size()];
		idents.toArray(result);
		Arrays.sort(result, new Comparator<FreeIdentifier>() {
			@Override
			public int compare(FreeIdentifier o1, FreeIdentifier o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return result;
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

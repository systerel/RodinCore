package org.eventb.core.seqprover.eventbExtentionTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;



/**
 * 
 * 
 * @author Farhad Mehta
 *
 */
public class RuleTests {
	
	
	public static void assertSerialization(IProofRule rule){
		
		IReasoner reasoner = rule.generatedBy();
		IReasonerInput reasonerInput = rule.generatedUsing();
		ReasonerInputSerializer serializer = new ReasonerInputSerializer(rule);
		
		IReasonerInput deserializedInput = null;
		try {
			reasoner.serializeInput(reasonerInput, serializer);
			deserializedInput = reasoner.deserializeInput(serializer);
		} catch (SerializeException e) {
			// This should not happen.
			assert false;
			e.printStackTrace();
		}
		
		assertNotNull(deserializedInput);
		assertFalse("Deserialized input has an error", deserializedInput.hasError());
				
	}

	public static class ReasonerInputSerializer implements IReasonerInputReader, IReasonerInputWriter{

		private final IProofRule rule;
		private final Map<String, Predicate[]> predicates;
		private final Map<String, Expression[]> expressions;
		private final Map<String, String> strings;

		public ReasonerInputSerializer(IProofRule rule) {
			this.rule = rule;
			this.predicates = new HashMap<String, Predicate[]>();
			this.expressions = new HashMap<String, Expression[]>();
			this.strings = new HashMap<String, String>();
		}

		public IAntecedent[] getAntecedents() {
			return rule.getAntecedents();
		}

		public int getConfidence() {
			return rule.getConfidence();
		}

		public String getDisplayName() {
			return rule.getDisplayName();
		}

		public Expression[] getExpressions(String key) throws SerializeException {
			return expressions.get(key);
		}

		public Predicate getGoal() {
			return rule.getGoal();
		}

		public Set<Predicate> getNeededHyps() {
			return rule.getNeededHyps();
		}

		public Predicate[] getPredicates(String key) throws SerializeException {
			return predicates.get(key);
		}

		public String getString(String key) throws SerializeException {
			return strings.get(key);
		}

		public void putExpressions(String key, Expression... exprs) throws SerializeException {
			expressions.put(key, exprs);	
		}

		public void putPredicates(String key, Predicate... preds) throws SerializeException {
			predicates.put(key,preds);
		}

		public void putString(String key, String str) throws SerializeException {
			strings.put(key, str);
		}
	}
	
}

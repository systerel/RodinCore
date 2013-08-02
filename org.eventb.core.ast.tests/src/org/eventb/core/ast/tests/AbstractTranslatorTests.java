/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.addToTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.datatype.DatatypeTranslation;

/**
 * Common methods to check translation of expression formulas and axioms.
 * 
 * @author "Thomas Muller"
 */
public abstract class AbstractTranslatorTests extends AbstractTests {

	/*-- Record datatype definition, type parameters, and type environment ---*/
	protected static final String MESSAGE__DT = "Message[U,V] ::= "
			+ "message[sender: U; receiver: U; identifier: V]";

	protected static final String[] MESSAGE_TPARAMS = mList("Agent",
			"Identifier", "Person", "Stamp");

	protected static final String MESSAGE_TYPE_ENV = "Message_Type=ℙ(Message_Type); "
			+ "message=ℙ(Agent × Agent × Identifier × Message_Type); " //
			+ "sender=ℙ(Message_Type × Agent); " //
			+ "receiver=ℙ(Message_Type × Agent); " //
			+ "identifier=ℙ(Message_Type × Identifier); " //
			+ "Message=ℙ(Agent × Identifier × Message_Type);" //
			+ "Message_Type0=ℙ(Message_Type0); " //
			+ "message0=ℙ(Person × Person × Stamp × Message_Type0); " //
			+ "sender0=ℙ(Message_Type0 × Person); " //
			+ "receiver0=ℙ(Message_Type0 × Person); " //
			+ "identifier0=ℙ(Message_Type0 × Stamp); " //
			+ "Message0=ℙ(Person × Stamp × Message_Type0)";

	/*------------------------------------------------------------------------*/
	/*- Recursive datatype definition, type parameters, and type environment -*/
	protected static final String LIST__DT = "List[S] ::="
			+ " nil || cons[head: S; tail: List]";

	protected static final String[] LIST_TPARAMS = mList("Object", "Thing");

	protected static final String LIST_TYPE_ENV = "List_Type=ℙ(List_Type); "
			+ "cons=ℙ(Object × List_Type × List_Type); " //
			+ "nil=List_Type; " //
			+ "head=ℙ(List_Type × Object); " //
			+ "tail=ℙ(List_Type × List_Type); " //
			+ "List=ℙ(Object×List_Type); " //
			+ "List_Type0=ℙ(List_Type0); " //
			+ "cons0=ℙ(Thing × List_Type0 × List_Type0); " //
			+ "nil0=List_Type0; " //
			+ "head0=ℙ(List_Type0 × Thing); " //
			+ "tail0=ℙ(List_Type0 × List_Type0); " //
			+ "List0=ℙ(Thing×List_Type0)";

	/*------------------------------------------------------------------------*/
	protected static class TestTranslationSupport {

		private final Set<IFormulaExtension> allExts = new LinkedHashSet<IFormulaExtension>();
		private final Set<IDatatype> datatypes = new LinkedHashSet<IDatatype>();

		private final ITypeEnvironmentBuilder sourceTypeEnv;
		private ITypeEnvironment targetTypeEnv;

		// Lazily computed so that clients can first enrich the source type
		// environment
		private DatatypeTranslation translation;

		public TestTranslationSupport(String... extensionSpecs) {
			this(ff, extensionSpecs);
		}

		public TestTranslationSupport(FormulaFactory startFac,
				String... extensionSpecs) {
			if (startFac != null) {
				allExts.addAll(startFac.getExtensions());
			}
			injectDatatypeExtensions(startFac, extensionSpecs);
			final FormulaFactory fac = buildSourceFactory();
			this.sourceTypeEnv = fac.makeTypeEnvironment();
		}

		private void injectDatatypeExtensions(FormulaFactory startFac,
				String[] extensionSpecs) {
			for (String spec : extensionSpecs) {
				final IDatatype datatype = DatatypeParser.parse(startFac, spec);
				datatypes.add(datatype);
			}
		}

		public void addGivenTypes(String... givenTypeNames) {
			for (final String typeName : givenTypeNames) {
				sourceTypeEnv.addGivenSet(typeName);
			}
		}

		public FormulaFactory buildSourceFactory() {
			for (IDatatype dt : datatypes) {
				allExts.addAll(dt.getExtensions());
			}
			return FormulaFactory.getInstance(allExts);
		}

		public List<IDatatype> getDatatypes() {
			return new ArrayList<IDatatype>(datatypes);
		}

		public ITypeEnvironment getSourceTypeEnvironment() {
			return sourceTypeEnv;
		}

		public DatatypeTranslation getTranslation() {
			if (translation == null) {
				translation = new DatatypeTranslation(sourceTypeEnv);
			}
			return translation;
		}

		public void setExpectedTypeEnvironment(String resultingTypeEnv) {
			final FormulaFactory targetFac = getTranslation()
					.getTargetFormulaFactory();
			final ITypeEnvironmentBuilder tempEnv = targetFac.makeTypeEnvironment();
			targetTypeEnv = addToTypeEnvironment(tempEnv, resultingTypeEnv);
		}

		public void assertExprTranslation(String exprStr, String expectedStr) {
			final Expression expr = parseExpression(exprStr, sourceTypeEnv);
			final Expression actual = expr.translateDatatype(getTranslation());
			final Expression expected = parseExpression(expectedStr,
					targetTypeEnv);
			assertEquals(expected, actual);
		}

		public void assertPredTranslation(String predStr, String expectedStr) {
			final Predicate pred = parsePredicate(predStr, sourceTypeEnv);
			final Predicate actual = pred.translateDatatype(getTranslation());
			final Predicate expected = parsePredicate(expectedStr,
					targetTypeEnv);
			assertEquals(expected, actual);
		}

		public void assertAxioms(String... expectedPredStrs) {
			final List<Predicate> predicates = getTranslation().getAxioms();
			int i = 0;
			for (final Predicate pred : predicates) {
				assertTrue(pred.isTypeChecked());
				checkPredicate(expectedPredStrs[i], pred);
				i++;
			}
			assertEquals(i, predicates.size());
		}

		private void checkPredicate(String expectedStr, Predicate actual) {
			final Predicate expected = parsePredicate(expectedStr,
					targetTypeEnv);
			assertEquals(expected, actual);
		}

		public void addToSourceEnvironment(String typenvImage) {
			addToTypeEnvironment(sourceTypeEnv, typenvImage);
		}

		public Expression parseSourceExpression(String expression) {
			return parseExpression(expression, sourceTypeEnv);
		}

	}

	public static TestTranslationSupport mSupport(String... dtSpecs) {
		return new TestTranslationSupport(dtSpecs);
	}

	public static TestTranslationSupport mSupport(
			FormulaFactory extendedFactory, String... extensionSpecs) {
		return new TestTranslationSupport(extendedFactory, extensionSpecs);
	}

}

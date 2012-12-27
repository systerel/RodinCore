/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pp;

import static org.eventb.internal.pp.core.elements.terms.Util.mSet;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests about the new Predicate Prover sensitivity about superfluous
 * hypotheses, as exposed in Feature Request #1949927.
 * 
 * The predicates used here come initially from a proof obligation of the CDIS
 * case-study by Michael Butler and Abdolbaghi Rezazadeh.
 * 
 * @author Laurent Voisin
 */
public class HypothesisSensitivity extends AbstractRodinTest {

	static final ITypeEnvironmentBuilder typenv = mTypeEnvironment(
			"MESG=ℙ(MESG); value=Attrs↔Attr_value;" //
			+" last_update=Attrs↔Date_time; Update_successful=MESG;" //
			+" time_now=Date_time; update_mesg=MESG; pages=Page_number↔Page;" //
			+" database=Attr_id↔Attrs; selected_ais=EDD_id↔Attr_id;" //
			+" Attr_id=ℙ(Attr_id); contents=Page↔Page_contents;" //
			+" fields=Page_contents↔Gr_fld; Attrs=ℙ(Attrs);" //
			+" ups=Attr_id↔Attrs; manually_updated=Attrs↔BOOL;" //
			+" EDD_id=ℙ(EDD_id); conform=Attr_id↔Attr_value;" //
			+" attr=Gr_fld↔Attr_id; page_selections=EDD_id↔Page_number;" //
			+" ai=Attr_id; changed_ais=ℙ(Attr_id)",
			ff);
	static final String H1 = "∀ai·ai∈dom(ups)⇒ai ↦ value(ups(ai))∈conform";
	static final String H2 = "∀ai·ai ↦ value(database(ai))∈conform";
	static final String H3 = "ups;last_update=dom(ups) × {time_now}";
	static final String H4 = "ups∈Attr_id ⇸ Attrs";
	static final String H5 = "changed_ais={ai·ai∈dom(ups)∧¬value(database(ai))=value(ups(ai)) ∣ ai}";
	static final String G = "ai ↦ value((databaseups)(ai))∈conform";

	private static void doTest(String... hyps) {
		doTest(typenv, mSet(hyps), G, true, 10000);
	}
	
	
	/**
	 * Reduced case with only needed hypotheses (63 steps). 
	 */
	@Test
	public void H1H2() {
		doTest(H1, H2);
	}

	/**
	 * 61 steps.
	 */
	@Test
	public void H1H2H3() {
		doTest(H1, H2, H3);
	}

	/**
	 * 72 steps.
	 */
	@Test
	public void H1H2H4() {
		doTest(H1, H2, H4);
	}

	/**
	 * 503 steps.
	 */
	@Test
	public void H1H2H5() {
		doTest(H1, H2, H5);
	}
	
	/**
	 * 20034 steps.
	 */
	@Test
    @Ignore("Takes too much time")
	public void H1H2H3H5() {
		doTest(H1, H2, H3, H5);
	}

	/**
	 * > 50000 steps.
	 */
	@Test
    @Ignore("Takes too much time")
	public void H1H2H3H4H5() {
		doTest(H1, H2, H3, H4, H5);
	}

	/**
	 * > 50000 steps.
	 */
	@Test
    @Ignore("Takes too much time")
	public void originalLemma() {
		doTest(typenv,
				mSet(
						"∀ai·ai∈Attr_id⇒ai ↦ value(database(ai))∈conform",
						"ups∈Attr_id ⇸ Attrs",
						"ups;last_update=dom(ups) × {time_now}",
						"ups;manually_updated=dom(ups) × {FALSE}",
						"∀ai·ai∈dom(ups)⇒ai ↦ value(ups(ai))∈conform",
						"selected_ais=page_selections;pages;contents;fields;attr",
						"update_mesg=Update_successful",
						"changed_ais={ai·ai∈dom(ups)∧¬value(database(ai))=value(ups(ai)) ∣ ai}",
						"∀ai·ai ↦ value(database(ai))∈conform",
						"page_selections;pages;contents;fields;attr∈EDD_id ↔ Attr_id",
						"Update_successful∈MESG",
						"{ai·ai∈dom(ups)∧¬value(database(ai))=value(ups(ai)) ∣ ai}⊆Attr_id"),
				"ai ↦ value((databaseups)(ai))∈conform", true, 10000);
	}

}

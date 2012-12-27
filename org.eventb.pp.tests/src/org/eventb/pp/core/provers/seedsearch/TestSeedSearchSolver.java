/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.core.provers.seedsearch;

import static org.eventb.internal.pp.core.elements.terms.Util.mSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.provers.seedsearch.solver.Instantiable;
import org.eventb.internal.pp.core.provers.seedsearch.solver.InstantiationValue;
import org.eventb.internal.pp.core.provers.seedsearch.solver.LiteralSignature;
import org.eventb.internal.pp.core.provers.seedsearch.solver.SeedSearchSolver;
import org.eventb.internal.pp.core.provers.seedsearch.solver.VariableLink;
import org.junit.Before;
import org.junit.Test;

public class TestSeedSearchSolver extends AbstractPPTest {

	private static class MySignature extends LiteralSignature {
		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		private String string;
		public MySignature(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	}
	
	private static class MyInstantiable extends Instantiable {
		private String string;
		public MyInstantiable(String string, LiteralSignature signature) {
			super(signature);
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj == this;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}
	
	private LiteralSignature S(String string) {
		return new MySignature(string);
	}
	
	private static Instantiable I(String string, LiteralSignature signature) {
		return new MyInstantiable(string, signature);
	}
	
	private Instantiable Pex;
	private Instantiable nSex;
	private Instantiable nPex;
//	private Instantiable Pxe;
//	private Instantiable nPxe;
	private Instantiable Qex;
	private Instantiable nQex;
	private Instantiable nRex;
//	private Instantiable Qxe;
//	private Instantiable nQxe;
//	private Instantiable Rexx;
//	private Instantiable nRexx;
//	private Instantiable Rxex;
//	private Instantiable nRxex;
	
	private LiteralSignature Pcx;
	private LiteralSignature nPcx;
	private LiteralSignature Pxc;
	private LiteralSignature nPxc;
	private LiteralSignature Qcx;
	private LiteralSignature nQcx;
	private LiteralSignature Qxc;
	private LiteralSignature nQxc;
	private LiteralSignature Rcx;
	private LiteralSignature nRcx;
	private LiteralSignature Rxc;
	private LiteralSignature nRxc;
	private LiteralSignature Sxc;
	private LiteralSignature nSxc;
	private LiteralSignature Rcxx;
	private LiteralSignature nRcxx;
	private LiteralSignature Rxcx;
	private LiteralSignature nRxcx;
	
//	private VariableLink PQcx;
	private VariableLink PQxc;
	private VariableLink PnQxc;
	private VariableLink SnPxc;
//	private VariableLink nPnQcx;
	private VariableLink nPnQxc;
	private VariableLink nQnRxc;
	private VariableLink RnPxc;
//	private VariableLink PRcx;
	private VariableLink PRxc;
//	private VariableLink QRcx;
//	private VariableLink QRxc;
	private VariableLink RSxc;
	
	private InstantiationValue Ia;
	
	private SeedSearchSolver solver;
	
    @Before
	public void setUp() throws Exception {
		solver = new SeedSearchSolver();
		
		Pcx = S("Pcx");
		nPcx = S("¬Pcx");
		Pcx.setMatchingLiteral(nPcx);
		nPcx.setMatchingLiteral(Pcx);
		
		Pxc = S("Pxc");
		nPxc = S("¬Pxc");
		Pxc.setMatchingLiteral(nPxc);
		nPxc.setMatchingLiteral(Pxc);
		
		Qcx = S("Qcx");
		nQcx = S("¬Qcx");
		Qcx.setMatchingLiteral(nQcx);
		nQcx.setMatchingLiteral(Qcx);
		
		Qxc = S("Qxc");
		nQxc = S("¬Qxc");
		Qxc.setMatchingLiteral(nQxc);
		nQxc.setMatchingLiteral(Qxc);
		
		Rcx = S("Rcx");
		nRcx = S("¬Rcx");
		Rcx.setMatchingLiteral(nRcx);
		nRcx.setMatchingLiteral(Rcx);
		
		Rxc = S("Rxc");
		nRxc = S("¬Rxc");
		Rxc.setMatchingLiteral(nRxc);
		nRxc.setMatchingLiteral(Rxc);
		
		Sxc = S("Sxc");
		nSxc = S("¬Sxc");
		Sxc.setMatchingLiteral(nSxc);
		nSxc.setMatchingLiteral(Sxc);
		
		Rcxx = S("Rcxx");
		nRcxx = S("¬Rcxx");
		Rcxx.setMatchingLiteral(nRcxx);
		nRcxx.setMatchingLiteral(Rcxx);
		
		Rxcx = S("Rxcx");
		nRxcx = S("¬Rxcx");
		Rxcx.setMatchingLiteral(nRxcx);
		nRxcx.setMatchingLiteral(Rxcx);
		
		Pex = I("Pex",Pxc);
		nPex = I("¬Pex",nPxc);
//		Pxe = I("Pxe",Pcx);
//		nPxe = I("¬Pxe",nPcx);
		Qex = I("Qex",Qxc);
		nQex = I("¬Qex",nQxc);
		nRex = I("¬Rex",nRxc);
//		Qxe = I("Qxe",Qcx);
//		nQxe = I("¬Qxe",nQcx);
//		Rexx = I("Rexx",Rxcx);
//		nRexx = I("¬Rexx",nRxcx);
//		Rxex = I("Rxex",Rcxx);
//		nRxex = I("¬Rxex",nRcxx);
		nSex = I("¬Sex",nSxc);
		
//		PQcx = new VariableLink(Pcx,Qcx);
		PQxc = new VariableLink(Pxc,Qxc);
		PnQxc = new VariableLink(Pxc,nQxc);
//		nPnQcx = new VariableLink(nPcx,nQcx);
		nPnQxc = new VariableLink(nPxc,nQxc);
		nQnRxc = new VariableLink(nQxc,nRxc);
		RnPxc = new VariableLink(Rxc,nPxc);
		PRxc = new VariableLink(Rxc,Pxc);
//		QRcx = new VariableLink(Qcx,Rcx);
//		QRxc = new VariableLink(Qxc,Rxc);
		RSxc = new VariableLink(Rxc,Sxc);
		SnPxc = new VariableLink(Sxc,nPxc);
		
//		Ia = new InstantiationValue(a);
//		Ia.setLevel(BASE);
	}
	
	private Set<String> dump(LiteralSignature... signatures) {
		Set<String> result = new HashSet<String>();
		for (LiteralSignature signature : signatures) {
			result.add(signature.dump());
		}
		return result;
	}
	
	private void assertEmpty(Collection<?> set) {
		assertTrue(set.isEmpty());
	}
	
	private void assertNotEmpty(Collection<?> set) {
		assertTrue(!set.isEmpty());
	}
	
    @Test
	public void testSimpleInstantiation() {
		Ia = new InstantiationValue(a, Pxc);
		assertEmpty(solver.addInstantiationValue(Ia));
		assertNotEmpty(solver.addInstantiable(nPex));
		assertEquals(mSet(
				"Pxc[C=[a],I=[¬Pex],]",
				"¬Pxc[]"
		),dump(Pxc,nPxc));
	}

    @Test
	public void testSimpleInstantiationFirst() {
		Ia = new InstantiationValue(a, Pxc);
		assertEmpty(solver.addInstantiable(nPex));
		assertNotEmpty(solver.addInstantiationValue(Ia));
		assertEquals(mSet(
				"Pxc[C=[a],I=[¬Pex],]",
				"¬Pxc[]"
		),dump(Pxc,nPxc));
	}

    @Test
	public void testSimpleNoInstantiation() {
		assertEquals(mSet(
				"¬Pxc[]"
		),dump(nPxc));
	}

    @Test
	public void testSimpleLinkedInstantiation() {
		Ia = new InstantiationValue(a, nQxc);
		assertEmpty(solver.addInstantiationValue(Ia));
		assertEmpty(solver.addVariableLink(PQxc));
		assertNotEmpty(solver.addInstantiable(nPex));
		assertEquals(mSet(
				"¬Qxc[C=[a],I=[¬Pex],]",
				"¬Pxc[]",
				"Pxc[V=[->Qxc],I=[¬Pex],]",
				"Qxc[V=[->Pxc],]"
		),dump(nQxc,nPxc,Pxc,Qxc));
	}

    @Test
	public void testSimpleLinkedInstantiationFirst() {
		assertEmpty(solver.addInstantiable(nPex));
		Ia = new InstantiationValue(a, nQxc);
		assertEmpty(solver.addInstantiationValue(Ia));
		assertNotEmpty(solver.addVariableLink(PQxc));
		assertEquals(mSet(
				"¬Qxc[C=[a],I=[¬Pex],]",
				"¬Pxc[]",
				"Pxc[V=[->Qxc],I=[¬Pex],]",
				"Qxc[V=[->Pxc],]"
		),dump(nQxc,nPxc,Pxc,Qxc));
	}

    @Test
	public void testSimpleLinkedVariableLast() {
		assertEmpty(solver.addInstantiable(nPex));
		Ia = new InstantiationValue(a, nQxc);
		assertEmpty(solver.addVariableLink(PQxc));
		assertNotEmpty(solver.addInstantiationValue(Ia));
		assertEquals(mSet(
				"¬Qxc[C=[a],I=[¬Pex],]",
				"¬Pxc[]",
				"Pxc[V=[->Qxc],I=[¬Pex],]",
				"Qxc[V=[->Pxc],]"
		),dump(nQxc,nPxc,Pxc,Qxc));
	}
	
    @Test
	public void testMutliLinkedVariable() {
		assertEmpty(solver.addInstantiable(nPex));
		assertEmpty(solver.addVariableLink(PQxc));
		assertEmpty(solver.addVariableLink(PnQxc));
		assertEquals(mSet(
				"¬Pxc[I=[¬Pex],]",
				"¬Qxc[V=[->Pxc],I=[¬Pex],]",
				"Pxc[V=[->Qxc,->¬Qxc],I=[¬Pex],]",
				"Qxc[V=[->Pxc],I=[¬Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc));
		Ia = new InstantiationValue(a, nPxc);
		assertNotEmpty(solver.addInstantiationValue(Ia));
		assertEquals(mSet(
				"¬Pxc[C=[a],I=[¬Pex],]",
				"¬Qxc[V=[->Pxc],I=[¬Pex],]",
				"Pxc[V=[->Qxc,->¬Qxc],I=[¬Pex],]",
				"Qxc[V=[->Pxc],I=[¬Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc));
	}

    @Test
	public void testLooping() {
		assertEmpty(solver.addInstantiable(nPex));
		assertEmpty(solver.addVariableLink(PQxc));
		assertEmpty(solver.addVariableLink(nPnQxc));
		Ia = new InstantiationValue(a, Pxc);
		assertNotEmpty(solver.addInstantiationValue(Ia));
		assertEquals(mSet(
				"Pxc[V=[->Qxc],C=[a],I=[¬Pex],]",
				"Qxc[V=[->Pxc],]",
				"¬Pxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Pxc],I=[¬Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc));
	}

    @Test
	public void testRedundantInstantiation() {
		assertEmpty(solver.addInstantiable(nPex));
		assertEmpty(solver.addVariableLink(PQxc));
		InstantiationValue Ia1 = new InstantiationValue(a, nQxc);
		assertNotEmpty(solver.addInstantiationValue(Ia1));
		InstantiationValue Ia2 = new InstantiationValue(a, Pxc);
		assertNotEmpty(solver.addInstantiationValue(Ia2));
		assertEquals(mSet(
				"Pxc[V=[->Qxc],C=[a],I=[¬Pex],]",
				"Qxc[V=[->Pxc],]",
				"¬Pxc[]",
				"¬Qxc[C=[a],I=[¬Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc));
	}

    @Test
	public void testRemoveSimpleVariableLink() {
		solver.addVariableLink(nPnQxc);
		solver.addInstantiable(Pex);
		assertEquals(mSet(
				"¬Pxc[V=[->¬Qxc],I=[Pex],]",
				"¬Qxc[V=[->¬Pxc],]",
				"Pxc[]",
				"Qxc[I=[Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc));
		solver.removeVariableLink(nPnQxc);
		assertEquals(mSet(
				"¬Pxc[I=[Pex],]",
				"¬Qxc[]",
				"Pxc[]",
				"Qxc[]"
		), dump(nQxc,nPxc,Pxc,Qxc));
	}
	
    @Test
	public void testRemoveComplexVariableLink() {
		solver.addVariableLink(PQxc);
		solver.addVariableLink(nQnRxc);
		solver.addVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[]",
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[V=[->Sxc],]",
				"Sxc[V=[->Rxc],]",
				"¬Sxc[]"
		), dump(nQxc,nPxc,Pxc,Qxc,nRxc,Rxc,Sxc,nSxc));
		solver.addInstantiable(nSex);
		assertEquals(mSet(
				"¬Pxc[I=[¬Sex],]",
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],I=[¬Sex],]",
				"¬Rxc[V=[->¬Qxc],I=[¬Sex],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[V=[->Sxc],]",
				"Sxc[V=[->Rxc],I=[¬Sex],]",
				"¬Sxc[]"
		), dump(nQxc,nPxc,Pxc,Qxc,nRxc,Rxc,Sxc,nSxc));
		solver.removeVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[]",
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[]",
				"Sxc[I=[¬Sex],]",
				"¬Sxc[]"
		), dump(nQxc,nPxc,Pxc,Qxc,nRxc,Rxc,Sxc,nSxc));
	}
	
    @Test
	public void testRemoveComplexVariableLinkWithLoop() {
		solver.addVariableLink(PQxc);
		solver.addVariableLink(nQnRxc);
		solver.addVariableLink(RnPxc);
		assertEquals(mSet(
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[V=[->¬Pxc],]",
				"¬Pxc[V=[->Rxc],]"
		), dump(nQxc,nPxc,Pxc,Qxc,nRxc,Rxc));
		solver.addInstantiable(Pex);
		assertEquals(mSet(
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],I=[Pex],]",
				"¬Rxc[V=[->¬Qxc],I=[Pex],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[V=[->¬Pxc],]",
				"¬Pxc[V=[->Rxc],I=[Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc,nRxc,Rxc));
		solver.removeVariableLink(RnPxc);
		assertEquals(mSet(
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[]",
				"¬Pxc[I=[Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc,nRxc,Rxc));
	}
	
    @Test
	public void testRemoveMutliLinkedVariable() {
		solver.addVariableLink(PQxc);
		solver.addVariableLink(PnQxc);
		assertEquals(mSet(
				"¬Qxc[V=[->Pxc],]",
				"Pxc[V=[->Qxc,->¬Qxc],]",
				"Qxc[V=[->Pxc],]",
				"¬Pxc[]"
		), dump(nQxc,nPxc,Pxc,Qxc));
		solver.addInstantiable(nPex);
		assertEquals(mSet(
				"¬Pxc[I=[¬Pex],]",
				"¬Qxc[V=[->Pxc],I=[¬Pex],]",
				"Pxc[V=[->Qxc,->¬Qxc],I=[¬Pex],]",
				"Qxc[V=[->Pxc],I=[¬Pex],]"
		), dump(nQxc,nPxc,Pxc,Qxc));
		solver.removeVariableLink(PnQxc);
		assertEquals(mSet(
				"¬Pxc[]",
				"¬Qxc[I=[¬Pex],]",
				"Pxc[V=[->Qxc],I=[¬Pex],]",
				"Qxc[V=[->Pxc],]"
		), dump(nQxc,nPxc,Pxc,Qxc));
	}
	
    @Test
	public void testRemoveVariableLink() {
		solver.addVariableLink(PQxc);
		solver.addVariableLink(nPnQxc);
		solver.addInstantiable(Pex);
		solver.addInstantiable(nQex);
		assertEquals(mSet(
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],I=[Pex, ¬Qex],]",
				"¬Pxc[V=[->¬Qxc],I=[Pex, ¬Qex],]",
				"¬Qxc[V=[->¬Pxc],]"
		), dump(nQxc,nPxc,Pxc,Qxc));

		solver.removeVariableLink(nPnQxc);
		assertEquals(mSet(
				"Pxc[V=[->Qxc],]",
				"Qxc[V=[->Pxc],I=[¬Qex],]",
				"¬Pxc[I=[Pex, ¬Qex],]",
				"¬Qxc[]"
		), dump(nQxc,nPxc,Pxc,Qxc));
	}
	
    @Test
	public void testRemoveVariableLinkComplexGraphWithConstantFirst() {
		solver.addInstantiable(Qex);
		solver.addVariableLink(PRxc);
		solver.addVariableLink(nQnRxc);
		solver.addVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[I=[Qex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],I=[Qex],]",
				"Rxc[V=[->Pxc,->Sxc],I=[Qex],]",
				"Sxc[V=[->Rxc],]",
				"¬Sxc[I=[Qex],]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
		solver.removeVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[I=[Qex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],I=[Qex],]",
				"Rxc[V=[->Pxc],I=[Qex],]",
				"Sxc[]",
				"¬Sxc[]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
	}
	
    @Test
	public void testRemoveVariableLinkComplexGraph() {
		solver.addVariableLink(PRxc);
		solver.addVariableLink(nQnRxc);
		solver.addVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[V=[->Pxc,->Sxc],]",
				"Sxc[V=[->Rxc],]",
				"¬Sxc[]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
		solver.addInstantiable(Qex);
		assertEquals(mSet(
				"¬Pxc[I=[Qex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],I=[Qex],]",
				"Rxc[V=[->Pxc,->Sxc],I=[Qex],]",
				"Sxc[V=[->Rxc],]",
				"¬Sxc[I=[Qex],]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
		solver.removeVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[I=[Qex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],I=[Qex],]",
				"Rxc[V=[->Pxc],I=[Qex],]",
				"Sxc[]",
				"¬Sxc[]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
	}
	
    @Test
	public void testRemoveVariableLinkComplexGraph2() {
		// begins like preceding test
		solver.addVariableLink(PRxc);
		solver.addVariableLink(nQnRxc);
		solver.addVariableLink(RSxc);
		solver.addVariableLink(SnPxc);
		assertEquals(mSet(
				"¬Pxc[V=[->Sxc],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[V=[->Pxc,->Sxc],]",
				"Sxc[V=[->Rxc,->¬Pxc],]",
				"¬Sxc[]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
		solver.addInstantiable(Qex);
		assertEquals(mSet(
				"¬Pxc[V=[->Sxc],I=[Qex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],I=[Qex],]",
				"Rxc[V=[->Pxc,->Sxc],I=[Qex],]",
				"Sxc[V=[->Rxc,->¬Pxc],]",
				"¬Sxc[I=[Qex],]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
		solver.removeVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[V=[->Sxc],I=[Qex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],I=[Qex],]",
				"Rxc[V=[->Pxc],I=[Qex],]",
				"Sxc[V=[->¬Pxc],]",
				"¬Sxc[I=[Qex],]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
	}
	
    @Test
	public void testRemoveVariableLinkComplexGraph3() {
		solver.addVariableLink(PRxc);
		solver.addVariableLink(nQnRxc);
		solver.addVariableLink(RSxc);
		assertEquals(mSet(
				"¬Pxc[]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],]",
				"Rxc[V=[->Pxc,->Sxc],]",
				"Sxc[V=[->Rxc],]",
				"¬Sxc[]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
		solver.addInstantiable(Qex);
		solver.addInstantiable(nRex);
		assertEquals(mSet(
				"¬Pxc[I=[Qex, ¬Rex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[V=[->¬Qxc],]",
				"¬Qxc[V=[->¬Rxc],I=[Qex],]",
				"Rxc[V=[->Pxc,->Sxc],I=[Qex, ¬Rex],]",
				"Sxc[V=[->Rxc],]",
				"¬Sxc[I=[Qex, ¬Rex],]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
		solver.removeVariableLink(nQnRxc);
		assertEquals(mSet(
				"¬Pxc[I=[¬Rex],]",
				"Pxc[V=[->Rxc],]",
				"¬Rxc[]",
				"¬Qxc[I=[Qex],]",
				"Rxc[V=[->Pxc,->Sxc],I=[¬Rex],]",
				"Sxc[V=[->Rxc],]",
				"¬Sxc[I=[¬Rex],]"
		), dump(nQxc,nRxc,Rxc,Pxc,nPxc,Sxc,nSxc));
	}
}

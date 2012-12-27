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
package org.eventb.pp.core.tracing;

import static org.eventb.internal.pp.core.elements.terms.Util.mSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.Tracer;
import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.junit.Test;

public class TestTracer extends AbstractPPTest {

	// nothing yet
	
	private static class MyOrigin implements IOrigin {
		private Level level;
		private Set<Level> dependencies;
		
		MyOrigin(Level level, Set<Level> dependencies) {
			assert dependencies.contains(level);
			
			this.level = level;
			this.dependencies = dependencies;
		}
		
		@Override
		public boolean dependsOnGoal() {
			// not tested
			return false;
		}

		@Override
		public void addDependenciesTo(Set<Level> dependencies) {
			dependencies.addAll(this.dependencies);
		}

		@Override
		public Level getLevel() {
			return level;
		}

		@Override
		public boolean isDefinition() {
			// not tested
			return false;
		}

		@Override
		public void trace(Tracer tracer) {
			// not tested
		}
		
		@Override
		public String toString() {
			return level+"";
		}

		@Override
		public int getDepth() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	private static IOrigin o0 = new MyOrigin(BASE, mSet(BASE));
	private static IOrigin o1 = new MyOrigin(ONE, mSet(BASE, ONE));
	private static IOrigin o2 = new MyOrigin(TWO, mSet(BASE, TWO));
	private static IOrigin o3 = new MyOrigin(THREE, mSet(BASE, THREE));
	private static IOrigin o4 = new MyOrigin(FOUR, mSet(BASE, FOUR));
//	private static IOrigin o5 = new MyOrigin(FIVE, mSet(BASE, FIVE));
	private static IOrigin o7 = new MyOrigin(SEVEN, mSet(BASE, SEVEN));
	private static IOrigin o8 = new MyOrigin(EIGHT, mSet(BASE, EIGHT));
	private static IOrigin o9 = new MyOrigin(NINE, mSet(BASE, NINE));
	private static IOrigin o10 = new MyOrigin(TEN, mSet(BASE, TEN));
	
//	private static IOrigin o13 = new MyOrigin(ONE, mSet(BASE, ONE, THREE));
//	private static IOrigin o14 = new MyOrigin(ONE, mSet(BASE, ONE, FOUR));
//	private static IOrigin o25 = new MyOrigin(TWO, mSet(BASE, TWO, FIVE));
//	private static IOrigin o26 = new MyOrigin(TWO, mSet(BASE, TWO, SIX));
	
	private static IOrigin o31 = new MyOrigin(THREE, mSet(BASE, ONE, THREE));
	private static IOrigin o41 = new MyOrigin(FOUR, mSet(BASE, ONE, FOUR));

	private static IOrigin o731 = new MyOrigin(SEVEN, mSet(BASE, ONE, THREE, SEVEN));
	private static IOrigin o71 = new MyOrigin(SEVEN, mSet(BASE, ONE, SEVEN));
	private static IOrigin o73 = new MyOrigin(SEVEN, mSet(BASE, THREE, SEVEN));
	
	private static IOrigin o831 = new MyOrigin(EIGHT, mSet(BASE, ONE, THREE, EIGHT));
	private static IOrigin o81 = new MyOrigin(EIGHT, mSet(BASE, ONE, EIGHT));
	private static IOrigin o83 = new MyOrigin(EIGHT, mSet(BASE, THREE, EIGHT));
	
	private static IOrigin o941 = new MyOrigin(NINE, mSet(BASE, ONE, FOUR, NINE));
	private static IOrigin o91 = new MyOrigin(NINE, mSet(BASE, ONE, NINE));
	private static IOrigin o94 = new MyOrigin(NINE, mSet(BASE, FOUR, NINE));
	
//	private static IOrigin o10_4_1 = new MyOrigin(TEN, mSet(BASE, ONE, FOUR, TEN));
	private static IOrigin o10_4 = new MyOrigin(TEN, mSet(BASE, FOUR, TEN));
	private static IOrigin o10_1 = new MyOrigin(TEN, mSet(BASE, ONE, TEN));
	
	private static IOrigin o19 = new MyOrigin(NINETEEN, mSet(NINETEEN));
	private static IOrigin o20_4 = new MyOrigin(TWENTY, mSet(FOUR, TWENTY));
	
	private final Tracer tracer = new Tracer();
	
	private void assertAddClosingClauseFails(IOrigin origin) {
		try {
			tracer.addClosingClauseAndUpdateLevel(origin);
			fail();
		}
		catch (Throwable e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}
	
    @Test
	public void testSimpleTracer() {
		tracer.addClosingClauseAndUpdateLevel(o0);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o0), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroWithUselessClosings() {
		tracer.addClosingClauseAndUpdateLevel(o1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o0);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o0), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroWithUselessClosings1() {
		tracer.addClosingClauseAndUpdateLevel(o31);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o31,o4,o2), tracer.getClosingOrigins());
	}

    @Test
	public void testProofAtOneWithUselessClosings() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o1, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelRight() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o94);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10_4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o94, o10_4), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelRight2() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o94);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10_1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o3, o94, o10_1), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelRight3() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o94);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o94, o10), tracer.getClosingOrigins());
	}
	
	//
    @Test
	public void testProofAtOneClosingAtLowerLevelRight4() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o91);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10_4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o3, o91, o10_4), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelRight5() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o91);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10_1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o91, o10_1), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelRight6() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o91);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o91, o10), tracer.getClosingOrigins());
	}
	
	//
    @Test
	public void testProofAtZeroClosingAtLowerLevelRight1() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10_4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o9, o10_4), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelRight2() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10_1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o9, o10_1), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelRight3() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o10);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o9, o10), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtFourClosingAtLowerLevelRight() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o4), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtFourClosingAtLowerLevelRight2() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o941);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o4), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtFourClosingAtLowerLevelRight3() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o3, o41), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtFourClosingAtLowerLevelRight4() {
		tracer.addClosingClauseAndUpdateLevel(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClauseAndUpdateLevel(o1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o1, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft() {
		tracer.addClosingClauseAndUpdateLevel(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o831, o41, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft2() {
		tracer.addClosingClauseAndUpdateLevel(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o83, o41, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft3() {
		tracer.addClosingClauseAndUpdateLevel(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o81, o41, o2), tracer.getClosingOrigins());
	}
	
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft4() {
		tracer.addClosingClauseAndUpdateLevel(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o831, o41, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft5() {
		tracer.addClosingClauseAndUpdateLevel(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o83, o41, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft6() {
		tracer.addClosingClauseAndUpdateLevel(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o81, o41, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft7() {
		tracer.addClosingClauseAndUpdateLevel(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o831, o41, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft8() {
		tracer.addClosingClauseAndUpdateLevel(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o83, o41, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtOneClosingAtLowerLevelLeft9() {
		tracer.addClosingClauseAndUpdateLevel(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o81);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		assertAddClosingClauseFails(o41);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o81, o2), tracer.getClosingOrigins());
	}
	
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft() {
		tracer.addClosingClauseAndUpdateLevel(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o831, o4, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft2() {
		tracer.addClosingClauseAndUpdateLevel(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o83, o4, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft3() {
		tracer.addClosingClauseAndUpdateLevel(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o81, o4, o2), tracer.getClosingOrigins());
	}
	
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft4() {
		tracer.addClosingClauseAndUpdateLevel(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o831, o4, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft5() {
		tracer.addClosingClauseAndUpdateLevel(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o73, o83, o4), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft6() {
		tracer.addClosingClauseAndUpdateLevel(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o81, o4, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft7() {
		tracer.addClosingClauseAndUpdateLevel(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o831, o4, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft8() {
		tracer.addClosingClauseAndUpdateLevel(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o83, o4, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAtZeroClosingAtLowerLevelLeft9() {
		tracer.addClosingClauseAndUpdateLevel(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o81);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		assertAddClosingClauseFails(o4);
		tracer.addClosingClauseAndUpdateLevel(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o81, o2), tracer.getClosingOrigins());
	}
	
    @Test
	public void testProofAlreadyExistant1() {
		tracer.addClosingClauseAndUpdateLevel(o7);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClauseAndUpdateLevel(o8);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		// here
		assertAddClosingClauseFails(o4);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o7, o8), tracer.getClosingOrigins());
	}

    @Test
	public void testSameSplitTwice() {
		tracer.addClosingClauseAndUpdateLevel(o31);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		assertAddClosingClauseFails(o7);
		assertAddClosingClauseFails(o8);
		assertEquals(mSet(o31, o41), tracer.getClosingOrigins());
	}
	
    @Test
	public void testClosingGetBackOnLeftBranch() {
		tracer.addClosingClauseAndUpdateLevel(o31);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClauseAndUpdateLevel(o19);
		assertEquals(tracer.getLastClosedLevel(), NINETEEN);
		tracer.addClosingClauseAndUpdateLevel(o20_4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
	}
}

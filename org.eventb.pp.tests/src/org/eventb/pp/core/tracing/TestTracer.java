package org.eventb.pp.core.tracing;

import static org.eventb.pp.Util.mSet;

import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.Tracer;
import org.eventb.pp.AbstractPPTest;

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
		
		public boolean dependsOnGoal() {
			// not tested
			return false;
		}

		public void getDependencies(Set<Level> dependencies) {
			dependencies.addAll(this.dependencies);
		}

		public Level getLevel() {
			return level;
		}

		public boolean isDefinition() {
			// not tested
			return false;
		}

		public void trace(Tracer tracer) {
			// not tested
		}
		
		@Override
		public String toString() {
			return level+"";
		}

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
			tracer.addClosingClause(origin);
			fail();
		}
		catch (Throwable e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}
	
	public void testSimpleTracer() {
		tracer.addClosingClause(o0);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o0), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroWithUselessClosings() {
		tracer.addClosingClause(o1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o0);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o0), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroWithUselessClosings1() {
		tracer.addClosingClause(o31);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o31,o4,o2), tracer.getClosingOrigins());
	}

	public void testProofAtOneWithUselessClosings() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o1, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelRight() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o94);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10_4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o94, o10_4), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelRight2() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o94);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10_1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o3, o94, o10_1), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelRight3() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o94);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o94, o10), tracer.getClosingOrigins());
	}
	
	//
	public void testProofAtOneClosingAtLowerLevelRight4() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o91);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10_4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o3, o91, o10_4), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelRight5() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o91);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10_1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o91, o10_1), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelRight6() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o91);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o91, o10), tracer.getClosingOrigins());
	}
	
	//
	public void testProofAtZeroClosingAtLowerLevelRight1() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10_4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o9, o10_4), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelRight2() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10_1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o9, o10_1), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelRight3() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o10);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o9, o10), tracer.getClosingOrigins());
	}
	
	public void testProofAtFourClosingAtLowerLevelRight() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o4), tracer.getClosingOrigins());
	}
	
	public void testProofAtFourClosingAtLowerLevelRight2() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o941);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o3, o4), tracer.getClosingOrigins());
	}
	
	public void testProofAtFourClosingAtLowerLevelRight3() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o2, o3, o41), tracer.getClosingOrigins());
	}
	
	public void testProofAtFourClosingAtLowerLevelRight4() {
		tracer.addClosingClause(o3);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o9);
		assertEquals(tracer.getLastClosedLevel(), NINE);
		tracer.addClosingClause(o1);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);		
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o1, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft() {
		tracer.addClosingClause(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o831, o41, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft2() {
		tracer.addClosingClause(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o83, o41, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft3() {
		tracer.addClosingClause(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o81, o41, o2), tracer.getClosingOrigins());
	}
	
	
	public void testProofAtOneClosingAtLowerLevelLeft4() {
		tracer.addClosingClause(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o831, o41, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft5() {
		tracer.addClosingClause(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o83, o41, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft6() {
		tracer.addClosingClause(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o81, o41, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft7() {
		tracer.addClosingClause(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o831, o41, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft8() {
		tracer.addClosingClause(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o83, o41, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtOneClosingAtLowerLevelLeft9() {
		tracer.addClosingClause(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o81);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		assertAddClosingClauseFails(o41);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o81, o2), tracer.getClosingOrigins());
	}
	
	
	public void testProofAtZeroClosingAtLowerLevelLeft() {
		tracer.addClosingClause(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o831, o4, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelLeft2() {
		tracer.addClosingClause(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o83, o4, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelLeft3() {
		tracer.addClosingClause(o731);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o731, o81, o4, o2), tracer.getClosingOrigins());
	}
	
	
	public void testProofAtZeroClosingAtLowerLevelLeft4() {
		tracer.addClosingClause(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o831, o4, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelLeft5() {
		tracer.addClosingClause(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o73, o83, o4), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelLeft6() {
		tracer.addClosingClause(o73);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o81);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o73, o81, o4, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelLeft7() {
		tracer.addClosingClause(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o831);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o831, o4, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelLeft8() {
		tracer.addClosingClause(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o83);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o83, o4, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAtZeroClosingAtLowerLevelLeft9() {
		tracer.addClosingClause(o71);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o81);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		assertAddClosingClauseFails(o4);
		tracer.addClosingClause(o2);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		assertEquals(mSet(o71, o81, o2), tracer.getClosingOrigins());
	}
	
	public void testProofAlreadyExistant1() {
		tracer.addClosingClause(o7);
		assertEquals(tracer.getLastClosedLevel(), SEVEN);
		tracer.addClosingClause(o8);
		assertEquals(tracer.getLastClosedLevel(), BASE);
		// here
		assertAddClosingClauseFails(o4);
		assertAddClosingClauseFails(o2);
		assertEquals(mSet(o7, o8), tracer.getClosingOrigins());
	}

	public void testSameSplitTwice() {
		tracer.addClosingClause(o31);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o41);
		assertEquals(tracer.getLastClosedLevel(), ONE);
		assertAddClosingClauseFails(o7);
		assertAddClosingClauseFails(o8);
		assertEquals(mSet(o31, o41), tracer.getClosingOrigins());
	}
	
	public void testClosingGetBackOnLeftBranch() {
		tracer.addClosingClause(o31);
		assertEquals(tracer.getLastClosedLevel(), THREE);
		tracer.addClosingClause(o19);
		assertEquals(tracer.getLastClosedLevel(), NINETEEN);
		tracer.addClosingClause(o20_4);
		assertEquals(tracer.getLastClosedLevel(), ONE);
	}
}

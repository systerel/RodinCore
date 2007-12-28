package org.eventb.pp.loader;

import java.util.Arrays;
import java.util.List;

import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;
import org.eventb.internal.pp.loader.predicate.IntermediateResult;

public class TestIndexUnification extends AbstractPPTest {

	private final TermSignature a = Util.mConstant("a");
	private final TermSignature b = Util.mConstant("b");
	private final TermSignature c = Util.mConstant("c");
	private final TermSignature x = Util.mVariable(0, 0);
	private final TermSignature y = Util.mVariable(1, 1);
	private final TermSignature z = Util.mVariable(2, 2);

	private final TermSignature xx = Util.mVariable(0, -1);
	private final TermSignature yy = Util.mVariable(1, -2);
	private final TermSignature zz = Util.mVariable(2, -3);

	private static IIntermediateResult mList(TermSignature... terms) {
		return new IntermediateResult(Arrays.asList(terms));
	}

	private static void doTest(IIntermediateResult source,
			IIntermediateResult target, IIntermediateResult expected) {
		final PredicateDescriptor desc = new PredicateDescriptor(
				new AbstractContext(), 0, NAT);
		desc.addResult(source);
		desc.addResult(target);
		final List<TermSignature> list = desc.getUnifiedResults();
		assertEquals(expected.getTerms(), list);
		assertEquals(expected.getTerms().toString(), list.toString());
	}

	public void testIndex() {
		doTest(mList(x), mList(x), mList(xx));
		doTest(mList(a), mList(x), mList(xx));
		doTest(mList(a), mList(a), mList(a));
		doTest(mList(a), mList(a), mList(a));
		doTest(mList(a), mList(a), mList(a));
		doTest(mList(a), mList(b), mList(xx));
		doTest(mList(a), mList(b), mList(xx));
		doTest(mList(x), mList(a), mList(xx));
		doTest(mList(x), mList(y), mList(xx));
		doTest(mList(x, x), mList(y, y), mList(xx, xx));
		doTest(mList(x, y), mList(y, y), mList(xx, yy));
		doTest(mList(x, x), mList(x, y), mList(xx, yy));
		doTest(mList(x, x, x), mList(x, x, x), mList(xx, xx, xx));
		doTest(mList(x, y, y), mList(x, x, x), mList(xx, yy, yy));
		doTest(mList(x, y, x), mList(x, x, x), mList(xx, yy, xx));
		doTest(mList(y, x, x), mList(x, x, x), mList(xx, yy, yy));
		doTest(mList(y, x, y), mList(x, x, x), mList(xx, yy, xx));
		doTest(mList(x, x, x), mList(y, x, x), mList(xx, yy, yy));
		doTest(mList(x, x, x), mList(x, y, x), mList(xx, yy, xx));
		doTest(mList(x, x, x), mList(x, x, y), mList(xx, xx, yy));
		doTest(mList(x, x, x), mList(y, y, x), mList(xx, xx, yy));
		doTest(mList(x, x, x), mList(y, x, y), mList(xx, yy, xx));
		doTest(mList(x, x, x), mList(x, y, y), mList(xx, yy, yy));
		doTest(mList(x, y, z), mList(x, x, x), mList(xx, yy, zz));
		doTest(mList(x, x, x), mList(x, y, z), mList(xx, yy, zz));
		doTest(mList(y, x, y), mList(x, y, x), mList(xx, yy, xx));
		doTest(mList(y, x, x), mList(y, y, x), mList(xx, yy, zz));
		doTest(mList(x, y, z), mList(x, y, y), mList(xx, yy, zz));
		doTest(mList(a, b, a), mList(x, x, x), mList(xx, yy, xx));
		doTest(mList(a, a, a), mList(x, x, x), mList(xx, xx, xx));
		doTest(mList(a, b, c), mList(a, x, x), mList(a, xx, yy));
	}

}

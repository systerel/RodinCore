package org.eventb.pp.loader;

import static org.eventb.internal.pp.core.elements.terms.Util.mIR;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eventb.internal.pp.core.elements.terms.AbstractPPTest;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;
import org.junit.Test;

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

    @Test
	public void testIndex() {
		doTest(mIR(x), mIR(x), mIR(xx));
		doTest(mIR(a), mIR(x), mIR(xx));
		doTest(mIR(a), mIR(a), mIR(a));
		doTest(mIR(a), mIR(a), mIR(a));
		doTest(mIR(a), mIR(a), mIR(a));
		doTest(mIR(a), mIR(b), mIR(xx));
		doTest(mIR(a), mIR(b), mIR(xx));
		doTest(mIR(x), mIR(a), mIR(xx));
		doTest(mIR(x), mIR(y), mIR(xx));
		doTest(mIR(x, x), mIR(y, y), mIR(xx, xx));
		doTest(mIR(x, y), mIR(y, y), mIR(xx, yy));
		doTest(mIR(x, x), mIR(x, y), mIR(xx, yy));
		doTest(mIR(x, x, x), mIR(x, x, x), mIR(xx, xx, xx));
		doTest(mIR(x, y, y), mIR(x, x, x), mIR(xx, yy, yy));
		doTest(mIR(x, y, x), mIR(x, x, x), mIR(xx, yy, xx));
		doTest(mIR(y, x, x), mIR(x, x, x), mIR(xx, yy, yy));
		doTest(mIR(y, x, y), mIR(x, x, x), mIR(xx, yy, xx));
		doTest(mIR(x, x, x), mIR(y, x, x), mIR(xx, yy, yy));
		doTest(mIR(x, x, x), mIR(x, y, x), mIR(xx, yy, xx));
		doTest(mIR(x, x, x), mIR(x, x, y), mIR(xx, xx, yy));
		doTest(mIR(x, x, x), mIR(y, y, x), mIR(xx, xx, yy));
		doTest(mIR(x, x, x), mIR(y, x, y), mIR(xx, yy, xx));
		doTest(mIR(x, x, x), mIR(x, y, y), mIR(xx, yy, yy));
		doTest(mIR(x, y, z), mIR(x, x, x), mIR(xx, yy, zz));
		doTest(mIR(x, x, x), mIR(x, y, z), mIR(xx, yy, zz));
		doTest(mIR(y, x, y), mIR(x, y, x), mIR(xx, yy, xx));
		doTest(mIR(y, x, x), mIR(y, y, x), mIR(xx, yy, zz));
		doTest(mIR(x, y, z), mIR(x, y, y), mIR(xx, yy, zz));
		doTest(mIR(a, b, a), mIR(x, x, x), mIR(xx, yy, xx));
		doTest(mIR(a, a, a), mIR(x, x, x), mIR(xx, xx, xx));
		doTest(mIR(a, b, c), mIR(a, x, x), mIR(a, xx, yy));
	}

}

package org.eventb.pp.loader;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;
import org.eventb.internal.pp.loader.predicate.IntermediateResult;
import org.eventb.pp.Util;

public class TestIndexUnification extends TestCase {

	class TestPair {
		IIntermediateResult source;
		IIntermediateResult target;
		IIntermediateResult result;
		
		public TestPair(IIntermediateResult source, IIntermediateResult target, IIntermediateResult result) {
			this.source = source;
			this.target = target;
			this.result = result;
		}
	}
	
	TermSignature a = Util.mConstant("a");
	TermSignature b = Util.mConstant("b");
	TermSignature c = Util.mConstant("c");
	TermSignature x = Util.mVariable(0,0);
	TermSignature y = Util.mVariable(1,1);
	TermSignature z = Util.mVariable(2,2);
	
	TermSignature xx = Util.mVariable(0, -1);
	TermSignature yy = Util.mVariable(0, -2);
	TermSignature zz = Util.mVariable(0, -3);
	
	private static IIntermediateResult mList(TermSignature... terms) {
		return new IntermediateResult(Arrays.asList(terms)/*,new TermOrderer()*/);
	}
	
	TestPair[] tests = new TestPair[] {
			new TestPair(
					mList(x),
					mList(x),
					mList(xx)
			),
			new TestPair(
					mList(a),
					mList(x),
					mList(xx)
			),
			new TestPair(
					mList(a),
					mList(a),
					mList(a)
			),
			new TestPair(
					mList(a),
					mList(a),
					mList(a)
			),
			new TestPair(
					mList(a),
					mList(a),
					mList(a)
			),
			new TestPair(
					mList(a),
					mList(b),
					mList(xx)
			),
			new TestPair(
					mList(a),
					mList(b),
					mList(xx)
			),
			new TestPair(
					mList(x),
					mList(a),
					mList(xx)
			),
			new TestPair(
					mList(x),
					mList(y),
					mList(xx)
			),
			new TestPair(
					mList(x,x),
					mList(y,y),
					mList(xx,xx)
			),
			new TestPair(
					mList(x,y),
					mList(y,y),
					mList(xx,yy)
			),
			new TestPair(
					mList(x,x),
					mList(x,y),
					mList(xx,yy)
			),
			new TestPair(
					mList(x,x,x),
					mList(x,x,x),
					mList(xx,xx,xx)
			),
			new TestPair(
					mList(x,y,y),
					mList(x,x,x),
					mList(xx,yy,yy)
			),
			new TestPair(
					mList(x,y,x),
					mList(x,x,x),
					mList(xx,yy,xx)
			),
			new TestPair(
					mList(y,x,x),
					mList(x,x,x),
					mList(xx,yy,yy)
			),
			new TestPair(
					mList(y,x,y),
					mList(x,x,x),
					mList(xx,yy,xx)
			),
			new TestPair(
					mList(x,x,x),
					mList(y,x,x),
					mList(xx,yy,yy)
			),
			new TestPair(
					mList(x,x,x),
					mList(x,y,x),
					mList(xx,yy,xx)
			),
			new TestPair(
					mList(x,x,x),
					mList(x,x,y),
					mList(xx,xx,yy)
			),
			new TestPair(
					mList(x,x,x),
					mList(y,y,x),
					mList(xx,xx,yy)
			),
			new TestPair(
					mList(x,x,x),
					mList(y,x,y),
					mList(xx,yy,xx)
			),
			new TestPair(
					mList(x,x,x),
					mList(x,y,y),
					mList(xx,yy,yy)
			),
			new TestPair(
					mList(x,y,z),
					mList(x,x,x),
					mList(xx,yy,zz)
			),
			new TestPair(
					mList(x,x,x),
					mList(x,y,z),
					mList(xx,yy,zz)
			),
			new TestPair(
					mList(y,x,y),
					mList(x,y,x),
					mList(xx,yy,xx)
			),
			new TestPair(
					mList(y,x,x),
					mList(y,y,x),
					mList(xx,yy,zz)
			),
			new TestPair(
					mList(x,y,z),
					mList(x,y,y),
					mList(xx,yy,zz)
			),
			new TestPair(
					mList(a,b,a),
					mList(x,x,x),
					mList(xx,yy,xx)
			),
			new TestPair(
					mList(a,a,a),
					mList(x,x,x),
					mList(xx,xx,xx)
			),
			new TestPair(
					mList(a,b,c),
					mList(a,x,x),
					mList(a,xx,yy)
			),
	};
	
	public void doTest(IIntermediateResult source, IIntermediateResult target, IIntermediateResult expected) {
		PredicateDescriptor desc = new PredicateDescriptor(new AbstractContext(),0,Util.INTEGER());
		desc.addResult(source);
		desc.addResult(target);
		List<TermSignature> list = desc.getUnifiedResults();
		
		assertEquals(expected.getTerms(), list);
	}
	
	public void testIndex() {
		for (TestPair test : tests) {
			doTest(test.source, test.target, test.result);
		}
	}
	
}

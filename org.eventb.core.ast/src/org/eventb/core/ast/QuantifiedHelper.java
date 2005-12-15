/*
 * Created on 11-may-2005
 *
 */
package org.eventb.core.ast;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Helper class for implementing quantified formulae of event-B.
 * <p>
 * Provides methods which implement common behavior of classes
 * <code>QuantifiedPredicate</code> and <code>QuantifiedExpression</code>.
 * </p>
 * 
 * @author Laurent Voisin
 */
class QuantifiedHelper {

	// Disable default constructor.
	private QuantifiedHelper() {
		assert false;
	}

	protected static String getSyntaxTreeQuantifiers(String[] boundBelow, String tabs, BoundIdentDecl[] boundHere) {
		StringBuffer str = new StringBuffer();
		str.append(tabs + "**quantifiers**" + "\n");
		String newTabs = tabs + "\t";
		for (BoundIdentDecl ident: boundHere) {
			str.append(ident.getSyntaxTree(boundBelow, newTabs));
		}
		return str.toString();
	}
	
	/*
	 * Concatenates the two arrays passed as parameter.
	 */
	protected static BoundIdentDecl[] catenateBoundIdentLists(BoundIdentDecl[] bound1, BoundIdentDecl[] bound2) {
		BoundIdentDecl[] newBoundIdents = new BoundIdentDecl[bound1.length + bound2.length];
		System.arraycopy(bound1, 0, newBoundIdents, 0, bound1.length);
		System.arraycopy(bound2, 0, newBoundIdents, bound1.length, bound2.length);
		return newBoundIdents;
	}

	protected static String[] catenateBoundIdentLists(String[] boundNames, BoundIdentDecl[] quantifiedIdents) {
		String[] newBoundNames = new String[boundNames.length + quantifiedIdents.length];
		System.arraycopy(boundNames, 0, newBoundNames, 0, boundNames.length);
		int idx = boundNames.length;
		for (BoundIdentDecl ident : quantifiedIdents) {
			newBoundNames[idx ++] = ident.getName();
		}
		return newBoundNames;
	}

	protected static String[] catenateBoundIdentLists(String[] bound1, String[] bound2) {
		String[] newBoundIdents = new String[bound1.length + bound2.length];
		System.arraycopy(bound1, 0, newBoundIdents, 0, bound1.length);
		System.arraycopy(bound2, 0, newBoundIdents, bound1.length, bound2.length);
		return newBoundIdents;
	}

	protected static boolean areEqualQuantifiers(BoundIdentDecl[] leftBound, BoundIdentDecl[] rightBound, boolean withAlphaConversion) {
		if (leftBound.length != rightBound.length) {
			return false;
		}
		if (! withAlphaConversion) {
			// No alpha conversion, check each identifier.
			for (int i = 0; i < leftBound.length; i++) {
				if (! leftBound[i].equals(rightBound[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * Returns the list of names as a string.
	 */
	protected static String getBoundIdentifiersString(String[] names) {
		StringBuffer str = new StringBuffer();
		str.append(names[0]);
		for (int i= 1; i < names.length; ++i) {
			str.append("," + names[i]);
		}
		return str.toString();
	}

	// resolve the locally quantified names so that they do not conflict with
	// the given formulas.
	protected static String[] resolveIdents(BoundIdentDecl[] boundHere, final HashSet<String> usedNames) {
		final int length = boundHere.length;
		String[] result = new String[length];
		
		StringSetAccess access = new StringSetAccess() {
			public boolean contains(String string) {
				return usedNames.contains(string);
			}
		};
				
		// Create the new identifiers.
		for (int i = 0; i < length; i++) {
			result[i] = solve(boundHere[i].getName(), access);
			usedNames.add(result[i]);
		}
		
		return result;
	}

	private static String solve(String name, StringSetAccess access) {
		if (! access.contains(name)) {
			// Not used, this name is OK.
			return name;
		}
		
		// We have a name conflict, so we try with another name
		StructuredName sname = new StructuredName(name);
		String newName;
		do {
			++ sname.suffix;
			newName = sname.toString();
		} while (access.contains(newName));
		
		return newName;
	}
	
	// resolve (locally) quantified names so that they do not conflict with the given type environment
	protected static FreeIdentifier[] resolveIdents(BoundIdentDecl[] boundHere,
			final ITypeEnvironment environment, FormulaFactory factory) {
		
		final int length = boundHere.length;
		FreeIdentifier[] result = new FreeIdentifier[length];
		
		StringSetAccess access = new StringSetAccess() {
			public boolean contains(String string) {
				return environment.contains(string);
			}
		};

		// Create the new identifiers.
		for (int i = 0; i < length; i++) {
			assert boundHere[i].getType() != null;
			
			String name = solve(boundHere[i].getName(), access);
			result[i] = factory.makeFreeIdentifier(name, boundHere[i].getSourceLocation());
			result[i].setType(boundHere[i].getType(), null);
			environment.addName(name, result[i].getType());
		}
		
		return result;
	}

	private static interface StringSetAccess {
		boolean contains(String string);
	}
	
	private static class StructuredName {
		String prefix;
		int suffix;
		
		static Pattern suffixExtractor = Pattern.compile("^(.*)(\\d+)$", Pattern.DOTALL);		
		
		StructuredName(String name) {
			Matcher matcher = suffixExtractor.matcher(name);
			if (matcher.matches()) {
				prefix = matcher.group(1);
				suffix = Integer.valueOf(matcher.group(2));
			}
			else {
				prefix = name;
				suffix = -1;
			}
		}
		
		@Override 
		public String toString() {
			if (suffix < 0) {
				return prefix;
			}
			return prefix + suffix;
		}
	}

}

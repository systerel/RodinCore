package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;

/**
 * Simple matcher, expected to work only with singleton sets.
 * @since 1.0
 * @author maamria
 *
 */
public class SetExtensionMatcher extends ExpressionMatcher<SetExtension> {

	public SetExtensionMatcher(){
		super(SetExtension.class);
	}
	
	@Override
	protected boolean gatherBindings(SetExtension form, SetExtension pattern,
			IBinding existingBinding)  {
		Expression[] patternMembers = pattern.getMembers();
		Expression[] formMembers = form.getMembers();
		// work with singleton
		if(formMembers.length == 1 && patternMembers.length == 1){
			Expression formMem = formMembers[0];
			Expression patternMem = patternMembers[0];
			if(patternMem instanceof FreeIdentifier){
				if(existingBinding.putExpressionMapping((FreeIdentifier) patternMem, formMem)){
					return true;
				}
			}
			if(matchingFactory.match(formMem, patternMem, existingBinding)){
				return true;
			}
		}
		return false;
	}

	@Override
	protected SetExtension getExpression(Expression e) {
		return (SetExtension) e;
	}

}

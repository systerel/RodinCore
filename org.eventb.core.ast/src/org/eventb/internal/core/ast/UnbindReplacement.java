/**
 * 
 */
package org.eventb.internal.core.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.Expression;

/**
 * Substitution of bound variables. This works for a particular quantified 
 * node in the AST. This class contains the information for the relative displacement
 * of the bound variables in the subtree of the quantified node.
 * 
 * @author halstefa
 *
 */
public class UnbindReplacement extends Replacement {

	private final Map<Integer, Info> map;
	private final int[] displacement;
	private final int max;
	
	public UnbindReplacement(Map<Integer, Expression> map, int numVars) {
		displacement = new int[numVars];
		Arrays.fill(displacement, 0);
		for(Integer index : map.keySet()) {
			displacement[index] = -1;
		}
		int disp = 0;
		for(int i=0; i<displacement.length; i++) {
			if(displacement[i] == -1)
				disp++;
			else
				displacement[i] = i - disp;
		}
		this.max = disp;
		this.map = new HashMap<Integer, Info>(map.size()*4 / 3 + 1);
		for(Map.Entry<Integer, Expression> entry : map.entrySet()) {
			
			this.map.put(entry.getKey(), new Info(entry.getValue()));
		}
	}
	
	public UnbindReplacement(UnbindReplacement replacement) {
		map = replacement.map;
		displacement = replacement.displacement;
		max = replacement.max;
	}
	
	public int getDisplacement(int index) {
		return (index < displacement.length) ? displacement[index] : index - max;
	}
	
	public int getMaxDisplacement(int index) {
		return max;
	}
	
	public Info getInfo(int index) {
		return map.get(index);
	}
}

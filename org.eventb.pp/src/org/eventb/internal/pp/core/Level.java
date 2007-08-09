/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Level implements Comparable<Level> {
	private static final BigInteger ONE = BigInteger.ONE;
	private static final BigInteger TWO = BigInteger.valueOf(2);

	public static Level base = new Level(BigInteger.ZERO);
	
	private BigInteger level;
	
	public Level(BigInteger level) {
		this.level = level;
	}
	
	public long getHeight() {
		long i = 0;
		while (true) {
			if (level.add(ONE).compareTo(BigInteger.valueOf(Double.valueOf((Math.pow(2, i))).longValue()))>=0
				&&
				level.add(ONE).compareTo(BigInteger.valueOf(Double.valueOf((Math.pow(2, i+1))).longValue()))<0)
				return i;
			i++;
		}
	}
	
//	public Level getSibling() {
//		assert level.mod(TWO).intValue() != 0;
//		return new Level(level.add(ONE));
//	}
	
	public Level getLeftBranch() {
		return new Level(level.multiply(TWO).add(ONE));
	}
	
	public Level getRightBranch() {
		return new Level(level.multiply(TWO).add(TWO));
	}
	
	public Level getParent() {
		return new Level(level.subtract(ONE).divide(TWO));
	}
	
	public boolean isRightBranch() {
		return level.mod(TWO).intValue() == 0;
	}
	
	public boolean isLeftBranch() {
		return level.mod(TWO).intValue() != 0;
	}
	
	public boolean isAncestorOf(Level other) {
		return level.compareTo(other.level) < 0;
	}
	
	public boolean isAncestorInSameTree(Level other) {
		if (other.equals(this)) return false;
		
		Level tmp = other;
		while (!tmp.equals(Level.base) && !tmp.equals(this)) {
			tmp = tmp.getParent();
		}
		if (tmp.equals(this)) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Level) {
			Level temp = (Level) obj;
			return temp.level.equals(level);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return level.hashCode();
	}
	
	@Override
	public String toString() {
		return ""+level;
	}
	
	public int compareTo(Level o) {
		return level.compareTo(o.level);
	}
	
	public static Level getHighest(Level level1, Level level2) {
		if (level1.compareTo(level2) >= 0) return level1;
		return level2;
	}
	
	public static Level getHighestOdd(Set<Level> dependencies) {
		List<Level> list = new ArrayList<Level>(dependencies);
		Collections.sort(list);
		Collections.reverse(list);
		for (Level level : list) {
			if (level.isLeftBranch()) return level;
		}
		return Level.base;
	}

}

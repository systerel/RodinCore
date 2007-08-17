/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import java.math.BigInteger;

/**
 * This class represents a level of the proof tree.
 * <p>
 * Each proof begins at the base level, accessible using {@value #base}. Each case
 * split divides the proof tree in two branches, accessible using {@link #getLeftBranch()}
 * and {@link #getRightBranch()}. For instance, if the prover splits on a clause
 * having level base, the two corresponding branches will have level base.getLeftBranch()
 * and base.getRightBranch(). The prover also only treats one branch at a time. 
 * Each time a branch is closed, all clauses of this branch and of its children must
 * be deleted from the prover. This means that at any time, there exists no clause 
 * such that currentLevel.isAncestorOf(clause.getLevel()).
 * <p>
 *
 * @author François Terrier
 *
 */
public final class Level implements Comparable<Level> {
	private static final BigInteger ONE = BigInteger.ONE;
	private static final BigInteger TWO = BigInteger.valueOf(2);

	/**
	 * The base level
	 */
	public static Level base = new Level(BigInteger.ZERO);
	
	private final BigInteger level;
	
	public Level(BigInteger level) {
		this.level = level;
	}
	
	/**
	 * Returns the height of this level.
	 * <p>
	 * If the level is too big, this method will return {@link Long#MAX_VALUE},
	 * which is wrong. Therefore, use this method only for debugging purposes.
	 * 
	 * @return the height of this level
	 */
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
	
	/**
	 * Returns the left branch of this level
	 * 
	 * @return the left branch of this level
	 */
	public Level getLeftBranch() {
		return new Level(level.multiply(TWO).add(ONE));
	}
	
	/**
	 * Returns the right branch of this level
	 * 
	 * @return the right branch of this level
	 */
	public Level getRightBranch() {
		return new Level(level.multiply(TWO).add(TWO));
	}
	
	/**
	 * Returns the parent branch of this level or
	 * <code>Level.base</code> if this level is the base level.
	 *  
	 * @return the parent branch of this level or
	 * <code>Level.base</code> if this level is the base level
	 */
	public Level getParent() {
		return new Level(level.subtract(ONE).divide(TWO));
	}
	
	/**
	 * Returns <code>true</code> if this level is a right branch,
	 * <code>false</code> otherwise.
	 * <p>
	 * Returns <code>false</code> if this level is the base level.
	 * 
	 * @return <code>true</code> if this level is a right branch,
	 * <code>false</code> otherwise
	 */
	public boolean isRightBranch() {
		if (this.equals(base)) return false;
		return level.mod(TWO).intValue() == 0;
	}
	
	/**
	 * Returns <code>true</code> if this level is a left branch,
	 * <code>false</code> otherwise.
	 * <p>
	 * Returns <code>false</code> if this level is the base level.
	 * 
	 * @return <code>true</code> if this level is a left branch,
	 * <code>false</code> otherwise
	 */
	public boolean isLeftBranch() {
		return level.mod(TWO).intValue() != 0;
	}
	
	/**
	 * Returns <code>true</code> if this level is an ancestor 
	 * of the specified level. Return <code>false</code> otherwise. 
	 * <p>
	 * Returns <code>false</code> if this level is equal to the specified
	 * level.
	 * <p>
	 * A level A is an ancestor of a level B if the height of A is strictly
	 * smaller than the height of B.
	 * 
	 * @param other the level
	 * @return <code>true</code> if this level is an ancestor of the specified level,
	 * <code>false</code> otherwise.
	 */
	public boolean isAncestorOf(Level other) {
		return level.compareTo(other.level) < 0;
	}
	
	/**
	 * Returns <code>true</code> if this level is an ancestor 
	 * of the specified level and if the specified level is in this level's
	 * subtree. Return <code>false</code> otherwise. 
	 * <p>
	 * Returns <code>false</code> if this level is equal to the specified
	 * level.
	 * <p>
	 * A level A is an ancestor in the same tree of a level B if, starting
	 * from level B, we can reach level A using getParent().
	 * 
	 * @param other the level
	 * @return <code>true</code> if this level is an ancestor of the specified level,
	 * <code>false</code> otherwise.
	 */
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
	
}

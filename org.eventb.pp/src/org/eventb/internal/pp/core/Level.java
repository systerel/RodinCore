package org.eventb.internal.pp.core;

import java.math.BigInteger;

public class Level {
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
	
	
	public static Level getHighest(Level level1, Level level2) {
		if (level1.level.compareTo(level2.level) >= 0) return level1;
		return level2;
	}
}

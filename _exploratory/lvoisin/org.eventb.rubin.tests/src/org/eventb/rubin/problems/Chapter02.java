/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors,//
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.rubin.problems;

import static org.eventb.rubin.tests.Problem.mProblem;
import static org.eventb.rubin.tests.ProblemStatus.INVALID;
import static org.eventb.rubin.tests.ProblemStatus.VALID;

import org.eventb.rubin.Sequent;
import org.eventb.rubin.tests.IProblem;
import org.eventb.rubin.tests.ProblemStatus;

/**
 * All sequents of this file have been extracted from chapter 2 of the book:<br/>
 * Mathematical Logic: Applications and Theory<br/>
 * by Jean E. Rubin<br/>
 * Saunders College Publishing, 1990<br/>
 * ISBN 0-03-012808-0
 * 
 * @author Laurent Voisin
 */
public enum Chapter02 implements IProblem {

	Example_2_p14(VALID, "Rubin Example p14",//
			"A -> B",//
			"~A -> C",//
			"C -> B",//
			"B"),

	Example_2_1(INVALID, "Rubin Example 2.1, p15",//
			"B -> ~H",//
			"H -> (B -> D)",//
			"H -> B",//
			"~B -> ~D"),

	Example_2_2(VALID, "Rubin Example 2.2, p16",//
			"B -> H",//
			"H -> (B -> D)",//
			"B",//
			"D"),

	Example_2_p17(VALID, "Rubin Example p17",//
			"A -> ~B",//
			"C | B",//
			"D -> ~C",//
			"D",//
			"~A"),

	Example_2_3(VALID, "Rubin Example 2.3, p19",//
			"A -> B | C",//
			"A -> ~B",//
			"C -> ~D",//
			"A -> ~D"),

	Example_2_4(VALID, "Rubin Example 2.4, p21",//
			"A -> B",//
			"~A -> ~C | D",//
			"C & ~D",//
			"B"),

	Example_2_5(VALID, "Rubin Example 2.5, p25",//
			"A -> (B -> C)",//
			"C -> ~D",//
			"~E -> D",//
			"A & B",//
			"E"),

	Example_2_6a(VALID, "Rubin Example 2.6(a), p27",//
			"A -> B",//
			"~A -> C",//
			"C -> ~D",//
			"~B -> D",//
			"B"),

	Example_2_6b(VALID, "Rubin Example 2.6(b), p27",//
			"A | B",//
			"C -> ~A",//
			"~B | D",//
			"D -> ~C",//
			"~C"),

	Example_2_7a(VALID, "Rubin Example 2.7(a), p28",//
			"A -> B | C",//
			"B -> ~A",//
			"~D -> ~C",//
			"~A | D"),

	Example_2_7b(VALID, "Rubin Example 2.7(b), p28",//
			"A -> ~B",//
			"A | ~B | C",//
			"B",//
			"B & C"),

	Example_2_7c(INVALID, "Rubin Example 2.7(c), p28",//
			"~A -> B | C",//
			"~B -> ~A & D",//
			"D -> B | C",//
			"B"),

	Example_2_7d(VALID, "Rubin Example 2.7(d), p28",//
			"A -> C",//
			"~C | D",//
			"B <-> D",//
			"B -> ~(~A & D)",//
			"A <-> B"),

	Tautology_1(VALID, "Rubin Useful Tautologies(1), p30",//
			"P & (P -> Q) -> Q"),

	Tautology_2(VALID, "Rubin Useful Tautologies(2), p30",//
			"~Q & (P -> Q) -> ~P"),

	Tautology_3(VALID, "Rubin Useful Tautologies(3), p30",//
			"~P & (P | Q) -> Q"),

	Tautology_4(VALID, "Rubin Useful Tautologies(4), p30",//
			"P & Q -> P"),

	Tautology_5(VALID, "Rubin Useful Tautologies(5), p30",//
			"P -> P | Q"),

	Tautology_6a(VALID, "Rubin Useful Tautologies(6a), p30",//
			"P -> (Q -> P & Q)"),

	Tautology_6b(VALID, "Rubin Useful Tautologies(6a), p30",//
			"(P -> Q) & (P -> R) -> (P -> Q & R)"),

	Tautology_7(VALID, "Rubin Useful Tautologies(7), p31",//
			"(P -> Q) & (Q -> R) -> (P -> R)"),

	Tautology_8a(VALID, "Rubin Useful Tautologies(8a), p31",//
			"(P -> Q) & (~P -> Q) -> Q"),

	Tautology_8b(VALID, "Rubin Useful Tautologies(8b), p31",//
			"(P | Q -> R) <-> (P -> R) & (Q -> R)"),

	Tautology_9a(VALID, "Rubin Useful Tautologies(9a), p31",//
			"(P -> Q & ~Q) -> ~P"),

	Tautology_9b(VALID, "Rubin Useful Tautologies(9b), p31",//
			"P & ~P -> Q"),

	Tautology_10(VALID, "Rubin Useful Tautologies(10), p31",//
			"P | ~P"),

	Tautology_11(VALID, "Rubin Useful Tautologies(11), p31",//
			"~(P & ~P)"),

	Tautology_12a(VALID, "Rubin Useful Tautologies(12a), p31",//
			"P | Q <-> Q | P"),

	Tautology_12b(VALID, "Rubin Useful Tautologies(12b), p31",//
			"P & Q <-> Q & P"),

	Tautology_13a(VALID, "Rubin Useful Tautologies(13a), p31",//
			"P | (Q | R) <-> (P | Q) | R"),

	Tautology_13b(VALID, "Rubin Useful Tautologies(13b), p31",//
			"P & (Q & R) <-> (P & Q) & R"),

	Tautology_14a(VALID, "Rubin Useful Tautologies(14a), p31",//
			"P | (Q & R) <-> (P | Q) & (P | R)"),

	Tautology_14b(VALID, "Rubin Useful Tautologies(14b), p31",//
			"P & (Q | R) <-> (P & Q) | (P & R)"),

	Tautology_15a(VALID, "Rubin Useful Tautologies(15a), p31",//
			"~(P | Q) <-> ~P & ~Q"),

	Tautology_15b(VALID, "Rubin Useful Tautologies(15b), p31",//
			"~(P & Q) <-> ~P | ~Q"),

	Tautology_16(VALID, "Rubin Useful Tautologies(16), p31",//
			"~~P <-> P"),

	Tautology_17a(VALID, "Rubin Useful Tautologies(17a), p31",//
			"(P -> Q) <-> ~P | Q"),

	Tautology_17b(VALID, "Rubin Useful Tautologies(17b), p31",//
			"~(P -> Q) <-> P & ~Q"),

	Tautology_18a(VALID, "Rubin Useful Tautologies(18a), p31",//
			"(P <-> Q) <-> (P -> Q) & (Q -> P)"),

	Tautology_18b(VALID, "Rubin Useful Tautologies(18b), p31",//
			"(P <-> Q) <-> (P & Q) | (~P & ~Q)"),

	Tautology_18c(VALID, "Rubin Useful Tautologies(18c), p31",//
			"(P <-> Q) <-> (~P | Q) & (P | ~Q)"),

	Tautology_19a(VALID, "Rubin Useful Tautologies(19a), p31",//
			"P | P <-> P"),

	Tautology_19b(VALID, "Rubin Useful Tautologies(19b), p31",//
			"P & P <-> P"),

	Tautology_20a(VALID, "Rubin Useful Tautologies(20a), p31",//
			"(P -> Q) <-> (~Q -> ~P)"),

	Tautology_20b(VALID, "Rubin Useful Tautologies(20b), p31",//
			"(P -> ~Q) <-> (Q -> ~P)"),

	Tautology_20c(VALID, "Rubin Useful Tautologies(20c), p31",//
			"(~P -> Q) <-> (~Q -> P)"),

	Tautology_21(VALID, "Rubin Useful Tautologies(21), p31",//
			"(P -> (Q -> R)) <-> (P & Q -> R)"),

	Tautology_22a(VALID, "Rubin Useful Tautologies(22a), p31",//
			"P | (P & Q) <-> P"),

	Tautology_22b(VALID, "Rubin Useful Tautologies(22b), p31",//
			"P & (P | Q) <-> P"),

	Exercise_2_B1(VALID, "Rubin Exercise 2.B(1), p32",//
			"(A | B) | (C & D) <-> (A | B | C) & (A | B | D)"),

	Exercise_2_B2(VALID, "Rubin Exercise 2.B(2), p32",//
			"~A & (~A -> B | C) -> B | C"),

	Exercise_2_B3(VALID, "Rubin Exercise 2.B(3), p32",//
			"(A & B) | (A & ~B & C) <-> A & (B | (~B & C))"),

	Exercise_2_B4(VALID, "Rubin Exercise 2.B(4), p32",//
			"(A & B) | (A & B & C) <-> A & B"),

	Exercise_2_B5(VALID, "Rubin Exercise 2.B(5), p32",//
			"(A & B & C -> D & ~D) -> ~(A & B & C)"),

	Exercise_2_B6(VALID, "Rubin Exercise 2.B(6), p32",//
			"A & ~A -> A | ~A"),

	Exercise_2_B7(VALID, "Rubin Exercise 2.B(7), p32",//
			"(A & ~A) | ~(A & ~A)"),

	Exercise_2_B8(VALID, "Rubin Exercise 2.B(8), p32",//
			"(A & ~B & C) | (A & ~B & ~C) <-> (A & ~B) & (C | ~C)"),

	Exercise_2_B9(VALID, "Rubin Exercise 2.B(9), p32",//
			"~(A | ~B | C) <-> ~A & ~(~B | C)"),

	Exercise_2_B10(VALID, "Rubin Exercise 2.B(10), p32",//
			"(A & B -> C | D) <-> (A -> (B -> C | D))"),

	Exercise_2_C1(VALID, "Rubin Exercise 2.C(1), p32",//
			"O -> M | V",//
			"~M",//
			"O -> V"),

	Exercise_2_C2(INVALID, "Rubin Exercise 2.C(2), p32",//
			"O -> M",//
			"M -> G",//
			"O | G",//
			"~M"),

	Exercise_2_C3(INVALID, "Rubin Exercise 2.C(3), p32",//
			"O -> (G -> V)",//
			"~G",//
			"O -> V"),

	Exercise_2_C4(VALID, "Rubin Exercise 2.C(4), p32",//
			"O & G -> V",//
			"G",//
			"V | ~O"),

	Exercise_2_C5(INVALID, "Rubin Exercise 2.C(5), p33",//
			"O & G -> M | V",//
			"~(G | M)",//
			"O | V"),

	Exercise_2_C6(INVALID, "Rubin Exercise 2.C(6), p33",//
			"O & G -> M & V",//
			"~(V -> G)",//
			"~O"),

	Exercise_2_C7(INVALID, "Rubin Exercise 2.C(7), p33",//
			"O -> M | V",//
			"G -> V",//
			"~G | M",//
			"V -> O",//
			"V <-> O"),

	Exercise_2_C8(VALID, "Rubin Exercise 2.C(8), p33",//
			"O -> ~(M | V)",//
			"~V -> ~G",//
			"~M -> G",//
			"~O"),

	Exercise_2_E1(INVALID, "Rubin Exercise 2.E(1), p33",//
			"O -> M | V",//
			"M",//
			"V -> O",//
			"O"),

	Exercise_2_E2(VALID, "Rubin Exercise 2.E(2), p33",//
			"O -> (V <-> G)",//
			"~M -> (V & ~G)",//
			"~O | M"),

	Exercise_2_E3(INVALID, "Rubin Exercise 2.E(3), p33",//
			"O -> M",//
			"G -> V",//
			"~M | ~V",//
			"G | ~M",//
			"O <-> ~G"),

	Exercise_2_E4(VALID, "Rubin Exercise 2.E(4), p33",//
			"O -> V",//
			"G -> M",//
			"G -> O | ~M",//
			"G | ~M",//
			"M | ~V",//
			"O <-> G"),

	Exercise_2_E5(VALID, "Rubin Exercise 2.E(5), p33",//
			"O & G -> V",//
			"V -> ~M",//
			"~J -> M",//
			"M -> ~J",//
			"G -> (O -> J)"),

	Exercise_2_E6(VALID, "Rubin Exercise 2.E(6), p33",//
			"~O -> ~V",//
			"O -> ~G | M",//
			"~M",//
			"~G | ~V"),

	Exercise_2_E7(INVALID, "Rubin Exercise 2.E(7), p33",//
			"(M -> O) & (G -> V)",//
			"M | G",//
			"O",//
			"O & V"),

	Exercise_2_E8(VALID, "Rubin Exercise 2.E(8), p33",//
			"M | V -> G",//
			"G-> V",//
			"O -> ~J | ~V",//
			"O -> (M -> ~J)"),

	Exercise_2_G1(VALID, "Rubin Exercise 2.G(1), p34",//
			"P & Q -> R",//
			"P",//
			"~R",//
			"~Q"),

	Exercise_2_G2(INVALID, "Rubin Exercise 2.G(2), p34",//
			"P -> (Q <-> R)",//
			"R",//
			"Q"),

	Exercise_2_G3(INVALID, "Rubin Exercise 2.G(3), p34",//
			"P & Q -> R",//
			"~P & Q -> ~R",//
			"P <-> R"),

	Exercise_2_G4(INVALID, "Rubin Exercise 2.G(4), p34",//
			"P & Q -> R",//
			"S & R -> T",//
			"P & S",//
			"T -> Q"),

	Exercise_2_H1(VALID, "Rubin Exercise 2.H(1), p35",//
			"A -> (B -> C)",//
			"C -> ~D",//
			"~E -> D",//
			"B -> (A -> E)"),

	Exercise_2_H2(VALID, "Rubin Exercise 2.H(2), p35",//
			"A | B",//
			"C -> ~A",//
			"B -> D",//
			"C -> ~D",//
			"~C"),

	Exercise_2_H3(VALID, "Rubin Exercise 2.H(3), p35",//
			"(A | B) | C",//
			"A -> (D <-> ~E)",//
			"B -> ~(~D | E)",//
			"E -> ~(C | D)",//
			"~E -> C & D",//
			"D <-> ~E"),

	Exercise_2_H4(VALID, "Rubin Exercise 2.H(4), p35",//
			"(A -> B) | (A & ~C)",//
			"A",//
			"C -> ~B",//
			"~C -> B",//
			"~C"),

	Exercise_2_H5(VALID, "Rubin Exercise 2.H(5), p35",//
			"(A -> B) & (C -> ~D)",//
			"(E -> ~B) & (~F -> D)",//
			"~E | F -> G",//
			"~B -> D",//
			"A | C",//
			"B & G"),

	Exercise_2_H6(VALID, "Rubin Exercise 2.H(6), p35",//
			"A & C -> D",//
			"B & C -> D",//
			"~A & ~B -> E | F",//
			"G -> ~E",//
			"F -> H",//
			"C & ~D",//
			"~G | H"),

	Exercise_2_H7(VALID, "Rubin Exercise 2.H(7), p35",//
			"A & (B | C)",//
			"A & B -> D & ~F",//
			"A -> (C -> ~(D |~F))",//
			"D <-> ~F"),

	Exercise_2_H8(VALID, "Rubin Exercise 2.H(8), p35",//
			"A & B -> C",//
			"A | E",//
			"G -> (~C & ~D)",//
			"A <-> B",//
			"A -> C",//
			"C-> ~D",//
			"B -> D",//
			"~A & (G -> E)"),

	Exercise_2_H9(VALID, "Rubin Exercise 2.H(9), p36",//
			"~A -> C | D",//
			"B -> E & F",//
			"E -> D",//
			"~D",//
			"(A -> B) -> C"),

	Exercise_2_H10(VALID, "Rubin Exercise 2.H(10), p36",//
			"A -> B",//
			"C -> ~B",//
			"~C -> D",//
			"A & ~D",//
			"E -> F"),

	Exercise_2_H11(VALID, "Rubin Exercise 2.H(11), p36",//
			"B -> A",//
			"~A | C",//
			"C -> D | E",//
			"D -> F & ~B",//
			"E -> ~A & F",//
			"F -> A",//
			"~B | G -> H & I",//
			"H -> B",//
			"H & ~H"),

	Exercise_2_H12(VALID, "Rubin Exercise 2.H(12), p36",//
			"A -> (B -> C)",//
			"~A -> D & ~E",//
			"A & B -> ~C",//
			"D -> F | G",//
			"~B -> (G -> H)",//
			"E | (H | ~G)",//
			"G -> H");

	private final IProblem problem;

	private Chapter02(ProblemStatus status, String name, String... predImages) {
		problem = mProblem(status, name, predImages);
	}

	@Override
	public Sequent sequent() {
		return problem.sequent();
	}

	@Override
	public ProblemStatus status() {
		return problem.status();
	}

	@Override
	public IProblem invalidVariant() {
		return problem.invalidVariant();
	}

}

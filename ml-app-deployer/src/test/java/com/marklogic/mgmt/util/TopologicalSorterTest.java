/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TopologicalSorterTest  {

	/**
	 * Adapted from http://www.java2s.com/Code/Java/Collections-Data-Structure/Topologicalsorting.htm
	 */
	@Test
	public void test() {
		TopologicalSorter g = new TopologicalSorter(8);
		g.addVertex("A"); // 0
		g.addVertex("B"); // 1
		g.addVertex("C"); // 2
		g.addVertex("D"); // 3
		g.addVertex("E"); // 4
		g.addVertex("F"); // 5
		g.addVertex("G"); // 6
		g.addVertex("H"); // 7

		g.addEdge(0, 3); // AD
		g.addEdge(0, 4); // AE
		g.addEdge(1, 4); // BE
		g.addEdge(2, 5); // CF
		g.addEdge(3, 6); // DG
		g.addEdge(4, 6); // EG
		g.addEdge(5, 7); // FH
		g.addEdge(6, 7); // GH

		String[] sortedArray = g.sort();

		assertEquals("B", sortedArray[0]);
		assertEquals("A", sortedArray[1]);
		assertEquals("E", sortedArray[2]);
		assertEquals("D", sortedArray[3]);
		assertEquals("G", sortedArray[4]);
		assertEquals("C", sortedArray[5]);
		assertEquals("F", sortedArray[6]);
		assertEquals("H", sortedArray[7]);
	}
}

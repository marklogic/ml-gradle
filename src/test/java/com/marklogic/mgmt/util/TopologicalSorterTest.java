/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

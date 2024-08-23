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

/**
 * Adapted from http://www.java2s.com/Code/Java/Collections-Data-Structure/Topologicalsorting.htm
 */
public class TopologicalSorter {

	private String vertices[];

	private int matrix[][]; // adjacency matrix

	private int numVerts; // current number of vertices

	public TopologicalSorter(int vertexCount) {
		vertices = new String[vertexCount];
		matrix = new int[vertexCount][vertexCount];
		numVerts = 0;
		for (int i = 0; i < vertexCount; i++) {
			for (int k = 0; k < vertexCount; k++) {
				matrix[i][k] = 0;
			}
		}
	}

	public void addVertex(String vertex) {
		vertices[numVerts++] = vertex;
	}

	public void addEdge(int start, int end) {
		matrix[start][end] = 1;
	}

	public String[] sort() {
		String[] sortedArray = new String[vertices.length];

		while (numVerts > 0) // while vertices remain,
		{
			// get a vertex with no successors, or -1
			int currentVertex = noSuccessors();
			if (currentVertex == -1) // must be a cycle
			{
				throw new IllegalStateException("Graph has cycles");
			}
			// insert vertex label in sorted array (start at end)
			sortedArray[numVerts - 1] = vertices[currentVertex];
			deleteVertex(currentVertex); // delete vertex
		}

		return sortedArray;
	}

	private int noSuccessors() // returns vert with no successors (or -1 if no such verts)
	{
		boolean isEdge; // edge from row to column in adjMat

		for (int row = 0; row < numVerts; row++) {
			isEdge = false; // check edges
			for (int col = 0; col < numVerts; col++) {
				if (matrix[row][col] > 0) // if edge to another,
				{
					isEdge = true;
					break; // this vertex has a successor try another
				}
			}
			if (!isEdge) // if no edges, has no successors
				return row;
		}
		return -1; // no
	}

	private void deleteVertex(int delVert) {
		if (delVert != numVerts - 1) // if not last vertex, delete from vertices
		{
			for (int j = delVert; j < numVerts - 1; j++) {
				vertices[j] = vertices[j + 1];
			}

			for (int row = delVert; row < numVerts - 1; row++) {
				moveRowUp(row, numVerts);
			}

			for (int col = delVert; col < numVerts - 1; col++) {
				moveColLeft(col, numVerts - 1);
			}
		}
		numVerts--; // one less vertex
	}

	private void moveRowUp(int row, int length) {
		for (int col = 0; col < length; col++) {
			matrix[row][col] = matrix[row + 1][col];
		}
	}

	private void moveColLeft(int col, int length) {
		for (int row = 0; row < length; row++)
			matrix[row][col] = matrix[row][col + 1];
	}

}

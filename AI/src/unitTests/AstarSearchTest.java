package unitTests;

import static org.junit.Assert.*;
import rubiks.*;
import search.AstarSearch;

import org.junit.Test;

public class AstarSearchTest {

	@Test
	public void searchTest() {
		RubiksCube cube = new RubiksCube(2);
		cube.perturb(4);
		AstarSearch AstarSearch = new AstarSearch();
		RubiksCube searchResult = (RubiksCube)AstarSearch.search(cube, RubiksCube.createSolvedRubiksCube(cube.getSize()));
		assertTrue(searchResult.isSolved());
	}

}

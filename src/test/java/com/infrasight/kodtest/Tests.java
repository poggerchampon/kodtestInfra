package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

/**
 * Simple concrete class for JUnit tests with uses {@link TestsSetup} as a
 * foundation for starting/stopping the API server for tests.
 * 
 * You may configure port, api user and api port in {@link TestVariables} if
 * needed.
 */
public class Tests extends TestsSetup {
	ApiCaller caller = new ApiCaller();

	/**
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */
	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	@Test
	public void assignment1() throws InterruptedException {
		assertTrue(serverUp);
		/**
		 * TODO: Add code to solve the first assignment. Add Assert to show that you
		 * found the account for Vera
		 */
		String acc = "";
		while (acc == "") {
			acc = caller.getData(null, null, "employeeId=1337", "/api/accounts");
		}
		ArrayList<HashMap<String, String>> result = parseString(acc);
		// Remove surrounding brackets and split by commas to get individual key-value
		// pairs
		boolean isEqual = result.get(0).containsValue("1337") && result.get(0).containsValue("Vera")
				&& result.get(0).containsValue("Scope");
		assertTrue(isEqual);
	}

	@Test
	public void assignment2() throws InterruptedException {
		assertTrue(serverUp);
		String grp = "";
		while (grp == "") {
			grp = caller.getData(null, null, "memberId=vera_scope", "/api/relationships");
		}
		/**
		 * TODO: Add code to solve the second assignment where we expect the number of
		 * groups to be 3.
		 */
		ArrayList<HashMap<String, String>> result = parseString(grp);

		// Assert which verifies the expected group count of 3
		assertEquals(3, result.size());
		/**
		 * TODO: Add Assert to verify the IDs of the groups found
		 */

		assertTrue(containsValueList(result, "grp_köpenhamn"));
		assertTrue(containsValueList(result, "grp_malmo"));
		assertTrue(containsValueList(result, "grp_itkonsulter"));
	}

	@Test
	public void assignment3() throws InterruptedException {
		assertTrue(serverUp);
		String grp = "";
		while (grp == "") {
			grp = caller.getData(null, null, "memberId=vera_scope", "/api/relationships");
		}
		ArrayList<HashMap<String, String>> result = parseString(grp);
		ArrayList<HashMap<String, String>> newGroups = new ArrayList<>();

		for (HashMap<String, String> map : result) {
			ArrayList<HashMap<String, String>> temp = findGroups(map);
			if (temp != null) {
				newGroups.addAll(temp);
			}
		}
		for (HashMap<String, String> item : newGroups) {
			if (!result.contains(item)) {
				result.add(item);
			}
		}

	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fourth assignment. Add Asserts to verify the
		 * total salary requested
		 */
	}

	@Test
	public void assignment5() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fifth assignment. Add Asserts to verify the
		 * managers requested
		 */
	}

	private int countOccurrences(String str, char target) {
		return str.length() - str.replace(Character.toString(target), "").length();
	}

	private ArrayList<HashMap<String, String>> parseString(String str) {
		ArrayList<HashMap<String, String>> list = new ArrayList<>();
		HashMap<String, String> map = new HashMap<>();
		String[] objects = divideJSONObjects(str);

		for (String object : objects) {
			HashMap<String, String> hashMap = new HashMap<>();
			String[] keyValuePairs = object.split(",");

			for (String pair : keyValuePairs) {
				String[] parts = pair.split(":");
				if (parts.length == 2) {
					String key = parts[0].replaceAll("\"", "").replace("}", "").trim();
					String value = parts[1].replaceAll("\"", "").replace("}", "").trim();
					hashMap.put(key, value);
				}
			}

			list.add(hashMap);
		}

		return list;
	}

	private String[] divideJSONObjects(String jsonString) {
		// Remove square brackets at the beginning and end of the JSON array
		jsonString = jsonString.substring(1, jsonString.length() - 1);

		// Split the JSON array into individual objects based on curly braces
		String[] objects = jsonString.split("\\{");

		// Remove empty strings and add curly braces back to the objects
		ArrayList<String> nonEmptyObjects = new ArrayList<>();
		for (int i = 0; i < objects.length; i++) {
			if (!objects[i].isEmpty()) {
				nonEmptyObjects.add("{" + objects[i]);
			}
		}

		return nonEmptyObjects.toArray(new String[0]);
	}

	private static boolean containsValueList(ArrayList<HashMap<String, String>> hashMapList, String value) {
		for (HashMap<String, String> map : hashMapList) {
			if (map.values().contains(value)) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<HashMap<String, String>> findGroups(HashMap<String, String> map) {
		String grp = "";
		while (grp == "") {
			grp = caller.getData(null, null, "memberId=" + map.get("groupId").replace("ö", "%C3%B6"),
					"/api/relationships");

		}
		if (grp == "" || grp == null) {
			return null;
		}
		return parseString(grp);

	}
}

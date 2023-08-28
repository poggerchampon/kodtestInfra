package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;
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

	//could be solved with some recursion
	@Test
	public void assignment3() throws InterruptedException {
		assertTrue(serverUp);
		String grp = "";
		while (grp == "") {
			grp = caller.getData(null, null, "memberId=vera_scope", "/api/relationships");
		}
		ArrayList<HashMap<String, String>> result = parseString(grp);
		 ArrayList<HashMap<String, String>> resultCopy = new ArrayList<>(result); // Create a copy
		for (HashMap<String, String> map : resultCopy) {
			findAllGroups(result, map);
		}
		assertEquals(result.size(),9);
		assertTrue(containsValueList(result, "grp_köpenhamn"));
		assertTrue(containsValueList(result, "grp_malmo"));
		assertTrue(containsValueList(result, "grp_itkonsulter"));
		assertTrue(containsValueList(result, "grp_danmark"));
		assertTrue(containsValueList(result, "grp_sverige"));
		assertTrue(containsValueList(result, "grp_konfektyr"));
		assertTrue(containsValueList(result, "grp_choklad"));
		assertTrue(containsValueList(result, "grp_chokladfabrik"));
		assertTrue(containsValueList(result, "grp_inhyrda"));
	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);
		/**
		 * TODO: Add code to solve the fourth assignment. Add Asserts to verify the
		 * total salary requested
		 */
		ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> accountList = new ArrayList<HashMap<String, String>>();
		findAllMembers(res, 0);
		HashMap<String, Integer> salary = new HashMap<>();
		for (HashMap<String, String> map2 : res) {
			String acc = "";
			while (acc == "") {
				acc = caller.getData(null, null, "id="+map2.get("memberId"), "/api/accounts");
			}
			accountList.addAll(parseString(acc));
		}
		for(HashMap<String, String> map3 : accountList) {
			String key = (map3.get("salaryCurrency"));
			if(salary.containsKey(key)) {
				int currentValue = salary.get(key);
	            salary.put(key, currentValue + Integer.parseInt(map3.get("salary")));
			}else {
				salary.put(key,Integer.parseInt(map3.get("salary")));
			}	
		}
		assertEquals((int)salary.get("EUR"), 220789);
		assertEquals((int)salary.get("SEK"), 5252796);
		assertEquals((int)salary.get("DKK"), 639024);
	}

	@Test
	public void assignment5() throws InterruptedException {
		assertTrue(serverUp);
		HashMap<String, Integer> res = new HashMap<String, Integer>();
		findAllManagers(res,0);
		/**
		 * TODO: Add code to solve the fifth assignment. Add Asserts to verify the
		 * managers requested
		 */

        LinkedHashMap<String, Integer> sortedMap = res.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);
        
		assertEquals((int)sortedMap.get("acc43"),168);
		assertEquals((int)sortedMap.get("acc62"),153);
		assertEquals((int)sortedMap.get("acc4"),151);
		assertEquals((int)sortedMap.get("acc502"),88);
		assertEquals((int)sortedMap.get("acc489"),72);
		assertEquals((int)sortedMap.get("acc477"),62);
		assertEquals((int)sortedMap.get("acc818"),56);
		assertEquals((int)sortedMap.get("acc802"),46);
		assertEquals((int)sortedMap.get("acc808"),45);
		assertEquals((int)sortedMap.get("acc710"),37);
		assertEquals((int)sortedMap.get("acc711"),30);
		assertEquals((int)sortedMap.get("acc706"),30);
		assertEquals((int)sortedMap.get("acc971"),24);
		assertEquals((int)sortedMap.get("acc983"),20);
		assertEquals((int)sortedMap.get("acc958"),18);
	}

	private int countOccurrences(String str, char target) {
		return str.length() - str.replace(Character.toString(target), "").length();
	}
	
	//parses the json to a string and returns an array list of all the accounts/groups 
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
	
	//would be easier with some json library but this works fine
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

	//recursively finding all indirect groups
	private void findAllGroups(ArrayList<HashMap<String, String>> resultlist, HashMap<String, String> map){
		
		String grp = "";
		while (grp == "") {
			grp = caller.getData(null, null, "memberId=" + map.get("groupId").replace("ö", "%C3%B6"),
					"/api/relationships");

		}
		ArrayList<HashMap<String, String>> res = parseString(grp);
		if(res.isEmpty() || resultlist.containsAll(res)) {
			return;
		}else {
			for (HashMap<String, String> map2 : res) {
				if(!containsValueList(resultlist,map2.get("groupId"))) {
					resultlist.add(map2);
					findAllGroups(resultlist,map2);
				}
			}
		}
		
	}
	
	private void findAllMembers(ArrayList<HashMap<String, String>> resultlist, int skip) {
		String grp = "";
		while (grp == "") {
			grp = caller.getData(skip, null, "groupId%3Dgrp_inhyrda",
					"/api/relationships");
		}
		ArrayList<HashMap<String, String>> res = parseString(grp);
		resultlist.addAll(res);
		if(res.size()==50) {
			
			findAllMembers(resultlist, skip + 50);
		}else {
			return;
		}
	}
	private void findAllManagers(HashMap<String, Integer> resultmap, int skip) {
		String grp = "";
		while (grp == "") {
			grp = caller.getData(skip, null, "objectType%3DManagerFor",
					"/api/relationships");
		}
		ArrayList<HashMap<String, String>> res = parseString(grp);
		for(HashMap<String,String> map : res) {
			String key = (map.get("accountId"));
			if(resultmap.containsKey(key)) {
				int current = resultmap.get(key);
				resultmap.put(key,current+1);
			}else {
				resultmap.put(key,1);
			}
		}
		if(res.size()==50) {
			
			findAllManagers(resultmap, skip + 50);
		}else {
			return;
		
	}
}
}
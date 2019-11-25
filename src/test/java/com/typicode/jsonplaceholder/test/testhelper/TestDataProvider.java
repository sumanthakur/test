package com.typicode.jsonplaceholder.test.testhelper;

import java.util.List;

import org.testng.annotations.DataProvider;

public class TestDataProvider {

	private static List<Integer> postIds;

	@DataProvider(name = "postIdDataProvider")
	public static Object[][] getPostIdList() {
		Object[][] objList = new Object[postIds.size()][];
		for (int i = 0; i < postIds.size(); i++) {
			objList[i] = new Object[] { postIds.get(i) };
		}
		return objList;
	}

	public static void setPostIdList(List<Integer> postIds) {
		TestDataProvider.postIds = postIds;
	}

}

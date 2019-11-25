package com.typicode.jsonplaceholder.test.sampletest;

import static io.restassured.RestAssured.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.typicode.jsonplaceholder.test.config.InfraConfig;
import com.typicode.jsonplaceholder.test.testhelper.TestDataProvider;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CommentTest {
	
	@BeforeSuite
	public void setUpSuite() {
		RestAssured.baseURI = InfraConfig.baseUrl;
	}

	@BeforeTest
	public void setUpTest() {
		Integer postAuthorId = validateAndGetUserIdForUserName("Samantha");
		TestDataProvider.setPostIdList(validateAndGetUserPostIds(postAuthorId));
	}

	@Test(dataProvider = "postIdDataProvider", dataProviderClass = TestDataProvider.class)
	public void testCommentEmailIdFormatOnPostId(Integer postId) {
		validateCommentEmailIdFormatOnPostId(postId);
	}

	@AfterTest
	public void tearDownTest() {
		TestDataProvider.setPostIdList(null);
	}

	@AfterSuite
	public void tearDownSuite() {
		RestAssured.baseURI = null;
	}

	private Integer validateAndGetUserIdForUserName(String postAuthorUserName) {
		Response usersResponse = when().get("/users");
		assertEquals(usersResponse.getStatusCode(), 200, "Users api return non-success response code");

		List<Map<String, ?>> usersList = usersResponse.jsonPath().getList(".");
		assertNotEquals(usersList, null, "Users api response body is null");
		assertTrue(usersList.size() > 0, "Users api response body has 0 User's Data");

		List<Integer> matchedPostAuthorIdList = usersList.stream()
				.filter(obj -> postAuthorUserName.equals((String) obj.get("username")))
				.map(obj -> (Integer) obj.get("id")).collect(Collectors.toList());
		assertEquals(matchedPostAuthorIdList.size(), 1,
				"Users api response contains 0 or multiple Users with UserName: " + postAuthorUserName);
		assertNotEquals(matchedPostAuthorIdList.get(0), null,
				"Users api response has no/null Id field with UserName: " + postAuthorUserName);

		return matchedPostAuthorIdList.get(0);
	}

	private List<Integer> validateAndGetUserPostIds(Integer postAuthorId) {
		Response postsApiResponse = when().get("/posts?userId={id}", postAuthorId);
		assertEquals(postsApiResponse.getStatusCode(), 200,
				"Posts api return non-success response code for userId=" + postAuthorId);

		List<Map<String, ?>> postsList = postsApiResponse.jsonPath().getList(".");
		assertNotEquals(postsList, null, "Posts api response body is null for userId=" + postAuthorId);
		assertTrue(postsList.size() > 0, "Posts api response body has 0 Post's Data for userId=" + postAuthorId);

		postsList.forEach(postDetail -> assertNotEquals(postDetail.get("id"), null,
				"Posts Api response have posts with no/null post Id(id) for userId=" + postAuthorId));

		return postsList.stream().map(postDetail -> (Integer) postDetail.get("id")).collect(Collectors.toList());
	}

	private void validateCommentEmailIdFormatOnPostId(Integer postId) {
		Response commentsApiResponse = when().get("/comments?postId={id}", postId);
		assertEquals(commentsApiResponse.getStatusCode(), 200,
				"Comments api return non-success response code for postId=" + postId);

		List<Map<String, ?>> commentsList = commentsApiResponse.jsonPath().getList(".");
		assertNotEquals(commentsList, null, "Comments api response body is null for postId=" + postId);
		assertTrue(commentsList.size() > 0, "Comments api response body has 0 Post's Data for postId=" + postId);

		commentsList.forEach(commentDetail -> assertNotEquals(commentDetail.get("email"), null,
				"Comments api  response have comments with no/null email Id for postId=" + postId));

		commentsList.forEach(commentDetail -> assertTrue(
				commentDetail.get("email").toString().trim().matches(
						"[a-zA-Z0-9]+([\\.\\_]{1}[a-zA-Z0-9]+)*@([a-zA-Z0-9]+([\\-]*[a-zA-Z0-9]+)*[\\.]{1})+[a-zA-Z]+"),
				"Comments api response have comment id "+commentDetail.get("id")+" with improper email Id: " + commentDetail.get("email").toString()
						+ " for postId=" + postId));

	}

}

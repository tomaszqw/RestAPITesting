package com.example.rest;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class SimpleRestTests {
    private final static String URL = "https://jsonplaceholder.typicode.com";
    private final static String allPostsEndpoint = "/posts/";
    private final static String allCommentsForPostEndpoint = "/posts/1/comments";

    private final static String allPostsBodyFilePath = "src/test/resources/json_files/get_all_posts.json";
    private final static String allCommentsForPostBodyFilePath =
            "src/test/resources/json_files/get_all_comments_for_post.json";


//    GET	/posts
//    GET	/posts/1
//    GET	/posts/1/comments
//    POST	/posts
//    PUT	/posts/1
//    PATCH	/posts/1

//    GET	/comments?postId=1

//    DELETE	/posts/1


    @Test
    @DisplayName("Successfully get all posts")
    public void successfullyGetAllPosts() throws FileNotFoundException {
        Response response = get(URL + allPostsEndpoint);
        response.then().statusCode(200);
        compareJsonFileWithResponse(allPostsBodyFilePath, response);
    }

    @Test
    @DisplayName("Successfully get one post")
    public void successfullyGetOnePost() {
        given()
                .when().get(URL + allPostsEndpoint + "50")
                .then()
                .statusCode(200)
                .body("userId", equalTo(5))
                .body("id", equalTo(50))
                .body("title", equalTo("repellendus qui recusandae incidunt voluptates tenetur " +
                        "qui omnis exercitationem"))
                .body("body", equalTo("error suscipit maxime adipisci consequu" +
                        "ntur recusandae\nvoluptas eligendi et est et voluptates\nquia distinctio " +
                        "ab amet quaerat molestiae et vitae\nadipisci impedit sequi nesciunt quis consectetur"));
    }

    @Test
    @DisplayName("Successfully get comments for one post")
    public void successfullyGetCommentsForOnePost() throws FileNotFoundException {
        Response response = get(URL + allCommentsForPostEndpoint);
        response.then().statusCode(200);
        compareJsonFileWithResponse(allCommentsForPostBodyFilePath, response);
    }

    @Test
    @DisplayName("Add new post")
    public void addNewPost() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", 1);
        map.put("title", "New created post title");
        map.put("body", "New created post body");
        JSONObject request = new JSONObject(map);

        given().body(request.toJSONString())
                .contentType(ContentType.JSON)
                .when().post(URL + allPostsEndpoint)
                .then().statusCode(201)
                .statusLine("HTTP/1.1 201 Created")
                .body("userId", equalTo(1))
                .body("id", equalTo(101))
                .body("title", equalTo("New created post title"))
                .body("body", equalTo("New created post body"));
    }


    @Test
    @DisplayName("Update post using PUT method")
    public void changePost() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", 7);
        map.put("title", "Update post 69 title using put method by User 7");
        map.put("body", "Update post 69 body using put method by User 7");
        JSONObject request = new JSONObject(map);

        given().body(request.toJSONString())
                .contentType(ContentType.JSON)
                .when().put(URL + allPostsEndpoint + "69")
                .then().statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .body("userId", equalTo(7))
                .body("id", equalTo(69))
                .body("title", equalTo("Update post 69 title using put method by User 7"))
                .body("body", equalTo("Update post 69 body using put method by User 7"));
    }

    @Test
    @DisplayName("Update post title only using PATCH method")
    public void updatePostTitle() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Update only post 96 title using put method by User 7");
        JSONObject request = new JSONObject(map);

        given().body(request.toJSONString())
                .contentType(ContentType.JSON)
                .when().patch(URL + allPostsEndpoint + "96")
                .then().statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .body("userId", equalTo(10))
                .body("id", equalTo(96))
                .body("title", equalTo("Update only post 96 title using put method by User 7"))
                .body("body", equalTo("in non odio excepturi sint eum\nlabore voluptates vitae" +
                        " quia qui et\ninventore itaque rerum\nveniam non exercitationem delectus aut"));
    }

    @Test
    @DisplayName("Successfully delete post")
    public void deletePost() {
        given().
                when().delete(URL + allPostsEndpoint + "99")
                .then().statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .body("isEmpty()", Matchers.is(true));
    }

    public void compareJsonFileWithResponse(String expectedBodyFilePath, Response response)
            throws FileNotFoundException {
        String content = new Scanner(new File(expectedBodyFilePath))
                .useDelimiter("\\Z").next();
        String responseBody = response.asPrettyString();

        assertThat(responseBody.replaceAll("[ ]+", " "))
                .isEqualTo(content.replaceAll("[ ]+", " "));

    }

}

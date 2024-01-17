This Spring project aims to download the content of a jira issue from web GUI interfacesome.


Start the Java app. It will start a tomcat server on port 8080.

You can use the following URL to ask for statistics :

```
http://localhost:8080/track/{REPO_NAME}
```

For example, paste the following URL in your browser's search bar:

```
http://localhost:8080/track/CAMEL-10597
```


The server will automatically start on port 8080. If you want to start it on another port, just change the following code in ```src/main/resources/application.yml``` file :

``` yaml
server:
  port: 8080
```


Here is the result from querying Kaggle repositories :


```json
{
  "details" : {
    "issueType" : " Bug ",
    "priority" : " Minor ",
    "affectsVersion" : "2.18.0",
    "status" : "Resolved",
    "resolution" : " Fixed ",
    "fixVersion" : [ "2.17.5", "2.18.2", "2.19.0" ],
    "components" : [ "camel-swagger" ],
    "labels" : [ "None" ],
    "patchInfo" : "Patch Available",
    "estimatedComplexity" : "Unknown"
  },
  "people" : {
    "assignee" : "Claus Ibsen",
    "reporter" : "Bob Paulin",
    "votes" : 0,
    "watchers" : 4
  },
  "dates" : {
    "createAt" : "14/Dec/16 14:42",
    "createdAtTimestamp" : "2016-12-14T14:42:08+0000",
    "updatedAt" : "15/Dec/16 14:26",
    "updatedAtTimestamp" : "2016-12-15T14:26:13+0000",
    "resolvedAt" : "14/Dec/16 15:31",
    "resolvedAtTimestamp" : "2016-12-14T15:31:58+0000"
  },
  "description" : {
    "description" : [ "Assume I have rest path", "<div class=\"code panel\" style=\"border-width: 1px;\">\n <div class=\"codeContent panelContent\">\n  <pre class=\"code-java\"><span class=\"code-keyword\">rest</span>(<span class=\"code-quote\">\"/test\"</span>).get().type(ClassA.class).to(<span class=\"code-quote\">\"direct:someRoute\"</span>);\n\n<span class=\"code-keyword\">rest</span>(<span class=\"code-quote\">\"/testSub\"</span>).get().type(ClassB.class).to(<span class=\"code-quote\">\"direct:someOtherRoute\"</span>);\n</pre>\n </div>\n</div>", "And in the type ClassA contains a reference to ClassB.", "Within the Swagger Doc the path for ClassA renders as expected:", "<div class=\"code panel\" style=\"border-width: 1px;\">\n <div class=\"codeContent panelContent\">\n  <pre class=\"code-java\">/test:\n    get:\n      responses:\n        200:\n          schema:\n            $ref: <span class=\"code-quote\">'#/definitions/ClassA'</span>\n</pre>\n </div>\n</div>", "However ClassB gets a string parameter scheme", "<div class=\"code panel\" style=\"border-width: 1px;\">\n <div class=\"codeContent panelContent\">\n  <pre class=\"code-java\"> \n/testSub:\n    get:\n      responses:\n        200:\n          schema:\n             type : <span class=\"code-quote\">'string'</span>\n             format : <span class=\"code-quote\">'com.ClassB'</span>\n</pre>\n </div>\n</div>", "However I'd expect it to be:", "<div class=\"code panel\" style=\"border-width: 1px;\">\n <div class=\"codeContent panelContent\">\n  <pre class=\"code-java\">/testSub:\n    get:\n      responses:\n        200:\n          schema:\n            $ref: <span class=\"code-quote\">'#/definitions/ClassB'</span>\n</pre>\n </div>\n</div>" ]
  },
  "comments" : null
}
```


The results will be saved in two format in ```src/main/resources/ ``` folder: a CSV version + a JSON version.

// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.servlets;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When user submits the form, Blobstore processes the image upload and then forwards the request
 * to this servlet. This servlet processes the request using the file URL we get from Blobstore.
 */
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("siteComments").addSort("timeSubmitted", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        List<String> allComments = new ArrayList<>();
        List<String> allImageURLs = new ArrayList<>();

        int numCommentsWanted = 0;
        boolean desiredNumCommentsFound = false;
        for (Entity entity : results.asIterable()) {
            String comment = (String) (entity.getProperty("comment"));
            allComments.add(comment);
            String imageURL = (String) (entity.getProperty("imageURL"));
            allImageURLs.add(imageURL);

            // Grab number of comments wanted to be seen by user only in the first entity
            if (!desiredNumCommentsFound) {
                numCommentsWanted = Integer.parseInt((String)(entity.getProperty("numCommentsWanted")));
                desiredNumCommentsFound = true;
            }   
        }

        if (numCommentsWanted > allComments.size()) {
            numCommentsWanted = allComments.size();           
        }

        // Chop out comments beyond the number of comments wanted
        allComments = allComments.subList(0, numCommentsWanted);
        allImageURLs = allImageURLs.subList(0, numCommentsWanted);
        
        List<String> commentsAndImageURLs = new ArrayList<>();
        commentsAndImageURLs.addAll(allComments);
        commentsAndImageURLs.addAll(allImageURLs);

        response.setContentType("application/json;");
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(commentsAndImageURLs));

    }
}
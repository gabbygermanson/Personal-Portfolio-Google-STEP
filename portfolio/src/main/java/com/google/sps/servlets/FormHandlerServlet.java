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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.IOException;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * When user submits the form, Blobstore processes the image upload and then forwards the request
 * to this servlet. This servlet processes the request using the file URL we get from Blobstore.
 */
@WebServlet("/my-form-handler")
public class FormHandlerServlet extends HttpServlet {

  /** Handles form input of commment, number of comments, and an image converted to JSON to put on servlet. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        // COMMENTING REASON: Former Datastore implementation of comments form, saving as reference for step 2 of new Blobstore implementation:


        // DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        // Query query = new Query("siteComments").addSort("timeStamp", SortDirection.DESCENDING);
        // PreparedQuery results = datastore.prepare(query);
 
        // List<String> pastComments = new ArrayList<>();
 
        // int numCommentsWanted = 0;
        // boolean desiredNumCommentsFound = false;
        // for (Entity entity : results.asIterable()) {
        //     String comment = (String) (entity.getProperty("newComment"));
        //     pastComments.add(comment);

        //     // Grab number of comments wanted to be seen by user only in the first entity
        //     if (!desiredNumCommentsFound) {
        //         numCommentsWanted = Integer.parseInt((String)(entity.getProperty("seeNumComments")));
        //         desiredNumCommentsFound = true;
        //     }   
        // }
 
        // if (numCommentsWanted > pastComments.size()) {
        //     numCommentsWanted = pastComments.size();           
        // }
 
        // pastComments = pastComments.subList(0, numCommentsWanted);
 
        // response.setContentType("application/json;");
        // Gson gson = new Gson();
        // response.getWriter().println(gson.toJson(pastComments));

  }

}


 
    // COMMENTING REASON: Former Datastore implementation of comments form, saving as reference for step 2 of new Blobstore implementation:


    // /** Process POST request form input, add to pastComments, and redirect to index.html */
    // @Override
    // public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //     long timestamp = System.currentTimeMillis();
        
    //     // Get the input from the form.
    //     String comment = getParameter(request, "text-input", ""); 
    //     String numCommentsString = getParameter(request, "num-comments", "0");

    //     Entity commentEntity = new Entity("siteComments");
    //     commentEntity.setProperty("timeStamp", timestamp);
    //     commentEntity.setProperty("newComment", comment);
    //     commentEntity.setProperty("seeNumComments", numCommentsString);

    //     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    //     datastore.put(commentEntity);

    //     response.sendRedirect("/index.html");
    // }
 
    // /** Gets certain name (type) form input. 
    // * No form input converts to default (ex: No number input is handled as 0 comments wanted.)
    // * Number input will be none or >= 0 due to HTML form restrictions.
    // * @return request parameter or default value if parameter was not specified by client */
    // private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    //     String value = request.getParameter(name);
    //     if (value == null) {
    //         System.out.println("No input received for " + name + ".");
    //         return defaultValue;
    //     }
    //     return value;
    // }
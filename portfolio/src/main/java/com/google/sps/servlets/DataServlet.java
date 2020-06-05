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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a random quote. */
@WebServlet("/data")
public final class DataServlet extends HttpServlet {
    
    /** One time initialization may be used for future. */
    // @Override
    // public void init() {
    // }


    /** Merges dataFactsAndComments with pastComments to convert as one ArrayList into JSON to put on servlet. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> funFacts = new ArrayList<>();
        funFacts.add("I enjoy all music genres especially EDM!");
        funFacts.add("I studied abroad in Barcelona :)");
        funFacts.add("My favorite food is rotisserie chicken!");
        funFacts.add("I spend a lot of days on the lake!");
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("siteComments").addSort("timeStamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);

        List<String> pastComments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            String comment = (String) entity.getProperty("newComment");
            pastComments.add(comment);
        }

        List<String> factsAndComments =  new ArrayList<>();
        factsAndComments.addAll(funFacts);
        factsAndComments.addAll(pastComments);

        response.setContentType("application/json;");
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(factsAndComments));
    }

    /** Process POST request form input, add to pastComments, and redirect to index.html */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the input from the form.
        String comment = getParameter(request, "text-input", "");
        long timestamp = System.currentTimeMillis();

        Entity commentEntity = new Entity("siteComments");
        commentEntity.setProperty("newComment", comment);
        commentEntity.setProperty("timeStamp", timestamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        response.sendRedirect("/index.html");
    }

    /** Gets form input of specified type
    * @return request parameter or default value if parameter was not specified by client */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

}
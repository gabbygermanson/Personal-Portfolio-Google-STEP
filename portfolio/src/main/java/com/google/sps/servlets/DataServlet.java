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
 
    List<String> funFacts = new ArrayList<>();
    
    /** Sets up set of fun facts that will be displayed to site users. */
    @Override
    public void init() {
        funFacts.add("I enjoy all music genres especially EDM!");
        funFacts.add("I studied abroad in Barcelona :)");
        funFacts.add("My favorite food is rotisserie chicken!");
        funFacts.add("I spend a lot of days on the lake!");
    }
 
    /** Merges dataFactsAndComments with pastComments to convert as one ArrayList into JSON to put on servlet. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        // Add fun facts first to factsAndComments return variable.
        List<String> factsAndComments = new ArrayList<>();
        factsAndComments.addAll(funFacts);        
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("siteComments").addSort("timeStamp", SortDirection.DESCENDING);
        PreparedQuery results = datastore.prepare(query);
 
        List<String> pastComments = new ArrayList<>();
 
        for (Entity entity : results.asIterable()) {
            int numCommentsWanted = 0;
            boolean setNumCommentsWanted = true;  
            
            // Grab number of comments wanted to be seen by user only in the first entity
            if (numCommentsWantedTrigger) {
                numCommentsWanted = validNumInput;
                setNumCommentsWanted = false;
            }          
            
            String comment = (String) (entity.getProperty("newComment"));
            pastComments.add(comment);
        }
 
        if (numCommentsWanted > pastComments.size()) {
            numCommentsWanted = pastComments.size();           
        }
 
        pastComments = pastComments.subList(0, numCommentsWanted);

        // For testing proper number and right comments are in sublist
        //for (String comment : pastComments) {
        //    System.out.println("sublist comment: " + comment);
        //}

        factsAndComments.addAll(pastComments);
 
        response.setContentType("application/json;");
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(factsAndComments));
    }
 
    /** Process POST request form input, add to pastComments, and redirect to index.html */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long timestamp = System.currentTimeMillis();
        
        // Get the input from the form.
        String comment = getParameter(request, "text-input", ""); 
        String numCommentsString = getParameter(request, "num-comments", "0");

        // validNumInput specifies if the form input for number of comments wanted is valid
            // (meaning >= 0). If not, then the comment entered in that entity won't be shown on the site. 
        int validNumInput = Integer.parseInt(numCommentsString);

        if (validNumInput >= 0) {
            Entity commentEntity = new Entity("siteComments");
            commentEntity.setProperty("timeStamp", timestamp);
            commentEntity.setProperty("newComment", comment);
            commentEntity.setProperty("seeNumComments", numCommentsString);
    
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(commentEntity);
        } else {
            System.out.println("Number of comments wanted is not greater than or equal to 0: " + validNumInput);
        }

        response.sendRedirect("/index.html");
    }
 
    /** Gets form input of specified type
    * @return request parameter or default value if parameter was not specified by client */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            System.out.println("No input received for " + name + ".");
            return defaultValue;
        }
        return value;
    }
 
}
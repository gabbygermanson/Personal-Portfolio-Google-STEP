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
    
    private List<String> funFacts = new ArrayList<>();
    private List<String> pastComments = new ArrayList<>(); 
    private List<String> factsAndComments;

    /** One time hardcode initialization of fun facts. */
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
        factsAndComments =  new ArrayList<>();
        factsAndComments.addAll(funFacts);
        factsAndComments.addAll(pastComments);

        String jsonData = toJsonUsingGson(factsAndComments);
        response.setContentType("application/json;");
        response.getWriter().println(jsonData);
    }

    /** Converts Arraylist of data into JSON string using Gson library dependency in pom.xml. */
    private String toJsonUsingGson(List<String> data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        return json;
    }

    /** Process POST request form input, add to pastCommens, and redirect to index.html */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the input from the form.
        String comment = getParameter(request, "text-input", "");
        pastComments.add(0, comment);

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
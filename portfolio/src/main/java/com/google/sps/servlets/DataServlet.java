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
 
/** Servlet that prints fun facts. */
@WebServlet("/data")
public final class DataServlet extends HttpServlet {
 
    List<String> funFacts = new ArrayList<>();
    
    /** Sets up set of fun facts. */
    @Override
    public void init() {
        funFacts.add("I enjoy all music genres especially EDM!");
        funFacts.add("I studied abroad in Barcelona :)");
        funFacts.add("My favorite food is rotisserie chicken!");
        funFacts.add("I spend a lot of days on the lake!");
    }
 
    /** Converts funFacts ArrayList into JSON to print on servlet. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {        
        response.setContentType("application/json;");
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(funFacts));
    }
 
}
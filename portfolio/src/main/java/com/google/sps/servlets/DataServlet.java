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
  private int hardCodedNumFacts = 4;

//   @Override
//   public void init() {
//       One time initialization
//   }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<String> data = new ArrayList<>();

    data.add("I enjoy all music genres especially EDM!");
    data.add("I studied abroad in Barcelona :)");
    data.add("My favorite food is rotisserie chicken!");
    data.add("I spend a lot of days on the lake!");
    
    data.add("LGTM!");
    data.add("I'd change the website spacing.");
    data.add("A task bar would be great to add.");

    String fact = data.get((int) (Math.random() * hardCodedNumFacts));

    int factsDeleted = 0;
    int index = 0;
    while (factsDeleted != hardCodedNumFacts - 1) {
        if ((data.get(index)).equals(fact)) {
            index++;            
        } else {
            data.remove(index);
            factsDeleted++; 
            if (factsDeleted == hardCodedNumFacts - 1) {
                break;
            }            
        }
    }

    String jsonData = toJsonUsingGson(data);
    response.setContentType("application/json;");
    response.getWriter().println(jsonData);
  }

  /**
   * Converts dataArray instance into JSON string using Gson library dependency in pom.xml
   */
  private String toJsonUsingGson(List<String> dataArray) {
    Gson gson = new Gson();
    String json = gson.toJson(dataArray);
    return json;
  }

}

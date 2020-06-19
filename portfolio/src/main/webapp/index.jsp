<%--
Copyright 2019 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>

<%@ page isErrorPage="true" %>

<%-- Create Blobstore upload URL which will direct to --%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
   String uploadUrl = blobstoreService.createUploadUrl("/my-form-handler"); %>


<html>
    <head>
        <meta charset="UTF-8">
        <title>Gabrielle's Portfolio</title>
        <link rel="stylesheet" href="/style.css">
        <script src="script.js"></script>
    </head>
 
    <body onload="getComments()">
 
    <div id="content">
        <div class="text-container">
 
        <div><h1>Gabrielle's Portfolio</h1></div>
        <a href="index.html">About Me</li>
        <br>
        <a href="projects.html">Projects</li>
        <br>
        <a href="STEP.html">STEP Internship</li>
        <br>
        <br>
        <a href="https://linkedin.com/in/gabrielle-germanson">My LinkedIn</a>
        <br> 
        <a href="https://github.com/gabbygermanson">My GitHub</a>
 
        <div class="mainImages"><img src="/images/rsz_1gabbyvalencia.jpg"/></div>
 
        <p>Hello! I am a STEP intern on the Seattle Geo Core Maps team and a rising junior at Georgia Tech. I'm working from my home in the suburbs of Minneapolis, MN.</p>
        
        <br>
        <h2>What Makes Me, ME!</h2>
        <p>Click here to get a random fun fact:</p>
        <button onclick="getFunFact()" class="myButton">Generate</button>
        <div id="fact-container"></div>
        <br>

        <h1>Countries I've Been To</h2>
        <p><i>For service, band, Spanish, and study abroad!</i></p>
        <div>
            <button class="myButton" onclick="myHomeTown()">My Home Town</button>
            <button class="myButton" onclick="serviceCountry()">Service Trip</button>
            <button class="myButton" onclick="bandTourCounty()">Wind Ensemble Tour</button>
            <button class="myButton" onclick="hostCountry()">Spanish Host Family</button>
            <button class="myButton" onclick="studyAbroadMarkers()">Barcelona Study Abroad</button>
        </div>
        <br>
        <div>
            <button class="deleteButton" onclick="clearAllMarkers()">Clear Markers</button>
        </div>
        <div id="map"></div>
        <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBtqf-TDvu363fF63gLIJG62LU-4Mdu7Ss&callback=createMap"></script>
        <br>
        <br>
        <br>

        <p class="thorText">The confidence roller-coaster of Computer Science...</p>
        <div class="mainImages"><img src="/images/thor.jpg"/></div>
        <br>
        <br>
        <br>
        <form method="POST" action="<%= uploadUrl %>" enctype="multipart/form-data">
            
            <p>Post a Site Comment with a Corresponding Image:</p>
            <textarea name="text-input">Write comment here</textarea>
            <p>Upload an image:</p>
            <input type="file" name="image">
            
            <p>How Many Past Comments Would You Like to See?</p>
            <input type="number" name="num-comments" min="0" value ="0">
            <br/>
            <input type="submit"/>
        </form> 
        <div>
            <p>Site Comments:</p>
            <button class="deleteButton" onclick="deleteComments()">Delete Comments</button>
            <ul id="commentHistory"></ul>
        </div>
        </div>
    </div>
 
    </body>
 
</html>
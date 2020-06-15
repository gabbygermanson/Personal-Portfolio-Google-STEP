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
 
 
/** Fetches and parses DataServlet data to add to DOM by calling desired function call 
*@param funFactTrigger boolean specifying whether method will make function call to add a fun fact or site comment */
function fetchFactsComments(funFactTrigger) {
    fetch('/data').then(response => response.json()).then((allFactsComments) => { 
        // Split allFactsComments into proper sub-arrays of fun facts or comments for next funciton call
        var funFacts = allFactsComments.slice(0, 4);
 
        var comments = [];
        if (allFactsComments.length > 4) {
            var comments = allFactsComments.slice(4);
        }
            
        if (funFactTrigger) {
            getFunFact(funFacts);
        } else {
            getComments(comments);
        }
  });
}
 
/** Random fun fact selected to be added to index.html */
function getFunFact() {
    fetch('/data').then(response => response.json()).then((allFunFacts) => {   
        fact = allFunFacts[Math.floor((Math.random() * allFunFacts.length))];    
        document.getElementById('fact-container').innerText = fact;
    });
}
 
/** Builds Unordered List of site comment history */
function getComments() {
    fetch('/comments').then(response => response.json()).then((allCommentsImages) => {       
        var halfLength = allCommentsImages.length / 2;

        var comments = allCommentsImages.slice(0, halfLength);
        var imageURLS = allCommentsImages.slice(halfLength)
        
        const historyUL = document.getElementById('commentHistory');

        var index = 0;
        comments.forEach((comment) => {
            historyUL.appendChild(createListComment(comment));
            historyUL.appendChild(createListImage(imageURLS[index]));
            index++;
        });
    });
}
 
/** Creates an comment list element. */
function createListComment(text) {
    const liElement = document.createElement('li');
    liElement.innerText = text;
    return liElement;
}

/** Creates an <img> list element. */
function createListImage(url) {
    const liElement = document.createElement('li');
    if (url == null) {
        liElement.innerText = "No Image Submitted"
    } else {
        var imgElement = document.createElement("img");
        imgElement.setAttribute("src", url);
        liElement.appendChild(imgElement)
    }
    return liElement;
}


/** Removes any current li elements on the page and fetches DeleteDataServlet to rid Datastore data. */
async function deleteComments() {    
    var list = document.getElementById("commentHistory");

    // As long as <ul> has a child node, remove it
    while (list.hasChildNodes()) {
        list.removeChild(list.firstChild);
    }
    let response = await fetch('/delete-data', {method: 'POST'});
    getComments();
}
  

// Study abroad variables keeping for future different implementations
//var barcelona = {lat: 41.3851, lng: 2.1734};
//var czech = {lat: 49.8175, lng: 15.4730};
//var hungary = {lat: 47.1625, lng: 19.5033};
//var france = {lat: 46.2276, lng: 2.2137};
//var netherlands = {lat: 52.1326, lng: 5.2913};
//var germany = {lat: 51.1657, lng: 10.4515};
//var greece = {lat: 39.0742, lng: 21.8243};

var map;

var home = {lat: 44.962009, lng: -93.52606};
var haiti = {lat: 18.9712, lng: -72.2852};
var spain = {lat: 40.4637, lng: -3.7492};
var ecuador = {lat: -1.8312, lng: -78.1834};

var homeMarker = null;
var serviceMarker = null;
var bandTourMarker = null;
var hostMarker = null;
var studyAbroad = [];

/** Makes a map and adds it to the page. */
function createMap() {
  map = new google.maps.Map(document.getElementById('map'), {center: {lat: 30, lng: -35}, zoom: 4});
}

/** Creates a marker with given destinationCoords coordinates and puts it on the map.
  * @param destinationCoords is the coordinates to make a marker at on the map.
  * @param specificTitle is the title for the certain marker being made. */
function makeMarker(destinationCoords, specificTitle) {
    return (new google.maps.Marker({
        position: destinationCoords,
        map: map,
        title: specificTitle
    }));
}

/** Removes homeMarker if it exists on the map before making a new homeMarker */
function myHomeTown() {
    if (homeMarker != null) {
        homeMarker.setMap(null);
        homeMarker = null;
    }
    homeMarker = makeMarker(home, "Wayzata, Minnesota");
}

/** Removes serviceMarker if it exists on the map before making a new serviceMarker */
function serviceCountry() {
    if (serviceMarker != null) {
        serviceMarker.setMap(null);
        serviceMarker = null;
    }
    serviceMarker = makeMarker(haiti, "Haiti");
}

/** Removes bandTourMarker if it exists on the map before making a new bandTourMarker */
function bandTourCounty() {
    if (bandTourMarker != null) {
        bandTourMarker.setMap(null);
        bandTourMarker = null;
    }
    bandTourMarker = makeMarker(spain, "Spain");
}

/** Removes hostMarker if it exists on the map before making a new hostMarker */
function hostCountry() {
    if (hostMarker != null) {
        hostMarker.setMap(null);
        hostMarker = null;
    }
    hostMarker = makeMarker(ecuador, "Ecuador");
}

/** Fetch study abroad country coordinates from a servlet which reads in a CSV of coordinates. */
function studyAbroadMarkers() {
  clearMarkerSet(studyAbroad);
  var count = 1;
  fetch('/study-abroad').then(response => response.json()).then((studyAbroadCountries) => {
    studyAbroadCountries.forEach((country) => {
        addMarkerWithTimeout(country.name, {lat: country.lat, lng: country.lng}, count * 200);
        count++;
    });
  });
}

/** Makes markers given position coordinates with drop animation after a timeout pause.
  * @param position is the coordinates to make a marker at on the map.
  * @param timeout is the period of time before new marker is dropped on the map. */
function addMarkerWithTimeout(placeName, position, timeout) {
    window.setTimeout(function() {
        studyAbroad.push(new google.maps.Marker({
          position: position,
          map: map,
          title: placeName,
          animation: google.maps.Animation.DROP
        }));
    }, timeout);
}

/** Clear all markers present on the map, if any. */
function clearAllMarkers() {
    clearMarkerSet(studyAbroad);
    if (homeMarker != null) {
        homeMarker.setMap(null);
        homeMarker = null;
    }

    if (serviceMarker != null) {
        serviceMarker.setMap(null);
        serviceMarker = null;
    }

    if (bandTourMarker != null) {
        bandTourMarker.setMap(null);
        bandTourMarker = null;
    }

    if (hostMarker != null) {
        hostMarker.setMap(null);
        hostMarker = null;
    }
}

/** Clears an array of markers on the map.
  * @param markerSet is the grouped markers in an array to be removed. */
function clearMarkerSet(markerSet) {
    if (markerSet.length != 0) {
        for (var i = 0; i < markerSet.length; i++) {
            markerSet[i].setMap(null);
        }
        markerSet = [];
    }
}

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
        // allFactsComments is a JSON object ArraryList of first facts then site comments
        console.log("All facts and comments fetched: " + allFactsComments);
 
        // Split allFactsComments into proper sub-arrays of either fun facts or comments for next funciton call
        var funFacts = allFactsComments.slice(0, 4);
        console.log("Funfacts: " + funFacts);
 
        var comments = [];
        if (allFactsComments.length > 4) {
            var comments = allFactsComments.slice(4);
            console.log("Site comments: " + comments);
        }
            
        if (funFactTrigger) {
            getFunFact(funFacts);
        } else {
            getComments(comments);
        }
  });
}
 
/** Random fun fact selected to be added to index.html */
function getFunFact(facts) {
    fact = facts[Math.floor((Math.random() * facts.length))];
    console.log("Random fact is " + fact);
    
    document.getElementById('fact-container').innerText = fact;
}
 
/** Builds Unordered List of site comment history */
function getComments(siteComments) {
    const historyUL = document.getElementById('commentHistory');
    siteComments.forEach((line) => {
        historyUL.appendChild(createListElement(line));
    });
}
 
/** Creates an <li> list item element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
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
    fetchFactsComments(0);
}
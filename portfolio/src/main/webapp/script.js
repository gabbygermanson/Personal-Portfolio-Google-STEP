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

/** When content was HTML: Fetch with async and await keywords allowing the return values
 * directly instead of going through Promises. */
// async function getFunFact() {
//   const response = await fetch('/data');
//   const fact = await response.text();
//   document.getElementById('fact-container').innerText = fact;
// }


/** Fetches data from the servers and adds them to the DOM.
    funFactTrigger was used when two places in index.HTML called this method so this parameter
    shows which place called it to distribute data properly*/
function getFunFact(funFactTrigger) {
    fetch('/data').then(response => response.json()).then((allData) => {
    // allData is an object, not a string

    if (funFactTrigger) {
        document.getElementById('fact-container').innerText = allData[0];
    } else {
        const commentsListElement = document.getElementById('comments-container');
        commentsListElement.innerHTML = '';
        commentsListElement.appendChild(createListElement(allData[1]));
        commentsListElement.appendChild(createListElement(allData[2]));
        commentsListElement.appendChild(createListElement(allData[3]));
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

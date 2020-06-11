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

package com.google.sps.data;

/** Represents a country at a specific lat lng point. */
public class Country {
  private String name;
  private double lat;
  private double lng;

  public Country(String placeName, double latitude, double longitude) {
    this.name = placeName;
    this.lat = latitude;
    this.lng = longitude;
  }

  public String getName() {
      return name;
  }

  public double getLat() {
      return lat;
  }

  public double getLng() {
      return lng;
  }
}

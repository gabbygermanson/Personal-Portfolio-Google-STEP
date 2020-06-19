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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

public final class FindMeetingQuery {
    
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        ArrayList<TimeRange> timesWithOptionals = new ArrayList<>();
        ArrayList<TimeRange> timesNoOptionals = new ArrayList<>();
        timesWithOptionals.add(TimeRange.WHOLE_DAY);
        timesNoOptionals.add(TimeRange.WHOLE_DAY);

        if (request.getDuration() > (24 * 60)) {
            return new ArrayList<>();
        } else if (events.size() == 0) {
            return timesWithOptionals;
        }

        Collection<String> requestersWithOptionals = new ArrayList<>();
        requestersWithOptionals.addAll(request.getAttendees());
        requestersWithOptionals.addAll(request.getOptionalAttendees());
        Collection<String> requestersNoOptionals = new ArrayList<>();
        requestersNoOptionals.addAll(request.getAttendees());

        ArrayList<Event> eventsWithOptionals = new ArrayList<>(events);
        ArrayList<Event> eventsNoOptionals = new ArrayList<>(events);

        ArrayList<Event> currEventList = eventsWithOptionals;
        Collection<String> currRequesters = requestersWithOptionals;
        int eventsLength = eventsWithOptionals.size();
        for (int i = 0; i < 2; i++) {
            if (i == 1) {
                currEventList = eventsNoOptionals;
                currRequesters = requestersNoOptionals;
                eventsLength = eventsNoOptionals.size();
            }
            for (int eventsIndex = 0; eventsIndex < eventsLength; eventsIndex++) {
                Event currEvent = currEventList.get(eventsIndex);
                Set<String> currEventAttendees = currEvent.getAttendees();
                
                boolean relevantEvent = false;
                for (String person : currEventAttendees) {
                    if (currRequesters.contains(person)) {
                        relevantEvent = true;
                    }
                }
                if (!relevantEvent) {
                    currEventList.remove(currEvent);
                    eventsIndex--;
                    eventsLength--;
                }
                
            }
        }

        ArrayList<Event> eventsList = eventsWithOptionals;
        ArrayList<TimeRange> timesFreeList = timesWithOptionals;
        int length = timesWithOptionals.size();
        for (int j = 0; j < 2; j++) {
            if (j == 1) {
                eventsList = eventsNoOptionals;
                timesFreeList = timesNoOptionals;
                length = timesNoOptionals.size();
            }

            for (Event event : eventsList) {
                for (int index = 0; index < length; index++) {
                    TimeRange freeTime = timesFreeList.get(index);
                    boolean includeEnd = false;

                    if (freeTime.overlaps(event.getWhen())) {

                        if (freeTime.contains(event.getWhen())) {  // Free time completely contains event time range

                            if (freeTime.equals(event.getWhen())) {
                                // Handled at end of overlapping if statement. Case is present for reader understanding
                            } else if (freeTime.end() == event.getWhen().end()) {
                                TimeRange shorterBeforeFree = TimeRange.fromStartEnd(freeTime.start(), event.getWhen().start(), includeEnd);
                                timesFreeList.add(shorterBeforeFree);
                                length++;
                            } else if (freeTime.start() == event.getWhen().start()) {
                                if (freeTime.end() == TimeRange.END_OF_DAY) {
                                    includeEnd = true;
                                }
                                TimeRange shorterAfterFree = TimeRange.fromStartEnd(event.getWhen().end(), freeTime.end(), includeEnd);
                                timesFreeList.add(shorterAfterFree);
                                length++;
                            } else {
                                TimeRange shorterBeforeFree = TimeRange.fromStartEnd(freeTime.start(), event.getWhen().start(), includeEnd);
                                if (freeTime.end() == TimeRange.END_OF_DAY) {
                                    includeEnd = true;
                                }                            
                                TimeRange shorterAfterFree = TimeRange.fromStartEnd(event.getWhen().end(), freeTime.end(), includeEnd);
                                timesFreeList.add(shorterBeforeFree);
                                timesFreeList.add(shorterAfterFree);
                                length += 2;                             
                            }
                        } else if ((event.getWhen()).contains(freeTime)) {  // Event completely contains free time range
                            // Handled at end of overlapping if statement. Case is present for reader understanding
                        } else {  // Partial overlap between event and free time ranges
                            if (freeTime.start() < event.getWhen().start()) {
                                TimeRange shorterBeforeFree = TimeRange.fromStartEnd(freeTime.start(), event.getWhen().start(), includeEnd);
                                timesFreeList.add(shorterBeforeFree);
                            } else {
                                if (freeTime.end() == TimeRange.END_OF_DAY) {
                                    includeEnd = true;
                                }                           
                                TimeRange shorterAfterFree = TimeRange.fromStartEnd(event.getWhen().end(), freeTime.end(), includeEnd);
                                timesFreeList.add(shorterAfterFree);
                            }
                            length++;
                        }
                        timesFreeList.remove(freeTime);
                        length--;
                        index--;
                    }
                }
            }

            for (int k = 0; k < timesFreeList.size(); k++) {
                TimeRange foundFreeTime = timesFreeList.get(k);
                if (foundFreeTime.duration() < request.getDuration()) {
                    timesFreeList.remove(foundFreeTime);
                }
            }

            Collections.sort(timesFreeList, TimeRange.ORDER_BY_START);
        }

        if (timesWithOptionals.size() == 0) {
            if (requestersNoOptionals.size() == 0) {
                return new ArrayList<TimeRange>();
            } else {
                return timesNoOptionals;
            }
        }
        return timesWithOptionals;
    }

}

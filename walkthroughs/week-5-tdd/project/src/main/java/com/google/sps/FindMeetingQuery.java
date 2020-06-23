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
        if (request.getDuration() > TimeRange.MINUTES_WHOLE_DAY) {
            // Meeting request duration must be less than a day
            return new ArrayList<>();
        } else if (events.size() == 0) {
            // The entire day is available for a requested meeting if attendees have no scheduled events
            return new ArrayList<>(Arrays.asList(TimeRange.WHOLE_DAY));
        }

        // Initialize meeting requesters for both with and without optional attendees
        Collection<String> requestersWithOptionals = new ArrayList<>();
        requestersWithOptionals.addAll(request.getAttendees());
        requestersWithOptionals.addAll(request.getOptionalAttendees());
        Collection<String> requestersNoOptionals = new ArrayList<>();
        requestersNoOptionals.addAll(request.getAttendees());

        // Initialize scheduled event list, each having at least one of the meeting requesters
        ArrayList<Event> eventsWithOptionals = ridIrrelevantEvents(new ArrayList<>(events), requestersWithOptionals);
        ArrayList<Event> eventsNoOptionals = ridIrrelevantEvents(new ArrayList<>(events), requestersNoOptionals);

        // Initialize available meeting times for both required attendees with and without optional attendees
        ArrayList<TimeRange> timesWithOptionals = findAvailableMeetingTimes(new ArrayList<>(Arrays.asList(TimeRange.WHOLE_DAY)), eventsWithOptionals);
        ArrayList<TimeRange> timesNoOptionals = findAvailableMeetingTimes(new ArrayList<>(Arrays.asList(TimeRange.WHOLE_DAY)), eventsNoOptionals);

        removeShortTimesSort(timesWithOptionals, request);
        removeShortTimesSort(timesNoOptionals, request);

        if (timesWithOptionals.size() == 0) {
            if (requestersNoOptionals.size() == 0) {
                return new ArrayList<TimeRange>();
            } else {
                return timesNoOptionals;
            }
        }
        return timesWithOptionals;
    }

    /**
    * Remove events from the query's scheduled event list that do not have any of the meeting requesters
    * @param scheduledList the query's scheduled event list being handled
    * @param meetingRequesters the people who want to schedule a meeting
    * @return list of events with at least one meeting requester as an attendee
    */
    ArrayList<Event> ridIrrelevantEvents(ArrayList<Event> scheduledList, Collection<String> meetingRequesters) {
        int lengthScheduledList = scheduledList.size();
        for (int indexEvents = 0; indexEvents < lengthScheduledList; indexEvents++) {
            Event scheduledEvent = scheduledList.get(indexEvents);
            Set<String> scheduledAttendees = scheduledEvent.getAttendees();

            boolean relevantEvent = false;
            for (String attendee : scheduledAttendees) {
                if (meetingRequesters.contains(attendee)) {
                    relevantEvent = true;
                }
            }
            if (!relevantEvent) {
                scheduledList.remove(scheduledEvent);
                indexEvents--;
                lengthScheduledList--;
            }
        }
        return scheduledList;
    }

    /**
    * Find the available times to meet across each requester's events
    * @param availableTimesList current list of available times to meet
    * @param scheduledEventList list of all requester's scheduled events
    * @return list of possible times for requesters to meet
    */
    ArrayList<TimeRange> findAvailableMeetingTimes(ArrayList<TimeRange> availableTimesList, ArrayList<Event> scheduledEventList) {
        int lengthAvailableTimes = availableTimesList.size();

        for (Event eventScheduled : scheduledEventList) {

            for (int indexAvailableTime = 0; indexAvailableTime < lengthAvailableTimes; indexAvailableTime++) {
                TimeRange availableTime = availableTimesList.get(indexAvailableTime);
                boolean includeEndTime = false;

                if (availableTime.overlaps(eventScheduled.getWhen())) {
                    if (availableTime.equals(eventScheduled) || (eventScheduled.getWhen()).contains(availableTime)) {
                        // Remove availableTime from availableTimeList handled after if statement block
                    } else if (availableTime.contains(eventScheduled.getWhen())) {
                        // availableTime entirely contains eventScheduled time range

                        if (availableTime.end() == eventScheduled.getWhen().end()) {
                            // Both availableTime and eventScheduled end at same time
                            availableTimesList.add(TimeRange.fromStartEnd(availableTime.start(), eventScheduled.getWhen().start(), includeEndTime));
                            lengthAvailableTimes++;
                        } else if (availableTime.start() == eventScheduled.getWhen().start()) {
                            // Both availableTime and eventScheduled start at same time
                            if (availableTime.end() == TimeRange.END_OF_DAY) {
                                includeEndTime = true;
                            }
                            availableTimesList.add(TimeRange.fromStartEnd(eventScheduled.getWhen().end(), availableTime.end(), includeEndTime));
                            lengthAvailableTimes++;
                        } else {
                            // eventScheduled lies within availableTime
                            if (availableTime.end() == TimeRange.END_OF_DAY) {
                                includeEndTime = true;
                            }
                            availableTimesList.add(TimeRange.fromStartEnd(availableTime.start(), eventScheduled.getWhen().start(), includeEndTime));
                            availableTimesList.add(TimeRange.fromStartEnd(eventScheduled.getWhen().end(), availableTime.end(), includeEndTime));
                            lengthAvailableTimes += 2;
                        }
                    } else { 
                        // Partial overlap between availableTime and eventScheduled
                        if (availableTime.start() < eventScheduled.getWhen().start()) {
                            // availableTime starts before eventSchedule
                            availableTimesList.add(TimeRange.fromStartEnd(availableTime.start(), eventScheduled.getWhen().start(), includeEndTime));
                        } else {
                            // eventSchedule starts before availableTime
                            if (availableTime.end() == TimeRange.END_OF_DAY) {
                                includeEndTime = true;
                            }
                            availableTimesList.add(TimeRange.fromStartEnd(eventScheduled.getWhen().end(), availableTime.end(), includeEndTime));
                        }
                        lengthAvailableTimes++;
                    }

                    availableTimesList.remove(availableTime);
                    lengthAvailableTimes--;
                    indexAvailableTime--;
                }
            }
        }

        return availableTimesList;
    }

    /**
    * Take out times with a shorter duration than requested meeting duration, then sort
    * @param freeTimesList current list of available times to meet
    * @param request requester's request of a meeting of a certain duration and attendee list
    * @return sorted list of possible times for requesters to meet with valid duration
    */
    ArrayList<TimeRange> removeShortTimesSort(ArrayList<TimeRange> freeTimesList, MeetingRequest request) {
        for (int indexFreeTime = 0; indexFreeTime < freeTimesList.size(); indexFreeTime++) {
            TimeRange freeTime = freeTimesList.get(indexFreeTime);
            if (freeTime.duration() < request.getDuration()) {
                freeTimesList.remove(freeTime);
            }
        }
        Collections.sort(freeTimesList, TimeRange.ORDER_BY_START);
        return freeTimesList;
    }
}
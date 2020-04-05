package com.kushcabbage.friday_android.gsonParsers;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

public class GsonSongKickParser {


        //List<CalenderEntry> calenderEntryList;
        public ResultsPage resultsPage;

        public List<CalenderEntry> getevents(){
            return resultsPage.results.calendarEntry;
        }

        public class ResultsPage{
            Results results;
            String status;
        }

        public class Results {
            List<CalenderEntry> calendarEntry;
        }

        public class CalenderEntry{
            public Event getEvent() {
                return event;
            }

            public Event event;

            @NonNull
            @Override
            public String toString() {
                return event.displayName;
            }

            public Date getDateObj() {
                return event.start.dateobj;
            }

            public List<Performance> getPerformance(){
                return event.performance;
            }

            public Venue getVenue(){
                return event.venue;
            }

            public String getFeaturedArtists(){
                return event.featuredArtists;
            }

        }

        public class Event{
            String displayName;
            public Start start;

            public List<Performance> getPerformance() {
                return performance;
            }

            public List<Performance> performance;

            public Venue getVenue() {
                return venue;
            }

            Venue venue;
            Location location;



            public String featuredArtists;
        }

        public class Performance{
            public String getDisplayName() {
                return displayName;
            }

            public String displayName;
        }

        public class Start{
            public String datetime;
            public Date dateobj;



        }
        public class Venue{
            public String getDisplayName() {
                return displayName;
            }

            String displayName;
        }
        class Location{
            float lat;
            float lng;
        }


}

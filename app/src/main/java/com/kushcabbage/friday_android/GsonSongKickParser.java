package com.kushcabbage.friday_android;

import java.util.Date;
import java.util.List;

public class GsonSongKickParser {


        //List<CalenderEntry> calenderEntryList;
        ResultsPage resultsPage;

        public List<CalenderEntry> getevents(){
            return resultsPage.results.calendarEntry;
        }


        class ResultsPage{
            Results results;
            String status;
        }

        class Results {
            List<CalenderEntry> calendarEntry;
        }

        class CalenderEntry{
            Event event;

        }

        class Event{
            String displayName;
            Start start;
            List<Performance> performance;
            Venue venue;
            Location location;
            String featuredArtists;
        }

        class Performance{
            String displayName;
        }

        class Start{
            String datetime;
            Date dateobj;



        }
        class Venue{
            String displayName;
        }
        class Location{
            float lat;
            float lng;
        }


}

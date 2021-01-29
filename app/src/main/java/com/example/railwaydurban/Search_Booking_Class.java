package com.example.railwaydurban;

public class Search_Booking_Class {

    //Variables
    private String Train;
    private String StationTo;
    private String StationFrom;
    private String DepatureDate;
    private String TravellerAdult;
    private String TravellerKid;

    //Constructor method


    public Search_Booking_Class(String train, String stationTo, String stationFrom, String depatureDate, String travellerAdult, String travellerKid) {
        Train = train;
        StationTo = stationTo;
        StationFrom = stationFrom;
        DepatureDate = depatureDate;
        TravellerAdult = travellerAdult;
        TravellerKid = travellerKid;
    }

    //Getter and Setter methods

    public String getTrain() {
        return Train;
    }

    public void setTrain(String lineType) {
        Train = lineType;
    }

    public String getStationTo() {
        return StationTo;
    }

    public void setStationTo(String stationTo) {
        StationTo = stationTo;
    }

    public String getStationFrom() {
        return StationFrom;
    }

    public void setStationFrom(String stationFrom) {
        StationFrom = stationFrom;
    }

    public String getDepatureDate() {
        return DepatureDate;
    }

    public void setDepatureDate(String depatureDate) {
        DepatureDate = depatureDate;
    }

    public String getTravellerAdult() {
        return TravellerAdult;
    }

    public void setTravellerAdult(String travellerAdult) {
        TravellerAdult = travellerAdult;
    }

    public String getTravellerKid() {
        return TravellerKid;
    }

    public void setTravellerKid(String travellerKid) {
        TravellerKid = travellerKid;
    }
}

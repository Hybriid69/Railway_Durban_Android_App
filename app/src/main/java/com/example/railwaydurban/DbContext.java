package com.example.railwaydurban;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class DbContext extends SQLiteOpenHelper {

    //Db Name
    public static final String DATABASE_NAME = "Railway";
    // Db Tables and Properties
    public static final String USERS_TABLE = "UserDetails";
    public static final String USERS_COL1 = "ID";
    public static final String USERS_COL2 = "FirstName";
    public static final String USERS_COL3 = "LastName";
    public static final String USERS_COL4 = "EmailAddress";
    public static final String USERS_COL5 = "Password";
    public static final String USERS_COL6 = "PhoneNumber";

    public static final String TRAINS_TABLE = "TrainDetails";
    public static final String TRAINS_COL1 = "ID";
    public static final String TRAINS_COL2 = "TrainName";
    public static final String TRAINS_COL3 = "NumberOfPassengers";
    public static final String TRAINS_COL4 = "SeatsAvailable";

    public static final String BOOKINGS_TABLE = "BookingDetails";
    public static final String BOOKINGS_COL1 = "ID";
    public static final String BOOKINGS_COL2 = "Email";
    public static final String BOOKINGS_COL3 = "TrainName";
    public static final String BOOKINGS_COL4 = "Date";
    public static final String BOOKINGS_COL5 = "DepartureTime";
    public static final String BOOKINGS_COL6 = "Departure";
    public static final String BOOKINGS_COL7 = "Destination";
    public static final String BOOKINGS_COL8 = "Adults";
    public static final String BOOKINGS_COL9 = "Kids";
    public static final String BOOKINGS_COL10 = "Cost";

    public static final String PAYMENT_TABLE = "PaymentDetails";
    public static final String PAYMENT_COL1 = "ID";
    public static final String PAYMENT_COL2 = "UserID";
    public static final String PAYMENT_COL3 = "Date";
    public static final String PAYMENT_COL4 = "Amount";

    public static final String SCHEDULE_TABLE = "Schedules";
    public static final String SCHEDULE_COL1 = "ID";
    public static final String SCHEDULE_COL2 = "TrainName";
    public static final String SCHEDULE_COL3 = "Date";
    public static final String SCHEDULE_COL4 = "Time";
    public static final String SCHEDULE_COL5 = "Departure";
    public static final String SCHEDULE_COL6 = "Destination";


    public DbContext(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase(); // create db
        //check if admin credentials exist in DB
        Cursor check = checkAdmin();
        if(check.getCount() <=0)
        {
            adminSeed(); // if not- insert them
            seedScheduleData();
            seedTrainData();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + USERS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, FirstName TEXT, LastName TEXT, EmailAddress TEXT, Password TEXT, PhoneNumber TEXT)");

        db.execSQL("create table " + TRAINS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TrainName TEXT, NumberOfPassengers INTEGER, SeatsAvailable INTEGER)");

        db.execSQL("create table " + BOOKINGS_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, Email Text, TrainName Text,  Date TEXT,  Departure TEXT,  Destination TEXT,  DepartureTime TEXT,  ArrivalTime TEXT, Adults INTEGER, Kids INTEGER, Cost REAL)");

        db.execSQL("create table " + PAYMENT_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, Email TEXT, Date TEXT, Amount REAL)");

        db.execSQL("create table " + SCHEDULE_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TrainName TEXT, Date TEXT, Time TEXT, Departure TEXT, Destination TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRAINS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BOOKINGS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PAYMENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_TABLE);
        onCreate(db);
    }
    public boolean adminSeed() {
        SQLiteDatabase db = this.getWritableDatabase(); // check  db
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COL2, "Admin");
        contentValues.put(USERS_COL3, "Admin");
        contentValues.put(USERS_COL4, "Admin");
        contentValues.put(USERS_COL5, "Password01");
        contentValues.put(USERS_COL6, 031);
        long result = db.insert(USERS_TABLE, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }
    //// Train Details from Admin Tasks
    public boolean seedTrainData() {
        SQLiteDatabase db = this.getWritableDatabase(); // check  db
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRAINS_COL2, "Bullet");
        contentValues.put(TRAINS_COL3, 100);
        contentValues.put(TRAINS_COL4, 100);

        long result = db.insert(TRAINS_TABLE, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }
    public boolean seedScheduleData() {
        Date date = java.util.Calendar.getInstance().getTime();
        SQLiteDatabase db = this.getWritableDatabase(); // check db
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCHEDULE_COL2, "Bullet");
        contentValues.put(SCHEDULE_COL3, String.valueOf(date));
        contentValues.put(SCHEDULE_COL4, "10,00 AM");
        contentValues.put(SCHEDULE_COL5, "Durban");
        contentValues.put(SCHEDULE_COL6, "Gateway");
        long result = db.insert(SCHEDULE_TABLE, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }


    public boolean insertScheduleData(String tName, String date, String time, String departure, String destination) {
        SQLiteDatabase db = this.getWritableDatabase(); // check db
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCHEDULE_COL2, tName);
        contentValues.put(SCHEDULE_COL3, date);
        contentValues.put(SCHEDULE_COL4, time);
        contentValues.put(SCHEDULE_COL5, departure);
        contentValues.put(SCHEDULE_COL6, destination);
        long result = db.insert(SCHEDULE_TABLE, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertRegistrationData(String firstname, String lastname, String emailaddress, String password, String phonenumber) {
        SQLiteDatabase db = this.getWritableDatabase(); // check db
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COL2, firstname);
        contentValues.put(USERS_COL3, lastname);
        contentValues.put(USERS_COL4, emailaddress);
        contentValues.put(USERS_COL5, password);
        contentValues.put(USERS_COL6, phonenumber);
        long result = db.insert(USERS_TABLE, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }
    //// Train Details from Admin Tasks
    public boolean insertTrainData(String name, Integer passengers, Integer seats) {
        SQLiteDatabase db = this.getWritableDatabase(); // check  db
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRAINS_COL2, name);
        contentValues.put(TRAINS_COL3, passengers);
        contentValues.put(TRAINS_COL4, seats);

        long result = db.insert(TRAINS_TABLE, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }
/////   Insert Booking data
    public boolean insertBookingData(String Email, String TrainName, String Date, String time, String stationFrom, String stationTo, Integer Adult, Integer Kid, Float cost) {
        SQLiteDatabase db = this.getWritableDatabase(); // check  db
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOKINGS_COL2, Email);
        contentValues.put(BOOKINGS_COL3, TrainName);
        contentValues.put(BOOKINGS_COL4, Date);
        contentValues.put(BOOKINGS_COL5,time);
        contentValues.put(BOOKINGS_COL6, stationFrom);
        contentValues.put(BOOKINGS_COL7, stationTo);
        contentValues.put(BOOKINGS_COL8, Adult);
        contentValues.put(BOOKINGS_COL9, Kid);
        contentValues.put(BOOKINGS_COL10, cost);

        long result = db.insert(BOOKINGS_TABLE, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //////////////////////////////////////// Retrieve  /////////////////////////////////////////////
    public Cursor checkAdmin() {
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select * from " + USERS_TABLE+" where "+ USERS_COL4 + " = 'Admin'"+" and " +USERS_COL5+ " = 'Admin'", null);
        return result;
    }
    public Cursor getUserDetails(String email){
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select * from " + USERS_TABLE+" where "+ USERS_COL4+ " = '" +email+ "'", null);
        return result;
    }
    public Cursor checkCredentials(String emailaddress, String password) {
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select * from " + USERS_TABLE+" where "+ USERS_COL4 + " = '" + emailaddress + "'"+" and " +USERS_COL5+ " = '" + password + "'", null);
        return result;
    }

    public Cursor getScheduleData() {
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select * from " + SCHEDULE_TABLE, null);
        return result;
    }
    public Cursor checkTrainNames(String name) {
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select TrainName from " + TRAINS_TABLE+" where "+ TRAINS_COL2 + " = '" + name + "'", null);
        return result;
    }
    public Cursor getTrainNames() {
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select TrainName from " + TRAINS_TABLE, null);
        return result;
    }
    public Cursor getTrainDepartures() {      // FOR BOOKING CLASS
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select Departure from " + SCHEDULE_TABLE, null);
        return result;
    }
    public Cursor getTrainDepartureDates(String name, String from, String to) {      // FOR BOOKING CLASS
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select Date from " + SCHEDULE_TABLE + " where "+SCHEDULE_COL2+ " = '" + name+ "'"+" and " + SCHEDULE_COL5 + " = '" + from + "'"+" and " +SCHEDULE_COL6+ " = '" + to+ "'", null);
        return result;
    }
    public Cursor getTrainDepartureTimes(String name,String date, String from, String to) {      // FOR BOOKING CLASS
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select Time from " + SCHEDULE_TABLE + " where "+SCHEDULE_COL2+ " = '" + name+ "'" +" and " +SCHEDULE_COL3+ " = '" + date + "'"+" and "+ SCHEDULE_COL5 + " = '" + from + "'"+" and " +SCHEDULE_COL6+ " = '" + to+ "'", null);
        return result;
    }
    public Cursor getTrainDestination(String name) {      // FOR BOOKING CLASS
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select Destination from " + SCHEDULE_TABLE + " where " + SCHEDULE_COL5 + " = '" + name + "'", null);
        return result;
    }
    public Cursor getTrainNamesfiltered( String depart, String to) {      // FOR BOOKING CLASS
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select TrainName from " + SCHEDULE_TABLE + " where " + SCHEDULE_COL5 + " = '" + depart + "'"+" and " +SCHEDULE_COL6+ " = '" + to + "'", null);
        return result;
    }
    public Cursor validateEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select * from " + USERS_TABLE + " where " + USERS_COL4 + " = '" + email + "'", null);
        return result;
    }
    public Cursor getAvailableSeats(String name) {
        SQLiteDatabase db = getReadableDatabase();  // check  db
        Cursor result = db.rawQuery("select SeatsAvailable from " + TRAINS_TABLE + " where " + TRAINS_COL2 + " = '" + name + "'", null);
        return result;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Update Password
    public Boolean updatePassword(String email, String pass) {
        SQLiteDatabase db = getReadableDatabase();  // check db

        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COL5, pass);
        long result = db.update(USERS_TABLE,contentValues,USERS_COL4+" = '" + email + "'",null);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    //Update Avaiable seats
    public Boolean updateAvailableSeats(String name, Integer bookedSeats) {
        SQLiteDatabase db = getReadableDatabase();  // check db
ContentValues contentValues = new ContentValues();
        contentValues.put(TRAINS_COL4, bookedSeats);
        long result = db.update(TRAINS_TABLE, contentValues, TRAINS_COL2+" = '" + name + "'", null);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


}
package com.example.jerrysprendimai;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ObjectEvent {
    String id, calendar_id, dstart, dtend, title, description, allDay, eventLocation, eventDate;
    int month, year, day;
    Date date;
    Calendar calendar;
    MyAdapterCalendarEvents.MyViewHolder myViewHolderCalendarEvents;

    public ObjectEvent(Cursor cursor, int indicator){
        this.id            = cursor.getString(0);   //ID
        this.calendar_id   = "0";
        this.dstart        = cursor.getString(5);   //DSTART
        this.dtend         = cursor.getString(5);   //DSTART
        this.title         = cursor.getString(2);   //TITLE
        this.description   = cursor.getString(4);   //ADDRESS
        this.allDay        = "";
        this.eventLocation = "";

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.calendar = Calendar.getInstance();
        this.calendar.setTimeInMillis(Long.parseLong(dstart));
        this.date = new Date();
        this.date.setTime(this.calendar.getTimeInMillis());
        this.eventDate = displayDateFormat.format(calendar.getTime());

        this.year  = this.calendar.get(Calendar.YEAR);
        this.month = this.calendar.get(Calendar.MONTH);
        this.day   = this.calendar.get(Calendar.DAY_OF_MONTH);
    }
    public ObjectEvent(Cursor cursor){
        this.id            = cursor.getString(0);
        this.calendar_id   = cursor.getString(1);
        this.dstart        = cursor.getString(2);
        this.dtend         = cursor.getString(3);
        this.title         = cursor.getString(4);
        this.description   = cursor.getString(5);
        this.allDay        = cursor.getString(6);
        this.eventLocation = cursor.getString(7);

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.calendar = Calendar.getInstance();
        this.calendar.setTimeInMillis(Long.parseLong(dstart));
        this.date = new Date();
        this.date.setTime(Long.parseLong(dstart));
        this.eventDate = displayDateFormat.format(calendar.getTime());

        this.year  = this.calendar.get(Calendar.YEAR);
        this.month = this.calendar.get(Calendar.MONTH);
        this.day   = this.calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String getId() {       return id;    }
    public void setId(String id) {        this.id = id;    }
    public String getCalendar_id() {        return calendar_id;    }
    public void setCalendar_id(String calendar_id) {        this.calendar_id = calendar_id;    }
    public String getDstart() {        return dstart;    }
    public void setDstart(String dstart) {        this.dstart = dstart;    }
    public String getDtend() {        return dtend;    }
    public void setDtend(String dtend) {        this.dtend = dtend;    }
    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }
    public String getDescription() {        return description;    }
    public void setDescription(String description) {        this.description = description;    }
    public String getAllDay() {        return allDay;    }
    public void setAllDay(String allDay) {        this.allDay = allDay;    }
    public String getEventLocation() {        return eventLocation;    }
    public void setEventLocation(String eventLocation) {        this.eventLocation = eventLocation;    }
    public String getEventDate() {        return eventDate;    }
    public void setEventDate(String eventDate) {        this.eventDate = eventDate;    }
    public int getMonth() {        return month;    }
    public void setMonth(int month) {        this.month = month;    }
    public int getYear() {        return year;    }
    public void setYear(int year) {        this.year = year;    }
    public int getDay() {        return day;    }
    public void setDay(int day) {        this.day = day;    }
    public Calendar getCalendar() {        return calendar;    }
    public void setCalendar(Calendar calendar) {        this.calendar = calendar;    }
    public Date getDate() {       return date;    }
    public void setDate(Date date) {        this.date = date;    }
    public MyAdapterCalendarEvents.MyViewHolder getMyViewHolderCalendarEvents() {        return myViewHolderCalendarEvents;    }
    public void setMyViewHolderCalendarEvents(MyAdapterCalendarEvents.MyViewHolder myViewHolderCalendarEvents) {        this.myViewHolderCalendarEvents = myViewHolderCalendarEvents;    }

}

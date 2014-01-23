package edu.drexel.Examiner.math;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegressionInfo {
    private Double m;
    private Double y;
    private String curr_date_time;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public RegressionInfo() {
    }

    public RegressionInfo(Double mVal, Double yVal) {
        m = mVal;
        y = yVal;

        Calendar cal = Calendar.getInstance();

        curr_date_time = dateFormat.format(cal.getTime()).toString();
    }

    public Double getY() {
        return y;
    }

    public void setY(Double yVal) {
        y = yVal;
    }

    public Double getM() {
        return m;
    }

    public void setM(Double mVal) {
        y = mVal;
    }

    public String getDateTime() {
        return curr_date_time;
    }

    public void setDateTime(String dateVal) {
        curr_date_time = dateVal;
    }
}
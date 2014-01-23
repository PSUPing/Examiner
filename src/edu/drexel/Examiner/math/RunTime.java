package edu.drexel.Examiner.math;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RunTime {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private String last_run;

    public RunTime(String lastRun) {
        last_run = lastRun;
    }

    public RunTime(Date lastRun) {
        last_run = dateFormat.format(lastRun);
    }

    public Date getRunTime() {
        try {
            return dateFormat.parse(last_run);
        }
        catch (ParseException parseEx) {
            System.out.println(parseEx.getMessage().toString());
        }

        return null;
    }

    public void updateRunTime(String dateTime) {
        last_run = dateTime;
    }
}

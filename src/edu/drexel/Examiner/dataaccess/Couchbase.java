package edu.drexel.Examiner.dataaccess;

import edu.drexel.Examiner.math.RegressionInfo;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.protocol.views.*;
import com.google.gson.Gson;
import edu.drexel.Examiner.math.RunTime;
import net.spy.memcached.internal.OperationFuture;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Couchbase {
    private CouchbaseClient client = null;
    private ArrayList<URI> nodes = new ArrayList<URI>();

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public Couchbase() {
        System.setProperty("viewmode", "development"); // before the connection to Couchbase

        try {
            nodes.add(URI.create("http://127.0.0.1:8091/pools"));
            client = new CouchbaseClient(nodes, "examiner", "");
        }
        catch(IOException ioEx) {
            System.out.println(ioEx.getMessage().toString());
            System.exit(1);
        }
    }

    public void storeRegressionData(RegressionInfo regInfo) {
        Gson gson = new Gson();

        try {
            UUID randID = UUID.randomUUID();

            client.set(randID.toString(), gson.toJson(regInfo)).get();
        }
        catch (InterruptedException intEx) {
            System.out.println(intEx.getMessage().toString());
            System.exit(1);
        }
        catch (ExecutionException exeEx) {
            System.out.println(exeEx.getMessage().toString());
            System.exit(1);
        }
    }

    public void updateLastRun(RunTime runTime) {
        Gson gson = new Gson();

        try {
            client.set("1", gson.toJson(runTime)).get();
        }
        catch (InterruptedException intEx) {
            System.out.println(intEx.getMessage().toString());
            System.exit(1);
        }
        catch (ExecutionException exeEx) {
            System.out.println(exeEx.getMessage().toString());
            System.exit(1);
        }
    }

    public double[] getSingleStat(String statName, Date currDate) {
        return listToDblArray(getStat(statName, currDate));
    }

    public double[] getProcStat(Date currDate) {
        return listToDblArray(getStat("proc_count", currDate));
    }

    public double[] getRunProcStat(Date currDate) {
        return listToDblArray(getStat("run_proc_count", currDate));
    }

    public double[] getThreadCount(Date currDate) {
        return listToDblArray(getStat("thread_count", currDate));
    }

    public double[] getVMMemory(Date currDate) {
        return listToDblArray(getStat("vm_memory", currDate));
    }

    public void getRunName() {
        try {
            View view = client.getView("get", "get_run");
            Query query = new Query();
            query.setIncludeDocs(true).setLimit(20);
            query.setStale(Stale.FALSE);
            ViewResponse result = client.query(view, query);

            for (ViewRow row : result) {
                System.out.println(row.getDocument().toString());
                break;
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage().toString());
            System.exit(1);
        }
    }

    public RunTime getLastRun() {
        RunTime runTime = null;

        try {
            View view = client.getView("get", "get_last_run");
            Query query = new Query();
            query.setIncludeDocs(true).setLimit(20);
            query.setStale(Stale.FALSE);
            ViewResponse result = client.query(view, query);

            for (ViewRow row : result) {
                String dateToParse = row.getDocument().toString();
                dateToParse = dateToParse.substring(1, dateToParse.length()).substring(0, dateToParse.length() - 2);

                String[] splitDate = dateToParse.split("\":\"");
                dateToParse = splitDate[1].substring(0, splitDate[1].length() - 1);

                runTime = new RunTime(dateToParse);
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage().toString());
            System.exit(1);
        }

        return runTime;
    }

    public List<Double> getStat(String statIndex, Date currDate) {
        List<Double> retList = new ArrayList<Double>();

        try {
            View view = client.getView("get", "get_thread_vm");
            Query query = new Query();
            query.setIncludeDocs(true).setLimit(20);
            query.setStale(Stale.FALSE);
            ViewResponse result = client.query(view, query);

            for (ViewRow row : result) {
                String wrkString = row.getDocument().toString();
                wrkString = wrkString.substring(1, wrkString.length()).substring(0, wrkString.length() - 2);

                String[] statVals = wrkString.split(",");
                String dblToParse = null;
                String dateToParse = null;

                for(String vals : statVals) {
                    String[] pairs = vals.split(":");
                    String key = pairs[0].substring(1, pairs[0].length()).substring(0, pairs[0].length() - 2);

                    if (key.equals(statIndex))
                        dblToParse = pairs[1].substring(1, pairs[1].length()).substring(0, pairs[1].length() - 2);

                    if (key.equals("date_time")) {
                        String[] dateParts = vals.split("\":\"");
                        dateToParse = dateParts[1].substring(0, dateParts[1].length() - 1);
                    }
                }

                Date dateToCheck = dateFormat.parse(dateToParse);

                if (currDate.compareTo(dateToCheck) < 0)
                    retList.add(Double.parseDouble(dblToParse));
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage().toString());
            System.exit(1);
        }

        return retList;
    }

    private double[] listToDblArray(List<Double> doubleList) {
        double[] retVal = new double[doubleList.size()];
        int retValIdx = 0;

        for (Double procVal : doubleList) {
            retVal[retValIdx] = procVal;
            retValIdx++;
        }

        return retVal;
    }

    public void destroyCouch() {
        client.shutdown();
    }
}

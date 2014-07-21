package edu.drexel.Examiner;

import edu.drexel.Examiner.dataaccess.Couchbase;
import edu.drexel.Examiner.math.LinearRegression;
import edu.drexel.Examiner.math.RegressionInfo;
import edu.drexel.Examiner.math.RunTime;

public class Main {
    public static void main(String[] args) {
        Couchbase cb = new Couchbase();
        LinearRegression reg = new LinearRegression();

        // TODO: Modify to support grabbing only current requests and then shift to double arrays

/*        RunTime runTime = cb.getLastRun();
        double[] threadStat = cb.getThreadCount(runTime.getRunTime());
        double[] vmMemStat = cb.getVMMemory(runTime.getRunTime());
        RegressionInfo regInfo = reg.runRegression(threadStat, vmMemStat);

        cb.storeRegressionData(regInfo);
        cb.updateLastRun(new RunTime(regInfo.getDateTime()));*/

        cb.getRunName();
        cb.destroyCouch();
    }
}
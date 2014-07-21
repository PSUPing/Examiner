package edu.drexel.Examiner.math;

import dk.ange.octave.*;
import dk.ange.octave.type.OctaveDouble;

public class LinearRegression {
    private OctaveEngine octave;

    public LinearRegression() {
    }

    public RegressionInfo runRegression(double[] x, double[] y) {
        Double octM = null;
        Double octY = null;

        try {
            OctaveDouble octList1 = new OctaveDouble(x, x.length, 1);
            OctaveDouble octList2 = new OctaveDouble(y, y.length, 1);

            if (octave == null)
                octave = new OctaveEngineFactory().getScriptEngine();

            octave.put("x", octList1);
            octave.put("y", octList2);
            octave.eval("m = length(x);");
            octave.eval("X = [ones(m, 1) x];");
            octave.eval("theta = (pinv(X' * X)) * X' * y;");
            octave.eval("dist = y - (theta'(:,2) * x + theta'(:,1));");

            octM = octave.get(OctaveDouble.class, "theta").get(2);
            octY = octave.get(OctaveDouble.class, "theta").get(1);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage().toString());
            System.exit(1);
        }
        finally {
            octave.close();
        }

        return new RegressionInfo(octM, octY);
    }
}

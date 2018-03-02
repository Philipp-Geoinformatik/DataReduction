package de.grashorn.model.de.grashorn.model.entities;

import de.grashorn.model.TimedValue;

public class GraphColumn {
    int count;
    public TimedValue min;
    public TimedValue max;


    public GraphColumn() {
//        this.min = new TimedValue();
//        this.max = new

    }

    public void addValue(TimedValue tv) {
        count++;
        if (count == 1) {
            max = tv;
        } else if (count == 2) {
            if (tv.value > max.value) {
                TimedValue temp;
                temp = max;
                max = tv;
                min = temp;
            } else {
                min = tv;
            }
        } else if (count > 2) {
            if (tv.value > max.value)
                max = tv;
            else if (tv.value < min.value)
                min = tv;
        }
    }
}

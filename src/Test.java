import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Test extends Application {
    public class GraphColumn {
        int count;
        public TimedValue min;
        public TimedValue max;

        public void addValue(TimedValue v) {
            count++;
            if (count == 1) {
                //erster Wert der interessant ist
                max = v;
            } else if (count == 2) {
                min = v;
                // zweiter Wert der interessant ist
            } else if (count == 3) {
                //austauschen!
                if (v.value > max.value)
                    max = v;
                if (v.value < min.value)
                    min = v;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(1920, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);


        Test t = new Test();
        LinkedList<TimedValue> list = t.createVals(20);
        LinkedList<TimedValue> small = t.reduce(list, 2920);

        drawData(small, gc);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    private void drawData(LinkedList<TimedValue> values, GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        int height_offset = (int) gc.getCanvas().getHeight() / 2;
        double scaleY = 100;
        double scaleX = gc.getCanvas().getWidth() / values.size();
        for (int i = 0; i < values.size() - 1; i++) {
            gc.setStroke(new Color(0, 0, 0, 1));
            gc.setLineWidth(1);
            gc.strokeLine(values.get(i).time * scaleX, values.get(i).value * scaleY + height_offset, values.get(i + 1).time * scaleX, values.get(i + 1).value * scaleY + height_offset);
            if (values.size() <30) {
                gc.setFill(new Color(.5,0,0,0.9));
                gc.fillOval(values.get(i).time * scaleX, (values.get(i).value * scaleY + height_offset), 5, 5);
                gc.fillText(" " + i + " (" + values.get(i).time + " | " + (values.get(i).value + ")"), values.get(i).time * scaleX, values.get(i).value * scaleY + height_offset);
            }
        }
        int i = values.size() - 1;
        gc.fillText(" " + i + " (" + values.get(i).time + " | " + (values.get(i).value + ")"), values.get(i).time * scaleX, values.get(i).value * scaleY + height_offset);


    }


    public class TimedValue {
        public TimedValue(Long time, Double value) {
            this.time = time;
            this.value = value;
        }

        public Long time;
        public Double value;
    }

    public LinkedList<TimedValue> createVals(int values) {
        LinkedList<TimedValue> vals = new LinkedList<>();
        int i = 0;
        while (i < values) {
            vals.add(new TimedValue((long) i, Math.sin(i * 200.51)));
            i++;
        }
        return vals;
    }

    public LinkedList<TimedValue> reduce(List<TimedValue> list, int windowWidth) {
        LinkedList<TimedValue> finalList = new LinkedList<>();
        //IntervalToCheckForColum
        int interval = list.size() / windowWidth;
        if (list.size() / windowWidth <= 2) {
            interval = 2;
        }
        int counter = 0;
        GraphColumn col = new GraphColumn();
        for (TimedValue v : list) {
            col.addValue(v);//adding mins and max as long the interval is open
            counter++;
            if (counter >= interval) {//then add minimal min and maximal max
                finalList.add(new TimedValue(col.max.time, col.max.value));
                finalList.add(new TimedValue(col.min.time, col.min.value));
                counter = 0;
                col = new GraphColumn();
            }
        }
        return finalList;
    }

    public static void main(String[] args) {
        launch(args);

    }

}
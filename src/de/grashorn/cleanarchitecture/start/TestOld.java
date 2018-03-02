package de.grashorn.cleanarchitecture.start;

import de.grashorn.cleanarchitecture.model.entities.GraphColumn;
import de.grashorn.cleanarchitecture.model.entities.TimedValue;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestOld extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Pane root = new Pane();
        int paneWidth = 500;
        int paneHeight = 500;
        Canvas canvas = new Canvas(paneWidth, paneHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        //
        ArrayList<TimedValue> list = createSinus(7_000_000, 0.00001, 200);
        Instant before =Instant.now();
        ArrayList<TimedValue> small = reduce(list, paneWidth);
        Instant after = Instant.now();
        System.out.println("Reduce data needs: " + Duration.between(before,after));
        //
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        //
        before =Instant.now();
        root.heightProperty().addListener(l -> drawData(small, gc));
        canvas.widthProperty().addListener(l -> drawData(small, gc));
         after = Instant.now();
        System.out.println("Painting data needs: " + Duration.between(before,after));
        //
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    private void drawData(ArrayList<TimedValue> values, GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeLine(0, gc.getCanvas().getHeight() / 2, gc.getCanvas().getWidth(), gc.getCanvas().getHeight() / 2);
        double scaleX = gc.getCanvas().getWidth() / (values.size());
        double offset_y = gc.getCanvas().getHeight() / 2;
        gc.fillText("(" + values.size() + " points)", gc.getCanvas().getWidth() / 2, gc.getCanvas().getHeight() / 2);
        for (int i = 0; i < values.size() - 1; i++) {
            TimedValue tv1 = values.get(i);
            TimedValue tv2 = values.get(i + 1);
            gc.strokeLine(i * scaleX, tv1.value + offset_y, (i + 1) * scaleX, tv2.value + offset_y);
            gc.fillOval(i * scaleX, tv1.value + offset_y, 4, 4);
            if (values.size() <= 50)
                gc.fillText("( " + tv1.value + " )", i * scaleX, tv1.value + offset_y);
        }

    }

    public ArrayList<TimedValue> createSinus(int amount, double scaler, double factor) {
        ArrayList<TimedValue> vals = new ArrayList<>();
        int i = 0;
        while (i < amount) {
            vals.add(new TimedValue((long) i, Math.sin((double) i * scaler * Math.PI) * factor));
            i++;
        }
        return vals;

    }


    public LinkedList<TimedValue> createVals(int values) {
        LinkedList<TimedValue> vals = new LinkedList<>();
        int i = 0;
        while (i < values) {
            vals.add(new TimedValue((long) i, Math.sin(i * 200)));
            i++;
        }
        return vals;
    }

    /**
     * @param list
     * @param destSize
     * @return
     */
    public ArrayList<TimedValue> reduce(List<TimedValue> list, int destSize) {
        ArrayList<TimedValue> finalList = new ArrayList<>();
        //IntervalToCheckForColum
        int startSize = list.size();
        int PpCol = startSize / destSize;//points to check for each column
        //for size Columns. Get MIN and MAX in the current column
        int counter = 0;
        GraphColumn col = new GraphColumn();
        if (list.size() > destSize) {
            for (int i = 0; i < list.size(); i++) {
                col.addValue(list.get(i));
                counter++;
                // when the amount of potential values for the destinated pixel are checked
                if (counter == PpCol) {
                    if (PpCol > 1) {
                        TimedValue min = new TimedValue(col.min.time, col.min.value);
                        finalList.add(min);
                    }
                    TimedValue max = new TimedValue(col.max.time, col.max.value);
                    finalList.add(max);
                    col = new GraphColumn();
                    counter = 0;
                }
            }
        } else {
            finalList.addAll(list);
        }
        return finalList;
    }

    public static void main(String[] args) {
        launch(args);

    }
}

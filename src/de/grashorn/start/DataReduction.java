package de.grashorn.start;

import de.grashorn.model.TimedValue;
import de.grashorn.model.de.grashorn.model.entities.GraphColumn;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author Philipp Grashorn
 */
public class DataReduction extends Application {

    /**
     * Starts the {@link Application}
     *
     * @param primaryStage the {@link Stage} of this simple {@link Application}
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Pane root = new Pane();
        int paneWidth = 500;
        int paneHeight = 500;
        Canvas canvas = new Canvas(paneWidth, paneHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        //
        ArrayList<TimedValue> list = createSinus(1_000_000, 0.00001, 200);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        //
        root.heightProperty().addListener(l -> {
            ArrayList<TimedValue> small2 = reduce(list, (int) root.widthProperty().get());
            drawData(small2, gc);
        });
        canvas.widthProperty().addListener(l -> {
            ArrayList<TimedValue> small3 = reduce(list, (int) root.widthProperty().get());
            drawData(small3, gc);
        });
        //
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    /**
     * Draws the {@link TimedValue}s by the deliverd {@link GraphicsContext} @param values
     *
     * @param values the data to draw by the {@link GraphicsContext}
     * @param gc     the {@link GraphicsContext}
     */
    private void drawData(ArrayList<TimedValue> values, GraphicsContext gc) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setStroke(Color.GREY);
        gc.setLineWidth(1);
        gc.strokeLine(0, gc.getCanvas().getHeight() / 2, gc.getCanvas().getWidth(), gc.getCanvas().getHeight() / 2);
        double scaleX = gc.getCanvas().getWidth() / (values.size());
        double offset_y = gc.getCanvas().getHeight() / 2;
        gc.fillText("(" + values.size() + " points)", gc.getCanvas().getWidth() / 2, gc.getCanvas().getHeight() / 2);

        for (int i = 0; i < values.size() - 1; i++) {
            TimedValue tv1 = values.get(i);
            TimedValue tv2 = values.get(i + 1);
            gc.strokeLine(i * scaleX, tv1.value + offset_y, (i + 1) * scaleX, tv2.value + offset_y);
            gc.setFill(Color.RED);
            //gc.fillOval(i * scaleX, tv1.value + offset_y, 4.2, 4.2);
        }
    }

    /**
     * This method creates an {@link ArrayList<TimedValue>} which represent values of the configured sinus function.
     *
     * @param amount the amount of generated values
     * @param scaler the scale 1 = 1 * PI
     * @param factor the amplitude of the sinus function
     * @return an {@link ArrayList<TimedValue>} which represent the sinus function.
     */
    private ArrayList<TimedValue> createSinus(int amount, double scaler, double factor) {
        Instant before = Instant.now();
        //
        ArrayList<TimedValue> vals = new ArrayList<>();
        int i = 0;
        while (i < amount) {
            vals.add(new TimedValue((long) i, Math.sin((double) i * scaler * Math.PI) * factor));
            i++;
        }
        Instant after = Instant.now();
        System.out.println("Creation needs: " + Duration.between(before, after));
        return vals;
    }


    /**
     * @param values
     * @return
     */
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
        Instant before = Instant.now();
        //
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
        Instant after = Instant.now();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        System.out.println("Reduction of " + formatter.format(list.size()) + " points: " + Duration.between(before, after));
        return finalList;
    }

    public static void main(String[] args) {
        launch(args);

    }
}

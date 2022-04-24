package RAF.KiDSDomaci1.model.output;

import RAF.KiDSDomaci1.app.App;
import RAF.KiDSDomaci1.model.cruncher.CruncherToOutput;
import RAF.KiDSDomaci1.view.MainView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.concurrent.*;

public class CacheOutput extends Task<String> implements Runnable{

    private final BlockingQueue<CruncherToOutput> outputQueue = new LinkedBlockingQueue<>();
    private final Map<String, Future<Map<String, Integer>>> mapOutput = new ConcurrentHashMap<>();
    private static final ArrayList<XYChart.Series<Number, Number>> charts = new ArrayList<>();
    private static final Map<XYChart.Series<Number, Number>, Future<Map<String, Integer>>> mapsCharts = new LinkedHashMap<>();
    public CacheOutput() {
        App.getOutputPool().execute(this);
    }

    @Override
    public void run() {
        try {
            while (true) {
                CruncherToOutput cruncherToOutput = outputQueue.take();
                mapOutput.put(cruncherToOutput.getName(), cruncherToOutput.getData());

                String name = cruncherToOutput.getName();
                Future<Map<String, Integer>> map = cruncherToOutput.getData();
                String starName = "*" + name;
                XYChart.Series<Number, Number> chart = new XYChart.Series<>();
                chart.setName(name);

                System.out.println("Output: " + cruncherToOutput.getName() + " " + Thread.currentThread().getName());
                Platform.runLater(() -> MainView.getResults().getItems().add(starName));

                App.getOutputPool().execute(() -> {
                    int i = 0;
                    try {
                        for (Map.Entry<String, Integer> element : map.get().entrySet()) {
                            if (i == 100) {
                                break;
                            }
                            i++;
                            XYChart.Data<Number, Number> data = new XYChart.Data<>();
                            data.setXValue(i);
                            data.setYValue(element.getValue());
                            chart.getData().add(data);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    mapsCharts.put(chart, map);
                    charts.add(chart);

                    Platform.runLater(() -> MainView.getResults().getItems().remove(starName));
                    Platform.runLater(() -> MainView.getResults().getItems().add(name));
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            App.endAll();
        }
    }

    public void addToOutputQueue(CruncherToOutput cruncherToOutput) throws InterruptedException {
        this.outputQueue.put(cruncherToOutput);
    }

    public static XYChart.Series<Number, Number> getCharts(String s) {
        XYChart.Series<Number, Number> chrt = new XYChart.Series<>();
        for(XYChart.Series<Number, Number> chart : charts){
            String nme = "[" + chart.getName() + "]";
            if(nme.equals(s)){
                chrt = chart;
            }
        }
        return chrt;
    }

    public static XYChart.Series<Number, Number> sumCharts(String s, String sumName) {
        List<XYChart.Series<Number, Number>> list = new ArrayList<>();
        XYChart.Series<Number, Number> sumChart = new XYChart.Series<>();
        sumChart.setName(sumName);
        Map<String, Integer> map = new LinkedHashMap<>();
        int i = 0;
        String[] ss = s.split("[\\s,]+");
        for(int j = 0; j < ss.length; j++){
            if(ss[j].startsWith("[")){
                ss[j] += "]";
            } else if(ss[j].endsWith("]")){
                ss[j] = "[" + ss[j];
            } else {
                ss[j] = "[" + ss[j] + "]";
            }
        }
        for (XYChart.Series<Number, Number> chart : charts) {
            for(String sss : ss) {
                String nme = "[" + chart.getName() + "]";
                if (nme.equals(sss)) {
                    list.add(chart);
                }
            }
        }
        System.out.println("List: " + list);
        for (XYChart.Series<Number, Number> ch : list) {
            Map<String, Integer> map2 = new LinkedHashMap<>();

            try {
                map2 = mapsCharts.get(ch).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            map2.forEach((k, v) -> map.merge(k, v, (v1, v2) -> v1 + v2));
        }

        for (Map.Entry<String, Integer> element : map.entrySet()) {
            if (i == 100) {
                break;
            }
            i++;
            XYChart.Data<Number, Number> data = new XYChart.Data<>();
            data.setXValue(i);
            data.setYValue(element.getValue());
            sumChart.getData().add(data);
        }
        charts.add(sumChart);
        //mapsCharts.put(sumChart, (Future<Map<String, Integer>>) map);
        return sumChart;
    }

    @Override
    protected String call(){
        return null;
    }
}

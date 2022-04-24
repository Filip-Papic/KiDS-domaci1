package RAF.KiDSDomaci1.model.input;

import RAF.KiDSDomaci1.app.App;
import RAF.KiDSDomaci1.model.Disk;
import RAF.KiDSDomaci1.model.cruncher.CounterCruncher;
import javafx.concurrent.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class FileInput extends Task<String> implements Runnable {
    private final Disk disk;
    private final String name;
    private List<File> txtFiles = Collections.synchronizedList(new ArrayList<>());
    private final List<String> dirs = Collections.synchronizedList(new ArrayList<>());
    private final List<CounterCruncher> counterCrunchers = Collections.synchronizedList(new ArrayList<>());

    public FileInput(Disk disk) {
        AtomicInteger i = new AtomicInteger(0);
        this.name = String.valueOf(i);
        i.incrementAndGet();
        this.disk = disk;
    }

    @Override
    public void run() {
         for (String dir : dirs) {
            File file = new File(dir);
            txtFiles = findTxt(file, txtFiles);
            this.disk.getDiskQueue().addAll(txtFiles);
            while (true){
                try {
                    File f = this.disk.getDiskQueue().take();
                    updateMessage("Reading: " + f.getName());

                    System.out.println("Reading: " + f.getName() + " " + Thread.currentThread().getName());

                    Future<String> result = App.getInputPool().submit(new ReadTask(f));
                    String text = result.get();

                    InputToCruncher inputToCruncher = new InputToCruncher(f.getName(), f.getPath(), text);

                    for (CounterCruncher cc : FileInput.this.counterCrunchers) {
                        cc.addToCruncherQueue(inputToCruncher);
                    }
                    updateMessage("Idle");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    App.endAll();
                }
            }
         }
    }

    private List<File> findTxt(File file, List<File> txtFiles){
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    txtFiles.add(f);
                } else {
                    txtFiles = findTxt(f, txtFiles);
                }
            }
        }
        return txtFiles;
    }

    public void startFileInput(){
        App.getInputPool().execute(this);
    }

    public void addDirs(String dir){
        dirs.add(dir);
    }

    public void addCruncher(CounterCruncher counterCruncher){
        counterCrunchers.add(counterCruncher);
    }

    public void removeCruncher(CounterCruncher counterCruncher){
        counterCrunchers.remove(counterCruncher);
    }

    public Disk getDisk() {
        return disk;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected String call() {
        return null;
    }
}

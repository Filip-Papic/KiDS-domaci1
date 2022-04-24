package RAF.KiDSDomaci1.model.cruncher;

import RAF.KiDSDomaci1.app.App;
import RAF.KiDSDomaci1.model.input.InputToCruncher;
import RAF.KiDSDomaci1.model.output.CacheOutput;
import RAF.KiDSDomaci1.view.CruncherView;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterCruncher extends Task<String> implements Runnable{

	public static AtomicInteger cruncherNumber = new AtomicInteger(0);
	private final int arity;
	private final String name;
	private final BlockingQueue<InputToCruncher> cruncherQueue = new LinkedBlockingQueue<>();
	private final CacheOutput cacheOutput = new CacheOutput();

	public CounterCruncher(int arity) {
		this.arity = arity;
		this.name = "Counter " + cruncherNumber;
		cruncherNumber.incrementAndGet();
		App.getCruncherPool().execute(this);
	}

	@Override
	public void run() {
		try {
			while (true) {
				InputToCruncher inputToCruncher = cruncherQueue.take();
				String name = inputToCruncher.getName();
				String data = inputToCruncher.getData();
				int size = inputToCruncher.getData().length();
				String nameArity = name + "-arity" + arity;

				Platform.runLater(() -> CruncherView.getStatus3().setText(CruncherView.getStatus3().getText() + "\n" + name));
				System.out.println("Crunching: " + inputToCruncher.getName() + " " + Thread.currentThread().getName());

				Future<Map<String, Integer>> result = App.getCruncherPool().submit(new CountTask(arity, data, nameArity, 0, size));

				CruncherToOutput cruncherToOutput = new CruncherToOutput(result, nameArity);
				cacheOutput.addToOutputQueue(cruncherToOutput);

				App.getCruncherPool().execute(() -> {
					try {
						result.get();
						Platform.runLater(() -> CruncherView.getStatus3().setText(CruncherView.getStatus3().getText().replace("\n" + name, "")));
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				});
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			App.endAll();
		}
	}

	public void addToCruncherQueue(InputToCruncher inputToCruncher) throws InterruptedException {
		this.cruncherQueue.put(inputToCruncher);
	}

	@Override
	public String toString() {
		return name;
	}
	
	public int getArity() {
		return arity;
	}

	@Override
	protected String call() {
		return null;
	}
}

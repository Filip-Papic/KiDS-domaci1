package RAF.KiDSDomaci1.app;

import RAF.KiDSDomaci1.view.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;


/**
 * JavaFX App
 */
public class App extends Application {
	private static final ExecutorService inputPool = Executors.newCachedThreadPool();//newFixedThreadPool(2); deadlock?
	private static final ForkJoinPool cruncherPool = new ForkJoinPool();
	private static final ExecutorService outputPool = Executors.newCachedThreadPool();

	@Override
    public void start(Stage stage) {
    	BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 1300, 800);
		MainView mainView = new MainView();
		mainView.initMainView(root, stage);
		stage.setScene(scene);
		stage.show();
    }

	public static void endAll(){
		inputPool.shutdown();
		cruncherPool.shutdown();
		outputPool.shutdown();

		Platform.runLater(() -> {
					MainView.memoryError();
					Platform.exit();
					System.exit(0);
		});
	}

	public static void closeWindow(){
		inputPool.shutdown();
		cruncherPool.shutdown();
		outputPool.shutdown();

		Platform.runLater(() -> {
			Platform.exit();
			System.exit(0);
		});
	}

	public static ExecutorService getInputPool() {
		return inputPool;
	}

	public static ExecutorService getOutputPool() {
		return outputPool;
	}

	public static ForkJoinPool getCruncherPool() {
		return cruncherPool;
	}
}
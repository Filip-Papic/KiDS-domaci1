package RAF.KiDSDomaci1.view;

import RAF.KiDSDomaci1.model.cruncher.CounterCruncher;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CruncherView {

	private final MainView mainView;
	private final CounterCruncher cruncher;
	private static Label status3;
	private final Pane main;

	public CruncherView(MainView mainView, CounterCruncher cruncher) {
		this.mainView = mainView;
		this.cruncher = cruncher;
		
		main = new VBox();

		Text text = new Text("Name: " + cruncher.toString());
		main.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 2, 0));

		text = new Text("Arity: " + cruncher.getArity());
		main.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 5, 0));

		Button remove = new Button("Remove cruncher");
		remove.setOnAction(e -> removeCruncher());
		main.getChildren().add(remove);
		VBox.setMargin(remove, new Insets(0, 0, 5, 0));

		status3 = new Label();
		status3.setText("Crunching:");
		main.getChildren().add(status3);

		VBox.setMargin(main, new Insets(0, 0, 15, 0));
	}

	public Pane getCruncherView() {
		return main;
	}

	private void removeCruncher() {
		mainView.removeCruncher(this);
	}

	public CounterCruncher getCruncher() {
		return cruncher;
	}

	public static Label getStatus3() {
		return status3;
	}
}

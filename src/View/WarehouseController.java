package View;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Actor;
import model.ChargingPod;
import model.Floor;
import model.Location;
import model.LocationNotValidException;
import model.Order;
import model.PackingStation;
import model.Robot;
import model.StorageShelf;
import simulation.SimFileFormatException;
import simulation.Simulator;

public class WarehouseController {

	@FXML
	private TextArea txtRows;
	@FXML
	private TextArea txtCol;
	@FXML
	private Pane paneWarehouse;
	@FXML
	private GridPane grdWarehouse;
	@FXML
	private Slider sldCapacity;
	@FXML
	private Label lblCapacity;
	@FXML
	private Label lblCharge;
	@FXML
	private Slider sldCharge;
	@FXML
	private Label lblCount;
	private Simulator sim;
	@FXML
	private Label lblRows;
	@FXML
	private Label lblCol;
	@FXML
	ListView<Robot> robotsList;
	@FXML
	ListView<Order> unassignedOrders;
	@FXML
	ListView<Order> assignedOrders;
	@FXML
	ListView<Order> dispatchedOrders;
	private ArrayList<Robot> robots;
	@FXML
	private Button btnUploadFile;
	private Path filePath;
	@FXML
	private Label lblFile;
	private int rows;
	private int columns;

	/**
	 * initialize the simulation, add listeners to the sliders and text areas.
	 */
	@FXML
	public void initialize() {

		robots = new ArrayList<Robot>();

		txtRows.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					txtRows.setText("5");
				}
				rows = Integer.parseInt(txtRows.getText());
			}
		});

		txtCol.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					txtCol.setText("5");
				}
				columns = Integer.parseInt(txtCol.getText());
			}
		});

		sldCapacity.valueProperty().addListener((observable, oldValue, newValue) -> {
			lblCapacity.setText("Battery Capacity: " + Integer.toString(newValue.intValue()));
			for (Robot r : robots) {
				r.setPowerUnits(newValue.intValue());
			}
			sim.setMaxChargeCapacity(newValue.intValue());

		});

		sldCharge.valueProperty().addListener((observable, oldValue, newValue) -> {
			lblCharge.setText("Charge speed:" + Integer.toString(newValue.intValue()));
			sim.setChargeSpeed(newValue.intValue());
		});


		btnUploadFile.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("Uploader.fxml"));
				Stage stage = new Stage();
				stage.setTitle("FileChooser");
				// create a File chooser
				FileChooser fil_chooser = new FileChooser();
				// create a Label
				Label label = new Label("no files selected");
				// create a Button
				Button button = new Button("Select File");

				// create an Event Handler
				EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {

					public void handle(ActionEvent e) {
						// get the file selected
						File file = fil_chooser.showOpenDialog(stage);

						if (file != null) {

							label.setText(file.getAbsolutePath());
							lblFile.setText("File: " + file.getAbsolutePath() + "  selected");

						}
					}
				};
				button.setOnAction(event1);

				Button btnConfirm = new Button("Confirm");

				EventHandler<ActionEvent> confirm = new EventHandler<ActionEvent>() {

					public void handle(ActionEvent e) {
						filePath = Paths.get(label.getText());
						Stage stage = (Stage) btnConfirm.getScene().getWindow();
						stage.close();
						try {
							loadSimulation();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SimFileFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (LocationNotValidException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				};

				btnConfirm.setOnAction(confirm);

				// create a VBox
				VBox vbox = new VBox(30, label, button, btnConfirm);

				// set Alignment
				vbox.setAlignment(Pos.CENTER);
				Scene scene = new Scene(vbox, 800, 500);
				stage.setScene(scene);
				stage.show();

			}
		});


	}

	/**
	 * Resets the simulation back to it's default settings
	 */
	@FXML
	public void reset() {

		sldCapacity.setValue(10.0);
		txtRows.setText("0");
		txtCol.setText("0");
		sldCharge.setValue(1.0);
		lblCount.setText("Total tick count: 0");
		for (int i = sim.getFloor().getNumberOfRows() - 1; i >= 0; i--) {
			grdWarehouse.getRowConstraints().remove(i);
		}
		for (int i = sim.getFloor().getNumberOfColumns() - 1; i >= 0; i--) {
			grdWarehouse.getColumnConstraints().remove(i);
		}
		grdWarehouse.getChildren().clear();
		sim.resetSimulator();

	}

	/**
	 * 
	 * Run simulation which is triggered by pressing the run button, this sets up
	 * the simulation and starts the run method in simulation. Creates a hash map of
	 * cells mapped by their coordinates
	 * 
	 * @throws Exception
	 */

	@FXML
	public void runOneTick() throws Exception {
		sim.tick();

		grdWarehouse.getChildren().removeIf(n -> n instanceof Circle);
		lblCount.setText("Total tick count: " + sim.getTotalTickCount());

		for (Robot robo : robots) {
			Location l = robo.getLocation();
			Circle r = new Circle();
			r.setRadius(15);
			r.setFill(Color.RED);
			GridPane.setConstraints(r, l.getColumn(), l.getRow());
			grdWarehouse.getChildren().add(r);
		}
		//robotsList.setItems(sim.robotsProperty());

		new Thread(() -> {
			IntStream.range(0, sim.robotsProperty().size()).forEach(i -> {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(sim.robotsProperty().get(i));
				Platform.runLater(() -> {
					// Where the magic happens.
					sim.robotsProperty().get(i);
					triggerUpdate(robotsList, sim.robotsProperty().get(i), i);
				});            
			});
		}).start();

	}

	private void runOneTickSaftely() {
		try {
			runOneTick();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		}
	}

	public void loadSimulation() throws IOException, SimFileFormatException, LocationNotValidException {

		System.out.println("Loading Simulation");

		if(filePath != null) {

			sim = Simulator.createFromFile(filePath);

			// sets the grid size to be the same as the floor in the file
			Floor f = sim.getFloor();
			for (int i = 0; i < f.getNumberOfRows(); i++) {
				RowConstraints rowConst = new RowConstraints();
				rowConst.setMinHeight(40);
				grdWarehouse.getRowConstraints().add(rowConst);
			}

			for (int i = 0; i < f.getNumberOfColumns(); i++) {
				ColumnConstraints column = new ColumnConstraints();
				column.setMinWidth(40);
				grdWarehouse.getColumnConstraints().add(column);
			}

			sldCapacity.valueProperty().setValue(sim.getMaxChargeCapacity());
			sldCharge.valueProperty().setValue(sim.getChargeSpeed());

			txtRows.setText(Integer.toString(f.getNumberOfRows()));
			txtCol.setText(Integer.toString(f.getNumberOfColumns()));


			List<Actor> actors = sim.getActors();

			for (Actor a : actors) {


				if (a instanceof Robot) {
					robots.add((Robot) a);
					Location l = ((Robot) a).getLocation();
					StackPane stk = new StackPane();
					GridPane.setConstraints(stk, l.getColumn(), l.getRow());
					grdWarehouse.getChildren().add(stk);
					Circle cp1 = new Circle();
					cp1.setFill(Color.MEDIUMORCHID);
					cp1.setRadius(25);
					stk.getChildren().add(cp1);
					Circle rb1 = new Circle();
					rb1.setFill(Color.RED);
					rb1.setRadius(15);
					stk.getChildren().add(rb1);

				}
				if (a instanceof PackingStation) {
					Location l = ((PackingStation) a).getLocation();
					StackPane stk = new StackPane();
					GridPane.setConstraints(stk, l.getColumn(), l.getRow());
					grdWarehouse.getChildren().add(stk);
					Rectangle ps1 = new Rectangle();
					ps1.setFill(Color.AQUAMARINE);
					ps1.setHeight(35);
					ps1.setWidth(35);
					stk.getChildren().add(ps1);
				}
				if (a instanceof StorageShelf) {
					Location l = ((StorageShelf) a).getLocation();
					StackPane stk = new StackPane();
					GridPane.setConstraints(stk, l.getColumn(), l.getRow());
					grdWarehouse.getChildren().add(stk);
					Rectangle ss1 = new Rectangle();
					ss1.setFill(Color.DARKSALMON);
					ss1.setHeight(35);
					ss1.setWidth(35);
					stk.getChildren().add(ss1);
				}
			}
		}

		else {

			Floor floor = new Floor(rows, columns);

			//sim = new Simulator(floor );
			for (int i = 0; i < rows; i++) {
				RowConstraints rowConst = new RowConstraints();
				rowConst.setMinHeight(40);
				grdWarehouse.getRowConstraints().add(rowConst);
			}

			for (int i = 0; i < columns; i++) {
				ColumnConstraints column = new ColumnConstraints();
				column.setMinWidth(40);
				grdWarehouse.getColumnConstraints().add(column);
			}
		}
		
		robotsList.setItems(sim.robotsProperty());
		unassignedOrders.setItems(sim.unassignedOrdersProperty());
		assignedOrders.setItems(sim.assignedOrdersProperty());
		dispatchedOrders.setItems(sim.dispatchedOrdersProperty());


	}

	public Simulator getSimulation() {
		return sim;
	}

	@FXML
	public void runTenTicks() throws Exception {
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), ea -> runOneTickSaftely()));
		timeline.setCycleCount(10);
		timeline.play();
	}

	@FXML
	public void runToEnd() throws Exception {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.25), ea -> {
			runOneTickSaftely();
			if (sim.isComplete())
				timeline.stop();
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}


	Node getChildByRowColumn(final GridPane gridPane, final int row, final int col) {

		for (final Node node : gridPane.getChildren()) {
			if (GridPane.getRowIndex(node) == null)
				continue; // ignore Group
			if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col)
				if (node instanceof Circle) {
					return node;
				}
		}
		return null;
	}

	/**
	 * Informs the ListView that one of its items has been modified.
	 *
	 * @param listView The ListView to trigger.
	 * @param newValue The new value of the list item that changed.
	 * @param i The index of the list item that changed.
	 */
	public static <T> void triggerUpdate(ListView<T> listView, T newValue, int i) {
		EventType<? extends ListView.EditEvent<T>> type = ListView.editCommitEvent();
		Event event = new ListView.EditEvent<>(listView, type, newValue, i);
		listView.fireEvent(event);
	}

}

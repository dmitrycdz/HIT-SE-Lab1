package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.lang.Math;

import basis.DirectedGraph;
import basis.GraphProcessor;
import basis.Vertex;
import javafx.beans.binding.DoubleBinding;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

public class BaseWindowController {
	@FXML private MenuBar menuBar;
	@FXML private MenuItem saveMenuItem;
	
	@FXML private Button textButton;
	@FXML private Button showButton;
	@FXML private Button queryButton;
	@FXML private Button generateButton;
	@FXML private Button pathButton;
	@FXML private Button walkButton;

	@FXML private ScrollPane canvasContainer;
	@FXML private AnchorPane canvasPane;
	@FXML private TextArea console;
	@FXML private StackPane stackPane;
	
	private DirectedGraph graph;
	private File dataFile;
	
	private static HashMap<String, PointBox> points = new HashMap<>();
	private static HashMap<Arrow, Pair<String, String>> edges = new HashMap<>();
	private static final double radius = 25;
	private static Vertex[][] path;
	private static int[][] distance;
	private static final int infinity = Integer.MAX_VALUE / 2;
	
	@FXML
	protected void handleOpenMenuItemClicked(ActionEvent e) {
		Stage stage = (Stage)menuBar.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		
		fileChooser.setTitle("打开文件");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("文本文档", "*.txt"),
				new FileChooser.ExtensionFilter("所有文件", "*.*"));
		File file = fileChooser.showOpenDialog(stage);
		dataFile = file;
		if (file != null) {
			try (Scanner scan = new Scanner(file)) {
				String content = scan.useDelimiter("\\Z").next();
				console.setText(content);
				graph = GraphProcessor.generateGraph(file.getAbsolutePath());
			} catch (FileNotFoundException err) {
				err.printStackTrace();
			}
		}
		if (graph != null) {
			textButton.setDisable(false);
			showButton.setDisable(false);
			queryButton.setDisable(false);
			generateButton.setDisable(false);
			pathButton.setDisable(false);
			walkButton.setDisable(false);
		}
	}
	
	@FXML
	protected void handleSaveMenuItemClicked(ActionEvent e) {
		Stage stage = (Stage)menuBar.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		
		fileChooser.setTitle("保存图片");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("PNG图像", "*.png"),
				new FileChooser.ExtensionFilter("JPG图像", "*.jpg"),
				new FileChooser.ExtensionFilter("GIF图像", "*.gif"));
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			String name = file.getName();
			String extendName = name.substring(name.lastIndexOf('.') + 1);
			WritableImage image = new WritableImage((int)canvasPane.getWidth(), (int)canvasPane.getHeight());
			try {
				switch(extendName) {
				case "png":
					SnapshotParameters spPNG = new SnapshotParameters();
				    spPNG.setFill(Color.WHITE);
					ImageIO.write(SwingFXUtils.fromFXImage(canvasPane.snapshot(spPNG, image), null), "png", file);
					break;
				case "jpg":
					SnapshotParameters spJPG = new SnapshotParameters();
				    spJPG.setFill(Color.WHITE);
					ImageIO.write(SwingFXUtils.fromFXImage(canvasPane.snapshot(spJPG, image), null), "jpg", file);
					break;
				case "gif":
					SnapshotParameters spGIF = new SnapshotParameters();
				    spGIF.setFill(Color.WHITE);
					ImageIO.write(SwingFXUtils.fromFXImage(canvasPane.snapshot(spGIF, image), null), "gif", file);
					break;
				default:
					break;
				}
			} catch (IOException err) {
				err.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleCloseMenuItemClicked(ActionEvent e) {
		Stage stage = (Stage)menuBar.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	protected void handleTextButtonClicked(MouseEvent e) {
		if (dataFile != null) {
			try (Scanner scan = new Scanner(dataFile)) {
				String content = scan.useDelimiter("\\Z").next();
				console.setText(content);
			} catch (FileNotFoundException err) {
				err.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleShowButtonClicked(MouseEvent e) {
		if (this.graph == null) {
			console.setText("请先打开文本以生成有向图！");
			return;
		}
		points.clear();
		canvasPane.getChildren().clear();
		showDirectedGraph();
		saveMenuItem.setDisable(false);
	}
	
	@FXML
	protected void handleQueryButtonClicked(MouseEvent e) throws Exception {
		GridPane prePane = (GridPane)stackPane.getChildren().get(0);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("QueryPane.fxml"));
		GridPane pane = loader.load();
		TextField word1TF = (TextField)loader.getNamespace().get("word1TextField");
		TextField word2TF = (TextField)loader.getNamespace().get("word2TextField");
		Button returnBT = (Button)loader.getNamespace().get("returnButton");
		Button queryBT = (Button)loader.getNamespace().get("queryButton");
		returnBT.setOnMouseClicked(event -> {
			stackPane.getChildren().remove(pane);
			stackPane.getChildren().add(prePane);
		});
		queryBT.setOnMouseClicked(event -> {
			String word1 = word1TF.getText();
			String word2 = word2TF.getText();
			if (word1.equals("") && !word2.equals("")) {
				console.setText("请输入单词1！");
				return;
			} else if (!word1.equals("") && word2.equals("")) {
				console.setText("请输入单词2！");
				return;
			} else if (word1.equals("") && word2.equals("")) {
				console.setText("请输入单词1和单词2！");
				return;
			} else {
				String output = queryBridgeWords(word1, word2);
				console.setText(output);
			}
		});
		stackPane.getChildren().remove(prePane);
		stackPane.getChildren().add(pane);
	}
	
	@FXML
	protected void handleGenerateButtonClicked(MouseEvent e) throws Exception {
		GridPane prePane = (GridPane)stackPane.getChildren().get(0);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GeneratePane.fxml"));
		GridPane pane = loader.load();
		TextField inputTF = (TextField)loader.getNamespace().get("inputTextField");
		Button returnBT = (Button)loader.getNamespace().get("returnButton");
		Button generateBT = (Button)loader.getNamespace().get("generateButton");
		returnBT.setOnMouseClicked(event -> {
			stackPane.getChildren().remove(pane);
			stackPane.getChildren().add(prePane);
		});
		generateBT.setOnMouseClicked(event -> {
			String inputText = inputTF.getText();
			if (inputText.equals("")) {
				console.setText("请输入新文本！");
				return;
			}
			String newText = generateNewText(inputText);
			console.setText(newText);
		});
		stackPane.getChildren().remove(prePane);
		stackPane.getChildren().add(pane);
	}
	
	@FXML
	protected void handlePathButtonClicked(MouseEvent e) throws Exception {
		floyd();
		GridPane prePane = (GridPane)stackPane.getChildren().get(0);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PathPane.fxml"));
		GridPane pane = loader.load();
		TextField word1TF = (TextField)loader.getNamespace().get("word1TextField");
		TextField word2TF = (TextField)loader.getNamespace().get("word2TextField");
		Button returnBT = (Button)loader.getNamespace().get("returnButton");
		Button yesBT = (Button)loader.getNamespace().get("yesButton");
		returnBT.setOnMouseClicked(event -> {
			stackPane.getChildren().remove(pane);
			stackPane.getChildren().add(prePane);
		});
		yesBT.setOnMouseClicked(event -> {
			String word1 = word1TF.getText();
			String word2 = word2TF.getText();
			String result = calcShortestPath(word1, word2);
			console.setText(result);
		});
		stackPane.getChildren().remove(prePane);
		stackPane.getChildren().add(pane);
	}
	
	@FXML
	protected void handleWalkButtonClicked(MouseEvent e) throws Exception {
		String text = randomWalk();
		GridPane prePane = (GridPane)stackPane.getChildren().get(0);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("WalkPane.fxml"));
		GridPane pane = loader.load();
		TextArea resultTA = (TextArea)loader.getNamespace().get("resultTextArea");
		Button returnBT = (Button)loader.getNamespace().get("returnButton");
		Button saveBT = (Button)loader.getNamespace().get("saveButton");
		returnBT.setOnMouseClicked(event -> {
			stackPane.getChildren().remove(pane);
			stackPane.getChildren().add(prePane);
		});
		saveBT.setOnMouseClicked((event) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("保存文本");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("文本文档", "*.txt"));
			File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
			if (file != null) {
				try (FileWriter writer = new FileWriter(file)) {
					writer.write(text);
					writer.close();
				} catch (IOException err) {
					err.printStackTrace();
				}
			}
		});
		resultTA.setText(text);
		stackPane.getChildren().remove(prePane);
		stackPane.getChildren().add(pane);
	}

	@FXML
	protected void handleCanvasPaneClicked(MouseEvent e) throws Exception {
		if (e.getClickCount() == 2) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageWindow.fxml"));
			StackPane root = loader.load();
			root.getChildren().clear();
			root.getChildren().add(canvasPane);
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("查看大图");
			stage.show();
			stage.setOnCloseRequest(event -> {
				canvasContainer.setContent(null);
				canvasContainer.setContent(canvasPane);
			});
		}
	}
	
	private void showDirectedGraph() {
		points.clear();
		edges.clear();
		canvasPane.getChildren().clear();

		ArrayList<Vertex> vertices = this.graph.getVertices();
		String startName, endName;

		for (Vertex v : vertices) {
			PointBox start;
			if (!points.keySet().contains(v.name)) {
				PointBox box = addPoint(v.name);
				start = box;
			} else {
				start = points.get(v.name);
			}
			startName = v.name;
			for (Vertex e : v.successors) {
				PointBox end;
				if (!points.keySet().contains(e.name)) {
					PointBox box = addPoint(e.name);
					end = box;
				} else {
					end = points.get(e.name);
				}
				endName = e.name;
				if (start == null) {
					System.out.println("start");
				}
				if (end == null) {
					System.out.println("end");
				}

				double dx = end.getCenterX() - start.getCenterX();
				double dy= end.getCenterY() - start.getCenterY();
				double ds = Math.sqrt(dx * dx + dy * dy);
				Line line = new Line();
				line.setStroke(Color.GREEN);
				line.setStartX(start.getCenterX() + radius * dx / ds);
				line.setStartY(start.getCenterY() + radius * dy / ds);
				line.setEndX(end.getCenterX() - radius * dx / ds);
				line.setEndY(end.getCenterY() - radius * dy / ds);
				line.startXProperty().bind(
					new DoubleBinding() {
						{
							super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
						}
						@Override
						protected double computeValue() {
							DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
							DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
							DoubleBinding s = new DoubleBinding() {
								{
									super.bind(x, y);
								}
								@Override
								protected double computeValue() {
									return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
								}
							};
							return start.centerXProperty().add(x.divide(s).multiply(radius)).get();
						}
					}
				);
				line.startYProperty().bind(
					new DoubleBinding() {
						{
							super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
						}
						@Override
						protected double computeValue() {
							DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
							DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
							DoubleBinding s = new DoubleBinding() {
								{
									super.bind(x, y);
								}
								@Override
								protected double computeValue() {
									return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
								}
							};
							return start.centerYProperty().add(y.divide(s).multiply(radius)).get();
						}
					}
				);
				line.endXProperty().bind(
					new DoubleBinding() {
						{
							super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
						}
						@Override
						protected double computeValue() {
							DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
							DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
							DoubleBinding s = new DoubleBinding() {
								{
									super.bind(x, y);
								}
								@Override
								protected double computeValue() {
									return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
								}
							};
							return end.centerXProperty().subtract(x.divide(s).multiply(radius)).get();
						}
					}
				);
				line.endYProperty().bind(
					new DoubleBinding() {
						{
							super.bind(start.centerXProperty(), start.centerYProperty(), end.centerXProperty(), end.centerYProperty());
						}
						@Override
						protected double computeValue() {
							DoubleBinding x = end.centerXProperty().subtract(start.centerXProperty());
							DoubleBinding y = end.centerYProperty().subtract(start.centerYProperty());
							DoubleBinding s = new DoubleBinding() {
								{
									super.bind(x, y);
								}
								@Override
								protected double computeValue() {
									return Math.sqrt(Math.pow(x.get(), 2) + Math.pow(y.get(), 2));
								}
							};
							return end.centerYProperty().subtract(y.divide(s).multiply(radius)).get();
						}
					}
				);
				Arrow edge = new Arrow(line, v.weights.get(e));
				edges.put(edge, new Pair<>(startName, endName));
			}
		}
		canvasPane.getChildren().addAll(points.values());
		canvasPane.getChildren().addAll(edges.keySet());
	}

	private PointBox addPoint(String name) {
		Random random = new Random();
		int canvasWidth = (int)canvasPane.getWidth();
		int canvasHeight = (int)canvasPane.getHeight();
		double x, y;

		do {
			x = (double)random.nextInt(canvasWidth);
			y = (double)random.nextInt(canvasHeight);
		} while(!isGoodCircleCenter(x, y));

		PointBox box = new PointBox();
		box.setLayoutX(x - radius);
		box.setLayoutY(y - radius);
		box.setPrefSize(radius * 2, radius * 2);
		box.setStyle("-fx-background-color: transparent");

		Circle circle = new Circle();
		circle.setRadius(radius);
		circle.setFill(Color.PINK);
		circle.setMouseTransparent(true);

		Text text = new Text(name);
		text.setWrappingWidth(radius * 2);
		text.setFill(Color.BLUE);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setTextOrigin(VPos.CENTER);
		text.setMouseTransparent(true);

		box.getChildren().addAll(circle, text);
		box.setOnMouseEntered(event -> {
			box.getCircle().setFill(Color.YELLOW);
		});
		box.setOnMouseExited(event -> {
			box.getCircle().setFill(Color.PINK);
		});
		box.setOnMouseDragged(event -> {
			double eventX = event.getX() + box.getLayoutX();
			double eventY = event.getY() + box.getLayoutY();
			if (eventX - radius < 0) {
				box.setLayoutX(0);
			} else if (eventX + radius > canvasPane.getWidth()) {
				box.setLayoutX(canvasPane.getWidth() - radius * 2);
			} else {
				box.setLayoutX(eventX - radius);
			}
			if (eventY - radius < 0) {
				box.setLayoutY(0);
			} else if (eventY + radius > canvasPane.getHeight()) {
				box.setLayoutY(canvasPane.getHeight() - radius * 2);
			} else {
				box.setLayoutY(eventY - radius);
			}
		});
		box.setOnMousePressed(event -> {
			box.getCircle().setFill(Color.RED);
		});
		box.setOnMouseReleased(event -> {
			box.getCircle().setFill(Color.YELLOW);
		});
		points.put(name, box);
		return box;
	}
	
	private boolean isGoodCircleCenter(double x, double y) {
		if (x <= radius*2 || x >= canvasPane.getWidth() - radius*2 || y <= radius*2 || y >= canvasPane.getHeight() - radius*2) {
			return false;
		} else {
			for (String point : points.keySet()) {
				double a = points.get(point).getCenterX();
				double b = points.get(point).getCenterY();
				if (Math.pow(a - x, 2) + Math.pow(b - y, 2) <= Math.pow(radius * 2, 2)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private String queryBridgeWords(String word1, String word2) {
		ArrayList<Vertex> vertices = this.graph.getVertices();
		Vertex a = null;
		Vertex b = null;
		for (Vertex v : vertices) {
			if (v.name.equals(word1)) {
				a = v;
			}
			if (v.name.equals(word2)) {
				b = v;
			}
		}
		if (a == null || b == null) {
			return "No word1 or word2 in the graph!";
		}
		HashSet<Vertex> successors = a.successors;
		HashSet<Vertex> predecessors = b.predecessors;
		HashSet<Vertex> intersection = new HashSet<>();
		intersection.addAll(successors);
		intersection.retainAll(predecessors);
		if (intersection.size() == 0) {
			return "No bridge words from word1 to word2!";
		}
		String wordsName = intersection.toString();
		return "The bridge words from word1 to word2 are: " + wordsName.substring(1, wordsName.length() - 1) + ".";
	}
	
	private String generateNewText(String inputText) {
		String pre, post;
		Scanner scan = new Scanner(inputText);
		StringBuffer sb = new StringBuffer(inputText);
		HashSet<Vertex> bridgeWords;
		int fromIndex = 0;
		do {
			pre = GraphProcessor.parseText(scan.next());
		} while (pre == null && scan.hasNext());
		while (scan.hasNext()) {
			post = GraphProcessor.parseText(scan.next());
			if (post != null) {
				bridgeWords = getBridgeWords(pre, post);
				Vertex insertVertex = randomSelect(bridgeWords);
				int insertIndex = sb.indexOf(post, fromIndex);
				if (insertVertex != null) {
					sb.insert(insertIndex, insertVertex.name + " ");
					fromIndex = insertIndex + insertVertex.name.length() + 1;
				}
				pre = post;
			}
		}
		scan.close();
		return sb.toString();
	}
	
	private HashSet<Vertex> getBridgeWords(String word1, String word2) {
		ArrayList<Vertex> vertices = this.graph.getVertices();
		Vertex a = null;
		Vertex b = null;
		for (Vertex v : vertices) {
			if (v.name.equals(word1)) {
				a = v;
			}
			if (v.name.equals(word2)) {
				b = v;
			}
		}
		if (a == null || b == null) {
			return null;
		}
		HashSet<Vertex> successors = a.successors;
		HashSet<Vertex> predecessors = b.predecessors;
		HashSet<Vertex> intersection = new HashSet<>();
		intersection.addAll(successors);
		intersection.retainAll(predecessors);
		return (intersection.size() == 0)? null : intersection;
	}
	
	private String calcShortestPath(String startName, String endName) {
		ArrayList<Vertex> vertices = this.graph.getVertices();
		Vertex startVertex = null;
		Vertex endVertex = null;
		for (Vertex v : vertices) {
			if (v.name.equals(startName) ) {
				startVertex = v;
			}
			if (v.name.equals(endName)) {
				endVertex = v;
			}
		}
		if (startVertex == null || endVertex == null) {
			return "No " + startName + " or " + endName + " in the graph!";
		}
		int i = vertices.indexOf(startVertex);
		int j = vertices.indexOf(endVertex);
		if (distance[i][j] == infinity) {
			return "No path from " + startName + " to " + endName + "!";
		}
		Vertex post = endVertex;
		Vertex pre = null;
		Stack<Vertex> ps = new Stack<>();
		while (post != null) {
			ps.push(post);
			int k = vertices.indexOf(post);
			post = path[i][k];
		}
		ps.push(startVertex);
		
		pre = ps.pop();
		while (!ps.isEmpty()) {
			post = ps.pop();
			showPath(pre, post);
			pre = post;
		}
		return "The length of the shortest path is " + distance[i][j];
	}
	
	private void floyd() {
		int vNum = graph.getVertexNumber();
		ArrayList<Vertex> vertices = graph.getVertices();
		HashSet<Vertex> successors;
		HashMap<Vertex, Integer> weights;
		path = new Vertex[vNum][vNum];
		distance = new int[vNum][vNum];
		for (int i = 0; i < vNum; ++i) {
			for (int j = 0; j < vNum; ++j) {
				successors = vertices.get(i).successors;
				weights = vertices.get(i).weights;
				path[i][j] = null;
				distance[i][j] = successors.contains(vertices.get(j)) ? weights.get(vertices.get(j)) : infinity;
			}
		}
		for (int k = 0; k < vNum; ++k) {
			for (int i = 0; i < vNum; ++i) {
				for (int j = 0; j < vNum; ++j) {
					if (distance[i][k] + distance[k][j] < distance[i][j]) {
						distance[i][j] = distance[i][k] + distance[k][j];
						path[i][j] = vertices.get(k);
					}
				}
			}
		}
	}
	
	private void showPath(Vertex start, Vertex end) {
		for (Arrow edge : edges.keySet()) {
			if (edges.get(edge).getKey().equals(start.name) && edges.get(edge).getValue().equals(end.name)) {
				edge.setStroke(Color.RED);
				edge.setStrokeWidth(2);
				return;
			}
		}
	}
	
	private String randomWalk() {
		ArrayList<Vertex> vertices = this.graph.getVertices();
		HashMap<Vertex, HashSet<Vertex>> walkedVertices = new HashMap<>();
		for (Vertex v : vertices) {
			walkedVertices.put(v, new HashSet<>());
		}
		Vertex pre = vertices.get(new Random().nextInt(vertices.size()));
		Vertex next = null;
		StringBuffer sb = new StringBuffer();
		sb.append(pre.name);
		while (true) {
			next = randomSelect(pre.successors);
			if (next == null) {
				break;
			}
			sb.append(" " + next.name);
			if (walkedVertices.get(pre).contains(next)) {
				break;
			}
			walkedVertices.get(pre).add(next);
			pre = next;
		}
		return sb.toString();
	}
	
	private Vertex randomSelect(HashSet<Vertex> set) {
		if (set == null || set.size() == 0) {
			return null;
		}
		int item = new Random().nextInt(set.size());
		int i = 0;
		for (Vertex v : set) {
			if (i == item) {
				return v;
			}
			++i;
		}
		return null;
	}
}

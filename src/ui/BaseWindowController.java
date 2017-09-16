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
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class BaseWindowController {
	@FXML private MenuBar menuBar;
	@FXML private MenuItem openMenuItem;
	@FXML private MenuItem saveMenuItem;
	@FXML private MenuItem closeMenuItem;
	
	@FXML private Button textButton;
	@FXML private Button showButton;
	@FXML private Button queryButton;
	@FXML private Button generateButton;
	@FXML private Button pathButton;
	@FXML private Button walkButton;
	
	@FXML private Canvas canvas;
	@FXML private TextArea console;
	@FXML private StackPane stackPane;
	
	private DirectedGraph graph;
	private File dataFile;
	
	private static HashMap<String, double[]> points = new HashMap<>();
	private static final double radius = 25;
	private static final int arrow_size = 8;
	private static Vertex[][] path;
	private static int[][] distance;
	private static final int infinity = Integer.MAX_VALUE / 2;
	
	@FXML
	protected void handleOpenMenuItemClicked(ActionEvent e) {
		openFile();
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
		saveImage();
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
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		showDirectedGraph();
		saveMenuItem.setDisable(false);
	}
	
	@FXML
	protected void handleQueryButtonClicked(MouseEvent e) throws Exception {
		GridPane prePane = (GridPane)stackPane.getChildren().get(0);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("QueryPane.fxml"));
		GridPane pane = (GridPane)loader.load();
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
		GridPane pane = (GridPane)loader.load();
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
	protected void handlePathButtonClicked(MouseEvent e) {
		floyd();
		Stage stage = new Stage();
		GridPane pane = new GridPane();
		Scene scene = new Scene(pane);
		stage.setHeight(200);
		stage.setWidth(300);
		Label label1 = new Label("请输入单词1：");
		Label label2 = new Label("请输入单词2：");
		TextField word1TextField = new TextField();
		TextField word2TextField = new TextField();
		Button button = new Button("确定");
		pane.getChildren().addAll(label1, label2, word1TextField, word2TextField, button);
		pane.setVgap(20);
		GridPane.setConstraints(label1, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER);
		GridPane.setConstraints(label2, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER);
		GridPane.setConstraints(word1TextField, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER);
		GridPane.setConstraints(word2TextField, 1, 1, 1, 1, HPos.LEFT, VPos.CENTER);
		GridPane.setConstraints(button, 1, 2, 2, 1, HPos.CENTER, VPos.CENTER);
		button.setOnMouseClicked((event) -> {
			String word1 = word1TextField.getText();
			String word2 = word2TextField.getText();
			calcShortestPath(word1, word2);
			stage.close();
		});
		stage.setScene(scene);;
		stage.setTitle("求最短路径");
		stage.setResizable(false);
		stage.show();
	}
	
	@FXML
	protected void handleWalkButtonClicked(MouseEvent e) {
		String text = randomWalk();
		Stage stage = new Stage();
		GridPane pane = new GridPane();
		Scene scene = new Scene(pane);
		Label label = new Label();
		Button button = new Button("保存");
		button.setOnMouseClicked((event) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("保存文本");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("文本文档", "*.txt"));
			File file = fileChooser.showSaveDialog(stage);
			if (file != null) {
				try (FileWriter writer = new FileWriter(file)) {
					writer.write(text);
					writer.close();
				} catch (IOException err) {
					err.printStackTrace();
				}
			}
		});
		label.setText(text);
		pane.getChildren().addAll(label, button);
		GridPane.setConstraints(label, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER);
		GridPane.setConstraints(button, 0, 1, 1, 1, HPos.CENTER, VPos.CENTER);
		stage.setScene(scene);
		stage.setTitle("随机游走结果");
		stage.show();
	}
	
	private void floyd() {
		int vNum = graph.getVertexNumber();
		ArrayList<Vertex> vertices = graph.getVertices();
		HashSet<Vertex> successors = null;
		HashMap<Vertex, Integer> weights = null;
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
		HashSet<Vertex> bridgeWords = new HashSet<>();
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
	
	private String calcShortestPath(String beginName, String endName) {
		ArrayList<Vertex> vertices = this.graph.getVertices();
		Vertex beginVertex = null;
		Vertex endVertex = null;
		for (Vertex v : vertices) {
			if (v.name.equals(beginName) ) {
				beginVertex = v;
			}
			if (v.name.equals(endName)) {
				endVertex = v;
			}
		}
		if (beginVertex == null || endVertex == null) {
			return "No " + beginName + " or " + endName + " in the graph!";
		}
		int i = vertices.indexOf(beginVertex);
		int j = vertices.indexOf(endVertex);
		if (distance[i][j] == infinity) {
			return "No path from " + beginName + " to " + endName + "!";
		}
		Vertex post = endVertex;
		Vertex pre = null;
		Stack<Vertex> ps = new Stack<>();
		while (post != null) {
			ps.push(post);
			int k = vertices.indexOf(post);
			post = path[i][k];
		}
		ps.push(beginVertex);
		
		pre = ps.pop();
		while (!ps.isEmpty()) {
			post = ps.pop();
			showPath(pre, post);
			pre = post;
		}
		return "The length of the shortest path is " + distance[i][j];
	}
	
	private void showPath(Vertex begin, Vertex end) {
		double beginX = points.get(begin.name)[0];
		double beginY = points.get(begin.name)[1];
		double endX = points.get(end.name)[0];
		double endY = points.get(end.name)[1];
		int weight = begin.weights.get(end);
		drawEdge(beginX, beginY, endX, endY, weight, 2, Color.RED);
	}
	
	private String randomWalk() {
		ArrayList<Vertex> vertices = this.graph.getVertices();
		HashMap<Vertex, HashSet<Vertex>> walkedVertices = new HashMap<>();
		for (Vertex v : vertices) {
			walkedVertices.put(v, new HashSet<>());
		}
		Vertex pre = vertices.get(new Random().nextInt(vertices.size()));
		System.out.println(pre);
		Vertex next = null;
		StringBuffer sb = new StringBuffer();
		sb.append(pre.name);
		while (true) {
			next = randomSelect(pre.successors);
			System.out.println(next);
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
	
	private void openFile() {
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
	}
	
	private void saveImage() {
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
			WritableImage image = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
			try {
				switch(extendName) {
				case "png":
					SnapshotParameters spPNG = new SnapshotParameters();
				    spPNG.setFill(Color.WHITE);
					ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(spPNG, image), null), "png", file);
					break;
				case "jpg":
					SnapshotParameters spJPG = new SnapshotParameters();
				    spJPG.setFill(Color.WHITE);
					ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(spJPG, image), null), "jpg", file);
					break;
				case "gif":
					SnapshotParameters spGIF = new SnapshotParameters();
				    spGIF.setFill(Color.WHITE);
					ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(spGIF, image), null), "gif", file);
					break;
				default:
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showDirectedGraph() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		Random random = new Random();
		int canvasWidth = (int)canvas.getWidth();
		int canvasHeight = (int)canvas.getHeight();
		ArrayList<Vertex> vertices = this.graph.getVertices();
		double beginX, beginY, endX, endY;
		
		gc.setTextBaseline(VPos.CENTER);
		gc.setTextAlign(TextAlignment.CENTER);
		for (Vertex v : vertices) {
			if (!points.keySet().contains(v.name)) {
				do {
					beginX = (double)random.nextInt(canvasWidth);
					beginY = (double)random.nextInt(canvasHeight);
				} while (!isGoodCircleCenter(beginX, beginY));
				double[] position = new double[2];
				position[0] = beginX;
				position[1] = beginY;
				points.put(v.name, position);
				gc.setLineWidth(1);
				gc.setStroke(Color.BLUE);
				gc.strokeText(v.name, beginX, beginY, radius * 2);
				gc.setStroke(Color.BLACK);
				gc.strokeOval(beginX - radius, beginY - radius, radius * 2, radius * 2);
			} else {
				beginX = points.get(v.name)[0];
				beginY = points.get(v.name)[1];
			}
			for (Vertex e : v.successors) {
				if (!points.keySet().contains(e.name)) {
					do {
						endX = (double)random.nextInt(canvasWidth);
						endY = (double)random.nextInt(canvasHeight);
					} while(!isGoodCircleCenter(endX, endY));
					double[] position = new double[2];
					position[0] = endX;
					position[1] = endY;
					points.put(e.name, position);
					gc.setLineWidth(1);
					gc.setStroke(Color.BLUE);
					gc.strokeText(e.name, endX, endY, radius * 2);
					gc.setStroke(Color.BLACK);
					gc.strokeOval(endX - radius, endY - radius, radius * 2, radius * 2);
				} else {
					endX = points.get(e.name)[0];
					endY = points.get(e.name)[1];
				}
				drawEdge(beginX, beginY, endX, endY, v.weights.get(e), 1, Color.GREEN);
			}
		}
	}
	
	private boolean isGoodCircleCenter(double x, double y) {
		if (x <= radius*2|| x >= canvas.getWidth() - radius*2|| y <= radius*2 || y >= canvas.getHeight() - radius*2) {
			return false;
		} else {
			for (String point : points.keySet()) {
				double a = points.get(point)[0];
				double b = points.get(point)[1];
				if (Math.pow(a - x, 2) + Math.pow(b - y, 2) <= Math.pow(radius * 2, 2)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void drawEdge(double beginX, double beginY, double endX, double endY, int weight, double width, Color color) {
		double dx = endX - beginX;
		double dy= endY - beginY;
		double ds = Math.sqrt(dx * dx + dy * dy);
		double sin = dy / ds;
		double cos = dx / ds;
		double middleX = (beginX + endX) / 2;
		double middleY = (beginY + endY) / 2;
		double realBeginX = beginX + radius * cos;
		double realBeginY = beginY + radius * sin;
		double realEndX = endX - radius * cos;
		double realEndY = endY - radius * sin;
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(width);
		gc.setStroke(color);
		gc.strokeLine(realBeginX, realBeginY, realEndX, realEndY);
		gc.strokeText(String.valueOf(weight), middleX, middleY);
		
	    double angle = Math.atan2(dy, dx);
	    double rdx = realEndX - realBeginX;
	    double rdy = realEndY - realBeginY;
	    int len = (int) Math.sqrt(rdx * rdx + rdy * rdy);
	    Transform transform = Transform.translate(realBeginX, realBeginY);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));
	    gc.fillPolygon(new double[]{len, len - arrow_size, len - arrow_size, len}, new double[]{0, - arrow_size / 2, arrow_size / 2, 0}, 4);
	    gc.setTransform(new Affine());
	}
}

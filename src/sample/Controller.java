package sample;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import sun.net.util.IPAddressUtil;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static javafx.scene.input.KeyCode.*;

public class Controller {

    private static final double DEFAULT_RECTAGLE_SIZE = 50;
    private static final double RECTANGLE_OFFSET = 20;

    public ToggleButton drawCircleButton;
    public ToggleButton rectangleButton;
    public ToggleButton drawButton;
    public Canvas canvas;
    public ColorPicker colorPicker;
    public Slider circleDimensioningSlider;
    public CheckBox circleCheckBox;
    public Slider drawingSlider;
    public Button clearButton;
    public CheckBox dottedCheckBox;
    public CheckBox longDashCheckBox;
    public ToggleButton insertImageButton;
    public ToggleButton textButton;
    public ComboBox fontComboBox;
    public Slider fontSize;
    public ToggleButton cutButton;

    private GraphicsContext graphicsContext;
    private WritableImage canvasSnapshot;
    private double x = -1, y = -1;
    private double width = DEFAULT_RECTAGLE_SIZE, height = DEFAULT_RECTAGLE_SIZE;
    private Image image;
    private String text = "";
    private List<Point> list = new ArrayList<>();

    @FXML
    private void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        initializeTextComponents();
        setCanvasListener();
        initializeDrawingComponents();
        initializeToggleButtons();
        addColorPickerListener();
        initializeCircleActions();
        addRectangleButtonListener();
        showLoginDialog();
    }

    private void initializeTextComponents() {
        fontSize.setFocusTraversable(false);
        fontSize.setManaged(false);
        fontComboBox.setFocusTraversable(false);
        fontComboBox.setManaged(false);
        fontComboBox.getItems().addAll(Font.getFamilies());
        fontComboBox.getSelectionModel().selectFirst();
        fontComboBox.managedProperty().bind(fontComboBox.visibleProperty());
        fontSize.managedProperty().bind(fontComboBox.visibleProperty());
    }

    @FXML
    private void insertImageAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Images", "jpg", "png", "gif", "bmp");
        fileChooser.setSelectedExtensionFilter(filter);
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            image = new Image(file.toURI().toString());
            ActionsService.getInstance().setAction(ActionsService.INSERT_IMAGE);
        } else {
            insertImageButton.setSelected(false);
        }
    }

    @FXML
    private void startTextDrawing() {
        if (textButton.isSelected()) {
            fontSize.setVisible(true);
            fontComboBox.setVisible(true);
            text = getTextDialog();
            ActionsService.getInstance().setAction(ActionsService.DRAW_TEXT);
        } else {
            fontSize.setVisible(false);
            fontComboBox.setVisible(false);
        }
    }

    @FXML
    private void onCutPressed() {
        ActionsService.getInstance().setAction(ActionsService.CUT);
        width = DEFAULT_RECTAGLE_SIZE;
        height = DEFAULT_RECTAGLE_SIZE;
        graphicsContext.setLineWidth(1);
    }

    private void initializeDrawingComponents() {
        drawingSlider.setFocusTraversable(false);
        drawingSlider.setMin(1);
        drawingSlider.setMax(25);
        drawingSlider.setManaged(false);
        longDashCheckBox.setManaged(false);
        drawingSlider.managedProperty().bind(drawingSlider.visibleProperty());
        longDashCheckBox.managedProperty().bind(drawingSlider.visibleProperty());
        dottedCheckBox.managedProperty().bind(drawingSlider.visibleProperty());
        clearButton.setFocusTraversable(false);
        clearButton.setOnAction(event -> {
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            saveCanvas();
        });

        dottedCheckBox.setFocusTraversable(false);
        dottedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                longDashCheckBox.setSelected(false);
            }
        });

        longDashCheckBox.setFocusTraversable(false);
        longDashCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                dottedCheckBox.setSelected(false);
            }
        });
    }

    private void initializeCircleActions() {
        circleCheckBox.setManaged(false);
        circleDimensioningSlider.setManaged(false);
        circleCheckBox.managedProperty().bind(circleDimensioningSlider.visibleProperty());
        circleDimensioningSlider.managedProperty().bind(circleDimensioningSlider.visibleProperty());
        circleDimensioningSlider.setMax(300);
        circleDimensioningSlider.setMin(1);
    }

    private void addColorPickerListener() {
        colorPicker.setOnAction(event -> ActionsService.getInstance().setColor(colorPicker.getValue()));
    }

    private void addRectangleButtonListener() {
        rectangleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                saveCanvas();
                resetRectangle();
            } else {
                colorPicker.setDisable(false);
                graphicsContext.drawImage(canvasSnapshot, 0, 0);
            }
        });
    }

    private void resetRectangle() {
        startRectangleDrawing();
        ActionsService.getInstance().setAction(ActionsService.RECTANGLE);
        x = 0;
        y = 0;
        width = DEFAULT_RECTAGLE_SIZE;
        height = DEFAULT_RECTAGLE_SIZE;
    }

    private void startRectangleDrawing() {
        graphicsContext.setFill(ActionsService.getInstance().getColor());
        graphicsContext.fillRect(0, 0, DEFAULT_RECTAGLE_SIZE, DEFAULT_RECTAGLE_SIZE);
    }

    private void setCanvasListener() {
        canvas.setOnMouseDragged(event -> {
            if (ActionsService.getInstance().getAction() == ActionsService.DRAWING) {
                draw(event);
            }
        });

        canvas.addEventHandler(EventType.ROOT, event -> {

            if (ActionsService.getInstance().getAction() == ActionsService.DRAWING
                    && event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                saveCanvas();
                DrawingAction action = new DrawingAction();
                action.setNameOfSender(ActionsService.getInstance().getName());
                action.setDotted(dottedCheckBox.isSelected());
                action.setLongDash(longDashCheckBox.isSelected());
                action.setColor(ActionsService.getInstance().getColor());
                action.setPoints(list);
                action.setSize(drawingSlider.getValue());
                action.setType(ActionsService.getInstance().getAction());
                ActionsService.getInstance().onActionMade(action);
                list.clear();
                x = -1;
                y = -1;
            }

            if (ActionsService.getInstance().getAction() == ActionsService.DRAW_TEXT
                    && event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                WrittingAction writtingAction = new WrittingAction();
                writtingAction.setNameOfSender(ActionsService.getInstance().getName());
                writtingAction.setFontName(fontComboBox.getSelectionModel().getSelectedItem().toString());
                writtingAction.setFontSize(fontSize.getValue());
                writtingAction.setX(x);
                writtingAction.setY(y);
                writtingAction.setText(text);
                writtingAction.setColor(ActionsService.getInstance().getColor());
                writtingAction.setType(ActionsService.getInstance().getAction());
                ActionsService.getInstance().onActionMade(writtingAction);
                fontSize.setVisible(false);
                fontComboBox.setVisible(false);
                ActionsService.getInstance().setAction(ActionsService.NO_ACTION);
                text = "";
                textButton.setSelected(false);
                saveCanvas();
            }

            if (ActionsService.getInstance().getAction() == ActionsService.CIRCLE
                    || ActionsService.getInstance().getAction() == ActionsService.INSERT_IMAGE ||
                    ActionsService.getInstance().getAction() == ActionsService.DRAW_TEXT ||
                    ActionsService.getInstance().getAction() == ActionsService.CUT) {
                drawOnCanvas(event);
            }

            if (ActionsService.getInstance().getAction() == ActionsService.CIRCLE && event.getEventType() == MouseEvent.MOUSE_EXITED) {
                graphicsContext.drawImage(canvasSnapshot, 0, 0);
            }

            if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                graphicsContext.drawImage(canvasSnapshot, 0, 0);
                saveCanvas();
            }
        });
    }

    private void initializeToggleButtons() {
        ToggleGroup group = new ToggleGroup();
        textButton.setFocusTraversable(false);
        insertImageButton.setFocusTraversable(false);
        cutButton.setFocusTraversable(false);
        cutButton.setToggleGroup(group);
        textButton.setToggleGroup(group);
        insertImageButton.setToggleGroup(group);
        drawButton.setToggleGroup(group);
        drawButton.setFocusTraversable(false);
        drawCircleButton.setToggleGroup(group);
        drawCircleButton.setFocusTraversable(false);
        colorPicker.setFocusTraversable(false);
        rectangleButton.setToggleGroup(group);
        drawingSlider.setVisible(false);
        rectangleButton.setFocusTraversable(false);
        dottedCheckBox.setVisible(false);
        longDashCheckBox.setVisible(false);

        drawButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                x = -1;
                y = -1;
                dottedCheckBox.setVisible(true);
                longDashCheckBox.setVisible(true);
                drawingSlider.setVisible(true);
                ActionsService.getInstance().setAction(ActionsService.DRAWING);
            } else {
                dottedCheckBox.setVisible(false);
                drawingSlider.setVisible(false);
                longDashCheckBox.setVisible(false);
            }
        });

        drawCircleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ActionsService.getInstance().setAction(ActionsService.CIRCLE);
                circleCheckBox.setVisible(true);
                circleDimensioningSlider.setVisible(true);
            } else {
                circleCheckBox.setVisible(false);
                circleDimensioningSlider.setVisible(false);
            }
        });

    }

    private void saveCanvas() {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        canvasSnapshot = canvas.snapshot(params, null);
    }

    private void draw(MouseEvent event) {
        if (x == -1 && y == -1) {
            x = event.getX();
            y = event.getY();
        }

        if (!dottedCheckBox.isSelected() && !longDashCheckBox.isSelected()) {
            graphicsContext.setLineDashes(0);
        }

        if (dottedCheckBox.isSelected()) {
            graphicsContext.setLineDashes(1, 5, 1, 5, 1, 5);
        }

        if (longDashCheckBox.isSelected()) {
            graphicsContext.setLineDashes(4, 15, 4, 15, 4, 15);
        }

        graphicsContext.setLineWidth(drawingSlider.getValue());
        graphicsContext.setStroke(ActionsService.getInstance().getColor());
        graphicsContext.strokeLine(x, y, event.getX(), event.getY());
        x = event.getX();
        y = event.getY();
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        list.add(point);
    }

    private void drawOnCanvas(Event generalEvent) {
        if (width == DEFAULT_RECTAGLE_SIZE && height == DEFAULT_RECTAGLE_SIZE
                && image != null) {
            width = width * image.getWidth() / image.getHeight();
            height = height * image.getWidth() / image.getWidth();
        }

        if (generalEvent instanceof MouseEvent) {
            MouseEvent event = (MouseEvent) generalEvent;
            x = event.getX();
            y = event.getY();
            if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
                graphicsContext.drawImage(canvasSnapshot, 0, 0);
            }

            if (event.getEventType() == MouseEvent.MOUSE_MOVED ||
                    event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                graphicsContext.drawImage(canvasSnapshot, 0, 0);
                saveCanvas();
            }

            if (event.getEventType() == MouseEvent.MOUSE_CLICKED && ActionsService.getInstance().getAction() == ActionsService.CUT) {
                graphicsContext.drawImage(canvasSnapshot, 0, 0);
                cutButton.setSelected(false);
                saveCanvas();
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                WritableImage savedChunk = canvas.snapshot(params, null);
                Image image = new WritableImage(savedChunk.getPixelReader(), (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
                Random random = new Random();
                File file = new File(System.getProperty("user.home") + "\\Desktop" + "\\sample_" + random.nextInt() + ".png");
                BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
                try {
                    ImageIO.write(bImage, "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ActionsService.getInstance().setAction(ActionsService.NO_ACTION);

            }

            if (event.getEventType() == MouseEvent.MOUSE_CLICKED && ActionsService.getInstance().getAction() != ActionsService.CUT) {

                if (ActionsService.getInstance().getAction() == ActionsService.CIRCLE) {
                    ShapeAction action = new ShapeAction();
                    action.setType(ActionsService.CIRCLE);
                    action.setNameOfSender(ActionsService.getInstance().getName());
                    action.setColor(ActionsService.getInstance().getColor());
                    action.setHeight(width);
                    action.setWidth(width);
                    action.setFill(circleCheckBox.isSelected());
                    action.setX(x);
                    action.setY(y);
                    ActionsService.getInstance().onActionMade(action);
                    graphicsContext.drawImage(canvasSnapshot,0,0);
                    drawCircle();
                    saveCanvas();
                }
                saveCanvas();
                if (ActionsService.getInstance().getAction() == ActionsService.INSERT_IMAGE) {
                    ImageAction action = new ImageAction();
                    action.setNameOfSender(ActionsService.getInstance().getName());
                    action.setType(ActionsService.INSERT_IMAGE);
                    action.setColor(ActionsService.getInstance().getColor());
                    action.setHeight(height);
                    action.setWidth(width);
                    action.setFill(true);
                    action.setX(x);
                    action.setY(y);
                    graphicsContext.drawImage(image, x - width / 2, y - width / 2, width, height);
                    saveCanvas();

                    try {
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                        ByteArrayOutputStream s = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", s);
                        byte[] res = s.toByteArray();
                        action.setImage(res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ActionsService.getInstance().onActionMade(action);
                    width = DEFAULT_RECTAGLE_SIZE;
                    height = DEFAULT_RECTAGLE_SIZE;
                    ActionsService.getInstance().setAction(ActionsService.NO_ACTION);
                }
                if (insertImageButton.isSelected()) {
                    insertImageButton.setSelected(false);
                }
            }
            x = event.getX();
            y = event.getY();

            drawCircle();

            if (ActionsService.getInstance().getAction() == ActionsService.INSERT_IMAGE) {
                graphicsContext.drawImage(image, x - width / 2, y - width / 2, width, height);
            }

            if (ActionsService.getInstance().getAction() == ActionsService.DRAW_TEXT) {
                graphicsContext.setFill(ActionsService.getInstance().getColor());
                graphicsContext.setFont(new Font(fontComboBox.getSelectionModel().getSelectedItem().toString(), fontSize.getValue()));
                graphicsContext.fillText(text, x, y);
            }

            if (ActionsService.getInstance().getAction() == ActionsService.CUT) {
                graphicsContext.setStroke(ActionsService.getInstance().getColor());
                graphicsContext.strokeRect(x - width / 2, y - height / 2, width, height);
            }

        }
        if (generalEvent instanceof ScrollEvent) {
            graphicsContext.drawImage(canvasSnapshot, 0, 0);
            saveCanvas();
            ScrollEvent event = (ScrollEvent) generalEvent;
            if (event.getDeltaY() > 0) {
                width += RECTANGLE_OFFSET;
                height += RECTANGLE_OFFSET;
            } else if (event.getDeltaY() < 0 && width - RECTANGLE_OFFSET > 0) {
                width -= RECTANGLE_OFFSET;
                height -= RECTANGLE_OFFSET;
            }
            if (ActionsService.getInstance().getAction() == ActionsService.INSERT_IMAGE) {
                graphicsContext.drawImage(image, x - width / 2, y - width / 2, width, height);
            }
        }
    }

    private void drawCircle(){
        if (circleCheckBox.isSelected() && ActionsService.CIRCLE == ActionsService.getInstance().getAction()) {
            width = circleDimensioningSlider.getValue();
            graphicsContext.setFill(ActionsService.getInstance().getColor());
            graphicsContext.fillOval(x - width / 2, y - width / 2, width, width);
        } else if (ActionsService.CIRCLE == ActionsService.getInstance().getAction()) {
            width = circleDimensioningSlider.getValue();
            graphicsContext.setLineDashes(0);
            graphicsContext.setStroke(ActionsService.getInstance().getColor());
            graphicsContext.strokeOval(x - width / 2, y - width / 2, width, width);
        }
    }

    public EventHandler getKeyPressListener() {
        return keyPressListener;
    }

    public EventHandler getKeyReleaseListener() {
        return keyReleasedListener;
    }

    private final BooleanProperty shiftPressed = new SimpleBooleanProperty(false);
    private final BooleanProperty arrowPressed = new SimpleBooleanProperty(false);
    private final BooleanBinding keyCombination = shiftPressed.and(arrowPressed);

    private EventHandler<KeyEvent> keyReleasedListener = event -> {
        if (ActionsService.getInstance().getAction() == ActionsService.RECTANGLE) {
            if (event.getCode() == KeyCode.SHIFT) {
                shiftPressed.set(false);
            } else if (event.getCode() == RIGHT ||
                    event.getCode() == LEFT ||
                    event.getCode() == UP ||
                    event.getCode() == DOWN) {
                arrowPressed.set(false);
            }
        }
    };

    private void rectagleKeyAction(KeyEvent event) {
        colorPicker.setDisable(true);
        switch (event.getCode()) {
            case DOWN:
                y += RECTANGLE_OFFSET;
                break;
            case UP:
                y -= RECTANGLE_OFFSET;
                break;
            case LEFT:
                x -= RECTANGLE_OFFSET;
                break;
            case RIGHT:
                x += RECTANGLE_OFFSET;
                break;
        }

        escapeOrEnterAction(event);
    }

    private void escapeOrEnterAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            resetRectangle();
            ActionsService.getInstance().setAction(ActionsService.NO_ACTION);
            rectangleButton.setSelected(false);
            return;
        }

        if (event.getCode() == KeyCode.ENTER) {
            graphicsContext.drawImage(canvasSnapshot, 0, 0);
            graphicsContext.setFill(ActionsService.getInstance().getColor());
            graphicsContext.fillRect(x, y, width, height);
            ShapeAction action = new ShapeAction();
            action.setX(x);
            action.setY(y);
            action.setFill(true);
            action.setHeight(height);
            action.setWidth(width);
            action.setNameOfSender(ActionsService.getInstance().getName());
            action.setColor(ActionsService.getInstance().getColor());
            action.setType(ActionsService.RECTANGLE);
            ActionsService.getInstance().onActionMade(action);
            saveCanvas();
            resetRectangle();
            colorPicker.setDisable(false);
        } else {
            graphicsContext.drawImage(canvasSnapshot, 0, 0);
            graphicsContext.setFill(ActionsService.getInstance().getColor());
            graphicsContext.fillRect(x, y, width, height);
        }
    }

    private void rectangleCombinationPressed(KeyEvent event) {
        switch (event.getCode()) {
            case DOWN:
                height += RECTANGLE_OFFSET;
                break;
            case UP:
                height -= RECTANGLE_OFFSET;
                break;
            case LEFT:
                width -= RECTANGLE_OFFSET;
                break;
            case RIGHT:
                width += RECTANGLE_OFFSET;
                break;
        }
        graphicsContext.drawImage(canvasSnapshot, 0, 0);
        graphicsContext.setFill(ActionsService.getInstance().getColor());
        graphicsContext.fillRect(x, y, width, height);
    }

    private EventHandler<KeyEvent> keyPressListener = event -> {
        if (event.getCode() == KeyCode.SHIFT) {
            shiftPressed.set(true);
        } else if (event.getCode() == RIGHT ||
                event.getCode() == LEFT ||
                event.getCode() == UP ||
                event.getCode() == DOWN) {
            arrowPressed.set(true);
        }
        if (ActionsService.getInstance().getAction() == ActionsService.RECTANGLE && !keyCombination.get()) {
            rectagleKeyAction(event);
        } else if (ActionsService.getInstance().getAction() == ActionsService.RECTANGLE && keyCombination.get()) {
            rectangleCombinationPressed(event);
        }
    };

    private void showLoginDialog() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Please enter Ip and Username");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Log in", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("Username:");
        TextField to = new TextField();
        to.setPromptText("IP:");

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(from, 1, 0);
        gridPane.add(new Label("Ip:"), 2, 0);
        gridPane.add(to, 3, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(from.getText(), to.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (IPAddressUtil.isIPv4LiteralAddress(result.get().getValue())
                    && result.get().getKey() != null && !result.get().getKey().isEmpty()) {
                ActionsService.getInstance().startServer(result.get().getValue(), result.get().getKey(), listener);
            } else {
                showLoginDialog();
            }
        } else {
            Platform.exit();
            System.exit(0);
        }
    }

    private String getTextDialog() {
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Enter the text you want to draw");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Enter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField from = new TextField();
        from.setPromptText("Input");

        gridPane.add(new Label("Input:"), 0, 0);
        gridPane.add(from, 1, 0);
        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return from.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        } else {
            ActionsService.getInstance().setAction(ActionsService.NO_ACTION);
            drawingSlider.setVisible(false);
            fontSize.setVisible(false);
            return "";
        }
    }

    private OnItemDrawnListener listener = action -> {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(action.getType());
                switch (action.getType()) {
                    case ActionsService.DRAWING:
                        DrawingAction drawingAction = (DrawingAction) action;
                        List<Point> points = drawingAction.getPoints();
                        graphicsContext.setStroke(drawingAction.getColor());
                        graphicsContext.setLineWidth(drawingAction.getSize());

                        if (!drawingAction.isDotted() && !drawingAction.isLongDash()) {
                            graphicsContext.setLineDashes(0);
                        }

                        if (drawingAction.isDotted()) {
                            graphicsContext.setLineDashes(1, 5, 1, 5, 1, 5);
                        }

                        if (drawingAction.isLongDash()) {
                            graphicsContext.setLineDashes(4, 15, 4, 15, 4, 15);
                        }

                        for (int i = 0; i < points.size() - 2; i++) {
                            Point pointOne = points.get(i);
                            Point pointTwo = points.get(i + 1);
                            graphicsContext.strokeLine(pointOne.getX(), pointOne.getY(),
                                    pointTwo.getX(), pointTwo.getY());
                        }
                        saveCanvas();
                        break;
                    case ActionsService.CIRCLE:
                        ShapeAction circle = (ShapeAction) action;
                        if (circle.isFill()) {
                            graphicsContext.setFill(circle.getColor());
                            graphicsContext.fillOval(circle.getX() - circle.getWidth() / 2, circle.getY() - circle.getHeight() / 2, circle.getWidth(), circle.getHeight());
                            saveCanvas();
                        } else {
                            graphicsContext.setStroke(circle.getColor());
                            graphicsContext.strokeOval(circle.getX() - circle.getWidth() / 2, circle.getY() - circle.getHeight() / 2, circle.getWidth(), circle.getHeight());
                            saveCanvas();
                        }
                        break;
                    case ActionsService.RECTANGLE:
                        ShapeAction rectangle = (ShapeAction) action;
                        graphicsContext.setFill(rectangle.getColor());
                        graphicsContext.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                        saveCanvas();
                        break;
                    case ActionsService.INSERT_IMAGE:
                        ImageAction imageAction = (ImageAction) action;
                        try {
                            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageAction.getImage()));
                            Image image = SwingFXUtils.toFXImage(img, null);
                            graphicsContext.drawImage(image, imageAction.getX() - imageAction.getWidth()
                                            / 2, imageAction.getY() - imageAction.getWidth() / 2,
                                    imageAction.getWidth(), imageAction.getWidth());
                            saveCanvas();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case ActionsService.DRAW_TEXT:
                        System.out.println("intra");
                        WrittingAction writtingAction= (WrittingAction) action;
                        graphicsContext.setFill(writtingAction.getColor());
                        graphicsContext.setFont(new Font(writtingAction.getFontName(), writtingAction.getFontSize()));
                        graphicsContext.fillText(writtingAction.getText(),writtingAction.getX(), writtingAction.getY());
                        saveCanvas();
                        break;
                }
            }
        });
    };
}

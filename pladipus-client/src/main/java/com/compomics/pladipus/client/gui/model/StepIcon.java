package com.compomics.pladipus.client.gui.model;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

public class StepIcon extends StackPane {
	
	private WorkflowGuiStep step;
	private WorkflowGui workflow;
	private String id;
	private double size;
	private Text labelText;
	private Rectangle inner;
	private Rectangle labelRect;
	private Circle inCirc;
	private Circle outCirc;
	final IconInteract ii = new IconInteract();
	
	class IconInteract {
		double xDrag;
		double yDrag;
	}
	
	public StepIcon (WorkflowGuiStep step, double iconSize, Color color, String id) {
		super();
		this.step = step;
		this.workflow = step.getWorkflowGui();
		this.size = Math.max(iconSize, 50.0);
		this.id = id;
		
		Rectangle rect = new Rectangle(0, 0, size, size);
		rect.setArcHeight(size * 0.3);
		rect.setArcWidth(size * 0.3);
		setShape(rect);
		setMaxSize(size, size);
		
		createIconParts();
		
		getChildren().add(inner);
		getChildren().add(inCirc);
		getChildren().add(outCirc);
		getChildren().add(labelRect);
		getChildren().add(labelText);
		setStyle("-fx-border-color:black; -fx-background-color:" + color.toString().replaceFirst("0x", "#"));
		StackPane.setAlignment(this, Pos.TOP_LEFT);
		addIconListeners();
		addCircleListeners();
	}
	
	private DropShadow getHighlight(Color color) {
		DropShadow highlight = new DropShadow();
		highlight.setColor(color);
		highlight.setOffsetX(0.0);
		highlight.setOffsetY(0.0);
		highlight.setRadius(size*0.3);
		return highlight;
	}
	
	private InnerShadow getGlow(Color color) {
		InnerShadow glow = new InnerShadow();
		glow.setColor(color);
		glow.setOffsetX(0.0);
		glow.setOffsetY(0.0);
		return glow;
	}
	
	public void setInitPosition(double xpos, double ypos) {
		setManaged(false);
		setLayoutX(xpos);
		setLayoutY(ypos);
	}
	
	public void updateLabel(String newText) {
		getChildren().remove(labelText);
		id = newText;
		createLabelText();
		getChildren().add(labelText);
	}
	
	public void setComplete(boolean complete) {
		Color color = Color.RED;
		if (complete) color = Color.BLACK;
		labelText.setFill(color);
		labelRect.setStroke(color);
	}
	
	public void highlightIcon(boolean highlightOn) {
		if (highlightOn) {
			setEffect(getHighlight(Color.RED));
		} else {
			setEffect(null);
		}
	}
	
	public void highlightInCircle(boolean highlightOn) {
		if (highlightOn) {
			inCirc.setEffect(getGlow(Color.BLUE));
		} else {
			inCirc.setEffect(null);
		}
	}
	
	public Circle getOutCircle() {
		return outCirc;
	}
	
	public Circle getInCircle() {
		return inCirc;
	}
	
	public double getSize() {
		return size;
	}
	
	private void createIconParts() {
		createLabelText();
		createRectangles();
		createCircles();
	}
	
	private void createCircles() {
		inCirc = new Circle(0.0, size * 0.5, size * 0.16);
		inCirc.setStroke(Color.BLACK);
		inCirc.setFill(Color.GHOSTWHITE);
		inCirc.setManaged(false);
		outCirc = new Circle(size, size * 0.5, size * 0.16);
		outCirc.setStroke(Color.BLACK);
		outCirc.setFill(Color.GHOSTWHITE);
		outCirc.setManaged(false);
	}
	
	private void createRectangles() {
		inner = new Rectangle(0, size * 0.2, size, size * 0.6);
		inner.setStroke(Color.BLACK);
		inner.setFill(Color.GHOSTWHITE);
		
		labelRect = new Rectangle(size * 0.2, size * 0.3, size * 0.6, size * 0.4);
		labelRect.setArcHeight(size * 0.2);
		labelRect.setArcWidth(size * 0.2);
		labelRect.setStroke(Color.BLACK);
		labelRect.setFill(Color.GHOSTWHITE);
		labelRect.setManaged(false);
	}
	
	private void createLabelText() {
		labelText = new Text();
		
		// Check how large text would be, and make sure it isn't larger than the pane.
		// Reduce font size if needed so boundsInLocal fits in the icon.
		Text testWidth = new Text(id);
		testWidth.setFont(Font.font(size*0.4));
		double txtWidth = testWidth.getBoundsInLocal().getWidth();
		if (txtWidth > size) {
			labelText.setFont(Font.font(size * size * 0.4 / txtWidth));
		}
		
		labelText.setText(id);
		
		double textWidth = labelText.getBoundsInLocal().getWidth();
		if (textWidth > size * 0.48) {
			labelText.setScaleX(size * 0.48 / textWidth);
		}
		double textHeight = labelText.getBoundsInLocal().getHeight();
		if (textHeight > (size * 0.32)) {
			labelText.setScaleY(size * 0.32 / textHeight);
		}
		StackPane.setAlignment(labelText, Pos.CENTER);
	}
	
	private void addIconListeners() {
		
		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				setCursor(Cursor.HAND);
			}
		});
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				mouseEvent.consume();
				workflow.setSelectedStep(step);
				ii.xDrag = getLayoutX() - mouseEvent.getSceneX();
				ii.yDrag = getLayoutY() - mouseEvent.getSceneY();
				setCursor(Cursor.MOVE);
			}
		});
		
		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				if (workflow.getDrawingLink() == null) {
					mouseEvent.consume();
					setManaged(false);
	
					double buffer = size * 0.16;
					double movedX = mouseEvent.getSceneX() + ii.xDrag;
					double movedY = mouseEvent.getSceneY() + ii.yDrag;
					double boundX = ((Region) getParent()).getWidth() - getWidth() - buffer;
					double boundY = ((Region) getParent()).getHeight() - getHeight() - buffer;
					if (movedX > buffer && movedX < boundX) {
						setLayoutX(movedX);
					}
					if (movedY > buffer && movedY < boundY) {
						setLayoutY(movedY);
					}
				}
			}
		});
		
		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				setCursor(Cursor.HAND);
			}
		});

		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				setCursor(Cursor.DEFAULT);
			}
		});
		
		setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				if (!isStartLinkIcon()) {
					event.consume();
				    workflow.endDrawingLink(step);
			}}
		});
		
		setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				if (!isStartLinkIcon()) {
					event.consume();
			}}
		});
		
		setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				if (!isStartLinkIcon()) {
					event.consume();
					workflow.finaliseDrawingLink();
			}}
		});
		
		setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
			@Override
			public void handle(MouseDragEvent event) {
				if (!isStartLinkIcon()) {
					event.consume();
					workflow.endDrawingLink(null);
				}
			}
		});
	}
	
	private void addCircleListeners() {

		outCirc.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				mouseEvent.consume();
				setCursor(Cursor.DEFAULT);
				outCirc.setEffect(getGlow(Color.BLUE));
			}
		});
		
		outCirc.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				mouseEvent.consume();
				workflow.startDrawingLink(step);
			}
		});
		
		outCirc.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				mouseEvent.consume();
			}
		});
		
		outCirc.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				getParent().startFullDrag();
			}
		});
		
		outCirc.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				workflow.clearDrawingLink();
				outCirc.setEffect(null);
			}
		});

		outCirc.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override 
			public void handle(MouseEvent mouseEvent) {
				if (!isStartLinkIcon()) {
					outCirc.setEffect(null);
				}
			}
		});
	}

	private boolean isStartLinkIcon() {
		return ((workflow.getDrawingLink() != null) && (workflow.getDrawingLink().getStartStep().equals(step)));
	}
}

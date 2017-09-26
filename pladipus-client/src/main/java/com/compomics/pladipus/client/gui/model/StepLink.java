package com.compomics.pladipus.client.gui.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

public class StepLink {
	private WorkflowGuiStep startStep;
	private WorkflowGuiStep endStep;
	private Line line;
	private Polygon arrow;
	private Rotate rotate;
	private boolean drawn = false;
	
	public StepLink(WorkflowGuiStep startStep) {
		this.startStep = startStep;
		drawn = true;
		initLink();
	}
	
	public StepLink(WorkflowGuiStep startStep, WorkflowGuiStep endStep) {
		this.startStep = startStep;
		this.endStep = endStep;
		initLink();
		endLink();
	}
	
	public void setEndStep(WorkflowGuiStep endStep) {
		this.endStep = endStep;
		endLink();
	}
	
	private Circle getStartCircle() {
		return startStep.getIcon().getOutCircle();
	}
	
	private Circle getEndCircle() {
		return endStep.getIcon().getInCircle();
	}
	
	private double getArrowSize() {
		return startStep.getIcon().getSize() * 0.1;
	}
	
	private void initLink() {
		if (startStep != null) {
			line = new Line();
			line.setManaged(false);
			arrow = new Polygon();
			arrow.setManaged(false);
			line.startXProperty().bind(startStep.getIcon().layoutXProperty().add(getStartCircle().getCenterX()));
			line.startYProperty().bind(startStep.getIcon().layoutYProperty().add(getStartCircle().getCenterY()));
			line.setEndX(line.getStartX());
			line.setEndY(line.getStartY());
		    arrow.getPoints().addAll(new Double[]{
		            0.0, 0.0,
		            -getArrowSize(), getArrowSize() * 3,
		            getArrowSize(), getArrowSize() * 3 });
		    arrow.setFill(null);
		    arrow.setStroke(Color.BLACK);
		    rotate = new Rotate();
		    rotate.setPivotX(arrow.getLayoutX());
		    rotate.setPivotY(arrow.getLayoutY());
		    arrow.getTransforms().add(rotate);
		    updateArrow();
		    initLineListener();
		}
	}
	
	private void endLink() {
		if (endStep != null) {
			line.endXProperty().bind(endStep.getIcon().layoutXProperty().add(getEndCircle().getCenterX()));
			line.endYProperty().bind(endStep.getIcon().layoutYProperty().add(getEndCircle().getCenterY()));
		} else {
			line.endXProperty().unbind();
			line.endYProperty().unbind();
		}
	}
	
	public void updateLink(double newEndX, double newEndY) {
		line.setEndX(newEndX);
		line.setEndY(newEndY);
	}
	
	public Line getLine() {
		return line;
	}
	public Polygon getArrow() {
		return arrow;
	}
	
	private void updateArrow() {
		arrow.setTranslateX(line.getEndX());
		arrow.setTranslateY(line.getEndY());

		double xDiff = line.getEndX() - line.getStartX();
		double yDiff = line.getStartY() - line.getEndY();
		double degrees;
		if (yDiff == 0) {
			if (xDiff >=0) {
				degrees = 90;
			} else {
				degrees = -90;
			}
		} else {
			degrees = Math.toDegrees(Math.atan(xDiff/yDiff));
			if (yDiff < 0) degrees += 180;
		}
		rotate.setAngle(degrees);
	}
	
	private void initLineListener() {
		ChangeListener<Bounds> moveListener = new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> arg0, Bounds arg1, Bounds arg2) {
				updateArrow();
			}
		};
		line.boundsInParentProperty().addListener(moveListener);
	}
	
	public WorkflowGuiStep getStartStep() {
		return startStep;
	}
	public WorkflowGuiStep getEndStep() {
		return endStep;
	}
	
	public void toFront() {
		line.toFront();
		arrow.toFront();
	}
	
	public boolean isDrawn() {
		return drawn;
	}
}

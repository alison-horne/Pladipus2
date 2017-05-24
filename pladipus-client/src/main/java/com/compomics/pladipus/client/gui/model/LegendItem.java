package com.compomics.pladipus.client.gui.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LegendItem {
	private StringProperty toolName;
	private ObjectProperty<Rectangle> color;
	
	public LegendItem() {
		this(null, 0);
	}
	public LegendItem(String name, int colorId) {
		toolName = new SimpleStringProperty(name);
		color = new SimpleObjectProperty<Rectangle>(getLegendIcon(colorId));
	}
	
	public String getToolName() {
		return toolName.get();
	}
    public void setToolName(String name) {
        this.toolName.set(name);
    }
    public StringProperty toolNameProperty() {
        return toolName;
    }
    
    public Rectangle getColor() {
        return color.get();
    }
    public void setColor(int colorId) {
        color.set(getLegendIcon(colorId));
    }
    public ObjectProperty<Rectangle> colorProperty() {
        return color;
    }
	
	private Rectangle getLegendIcon(int colorId) {
		Rectangle rect = new Rectangle();
		rect.setWidth(25.0);
		rect.setHeight(25.0);
		rect.setArcHeight(8.0);
		rect.setArcWidth(8.0);
		rect.setStroke(Color.BLACK);
		rect.setFill(ToolColors.getColor(colorId));
		return rect;
	}
}

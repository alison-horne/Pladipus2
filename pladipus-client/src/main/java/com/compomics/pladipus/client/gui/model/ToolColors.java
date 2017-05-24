package com.compomics.pladipus.client.gui.model;

import javafx.scene.paint.Color;

public class ToolColors {

	private static final Color DEFAULT_COLOR = Color.BLUEVIOLET;
	private static final Color GLOBAL_COLOR = Color.DARKGREEN;
	
	private static final Color[] colors = new Color[] {
			Color.BLUE,
			Color.RED,
			Color.YELLOW,
			Color.BLACK,
			Color.DARKORANGE,
			Color.MEDIUMPURPLE,
			Color.DEEPSKYBLUE,
			Color.HOTPINK,
			Color.OLIVE,
			Color.BURLYWOOD,
			Color.WHITE,
			Color.AQUA
	};
	
	public static Color getColor(int i) {
		return colors[i%colors.length];
	}
	
	public static Color getDefaultColor() {
		return DEFAULT_COLOR;
	}
	
	public static Color getGlobalColor() {
		return GLOBAL_COLOR;
	}
}

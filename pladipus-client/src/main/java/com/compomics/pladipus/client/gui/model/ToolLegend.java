package com.compomics.pladipus.client.gui.model;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ToolLegend {
	
	private Map<String, Integer> toolStepCount = new HashMap<String, Integer>();
	private Map<String, Integer> toolColorMap = new HashMap<String, Integer>();
    private ObservableList<LegendItem> legendData = FXCollections.observableArrayList();
	
	public void clearLegend() {
		toolStepCount.clear();
		toolColorMap.clear();
		legendData.clear();
	}
	
	public int addTool(String toolName) {
		Integer count = toolStepCount.get(toolName);
		if (count != null) {
			count++;
		} else {
			count = 1;
			int colorId = getLowestFreeColorId();
			toolColorMap.put(toolName, colorId);
			legendData.add(new LegendItem(toolName, colorId));
		}
		toolStepCount.put(toolName, count);
		return toolColorMap.get(toolName);
	}
	
	public int getToolColorId(String toolName) {
		Integer colorId = toolColorMap.get(toolName);
		if (colorId != null) return colorId;
		return -1;
	}
	
	public void removeTool(String toolName) {
		Integer count = toolStepCount.get(toolName);
		if (count != null) {
			count--;
		}
		if (count == 0) {
			toolStepCount.remove(toolName);
			toolColorMap.remove(toolName);
			legendData.removeIf(e->e.getToolName().equals(toolName));
		} else {
			toolStepCount.put(toolName, count);
		}
	}
	
	private int getLowestFreeColorId() {
		int lowest = 0;
		while (toolColorMap.values().contains(lowest)) {
			lowest++;
		}
		return lowest;
	}
	
	public ObservableList<LegendItem> getLegendData() {
		return legendData;
	}
}
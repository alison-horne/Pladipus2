package com.compomics.pladipus.client.gui.model;

import java.util.ArrayList;
import java.util.List;

import com.compomics.pladipus.client.gui.model.GuiSubstitutions.DisplaySubstitution;
import com.compomics.pladipus.model.parameters.Substitution;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public abstract class ParameterGui {
	
	private List<String> initialValues;
	private List<String> values;
	private ObjectProperty<TextFlow> displayValue;
	private ObjectProperty<CheckBox> checkBox;
	private ObjectProperty<Button> editButton;
	private GuiSubstitutions subs;
	private boolean initialPerRun = false;
	private boolean perRun = false;
	private BooleanProperty invalidValue = new SimpleBooleanProperty(false);
	
	public ParameterGui() {
		this.displayValue = new SimpleObjectProperty<TextFlow>(null);
		this.checkBox = new SimpleObjectProperty<CheckBox>(null);
		this.editButton = new SimpleObjectProperty<Button>(null);
	}
	
	public ParameterGui(List<String> values, boolean perRun, GuiSubstitutions subs) {
		if (values != null) {
			initialValues = trimSpaces(values);
		} else {
			initialValues = new ArrayList<String>();
		}
		this.values = initialValues;
		this.initialPerRun = perRun;
		this.perRun = perRun;
		this.subs = subs;
		displayValue = new SimpleObjectProperty<TextFlow>(getDisplayValue());
		this.editButton = new SimpleObjectProperty<Button>(initEditButton());
		this.checkBox = new SimpleObjectProperty<CheckBox>(initCheckBox());
	}
	
	public BooleanProperty invalidValueProperty() {
		return invalidValue;
	}
	public boolean isPerRun() {
		return perRun;
	}
	public void setPerRun(boolean perRun) {
		checkBox.get().setSelected(perRun);
		this.perRun = perRun;
	}
	
	public void setDisplayValue() {
		displayValue.set(getDisplayValue());
	}
	public TextFlow getDisplayValue() {
		invalidValue.set(false);
		if (perRun) return getTextFlow("---");
        if ((values == null) || values.isEmpty()) return null;
        return getTextFlow(String.join(",", values));
	}
	public ObjectProperty<TextFlow> displayValueProperty() {
		return displayValue;
	}
	
    public ObjectProperty<CheckBox> checkBoxProperty() {
        return checkBox;
    }

    public ObjectProperty<Button> editButtonProperty() {
    	return editButton;
    }
    public Button getEditButton() {
    	return editButton.get();
    }
    public GuiSubstitutions getSubs() {
    	return subs;
    }
    public List<String> getValues() {
    	return values;
    }
    public void setValues(List<String> values) {
    	if (values == null) {
    		this.values.clear();
    	} else {
    		this.values = trimSpaces(values);
    	}
    	setDisplayValue();
    }
	
    private TextFlow getTextFlow(String vals) {
        List<Text> textList = new ArrayList<Text>();
        int subStart = vals.indexOf(Substitution.getPrefix());
        int subEnd = -Substitution.getEnd().length();
        while (subStart > -1) {
        	textList.add(getText(vals.substring(subEnd + Substitution.getEnd().length(), subStart)));
        	subEnd = vals.indexOf(Substitution.getEnd(), subStart);
        	textList.add(getSubText(vals.substring(subStart + Substitution.getPrefix().length(), subEnd)));
        	subStart = vals.indexOf(Substitution.getPrefix(), subEnd);
        }
        if (subEnd < vals.length() - Substitution.getEnd().length()) {
        	textList.add(getText(vals.substring(subEnd + Substitution.getEnd().length())));
        }
       	TextFlow textFlow = new TextFlow();
        textFlow.getChildren().setAll(textList);
        return textFlow;
    }
    
    private Text getText(String text) {
    	Text txt = new Text(text);
    	Font defaultFont = txt.getFont();
    	txt.setFont(Font.font(defaultFont.getFamily(), FontWeight.THIN, defaultFont.getSize()));
    	txt.setStroke(Color.GRAY);
    	return txt;
    }

    private Text getSubText(String text) {
    	DisplaySubstitution textSubs = subs.new DisplaySubstitution(text);
    	if (!textSubs.isValidSub()) {
    		invalidValue.set(true);
    		return getText(textSubs.getDisplayText());
    	}
    	Text txt = new Text(textSubs.getDisplayText());
		txt.setStroke(Color.BLACK);
		txt.setStrokeWidth(0.2);
    	txt.setFill(textSubs.getColor());
    	return txt;
    }
    
    private CheckBox initCheckBox() {
    	CheckBox checkBox = new CheckBox();
    	checkBox.setSelected(perRun);
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				perRun = newValue;
				setDisplayValue();
				editButton.get().setDisable(perRun);
			}
        	
        });
    	return checkBox;
    }
    
    private Button initEditButton() {
    	Button btn = new Button();
    	btn.setDisable(perRun);
    	return btn;
    }
    
    public boolean valueChanged() {
    	return (perRun != initialPerRun) || !((initialValues.size() == values.size()) && initialValues.containsAll(values));
    }
    
    private List<String> trimSpaces(List<String> vals) {
    	List<String> trimmed = new ArrayList<String>();
    	for (String val: vals) {
    		val = val.trim();
    		if (!val.isEmpty()) trimmed.add(val);
    	}
    	return trimmed;
    }
    
    public boolean isValid() {
    	return (perRun || (!values.isEmpty() && !invalidValue.get()));
    }
}

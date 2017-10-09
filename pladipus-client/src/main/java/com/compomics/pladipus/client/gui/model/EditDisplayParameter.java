package com.compomics.pladipus.client.gui.model;

import java.util.Iterator;

import org.springframework.util.StringUtils;

import com.compomics.pladipus.model.parameters.Substitution;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class EditDisplayParameter {
	private CheckBox chkBox;
	private HBox box;
	private Button insertBtn;
	private Button browseBtn;
	private HBox textStrip;
	private Node focusNode;
	private GuiSubstitutions subs;
	private ObjectProperty<HBox> textStripProperty;
	private ObjectProperty<EditDisplayParameter> selectedCell;
	private boolean header = false;

	public EditDisplayParameter(String initValue, GuiSubstitutions subs, ObjectProperty<EditDisplayParameter> selectedCell) {
		this(initValue, subs, selectedCell, false);
	}
	public EditDisplayParameter(String initValue, GuiSubstitutions subs, ObjectProperty<EditDisplayParameter> selectedCell, boolean header) {
		this.subs = subs;
		this.selectedCell = selectedCell;
		initTextStrip(initValue);
		textStripProperty = new SimpleObjectProperty<HBox>(textStrip);
		this.header = header;
	}
	
	public ObjectProperty<HBox> textStripProperty() {
		return textStripProperty;
	}
	
	public GuiSubstitutions getSubs() {
		return subs;
	}
	
	public EditDisplayParameter(String initValue, GuiSubstitutions subs, String insertButtonText, String browseButtonText) {
		this.subs = subs;
		box = new HBox();
		box.setMaxHeight(40);
		box.setMinHeight(40);
		box.setPrefHeight(40);
		chkBox = new CheckBox();
		box.getChildren().add(chkBox);
		initTextStrip(initValue);
		box.getChildren().add(textStrip);
		initInsertBtn(insertButtonText);
		box.getChildren().add(insertBtn);
		initBrowseBtn(browseButtonText);
		box.getChildren().add(browseBtn);
		HBox.setHgrow(textStrip, Priority.ALWAYS);
		HBox.setMargin(insertBtn, new Insets(5,5,5,5));
		HBox.setMargin(browseBtn, new Insets(5,5,5,5));
		HBox.setMargin(chkBox, new Insets(5,5,5,5));
	}
	
	public boolean isValid() {
		for (Node node: textStrip.getChildren()) {
			if (node instanceof ExpandField) {
				if (!((ExpandField) node).isValid()) return false;
			} else {
				if (!((SubsText) node).isValid()) return false;
			}
		}
		return true;
	}
	
	public String getValue() {
		String val = "";
		for (Node node: textStrip.getChildren()) {
			if (node instanceof ExpandField) {
				val += ((ExpandField) node).getText();
			} else {
				val += ((SubsText) node).getFullText();
			}
		}
		return val;
	}
	
	public boolean isSelected() {
		return chkBox.isSelected();
	}
	
	public HBox getBox() {
		return box;
	}
	private void initInsertBtn(String btnTxt) {
		if (insertBtn == null) {
			insertBtn = new Button();
			initBtnText(insertBtn, btnTxt);
		}
	}
	public Button getInsertBtn() {
		return insertBtn;
	}
	private void initBrowseBtn(String btnTxt) {
		if (browseBtn == null) {
			browseBtn = new Button();
			initBtnText(browseBtn, btnTxt);
		}
	}
	public Button getBrowseBtn() {
		return browseBtn;
	}
	private void initBtnText(Button btn, String btnTxt) {
		Text label = new Text(btnTxt);
		label.setFont(btn.getFont());
		btn.setMinWidth(label.getBoundsInLocal().getWidth() + 20);
		btn.setText(btnTxt);
	}
	public void disableEdit(boolean disable) {
		insertBtn.setDisable(disable);
		browseBtn.setDisable(disable);
		chkBox.setDisable(disable);
		for (Node str : textStrip.getChildren()) {
			if (str instanceof ExpandField) {
				((ExpandField) str).setEditable(!disable);
			}
		}
	}
	
	private void moveToNeighbourNode(Node node, boolean before) {
		ObservableList<Node> nodeList = textStrip.getChildrenUnmodifiable();
		int neighbour = -1;
		int nodeIndex = nodeList.indexOf(node);
		if (nodeIndex > -1) {
			if (before) {
				neighbour = nodeIndex - 1;
			} else {
				neighbour = nodeIndex + 1;
			}
		}
		if ((neighbour > -1) && (neighbour < nodeList.size())) {
			Node neighbourNode = nodeList.get(neighbour);
			neighbourNode.requestFocus();
			if (!before && neighbourNode instanceof ExpandField) {
				((ExpandField) neighbourNode).setPosCaret(0);
			}
		}
	}
	
	public void insertSub(String sub) {
		Node focus = getFocus();
		if (focus instanceof SubsText) {
			moveToNeighbourNode(focus, false);
			focus = getFocus();
		}
		if (focus instanceof ExpandField) {
			ExpandField ef = (ExpandField) focus;
			int insertIndex = textStrip.getChildren().indexOf(focus) + 1;
			int split = ef.getPosn();
			textStrip.getChildren().add(insertIndex, new ExpandField(ef.getText().substring(split)));
			textStrip.getChildren().add(insertIndex, new SubsText(sub));
			ef.setText(ef.getText().substring(0, split));
		} else {
			// TODO error...
		}
	}
	public void insertFileDir(String path) {
		Node focus = getFocus();
		if (focus instanceof SubsText) {
			moveToNeighbourNode(focus, false);
			focus = getFocus();
		}
		if (focus instanceof ExpandField) {
			ExpandField ef = (ExpandField) focus;
			int split = ef.getPosn();
			String newText = ef.getText().substring(0, split) + path + ef.getText().substring(split);
			ef.setText(newText);
		} else {
			// TODO error...
		}
	}
	private Node getFocus() {
		if (focusNode == null) {
			setLastNodeFocus();
		}
		return focusNode;
	}
	public HBox initTextStrip(String txt) {
		if (txt == null) txt = "";
		textStrip = new HBox();
		textStrip.setStyle("-fx-border-color:blue;-fx-border-radius: 10 10 10 10");
		textStrip.setPadding(new Insets(5,5,5,5));
		
        int subStart = txt.indexOf(Substitution.getPrefix());
        int subEnd = -Substitution.getEnd().length();
        while (subStart > -1) {
        	textStrip.getChildren().add(new ExpandField(txt.substring(subEnd + Substitution.getEnd().length(), subStart)));
        	subEnd = txt.indexOf(Substitution.getEnd(), subStart);
        	textStrip.getChildren().add(new SubsText(txt.substring(subStart, subEnd + Substitution.getEnd().length())));
        	subStart = txt.indexOf(Substitution.getPrefix(), subEnd);
        }

        textStrip.getChildren().add(new ExpandField(txt.substring(subEnd + Substitution.getEnd().length())));
        textStrip.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setLastNodeFocus();
				event.consume();
			}       	
        });
		return textStrip;
	}
	
	private void setSelectedCell() {
		if (selectedCell != null) {
			selectedCell.set(header? null : this);
		}
	}
	
	private void setLastNodeFocus() {
		focusNode = textStrip.getChildren().get(textStrip.getChildren().size() -1);
		focusNode.requestFocus();
		if (focusNode instanceof ExpandField) {
			((ExpandField) focusNode).setCaret();
		}
	}
	
	public void removeFocus() {
		focusNode = null;
	}
	
	class ExpandField extends TextField {
		final CaretPos pos = new CaretPos();
		public int getPosn() {
			return pos.pos;
		}
		public void setCaret() {
			setPosCaret(pos.pos);
		}
		public boolean isValid() {
			if (getText().contains(Substitution.getPrefix())) {
				if (!validSubEnds(getText())) return false;
		        int subStart = getText().indexOf(Substitution.getPrefix());
		        int subEnd = -Substitution.getEnd().length();
		        while (subStart > -1) {
		        	subEnd = getText().indexOf(Substitution.getEnd(), subStart);
		        	if (!subs.new DisplaySubstitution(getText().substring(subStart + Substitution.getPrefix().length(), subEnd)).isValidSub()) return false;
		        	subStart = getText().indexOf(Substitution.getPrefix(), subEnd);
		        }
			}
			return true;
		}
		public void setPosCaret(int posn) {
			pos.pos = posn;
			positionCaret(pos.pos);
		}
		void setFieldWidth(String text) {
			double width = 5.0;
			if (text != null && !text.isEmpty()) {
				Text testWidth = new Text(text);
				testWidth.setFont(getFont());
				width = testWidth.getBoundsInLocal().getWidth() + 1;
			}
	        setMaxWidth(width);
	        setPrefWidth(width);
	        setMinWidth(width);
		}
		
		ExpandField(String text) {		
			setPadding(new Insets(0,0,0,0));
			setStyle("-fx-background-color:transparent;-fx-text-inner-color:grey;");
			setText(text);
			setFieldWidth(text);
			
			textProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(ObservableValue<? extends String> observable,
			            String oldValue, String newValue) {
			    	setFieldWidth(newValue);
			    }
			});
			
			focusedProperty().addListener(new ChangeListener<Boolean>() {

                @SuppressWarnings("unchecked")
				@Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue.booleanValue()) {
                        focusNode = (Node) ((ReadOnlyProperty<Boolean>) observable).getBean();
                        pos.pos = getText().length();
                        setSelectedCell();
                    } else {
                    	pos.pos = getCaretPosition();
                    }
                }
            });
			
			setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.RIGHT) && (getCaretPosition() == getText().length())) {
						moveToNeighbourNode((Node)event.getSource(), false);
					} else if ((event.getCode().equals(KeyCode.LEFT) || event.getCode().equals(KeyCode.BACK_SPACE)) && (getCaretPosition() == 0)) {
						moveToNeighbourNode((Node)event.getSource(), true);
					}
				}
			});
		}
		
	}
	
	class CaretPos { int pos = 0; }
	
	class SubsText extends Label {
		GuiSubstitutions.DisplaySubstitution displaySub;
		
		public String getFullText() {
			return displaySub.getFullText();
		}
		
		public boolean isValid() {
			if (displaySub != null) return displaySub.isValidSub();
			return false;
		}
		
		SubsText(String text) {
			displaySub = subs.new DisplaySubstitution(text);
			Text txt = new Text(displaySub.getDisplayText());
			txt.setStroke(Color.BLACK);
			txt.setStrokeWidth(0.2);
			txt.setFill(displaySub.getColor());
			setGraphic(txt);
			setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					event.consume();
					requestFocus();
				}});
			
			setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.DELETE) || event.getCode().equals(KeyCode.BACK_SPACE)) {
						remove((SubsText) event.getSource());
					} else if (event.getCode().equals(KeyCode.RIGHT)) {
						moveToNeighbourNode((Node)event.getSource(), false);
					} else if (event.getCode().equals(KeyCode.LEFT)) {
						moveToNeighbourNode((Node)event.getSource(), true);
					}
				}
			});
			
			focusedProperty().addListener(new ChangeListener<Boolean>() {

                @SuppressWarnings("unchecked")
				@Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue.booleanValue()) {
                        setStyle("-fx-border-color:red");
                        focusNode = (Node) ((ReadOnlyProperty<Boolean>) observable).getBean();
                        setSelectedCell();
                    } else {
                        setStyle("-fx-border-color:transparent");
                    }
                }
            });
		}
	}
	
	private void remove(SubsText sub) {
		Iterator<Node> iter = textStrip.getChildren().iterator();
		ExpandField previous = null;
		ExpandField following = null;
		while (iter.hasNext()) {
			Node n = iter.next();
			if (n == sub) {
				iter.remove();
				Node next = iter.next();
				if (next instanceof ExpandField) {
					following = (ExpandField) next;
				}
				break;
			} else {
				if (n instanceof ExpandField) {
					previous = (ExpandField) n;
				} else {
					previous = null;
				}
			}
		}
		
		if (following != null && previous != null) {
			textStrip.getChildren().remove(following);
			int caretPos = previous.getText().length();
			previous.setText(previous.getText() + following.getText());
			previous.requestFocus();
			previous.setPosCaret(caretPos);
		}
	}
	
	private boolean validSubEnds(String valueString) {
		int startCount = StringUtils.countOccurrencesOf(valueString, Substitution.getPrefix());
		int endCount = StringUtils.countOccurrencesOf(valueString, Substitution.getEnd());
		if (startCount != endCount) return false;
		int subStart = 0;
		int subEnd = 0;
        for (int i = 0; i < startCount; i++) {
        	subStart = valueString.indexOf(Substitution.getPrefix(), subEnd);
        	subEnd = valueString.indexOf(Substitution.getEnd(), subStart);
        	if (subStart < 0 || subEnd < 0) return false;
        }
        return true;
	}
}

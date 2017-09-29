package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.DefaultOverview;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.client.gui.model.ToolColors;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.WindowEvent;

public class UserDefaultsController extends FxmlController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private Label headerLabel;
	@FXML
	private TableView<DefaultOverview> valueTable;
	@FXML
	private TableColumn<DefaultOverview, String> nameColumn;
	@FXML
	private TableColumn<DefaultOverview, String> valueColumn;
	@FXML
	private TableColumn<DefaultOverview, String> typeColumn;
	@FXML
	private TableColumn<DefaultOverview, Boolean> allUsersColumn;
	@FXML
	private Button okBtn;
	@FXML
	private Button acceptBtn;
	@FXML
	private Button cancelBtn;
    private String subValue;
	
	@FXML
	public void initialize() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
		typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		allUsersColumn.setCellValueFactory(cellData -> cellData.getValue().globalProperty());
        allUsersColumn.setCellFactory(CheckBoxTableCell.forTableColumn(allUsersColumn));
        nameColumn.setCellFactory(cellData -> new TableCell<DefaultOverview, String>() {
        	@Override
        	protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setTextFill(ToolColors.getDefaultColor());
					setText(item);
				} else {
					setText(null);
				}
        	}
        });
	}
	
	private void setupSelect() {
		headerLabel.setText(resources.getString("userdefaults.selectHeader"));
		buttonActive(okBtn, false);
		buttonActive(acceptBtn, true);
		buttonActive(cancelBtn, true);
	}
	private void setupShow() {
		headerLabel.setText(resources.getString("userdefaults.header"));
		buttonActive(okBtn, true);
		buttonActive(acceptBtn, false);
		buttonActive(cancelBtn, false);
	}
	private void buttonActive(Button btn, boolean active) {
		btn.setDisable(!active);
		btn.setVisible(active);
	}
		
	@Override
	public void setup(Object obj) {
		boolean select = (boolean) obj;
		valueTable.setItems(guiControl.getUserDefaults());
		if (select) {
			setupSelect();
		} else {
			setupShow();
		}
	}
	
	@FXML
	public void handleAccept() {
		DefaultOverview selected = valueTable.getSelectionModel().getSelectedItem();
		if (selected != null) {
			subValue = selected.getFullDefaultName();
			stage.close();
		} else {
			error(resources.getString("userdefaults.noSelection"));
		}
	}
	
	@FXML
	public void handleCancel() {
		subValue = null;
		stage.close();
	}
	
	@FXML
	public void handleAdd() {
		DefaultOverview def = (DefaultOverview) getFromScene(PladipusScene.NEW_DEFAULT);
		if (def != null) {
			valueTable.getSelectionModel().select(def);
		}
	}
	
    @Override
    public void postShow() throws PladipusReportableException {
    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				handleCancel();
			}
    	});
    }
	
	@Override
	public Object returnObject() {
		return subValue;
	}
}

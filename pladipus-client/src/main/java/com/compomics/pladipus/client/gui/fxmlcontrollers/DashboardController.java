package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.model.core.BatchOverview;
import com.compomics.pladipus.model.core.RunOverview;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.model.core.WorkflowOverview;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController extends FxmlController {

    @FXML
    private TableView<WorkflowOverview> workflowTable;
    @FXML
    private TableColumn<WorkflowOverview, String> workflowColumn;
    @FXML
    private TableView<BatchOverview> batchTable;
    @FXML
    private TableColumn<BatchOverview, String> batchNameColumn;
    @FXML
    private TableColumn<BatchOverview, Number> batchRunsColumn;
    
    @FXML
    private ResourceBundle resources;
    @FXML
    private Label userLabel;
	
    @FXML
    public void initialize() {
    	workflowColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    	batchNameColumn.setCellValueFactory(cellData -> cellData.getValue().batchNameProperty());
    	batchRunsColumn.setCellValueFactory(cellData -> cellData.getValue().runSizeProperty());
        showBatchDetails(null);
        workflowTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        workflowTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldWorkflow, newWorkflow) -> showBatchDetails(newWorkflow));
    }
    
    private void showBatchDetails(WorkflowOverview workflow) {
    	if (workflow == null) {
    		batchTable.setPlaceholder(new Label(resources.getString("dashboard.batchPlaceholder")));
    		batchTable.setItems(FXCollections.emptyObservableList());
    	} else {
    		batchTable.setPlaceholder(new Label(resources.getString("dashboard.batchWorkflowPlaceholder")));
    		batchTable.setItems(workflow.getBatches());
    	} 
    }
    
    @FXML
    public void handleNewWorkflow() {
    	nextScene(PladipusScene.NEW_WORKFLOW, true);
    }
    
    @FXML
    public void handleEditWorkflow() {
    	WorkflowOverview selected = workflowTable.getSelectionModel().getSelectedItem();
    	if (selected == null) {
    		nextScene(PladipusScene.EDIT_WORKFLOW, true);
    	} else {
    		nextScene(PladipusScene.WORKFLOW, true, guiControl.getWorkflow(selected.getName()));
    	}
    }
    
    @FXML
    public void handleDeleteWorkflow() {
    	WorkflowOverview selected = workflowTable.getSelectionModel().getSelectedItem();
    	if (selected == null) {
    		selected = guiControl.choiceWorkflow(stage);
    	}
    	if (selected != null) {
			if (alert("deleteWorkflow")) {			
				final WorkflowOverview deleteWorkflow = selected;
	    		LoadingTask<Void> deleteTask = new LoadingTask<Void>(resources.getString("dashboard.deleteWorkflowText"), resources.getString("dashboard.failedWorkflowDelete")) {
					@Override
					public Void doTask() throws Exception {
						guiControl.deleteWorkflow(deleteWorkflow);
						return null;
					}
	    		};
			    deleteTask.run();
			}
    	}
    }
    
    @FXML
    public void handleViewBatch() {
    	BatchOverview batch = getBatch();
    	if (batch != null) {
	    	LoadingTask<List<RunOverview>> statusTask = new LoadingTask<List<RunOverview>>(resources.getString("dashboard.loadingBatch"), null) {
	    		@Override
	    		public List<RunOverview> doTask() throws Exception {
	    			return guiControl.getBatchStatus(batch);
	    		}
	    		
	    		@Override
	    		public void onSuccess() {
	    			nextScene(PladipusScene.BATCH_VIEW, true, batch, returned);
	    		}
	    	};
	    	statusTask.run();
    	}
    }
	@FXML
	public void handleDeleteBatch() {
		BatchOverview batch = getBatch();
		if (batch != null && alert("deleteBatch")) {
			LoadingTask<Void> deleteTask = new LoadingTask<Void>(resources.getString("dashboard.deleteBatchText"), resources.getString("dashboard.failedBatchDelete")) {
				@Override
				public Void doTask() throws Exception {
					guiControl.deleteBatch(batch);
					return null;
				}
			};
			deleteTask.run();
		}
	}
	@FXML
	public void handleNewBatch() {
    	WorkflowOverview workflow = workflowTable.getSelectionModel().getSelectedItem();
    	if (workflow == null) {
    		workflow = guiControl.choiceWorkflow(stage);
    	}
    	if (workflow != null) {
        	if (workflow.getHeaders() == null) {
        		error(resources.getString("workflow.batchNoHeaders"));
        	} else if (workflow.getHeaders().size() == 1) {
        		final WorkflowOverview wo = workflow;
        		String header = wo.getHeaders().get(0);
        		String batchName = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        		LoadingTask<Void> singleTask = new LoadingTask<Void>(resources.getString("workflow.singleRunSave"), null) {
    				@Override
    				public Void doTask() throws Exception {
    					guiControl.loadBatchData(wo, batchName, header + "\nr1", true);
    					return null;
    				}
    				@Override
    				public void onSuccess() {
    			    	infoAlert("singleRun", true);
    				}
        		};
        		singleTask.run();
        	} else {
        		nextScene(PladipusScene.BATCH_LOAD, true, workflow);
        	}
    	}
	}
	
	private BatchOverview getBatch() {
		BatchOverview bo = batchTable.getSelectionModel().getSelectedItem();
		if (bo == null) {
	    	WorkflowOverview workflow = workflowTable.getSelectionModel().getSelectedItem();
	    	if (workflow == null) {
	    		workflow = guiControl.choiceWorkflow(stage);
	    	}
	    	if (workflow != null) {
	    		bo = guiControl.choiceBatch(workflow, stage);
	    	}
		}
		return bo;
	}
    
    @FXML
    public void logoutConfirm() {
    	if (alert("logout")) {
    		sceneControl.logout();
    		guiControl.logout();
    	    nextScene(PladipusScene.LOGIN, false);
    	}
    }
	
    @Override
	public void setupController() throws PladipusReportableException {
		workflowTable.setItems(guiControl.getUserWorkflows());
		userLabel.setText(guiControl.getUserName());
	}
    
    @FXML
    public void handleViewDefaults() {
    	nextScene(PladipusScene.USER_DEFAULTS, true, false);
    }
}

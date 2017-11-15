package com.compomics.pladipus.client.gui.fxmlcontrollers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import com.compomics.pladipus.client.gui.FxmlController;
import com.compomics.pladipus.client.gui.model.PladipusScene;
import com.compomics.pladipus.model.core.BatchOverview;
import com.compomics.pladipus.model.core.BatchRunOverview;
import com.compomics.pladipus.model.core.RunOverview;
import com.compomics.pladipus.model.core.RunStepOverview;
import com.compomics.pladipus.model.core.WorkerRunOverview;
import com.compomics.pladipus.model.persist.RunStatus;
import com.compomics.pladipus.shared.PladipusReportableException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class BatchViewController extends FxmlController {
	
	@FXML
	private Label batchName;
	@FXML
	private TreeTableView<TableContent> runsTable;
	@FXML
	private TreeTableColumn<TableContent, String> nameColumn;
	@FXML
	private TreeTableColumn<TableContent, String> statusColumn;
	@FXML
	private TreeTableColumn<TableContent, String> outputColumn;
	@FXML
	private TreeTableColumn<TableContent, HBox> buttonsColumn;
	@FXML
	private Button stopBtn;
    @FXML
    private ResourceBundle resources;
    private BatchOverview batch;
    private List<RunOverview> runs;
    private BooleanProperty inProgress;
    private String NOT_RUN, QUEUED, IN_PROGRESS, CANCELLED, CANCELLING, COMPLETED, ERROR, RETRY;
    private List<String> statusOrder;
    
	@FXML
	public void initialize() {
		stopBtn.setDisable(true);
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().idProperty());
		statusColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().statusProperty());
		outputColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().outputProperty());
		buttonsColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().buttonsProperty());
		outputColumn.setCellFactory(tc -> {
			TreeTableCell<TableContent, String> cell = new TreeTableCell<TableContent, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (item != null) {
						setText(item);
						if (getTreeTableView().getTreeItem(getIndex()).getValue().isError()) {
							setTextFill(Color.RED);
						} else {
							setTextFill(Color.BLACK);
						}
					} else {
						setText(null);
					}
				}
			};
            cell.setOnMouseClicked(e -> {
            	if (e.getClickCount() > 1) {
	                if (!cell.isEmpty() && cell.getItem() != null && !cell.getItem().isEmpty()) {
	                    infoAlert(runsTable.getTreeItem(cell.getIndex()).getValue().getId(), cell.getText(), false);
	                }
            	}
            });
            return cell ;
		});
		inProgress = new SimpleBooleanProperty();
		inProgress.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				stopBtn.setDisable(!newValue);
			}
		});
		NOT_RUN = resources.getString("batchview.noRunStatus");
		QUEUED = resources.getString("batchview.queueStatus");
		IN_PROGRESS = resources.getString("batchview.progressStatus");
		CANCELLED = resources.getString("batchview.cancelledStatus");
		CANCELLING = resources.getString("batchview.cancellingStatus");
		COMPLETED = resources.getString("batchview.completedStatus");
		ERROR = resources.getString("batchview.errorStatus");
		RETRY = resources.getString("batchview.retryStatus");
		statusOrder = Arrays.asList(NOT_RUN, COMPLETED, QUEUED, IN_PROGRESS, RETRY, ERROR, CANCELLED);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setup(Object...objects) throws PladipusReportableException {
		batch = (BatchOverview) objects[0];
		runs = (List<RunOverview>) objects[1];
		batchName.setText(batch.getBatchName());
		setupTable();
	}

	@FXML
	public void handleRunBatch() {
		if (!inProgress.get() || alert("restartBatch")) {
			LoadingTask<Void> runBatchTask = new LoadingTask<Void>(resources.getString("batchview.runBatch"), resources.getString("batchview.failedBatchRun")) {
				@Override
				public Void doTask() throws Exception {
					guiControl.runBatch(batch.getId());
					return null;
				}
				
				@Override
				public void onSuccess() {
					handleRefresh();
				}
			};
			runBatchTask.run();
		}
	}
	
	@FXML
	public void handleStopBatch() {
		if (alert("abortBatch")) {
			LoadingTask<Void> abortTask = new LoadingTask<Void>(resources.getString("batchview.abortRunLoading"), null) {
				@Override
				public Void doTask() throws Exception {
					guiControl.abortBatch(batch.getId());
					return null;
				}
				
				@Override
				public void onSuccess() {
					handleRefresh();
				}
			};
			abortTask.run();
		}
	}
	
	@FXML
	public void handleDeleteBatch() {
		if (alert("deleteBatch")) {
			LoadingTask<Void> deleteTask = new LoadingTask<Void>(resources.getString("batchview.deleteBatchText"), resources.getString("batchview.failedBatchDelete")) {
				@Override
				public Void doTask() throws Exception {
					guiControl.deleteBatch(batch);
					return null;
				}
				
				@Override
				public void onSuccess() {
					close();
				}
			};
			deleteTask.run();
		}
	}
	
	@FXML
	public void handleOK() {
		close();
	}
	
	@FXML
	public void handleRefresh() {
		LoadingTask<List<RunOverview>> refreshTask = new LoadingTask<List<RunOverview>>(resources.getString("batchview.refreshBatch"), null) {
    		@Override
    		public List<RunOverview> doTask() throws Exception {
    			return guiControl.getBatchStatus(batch);
    		}
    		
    		@Override
    		public void onSuccess() {
    			runs = returned;
    			setupTable();
    		}
		};
		refreshTask.run();
	}
	
	private void setupTable() {	
		inProgress.set(false);
		TreeItem<TableContent> root = new TreeItem<TableContent>();
		for (BatchRunOverview batchRun: batch.getRuns()) {
			root.getChildren().add(getRunEntry(batchRun));
		}
		runsTable.setRoot(root);
		runsTable.setShowRoot(false);
	}
	
	private TreeItem<TableContent> getRunEntry(BatchRunOverview batchRun) {
		RunOverview ro = getRun(batchRun);
		TableContent batchContent = new TableContent(batchRun);
		TreeItem<TableContent> runLine = new TreeItem<TableContent>(batchContent);
		String stat = NOT_RUN;
		if (ro != null) {
			int err = 0;
			int finished = 0;
			for (RunStepOverview rso: ro.getSteps()) {
				TableContent stepContent = new TableContent(rso);
				if (stepContent.isError()) err++;
				if (stepContent.isCompleted()) finished++;
				if (statusOrder.indexOf(stepContent.getStatus()) > statusOrder.indexOf(stat)) stat = stepContent.getStatus();
				runLine.getChildren().add(new TreeItem<TableContent>(stepContent));
			}
			if (err > 0) batchContent.setError(true);
			batchContent.setOutput(resources.getString("batchview.totalSteps") + ": " + ro.getSteps().size() + "; " +
								   resources.getString("batchview.totalCompleted") + ": " + finished + "; "+ 
								   resources.getString("batchview.totalError") + "; " + err);
			if (ro.getStatus().equals(RunStatus.ABORT)) stat = CANCELLING;
		}
		batchContent.setStatus(stat);
		if (stat.equals(CANCELLED) || stat.equals(COMPLETED) || stat.equals(CANCELLING) || stat.equals(ERROR) || stat.equals(NOT_RUN)) batchContent.setCompleted(true);
		return runLine;
	}
	
	private RunOverview getRun(BatchRunOverview batchRun) {
		if (runs != null) {
			for (RunOverview ro: runs) {
				if (ro.getBatchRunId() == batchRun.getId()) return ro;
			}
		}
		return null;
	}
	
	class TableContent {
		StringProperty id;
		StringProperty status;
		StringProperty output;
		ObjectProperty<HBox> buttons;
		boolean error = false;
		boolean progress = false;
		BooleanProperty completed = new SimpleBooleanProperty(false);
		
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public boolean isCompleted() {
			return completed.get();
		}
		public void setCompleted(boolean completed) {
			this.completed.set(completed);
		}
		public boolean isInProgress() {
			return progress;
		}
		
		public StringProperty idProperty() {
			return id;
		}
		public String getId() {
			return id.get();
		}
		public void setId(String id) {
			this.id.set(id);
		}
		public StringProperty statusProperty() {
			return status;
		}
		public String getStatus() {
			return status.get();
		}
		public void setStatus(String status) {
			this.status.set(status);
		}
		public StringProperty outputProperty() {
			return output;
		}
		public String getOutput() {
			return output.get();
		}
		public void setOutput(String output) {
			this.output.set(output);
		}
		public ObjectProperty<HBox> buttonsProperty() {
			return buttons;
		}
		
		public TableContent(RunStepOverview rso) {
			initProperties();
			setId(rso.getName() + ": " + rso.getTool());
			setStatus(mapStepStatus(rso));
			setOutput(getRunStepOutput(rso));
			addButton(viewWorkerBtn(rso));
			if (progress) inProgress.set(true); 
		}
		
		public TableContent(BatchRunOverview bro) {
			initProperties();
			setId(bro.getName());
			addButton(viewParamBtn(bro));
			addButton(runBtn(bro.getId(), batch.getId()));
			addButton(abortBtn(bro.getId()));	
		}
		
		void initProperties() {
			status = new SimpleStringProperty(null);
			id = new SimpleStringProperty(null);
			output = new SimpleStringProperty(null);
			HBox hbox = new HBox();
			hbox.setPadding(new Insets(5,5,5,5));
			hbox.setSpacing(10);
			buttons = new SimpleObjectProperty<HBox>(hbox);
		}
		
		private Button viewWorkerBtn(RunStepOverview rso) {
			Button workerBtn = new Button(resources.getString("batchview.viewWorkersBtn"));
			workerBtn.setDisable(rso.getWorkers().isEmpty());
			workerBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					nextScene(PladipusScene.WORKER, true, rso);
				}
			});
			return workerBtn;
		}
		
		private Button viewParamBtn(BatchRunOverview bro) {
			Button paramBtn = new Button(resources.getString("batchview.viewParamBtn"));
			paramBtn.setDisable(bro.getParameters().isEmpty());
			paramBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					nextScene(PladipusScene.BATCH_PARAMS, true, bro);
				}
			});
			return paramBtn;
		}
		
		private Button runBtn(long id, long batchId) {
			Button runBtn = new Button(resources.getString("batchview.runRunBtn"));
			runBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					if (completed.get() || alert("restartRun")) {
						LoadingTask<Void> runRunTask = new LoadingTask<Void>(resources.getString("batchview.runRun"), resources.getString("batchview.failedRunRun")) {
							@Override
							public Void doTask() throws Exception {
								guiControl.runBatchRun(id, batchId);
								return null;
							}
							@Override
							public void onSuccess() {
								handleRefresh();
							}
						};
						runRunTask.run();
					}
				}
			});
			return runBtn;
		}
		
		private Button abortBtn(long id) {
			Button abortBtn = new Button(resources.getString("batchview.abortRunBtn"));
			abortBtn.setDisable(completed.get());
			completed.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
					abortBtn.setDisable(newVal);
				}
			});
			abortBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					if (alert("abortRun")) {
						LoadingTask<Void> abortRunTask = new LoadingTask<Void>(resources.getString("batchview.abortRunLoading"), null) {
							@Override
							public Void doTask() throws Exception {
								guiControl.abortBatchRun(id);
								return null;
							}
							@Override
							public void onSuccess() {
								handleRefresh();
							}
						};
						abortRunTask.run();
					}
				}
			});
			return abortBtn;
		}
		
		private void addButton(Button btn) {
			buttons.get().getChildren().add(btn);
		}
		
		private String mapStepStatus(RunStepOverview rso) {
			switch (rso.getStatus()) {
				case ABORT:
				case CANCELLED:
					setCompleted(true);
					return CANCELLED;
				case COMPLETE:
					setCompleted(true);
					return COMPLETED;
				case ERROR:
					error = true;
					setCompleted(true);
					return ERROR;
				case IN_PROGRESS:
				case ON_WORKER:
					progress = true;
					if (wasError(rso)) return RETRY;
					return IN_PROGRESS;
				case QUEUED:
				case BLOCKED:
				case READY:
					progress = true;
					if (wasError(rso)) return RETRY;
					return QUEUED;
				default:
					return "";
			}
		}
		
		private boolean wasError(RunStepOverview rso) {
			for (WorkerRunOverview run : rso.getWorkers()) {
				if (run.getErrorMsg() != null) return true;
			}
			return false;
		}
		
		private String getRunStepOutput(RunStepOverview rso) {
			Set<String> outputs = new HashSet<String>();
			Set<String> errors = new HashSet<String>();
			if (!rso.getWorkers().isEmpty()) {
				for (WorkerRunOverview worker: rso.getWorkers()) {
					if (!worker.getOutputs().isEmpty()) {
						for (Entry<String, String> entry: worker.getOutputs().entrySet()) {
							outputs.add(entry.getKey() + ": " + entry.getValue());
						}
					}
					if (worker.getErrorMsg() != null && !worker.getErrorMsg().isEmpty()) {
						errors.add(worker.getErrorMsg());
					}
				}
			}
			if (!outputs.isEmpty()) {
				error = false;
				return String.join("\n", outputs);
			}
			if (!errors.isEmpty()) {
				error = true;
				return String.join("\n", errors);
			}
			return null;
		}
	}
}

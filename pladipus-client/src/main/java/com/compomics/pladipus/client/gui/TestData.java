package com.compomics.pladipus.client.gui;

import java.util.HashSet;
import java.util.Set;

import com.compomics.pladipus.client.gui.model.BatchOverview;
import com.compomics.pladipus.client.gui.model.DefaultGui;
import com.compomics.pladipus.client.gui.model.WorkflowOverview;
import com.compomics.pladipus.model.core.ToolInformation;
import com.compomics.pladipus.model.persist.Workflow;

public class TestData {
	private final static String USER1 = "test1";
	private final static String USER2 = "test2";
	private final static String PW1 = "password1";
	private final static String PW2 = "password2";
	private static Set<WorkflowOverview> USER1_WORKFLOWS = new HashSet<WorkflowOverview>();
	private static Set<ToolInformation> TOOLS = new HashSet<ToolInformation>();
	private static Set<DefaultGui> DEFS = new HashSet<DefaultGui>();
	private static ToolInformation TOOL1 = new ToolInformation("Tool1");
	private static ToolInformation TOOL2 = new ToolInformation("Tool2");
	private static ToolInformation TOOL3 = new ToolInformation("Tool3");
	private static ToolInformation TOOL4 = new ToolInformation("Tool4");
	private static ToolInformation TOOL5 = new ToolInformation("Tool5");
	private static DefaultGui DEF1 = new DefaultGui("user_def1", "def_val1", null, false);
	private static DefaultGui DEF2 = new DefaultGui("user_def2", "def_val2", "type1", false);
	private static DefaultGui SHARED_DEF1 = new DefaultGui("shared_def1", "shared_val1", "type1", true);
	private static DefaultGui SHARED_DEF2 = new DefaultGui("shared_def2", "shared_val2", "type2", true);
	private static Workflow WF1 = new Workflow();
	private static Workflow WF2 = new Workflow();
	private static Workflow WF3 = new Workflow();
	private static WorkflowOverview WG1;
	private static WorkflowOverview WG2;
	private static WorkflowOverview WG3;
	private static BatchOverview BATCH1_1 = new BatchOverview("Batch_one");
	private static BatchOverview BATCH1_2 = new BatchOverview("Batch_two");
	private static BatchOverview BATCH1_3 = new BatchOverview("Batch_three");
	private static BatchOverview BATCH2_1 = new BatchOverview("SuperBatch");
	
	public static boolean login(String user, String pw) {
		if (user.equals(USER1)) {
			if (pw.equals(PW1)) return true;
		} else if (user.equals(USER2)) {
			if (pw.equals(PW2)) return true;
		}
		return false;
	}
	
	public static Set<WorkflowOverview> getWorkflows(String username) {
		if (username.equals(USER1)) {
			if (USER1_WORKFLOWS.isEmpty()) {
				initWorkflows();			
			}
			return USER1_WORKFLOWS;
		} 
		return new HashSet<WorkflowOverview>();
	}
	
	public static Set<ToolInformation> getTools() {
		if (TOOLS.isEmpty()) {
			initTools();
		}
		return TOOLS;
	}
	
	public static Set<DefaultGui> getDefaults(String username) {
		if (DEFS.isEmpty()) {
			initDefaults(username);
		}
		return DEFS;
	}
	
	private static void initWorkflows() {
		WF1.setActive(true);
		WF2.setActive(true);
		WF3.setActive(true);
		WF1.setId(1L);
		WF2.setId(2L);
		WF3.setId(3L);
		WF1.setName("wf1");
		WF2.setName("wf2");
		WF3.setName("wf3");
		WF1.setTemplateXml(getTestWfXml());
		WG1 = new WorkflowOverview(WF1.getName(), WF1.getTemplateXml());
		WG1.addBatch(BATCH1_1);
		WG1.addBatch(BATCH1_2);
		WG1.addBatch(BATCH1_3);
		WG2 = new WorkflowOverview(WF2.getName(), WF2.getTemplateXml());
		WG2.addBatch(BATCH2_1);
		WG3 = new WorkflowOverview(WF3.getName(), WF3.getTemplateXml());
		USER1_WORKFLOWS.add(WG1);
		USER1_WORKFLOWS.add(WG2);
		USER1_WORKFLOWS.add(WG3);
	}
	
	private static void initTools() {
		TOOLS.add(TOOL1);
		TOOLS.add(TOOL2);
		TOOLS.add(TOOL3);
		TOOLS.add(TOOL4);
		TOOLS.add(TOOL5);
	}
	
	private static void initDefaults(String username) {
		DEFS.add(SHARED_DEF1);
		DEFS.add(SHARED_DEF2);
		if (username.equals(USER1)) {
			DEFS.add(DEF1);
			DEFS.add(DEF2);
		}
	}
	
	private static String getTestWfXml() {
		StringBuilder builder = new StringBuilder();
		builder.append("<template name=\"wf1\">");
		builder.append("<steps>");
		builder.append("<step>");
		builder.append("<id>s1</id>");
		builder.append("<name>Tool1</name>");
		builder.append("</step>");
		builder.append("<step>");
		builder.append("<id>s2</id>");
		builder.append("<name>Tool3</name>");
		builder.append("</step>");
		builder.append("</steps>");
		builder.append("</template>");
		return builder.toString();
	}
}

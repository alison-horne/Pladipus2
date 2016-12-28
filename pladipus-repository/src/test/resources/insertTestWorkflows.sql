use pladipus2;
-- Invalid template xml for now, to complete when validation tests required
INSERT INTO workflows (workflow_id, workflow_name, template, user_id, active) 
VALUES (1, 'test_workflow1', 'template1', 1, true);
INSERT INTO workflows (workflow_id, workflow_name, template, user_id, active) 
VALUES (2, 'test_workflow2', 'template2', 1, false);
INSERT INTO workflows (workflow_id, workflow_name, template, user_id, active) 
VALUES (3, 'test_workflow3', 'template3', 1, true);
INSERT INTO workflows (workflow_id, workflow_name, template, user_id, active) 
VALUES (4, 'test_workflow3', 'template1', 2, true);
INSERT INTO workflows (workflow_id, workflow_name, template, user_id, active) 
VALUES (5, 'test_workflow4', 'template1', 2, true);
INSERT INTO workflows (workflow_id, workflow_name, template, user_id, active) 
VALUES (6, 'test_workflow4', 'template1', 2, true);
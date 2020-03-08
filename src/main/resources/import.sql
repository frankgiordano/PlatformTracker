delete from REFERENCE_DATA 
commit;

--update resolution_project set owners='Frank.Giordano', rd_status_id= 43, rd_pdlc_status_id=161 where project_id<80
--update resolution_project set owners='Frank.Giordano', rd_status_id= 43, rd_pdlc_status_id=161 where project_id<50;
--update resolution_project set owners='Frank.Giordano', rd_status_id= 43, rd_pdlc_status_id=161 where project_id<30;

Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (40,'Open','Open',3);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (41,'Closed','Closed',3);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (42,'Hold','Hold',3);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (43,'Deferred','Deferred',3);

---Root Cause Resource
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (91,'Environment','Environment',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (81,'JVM Heap','JVM Heap',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (82,'CPU','CPU',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (83,'Storage','Storage',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (84,'Memory/Database','Memory/Database',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (85,'File','File',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (86,'Internet','Internet',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (87,'VPN Connectivity','VPN Connectivity',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (88,'Vendor Lan','RTS Lan',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (89,'Vendor Link','Vendor Link',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (90,'Vendor System','Vendor System',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (80,'Vendor Software','Vendor Software',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (92,'O/S','O/S',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (93,'Web Server','Web Server',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (94,'App Server','App Server',5);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (95,'Other','Other',5);

---Root Cause Category
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (70,'Problematic New Deployment','Problematic New Deployment',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (61,'Malfunction','Malfunction',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (62,'Deployment Execution Error','Insufficient Capacity',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (63,'Insufficient Regression Testing','Insufficient Regression Testing',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (64,'Requirement Miss','Requirement Miss',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (65,'Improper Exception Handling','Improper Exception Handling',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (66,'Network Issue','Network Issue',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (67,'Insufficient Capacity','Insufficient Capacity',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (68,'Misconfiguration','IMisconfiguration',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (69,'Resource Exhaustion','Resource Exhaustion',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (60,'Not Yet Determined','Not Yet Determined',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (71,'Unknown','Unknown',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (72,'Other','Other',4);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (73,'Environment Mismatch','Environment Mismatch',4);

--Horizon
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (100,'Immediate','Immediate',6);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (101,'Short-Term','Short-Term',6);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (102,'Long-Term','Long-Term',6);


--Resolution Type
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (140,'Add Capacity','Add Capacity',7);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (141,'Reboot','Reboot',7);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (142,'Repair','Repair',7);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (143,'Replace','Replace',7);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (144,'Upgrade','Upgrade',7);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (145,'Rebuild Transactions','Rebuild Transactions',7);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (146,'Enhance','Enhance',7);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (147,'Other','Other',7);

--PDLC Status
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (160,'Propose','Propose',8);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (161,'Define','Define',8);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (162,'Analyze','Analyze',8);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (163,'Construct','Construct',8);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (164,'Deploy','Deploy',8);

--Solution Type
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (186,'Best Practice Implementation','Best Practice Implementation',9);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (181,'Redundancy','Redundancy',9);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (182,'Consolidation','Consolidation',9);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (183,'Enhanced Condition Handling','Enhanced Condition Handling',9);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (184,'Monitoring and Alerting','Monitoring and Alerting',9);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (185,'Capacity Planning','Capacity Planning',9);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (180,'Assurance Development','Assurance Development',9);

--Application Status
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (200, "UP", "UP", 10);
Insert into REFERENCE_DATA (ID,DESCRIPTION,DISPLAY_NAME,GROUP_ID) values (201, "DOWN", "DOWN", 10);

--Error Condition
INSERT INTO error_condition(error_id,name) VALUES (1,'Application process not running');
INSERT INTO error_condition(error_id,name) VALUES (2,'Database errors in Logs');
INSERT INTO error_condition(error_id,name) VALUES (3,'Database Integrity Errors');
INSERT INTO error_condition(error_id,name) VALUES (4,'Disconnected Printers');
INSERT INTO error_condition(error_id,name) VALUES (5,'Errors in Communication Logs');
INSERT INTO error_condition(error_id,name) VALUES (6,'None Detected');
INSERT INTO error_condition(error_id,name) VALUES (7,'Service not responding');
INSERT INTO error_condition(error_id,name) VALUES (8,'Software error-failure');
INSERT INTO error_condition(error_id,name) VALUES (9,'System failure notification');
INSERT INTO error_condition(error_id,name) VALUES (9,'');

commit;

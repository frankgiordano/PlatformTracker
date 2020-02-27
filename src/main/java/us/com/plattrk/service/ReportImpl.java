package us.com.plattrk.service;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentReportByProduct;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.ProductComparator;
import us.com.plattrk.repository.IncidentChronologyRepository;
import us.com.plattrk.repository.ProductRepository;

@Service("Report")
public class ReportImpl implements Report {
	
	@Autowired
	private MailFormat mailFormat;
	
	// this is wired via xml configuration to allow us to easily switch between socket and java mail implementations.
	private MailService mailService;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private Properties appProperties;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00"); 
	private DecimalFormat decimalFormatPercent = new DecimalFormat("#0.0000"); 
	private List<Double> totalDowntimeALL = new ArrayList<Double>();
	private List<Long> totalDowntimeCLOUDALL = new ArrayList<Long>();
	private List<Long> totalDowntimeTRIVINALL = new ArrayList<Long>();
	private List<Long> totalDowntimeEXTALL = new ArrayList<Long>();
	
	public ReportImpl() {
	}
	
	@Override
	public void generateDailyReport(List<Incident> incidents, Date previousDayDate) {

		StringBuilder body = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String subject = appProperties.getProperty("SUBJECTMSG", "") + "Outages for " + dateFormat.format(previousDayDate);
		if (!incidents.isEmpty()) {
			
			body.append("<div>Good Morning,<div><br>");
			body.append("<div>Below are the outages for ");
			body.append(dateFormat.format(previousDayDate));
			body.append("<div><br>");
			
			for (Incident incident: incidents) {
				mailFormat.initialize(incident);
				body.append(mailFormat.generateBodyFormat(true));
				if (incidents.size() > 1) {
					body.append("<HR></HR>");
				}
			}
		
		} else {
			body.append("There were no reported outages for ");
			body.append(dateFormat.format(previousDayDate));
			body.append("<div><br>");
		}
		
		mailService.sendDailyReport(appProperties, body.toString(), subject);
		
	}
	
	@Override
	public void generateWeekEndReport(List<Incident> incidents, Date previousWeekEndDate, Date previousDayDate) {
		
		StringBuilder body = new StringBuilder();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String subject = appProperties.getProperty("SUBJECTMSG", "") + "Outages for " + dateFormat.format(previousWeekEndDate) + " to " + dateFormat.format(previousDayDate);
		if (!incidents.isEmpty()) {
			
			body.append("<div>Good Morning,<div><br>");
			body.append("<div>Below are the outages for ");
			body.append(dateFormat.format(previousWeekEndDate));
			body.append(" to ");
			body.append(dateFormat.format(previousDayDate));
			body.append("<div><br>");
			
			for (Incident incident: incidents) {
				mailFormat.initialize(incident);
				body.append(mailFormat.generateBodyFormat(true));
				if (incidents.size() > 1) {
					body.append("<HR></HR>");
				}
			}
		
		} else {
			body.append("There were no reported outages for ");
			body.append(dateFormat.format(previousWeekEndDate));
			body.append(" to ");
			body.append(dateFormat.format(previousDayDate));
			body.append("<div><br>");
		}
		
		mailService.sendDailyReport(appProperties, body.toString(), subject);
	}
	
	@Override
	public boolean generateWeeklyReport(List<Incident> incidents, Date previousWeeklyDate, Date previousDayDate, EmailAddress address) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat dateFormatItem = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		SimpleDateFormat dateFormatTitle = new SimpleDateFormat("MM/dd/yyyy");
		Workbook wb;
		String file;
		String fileName = "WeeklyReport.xlsx";
		String subject = appProperties.getProperty("SUBJECTMSG", "") + "Service Level Monitoring was executed at " + dateFormat.format(new Date());
		String body = "Weekly Production Report";
		String CLOUD = "";
		String EXT = "";
		
		if (System.getProperty("os.name").startsWith("Windows")) {
			file = "c:\\WeeklyReport.xlsx";
		}
		else
		{
			file = appProperties.getProperty("ReportLocation") + "//WeeklyReport.xlsx";
		}
		
		List<Long> totalDowntimeInternal = new ArrayList<Long>();
		List<Long> totalDowntimeExternal = new ArrayList<Long>();
		List<Long> totalDowntimeCLOUD = new ArrayList<Long>();
		Long currentIncidentDownTime = 0L;

	    try {

	         wb = new XSSFWorkbook();

	         Map<String, CellStyle> styles = createStyles(wb);
	         Sheet sheet = wb.createSheet("Service Level Monitoring");
	         sheet.setPrintGridlines(false);
	         sheet.setDisplayGridlines(false);

	         PrintSetup printSetup = sheet.getPrintSetup();
	         printSetup.setLandscape(true);
	         sheet.setFitToPage(true);
	         sheet.setHorizontallyCenter(true);

	         sheet.setColumnWidth(0, 24*256);
	         sheet.setColumnWidth(1, 42*256);
	         sheet.setColumnWidth(2, 30*256);
	         sheet.setColumnWidth(3, 18*256);
	         sheet.setColumnWidth(4, 18*256);
	         sheet.setColumnWidth(5, 30*256);
	         sheet.setColumnWidth(6, 18*256);
	         sheet.setColumnWidth(7, 5*256);

	         Row titleRow = sheet.createRow(0);
	         titleRow.setHeightInPoints(40);
	         for (int i = 2; i <= 7; i++) {
	             titleRow.createCell(i).setCellStyle(styles.get("title"));
	         }
	         Cell titleCell = titleRow.getCell(2);
	         titleCell.setCellValue("Production Service Level Monitoring Report for Time Period " 
	        		 				+ dateFormatTitle.format(previousWeeklyDate) 
	        		 				+ " to " + dateFormatTitle.format(previousDayDate));
	         
	         titleRow.createCell(0).setCellStyle(styles.get("title_left"));
	         titleRow.createCell(1).setCellStyle(styles.get("title_mid"));
	         
	         titleCell = titleRow.getCell(0);
	         titleCell.setCellValue("PLATTRK");
	         titleCell = titleRow.getCell(1);
	         titleCell.setCellValue("ORG");
	         
	         sheet.addMergedRegion(CellRangeAddress.valueOf("$C$1:$H$1"));	         

	         Row row = sheet.createRow(2);
	         
	         Cell cell = row.createCell(0);
	         cell.setCellValue("Product");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(1);
	         cell.setCellValue("Error Condition");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(2);
	         cell.setCellValue("Start Time");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(3);
	         cell.setCellValue("End Time");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(4);
	         cell.setCellValue("Duration");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(5);
	         cell.setCellValue("Summary");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(6);
	         cell.setCellValue("CLOUD");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(7);
	         cell.setCellValue("Ext");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         List<Product> products = productRepository.getProducts();
	         Collections.sort(products, new ProductComparator());
	         
	         int sectionCount = 3;
	         boolean matchFound = false;
	         String compareCurrent = null;
	         String compareIncoming = null;
	         
	         for (Product product: products) {
	        	 
	        	 if (product.getRevenue() == 0) {  // skip
	        		 continue;
	        	 }
	        	
	        	 compareCurrent = product.getIncidentName();
	        	 row = sheet.createRow(sectionCount);
	        	 for (int i = 0; i < 8; i++) { 
	        		 cell = row.createCell(i);
	        		 cell.setCellStyle(styles.get("item_left_section"));
	        	 }
	        	 
	        	 cell = row.getCell(0);
	        	 cell.setCellValue(product.getIncidentName());
	        	 cell = row.getCell(1);
	        	 cell.setCellValue("Max Weekly Uptime (Minutes):" + product.getMaxWeeklyUptime());
	        	 
	        	 for (Incident incident: incidents) {
	        		 
	        		 Set<Product> productList = incident.getProducts();
	        		 for (Product productIncoming: productList) {
	        			 
	        			 compareIncoming = productIncoming.getIncidentName();
	        			 if (compareCurrent.equals(compareIncoming)) {
	        				matchFound = true;
	 	        			row = sheet.createRow(++sectionCount);
	 	        			
	 	        			cell = row.createCell(1);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(incident.getError().getName());
	 	        			
	 	        			cell = row.createCell(2);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(dateFormatItem.format(incident.getStartTime()));
	 	        			
	 	        		    // need to check if there is a value here as this field is not required during incident create
	 	        			if (incident.getEndTime() != null) {  
		 	        			cell = row.createCell(3);
		 	        			cell.setCellStyle(styles.get("item_left_regular"));
		 	        			cell.setCellValue(dateFormatItem.format(incident.getEndTime()));
		 	        			
		 	        			Calendar startCalendar = Calendar.getInstance();
		 	        			startCalendar.setTime(incident.getStartTime());
		 	        			startCalendar.set(Calendar.MILLISECOND, 0);
		 	        			startCalendar.set(Calendar.SECOND, 0);
		 	        	        
		 	        			Calendar endCalendar = Calendar.getInstance();
		 	        			endCalendar.setTime(incident.getEndTime());
		 	        			endCalendar.set(Calendar.MILLISECOND, 0);
		 	        			endCalendar.set(Calendar.SECOND, 0);
		 	        			      			
		 	        			long diff = startCalendar.getTime().getTime() - endCalendar.getTime().getTime();
		 	        			long diffMinutes = Math.abs(diff / (60 * 1000));
		 	        			currentIncidentDownTime = diffMinutes;
		 	        			
		 	        			cell = row.createCell(4);
		 	        			cell.setCellStyle(styles.get("item_left_regular"));
		 	        			cell.setCellValue(decimalFormat.format(diffMinutes));
	 	        			}
	 	        			
	 	        			cell = row.createCell(5);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(incident.getDescription() + " " + (incident.getCorrectiveAction() !=null ? incident.getCorrectiveAction() : ""));
	 	        			
	 	        			switch (incident.getLocus()) {
	 	        				case "Internal":
	 	        					totalDowntimeInternal.add(currentIncidentDownTime);
	 	        					totalDowntimeTRIVINALL.add(currentIncidentDownTime);
	 	        					CLOUD = EXT = "N";
	 	        					break;
	 	        				case "CLOUD Comm":
	 	        					totalDowntimeExternal.add(currentIncidentDownTime);
	 	        					totalDowntimeEXTALL.add(currentIncidentDownTime);
	 	        					CLOUD = EXT = "Y";
	 	        					break;
	 	        				case "CLOUD Sys":
	 	        					totalDowntimeCLOUD.add(currentIncidentDownTime);
	 	        					totalDowntimeCLOUDALL.add(currentIncidentDownTime);
	 	        					CLOUD = "Y";
	 	        					EXT = "N";
	 	        					break;
	 	        				case "Internet":
	 	        					totalDowntimeExternal.add(currentIncidentDownTime);
 	        						totalDowntimeEXTALL.add(currentIncidentDownTime);
	 	        					CLOUD = "N";
	 	        					EXT = "Y";
	 	        					break;
	 	        				default:
	 	        					CLOUD = EXT = "";
	 	        			}
	 	        		
	 	        			cell = row.createCell(6);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(CLOUD);
	 	        			
	 	        			cell = row.createCell(7);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(EXT);
	 	        		 }

	        		 }
      			 
	        	 }
	        	 
	        	 if (!matchFound) {
	        		 row = sheet.createRow(++sectionCount);
	        		 cell = row.createCell(1);
	        		 cell.setCellStyle(styles.get("item_left_regular"));
	        		 cell.setCellValue("None Detected");
	        		 cell = row.createCell(4);
	        		 cell.setCellStyle(styles.get("item_left_regular"));
	        		 int num = 0;	
	        		 cell.setCellValue(decimalFormat.format(num));
	        	 }
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " TriVIN Related");        	 
	        	 fillRowStats(matchFound, false, row, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " TriVIN Related	");        	 
	        	 fillRowStats(matchFound, true, row, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " External");   	 
	        	 fillRowStats(matchFound, false, row, totalDowntimeExternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " External");        	 
	        	 fillRowStats(matchFound, true, row, totalDowntimeExternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " CLOUD");	        	 
	        	 fillRowStats(matchFound, false, row, totalDowntimeCLOUD, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " CLOUD"); 
	        	 fillRowStats(matchFound, true, row, totalDowntimeCLOUD, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " Cumulative");
	        	 fillRowStatsCumulative(matchFound, false, row, totalDowntimeExternal, totalDowntimeCLOUD, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " Cumulative");
	        	 fillRowStatsCumulative(matchFound, true, row, totalDowntimeExternal, totalDowntimeCLOUD, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Uptime for " + product.getIncidentName() + " Cumulative");
	        	 fillRowStatsCumulativeUptime(matchFound, row, totalDowntimeExternal, totalDowntimeCLOUD, totalDowntimeInternal, styles, product);
	        	 
	        	 // clear out for next product loop
	        	 sectionCount++;
	        	 matchFound = false;
	        	 currentIncidentDownTime = 0L;
	        	 totalDowntimeInternal.clear();
	        	 totalDowntimeExternal.clear();
	        	 totalDowntimeCLOUD.clear();
	        }
	        
	        fillFooter(sheet, sectionCount, styles, products);
	
	        FileOutputStream fos = new FileOutputStream(file);
	        wb.write(fos);
	        fos.close();
	        return mailService.sendWeeklyReport(appProperties, body, file, fileName, subject, address);
	    }
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	@Override
	public boolean generateReportByProduct(List<Incident> incidents, IncidentReportByProduct incidentReport) {
		
//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat dateFormatItem = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		SimpleDateFormat dateFormatTitle = new SimpleDateFormat("MM/dd/yyyy");
		Workbook wb;
		String file;
		String fileName = "IncidentReportByProducts.xlsx";
		StringBuilder subject = new StringBuilder();
		String body = "Incident Report By Product(s) and Date Range, Production Report";
		String CLOUD = "";
		String EXT = "";
		
		if (System.getProperty("os.name").startsWith("Windows")) {
			file = "c:\\IncidentReportByProducts.xlsx";
		}
		else
		{
			file = appProperties.getProperty("ReportLocation") + "//IncidentReportByProducts.xlsx";
		}
		
		List<Long> totalDowntimeInternal = new ArrayList<Long>();
		List<Long> totalDowntimeExternal = new ArrayList<Long>();
		List<Long> totalDowntimeCLOUD = new ArrayList<Long>();
		Long currentIncidentDownTime = 0L;

	    try {

	         wb = new XSSFWorkbook();

	         Map<String, CellStyle> styles = createStyles(wb);
	         Sheet sheet = wb.createSheet("Service Level Monitoring");
	         sheet.setPrintGridlines(false);
	         sheet.setDisplayGridlines(false);

	         PrintSetup printSetup = sheet.getPrintSetup();
	         printSetup.setLandscape(true);
	         sheet.setFitToPage(true);
	         sheet.setHorizontallyCenter(true);

	         sheet.setColumnWidth(0, 24*256);
	         sheet.setColumnWidth(1, 42*256);
	         sheet.setColumnWidth(2, 30*256);
	         sheet.setColumnWidth(3, 18*256);
	         sheet.setColumnWidth(4, 18*256);
	         sheet.setColumnWidth(5, 30*256);
	         sheet.setColumnWidth(6, 18*256);
	         sheet.setColumnWidth(7, 5*256);

	         Row titleRow = sheet.createRow(0);
	         titleRow.setHeightInPoints(40);
	         for (int i = 2; i <= 7; i++) {
	             titleRow.createCell(i).setCellStyle(styles.get("title"));
	         }
	         Cell titleCell = titleRow.getCell(2);
	         titleCell.setCellValue("Production Service Level Monitoring Report for Time Period " 
	        		 				+ dateFormatTitle.format(incidentReport.getStartDate()) 
	        		 				+ " to " + dateFormatTitle.format(incidentReport.getEndDate()));
	         
	         titleRow.createCell(0).setCellStyle(styles.get("title_left"));
	         titleRow.createCell(1).setCellStyle(styles.get("title_mid"));
	         
	         titleCell = titleRow.getCell(0);
	         titleCell.setCellValue("PLATTRK");
	         titleCell = titleRow.getCell(1);
	         titleCell.setCellValue("ORG");
	         
	         sheet.addMergedRegion(CellRangeAddress.valueOf("$C$1:$H$1"));	         

	         Row row = sheet.createRow(2);
	         
	         Cell cell = row.createCell(0);
	         cell.setCellValue("Product");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(1);
	         cell.setCellValue("Error Condition");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(2);
	         cell.setCellValue("Start Time");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(3);
	         cell.setCellValue("End Time");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(4);
	         cell.setCellValue("Duration");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(5);
	         cell.setCellValue("Summary");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(6);
	         cell.setCellValue("CLOUD");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         cell = row.createCell(7);
	         cell.setCellValue("Ext");
	         cell.setCellStyle(styles.get("item_left_title"));
	         
	         List<Product> products = productRepository.getProducts();
	         Collections.sort(products, new ProductComparator());
	         
	         int sectionCount = 3;
	         boolean matchFound = false;
	         String compareCurrent = null;
	         String compareIncoming = null;
	         HashMap<String, Boolean> cacheIncomingProducts = new HashMap<String, Boolean>();
	         List<Product> summaryProducts = new ArrayList<Product>();
	         
	         String[] incomingProducts = incidentReport.getProducts().split(",");
	         for (String product: incomingProducts) {
	        	 cacheIncomingProducts.put(product, true); 
	         }
	         
	         subject.append(appProperties.getProperty("SUBJECTMSG", ""));
	         subject.append("Incident Report for ");
	         if (incomingProducts.length <= 3) {
	        	 for (int i = 0; i < incomingProducts.length; i++) {
	        		 if (i != (incomingProducts.length - 1)) {
	        			 subject.append(incomingProducts[i] + ", ");
	        		 }
	        		 else subject.append(incomingProducts[i]);
	        	 } 
	         }
	         else 
	         {
	        	 subject.append("Multiple Products");
	         }
	         subject.append(" between ");
	         subject.append(dateFormatTitle.format(incidentReport.getStartDate()));
	         subject.append(" and ");
	         subject.append(dateFormatTitle.format(incidentReport.getEndDate()));
	         
	         long startDateTime = incidentReport.getStartDate().getTime();
	         long endDateTime = incidentReport.getEndDate().getTime();
	         long milPerDay = 1000*60*60*24;
	         int numOfDays = (int) ((endDateTime - startDateTime) / milPerDay);
	         numOfDays++;
	         
	                 
	         for (Product product: products) {
	        	 if (!cacheIncomingProducts.containsKey(product.getIncidentName())) {
	        		 continue;
	        	 }
	        	 product.setMaxWeeklyUptime( (product.getMaxWeeklyUptime() / 7 ) * numOfDays);
	        	 summaryProducts.add(product);
	        	 
	        	 compareCurrent = product.getIncidentName();
	        	 row = sheet.createRow(sectionCount);
	        	 for (int i = 0; i < 8; i++) { 
	        		 cell = row.createCell(i);
	        		 cell.setCellStyle(styles.get("item_left_section"));
	        	 }
	        	 
	        	 cell = row.getCell(0);
	        	 cell.setCellValue(product.getIncidentName());
	        	 cell = row.getCell(1);
	        	 cell.setCellValue("Max Weekly Uptime (Minutes):" + product.getMaxWeeklyUptime());
	        	 
	        	 for (Incident incident: incidents) {
	        		 
	        		 Set<Product> productList = incident.getProducts();
	        		 for (Product productIncoming: productList) {
	        			 
	        			 compareIncoming = productIncoming.getIncidentName();
	        			 if (compareCurrent.equals(compareIncoming)) {
	        				matchFound = true;
	 	        			row = sheet.createRow(++sectionCount);
	 	        			
	 	        			cell = row.createCell(1);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(incident.getError().getName());
	 	        			
	 	        			cell = row.createCell(2);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(dateFormatItem.format(incident.getStartTime()));
	 	        			
	 	        		    // need to check if there is a value here as this field is not required during incident create
	 	        			if (incident.getEndTime() != null) {  
		 	        			cell = row.createCell(3);
		 	        			cell.setCellStyle(styles.get("item_left_regular"));
		 	        			cell.setCellValue(dateFormatItem.format(incident.getEndTime()));
		 	        			
		 	        			Calendar startCalendar = Calendar.getInstance();
		 	        			startCalendar.setTime(incident.getStartTime());
		 	        			startCalendar.set(Calendar.MILLISECOND, 0);
		 	        			startCalendar.set(Calendar.SECOND, 0);
		 	        	        
		 	        			Calendar endCalendar = Calendar.getInstance();
		 	        			endCalendar.setTime(incident.getEndTime());
		 	        			endCalendar.set(Calendar.MILLISECOND, 0);
		 	        			endCalendar.set(Calendar.SECOND, 0);
		 	        			      			
		 	        			long diff = startCalendar.getTime().getTime() - endCalendar.getTime().getTime();
		 	        			long diffMinutes = Math.abs(diff / (60 * 1000));
		 	        			currentIncidentDownTime = diffMinutes;
		 	        			
		 	        			cell = row.createCell(4);
		 	        			cell.setCellStyle(styles.get("item_left_regular"));
		 	        			cell.setCellValue(decimalFormat.format(diffMinutes));
	 	        			}
	 	        			
	 	        			cell = row.createCell(5);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(incident.getDescription() + " " + (incident.getCorrectiveAction() !=null ? incident.getCorrectiveAction() : ""));
	 	        			
	 	        			// only calculate downtime for DOWN applicationStatus incidents
	 	        			switch (incident.getLocus()) {
	 	        				case "Internal":
	 	        					if (incident.getApplicationStatus().getDisplayName().equals("Down")) {
	 	        						totalDowntimeInternal.add(currentIncidentDownTime);
	 	        						totalDowntimeTRIVINALL.add(currentIncidentDownTime);
	 	        					}
	 	        					CLOUD = EXT = "N";
	 	        					break;
	 	        				case "CLOUD Comm":
	 	        					if (incident.getApplicationStatus().getDisplayName().equals("Down")) {
	 	        						totalDowntimeExternal.add(currentIncidentDownTime);
	 	        						totalDowntimeEXTALL.add(currentIncidentDownTime);
	 	        					}
	 	        					CLOUD = EXT = "Y";
	 	        					break;
	 	        				case "CLOUD Sys":
	 	        					if (incident.getApplicationStatus().getDisplayName().equals("Down")) {
	 	        						totalDowntimeCLOUD.add(currentIncidentDownTime);
	 	        						totalDowntimeCLOUDALL.add(currentIncidentDownTime);
	 	        					}
	 	        					CLOUD = "Y";
	 	        					EXT = "N";
	 	        					break;
	 	        				case "Internet":
	 	        					if (incident.getApplicationStatus().getDisplayName().equals("Down")) {
	 	        						totalDowntimeExternal.add(currentIncidentDownTime);
	 	        						totalDowntimeEXTALL.add(currentIncidentDownTime);
	 	        					}
	 	        					CLOUD = "N";
	 	        					EXT = "Y";
	 	        					break;
	 	        				default:
	 	        					CLOUD = EXT = "";
	 	        			}
	 	        		
	 	        			cell = row.createCell(6);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(CLOUD);
	 	        			
	 	        			cell = row.createCell(7);
	 	        			cell.setCellStyle(styles.get("item_left_regular"));
	 	        			cell.setCellValue(EXT);
	 	        		 }

	        		 }
      			 
	        	 }
	        	 
	        	 if (!matchFound) {
	        		 row = sheet.createRow(++sectionCount);
	        		 cell = row.createCell(1);
	        		 cell.setCellStyle(styles.get("item_left_regular"));
	        		 cell.setCellValue("None Detected");
	        		 cell = row.createCell(4);
	        		 cell.setCellStyle(styles.get("item_left_regular"));
	        		 int num = 0;	
	        		 cell.setCellValue(decimalFormat.format(num));
	        	 }
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " TriVIN Related");        	 
	        	 fillRowStats(matchFound, false, row, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " TriVIN Related	");        	 
	        	 fillRowStats(matchFound, true, row, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " External");   	 
	        	 fillRowStats(matchFound, false, row, totalDowntimeExternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " External");        	 
	        	 fillRowStats(matchFound, true, row, totalDowntimeExternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " CLOUD");	        	 
	        	 fillRowStats(matchFound, false, row, totalDowntimeCLOUD, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " CLOUD"); 
	        	 fillRowStats(matchFound, true, row, totalDowntimeCLOUD, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("Total Downtime for " + product.getIncidentName() + " Cumulative");
	        	 fillRowStatsCumulative(matchFound, false, row, totalDowntimeExternal, totalDowntimeCLOUD, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Downtime for " + product.getIncidentName() + " Cumulative");
	        	 fillRowStatsCumulative(matchFound, true, row, totalDowntimeExternal, totalDowntimeCLOUD, totalDowntimeInternal, styles, product);
	        	 
	        	 row = sheet.createRow(++sectionCount);
	        	 cell = row.createCell(1);
	        	 cell.setCellStyle(styles.get("item_left_regular"));
	        	 cell.setCellValue("% Uptime for " + product.getIncidentName() + " Cumulative");
	        	 fillRowStatsCumulativeUptime(matchFound, row, totalDowntimeExternal, totalDowntimeCLOUD, totalDowntimeInternal, styles, product);
	        	 
	        	 // clear out for next product loop
	        	 sectionCount++;
	        	 matchFound = false;
	        	 currentIncidentDownTime = 0L;
	        	 totalDowntimeInternal.clear();
	        	 totalDowntimeExternal.clear();
	        	 totalDowntimeCLOUD.clear();
	        }
	        
	        fillFooter(sheet, sectionCount, styles, summaryProducts);
	
	        FileOutputStream fos = new FileOutputStream(file);
	        wb.write(fos);
	        fos.close();
	        EmailAddress address = new EmailAddress();
	        address.setAddress(incidentReport.getAddress());
	        return mailService.sendWeeklyReport(appProperties, body, file, fileName, subject.toString(), address);
	    }
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    	return false;
	    }
	}
	
	private void clearALL() {
		
		totalDowntimeALL.clear();
		totalDowntimeCLOUDALL.clear();
		totalDowntimeTRIVINALL.clear();
		totalDowntimeEXTALL.clear();
		
	}
	
	private void fillFooter(Sheet sheet, int sectionCount, Map<String, CellStyle> styles, List<Product> myProducts) {
		
   	 	double totalProduct = 0;
   	 	double totalDown = 0;
   	 	StringBuilder num = new StringBuilder();
		
		Row row = sheet.createRow(++sectionCount);
   	 	Cell cell = row.createCell(2);
   	 	cell.setCellStyle(styles.get("item_left_regular_bold"));
   	 	cell.setCellValue("Max Uptime (Minutes)");
   	 	for (Product product: myProducts) {
   	 	totalProduct += product.getMaxWeeklyUptime();
   	 	}
   	 	cell = row.createCell(4);
	 	cell.setCellStyle(styles.get("item_left_regular"));
	 	num.append(decimalFormatPercent.format(totalProduct));
	 	cell.setCellValue(num.toString());
   	 	
	 	row = sheet.createRow(++sectionCount);
   	 	cell = row.createCell(2);
   	 	cell.setCellStyle(styles.get("item_left_regular_bold"));
   	 	cell.setCellValue("Total DownTime (Minutes)");
   	 	cell = row.createCell(4);
	 	cell.setCellStyle(styles.get("item_left_regular"));
	 	for (double item: totalDowntimeALL) {
	 		totalDown += item;
	 	}
	 	cell.setCellValue(decimalFormat.format(totalDown));
   	 	
	 	row = sheet.createRow(++sectionCount);
   	 	cell = row.createCell(2);
   	 	cell.setCellStyle(styles.get("item_left_regular_bold"));
   	 	cell.setCellValue("% Uptime Cross Products");
   	 	cell = row.createCell(4);
	 	cell.setCellStyle(styles.get("item_left_regular"));
	 	num.setLength(0);
	 	num.append(decimalFormatPercent.format(100-((totalDown/totalProduct)*100)));
	 	num.append("%");
	 	cell.setCellValue(num.toString());
	 	
	 	row = sheet.createRow(++sectionCount);
   	 	cell = row.createCell(2);
   	 	cell.setCellStyle(styles.get("item_left_regular_bold"));
   	 	cell.setCellValue("% Downtime CLOUD Cross Products");
   	 	cell = row.createCell(4);
	 	cell.setCellStyle(styles.get("item_left_regular"));
	 	totalDown = 0;
	 	for (double item: totalDowntimeCLOUDALL) {
	 		totalDown += item;
	 	}
	 	num.setLength(0);
	 	num.append(decimalFormatPercent.format((totalDown/totalProduct)*100));
	 	num.append("%");
	 	cell.setCellValue(num.toString());
   	 	
	 	row = sheet.createRow(++sectionCount);
   	 	cell = row.createCell(2);
   	 	cell.setCellStyle(styles.get("item_left_regular_bold"));
   	 	cell.setCellValue("% Downtime Trivin Cross Products");
   	 	cell = row.createCell(4);
	 	cell.setCellStyle(styles.get("item_left_regular"));
   	 	totalDown = 0;
	 	for (double item: totalDowntimeTRIVINALL) {
	 		totalDown += item;
	 	}
	 	num.setLength(0);
	 	num.append(decimalFormatPercent.format((totalDown/totalProduct)*100));
	 	num.append("%");
	 	cell.setCellValue(num.toString());
   	 	
	 	row = sheet.createRow(++sectionCount);
   	 	cell = row.createCell(2);
   	 	cell.setCellStyle(styles.get("item_left_regular_bold"));
   	 	cell.setCellValue("% Downtime EXT Cross Products");
   	 	cell = row.createCell(4);
	 	cell.setCellStyle(styles.get("item_left_regular"));
   	 	totalDown = 0;
	 	for (double item: totalDowntimeEXTALL) {
	 		totalDown += item;
	 	}
	 	num.setLength(0);
	 	num.append(decimalFormatPercent.format((totalDown/totalProduct)*100));
	 	num.append("%");
	 	cell.setCellValue(num.toString());
   	 	clearALL();
	}

	private void fillRowStats(boolean matchFound, boolean byPercentage, Row row, List<Long> items, Map<String, CellStyle> styles, Product product) {
		
		double total = 0;
		Cell cell = row.createCell(4);
		cell.setCellStyle(styles.get("item_left_regular"));
		
		if (matchFound) {
			for (Long item: items) {
   			 	total += item;
			}
		}
		
		if (byPercentage) {
			String num = decimalFormatPercent.format((total/product.getMaxWeeklyUptime())*100) + "%";
			cell.setCellValue(num);
		}
		else 
		{
			cell.setCellValue(decimalFormat.format(total));
		}
   	}
	
	private void fillRowStatsCumulative(boolean matchFound, boolean byPercentage, Row row, List<Long> totalDowntimeExternal, List<Long> totalDowntimeCLOUD, List<Long> totalDowntimeInternal, Map<String, CellStyle> styles, Product product) {
		
		double total = 0;
		Cell cell = row.createCell(4);
		cell.setCellStyle(styles.get("item_left_regular"));
		
		if (matchFound) {
			for (Long item: totalDowntimeExternal) {
   			 	total += item;
			}
   		 	for (Long item: totalDowntimeCLOUD) {
   		 		total += item;
   		 	}
   		 	for (Long item: totalDowntimeInternal) {
   		 		total += item;
   		 	}
		}
		
		if (byPercentage) {
			String num = decimalFormatPercent.format((total/product.getMaxWeeklyUptime())*100) + "%";
			cell.setCellValue(num);
		}
		else 
		{
			cell.setCellValue(decimalFormat.format(total));
			totalDowntimeALL.add(total);
		}
   	 }
	
	private void fillRowStatsCumulativeUptime(boolean matchFound, Row row, List<Long> totalDowntimeExternal, List<Long> totalDowntimeCLOUD, List<Long> totalDowntimeInternal, Map<String, CellStyle> styles, Product product) {
		
		double total = 0;
		Cell cell = row.createCell(4);
		cell.setCellStyle(styles.get("item_left_regular"));
		
		if (matchFound) {
			for (Long item: totalDowntimeExternal) {
   			 	total += item;
			}
   		 	for (Long item: totalDowntimeCLOUD) {
   		 		total += item;
   		 	}
   		 	for (Long item: totalDowntimeInternal) {
   		 		total += item;
   		 	}
		}
		
		String num = decimalFormatPercent.format(100.0000-((total/product.getMaxWeeklyUptime())*100)) + "%";
		cell.setCellValue(num);
   	 }
	
	private static Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)16);
        titleFont.setFontName("Tahoma");
        titleFont.setColor(HSSFColor.VIOLET.index);
        style = wb.createCellStyle();
        style.setFont(titleFont);
        style.setWrapText(true);
        styles.put("title", style);
        
        Font titleLeftFont = wb.createFont();
        titleLeftFont.setFontHeightInPoints((short)16);
        titleLeftFont.setFontName("Tahoma");
        titleLeftFont.setColor(HSSFColor.BLACK.index);
        titleLeftFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setFont(titleLeftFont);
        style.setWrapText(false);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        styles.put("title_left", style);
        
        Font titleMidFont = wb.createFont();
        titleMidFont.setFontHeightInPoints((short)16);
        titleMidFont.setFontName("Tahoma");
        titleMidFont.setColor(HSSFColor.ROSE.index);
        titleMidFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setFont(titleMidFont);
        style.setWrapText(false);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put("title_mid", style);

        Font itemFont = wb.createFont();
        itemFont.setFontHeightInPoints((short)8);
        itemFont.setFontName("Arial");
        itemFont.setColor(HSSFColor.WHITE.index);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(itemFont);
        style.setFillForegroundColor(HSSFColor.VIOLET.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("item_left_title", style);
        
        Font itemFontSection = wb.createFont();
        itemFontSection.setFontHeightInPoints((short)8);
        itemFontSection.setFontName("Arial");
        itemFontSection.setColor(HSSFColor.BLACK.index);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setFont(itemFontSection);
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("item_left_section", style);
        
        Font itemFontRegular = wb.createFont();
        itemFontRegular.setFontHeightInPoints((short)8);
        itemFontRegular.setFontName("Arial");
        itemFontRegular.setColor(HSSFColor.BLACK.index);
        style = wb.createCellStyle();
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        style.setFont(itemFontRegular);
        style.setWrapText(true);
        styles.put("item_left_regular", style);

        Font itemFontRegularBold = wb.createFont();
        itemFontRegularBold.setFontHeightInPoints((short)8);
        itemFontRegularBold.setFontName("Arial");
        itemFontRegularBold.setColor(HSSFColor.BLACK.index);
        itemFontRegularBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        style.setFont(itemFontRegularBold);
        style.setWrapText(true);
        styles.put("item_left_regular_bold", style);
        
        return styles;
    }
	
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

}

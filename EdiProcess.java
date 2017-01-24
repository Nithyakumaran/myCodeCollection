package com.medhok.edi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.resource.cci.ResultSet;

import org.jboss.identity.idm.common.transaction.Transactions;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import com.medhok.edi.exceptions.DiagnosisNotFoundException;
import com.medhok.edi.exceptions.MemberNotFoundException;
import com.medhok.edi.exceptions.ProcedureNotFoundException;
import com.medhok.edi.model.EdiRecord;
import com.medhok.edi.model.HealthcareCodeInformation;
import com.medhok.edi.model.PatientEvent;
import com.medhok.edi.model.Service;
import com.medhok.edi.model.Transaction;
import com.medhok.mm.server.dao.AppServiceDAO;
import com.medhok.mm.server.models.AuthReview;
import com.medhok.mm.server.models.Authorizations;
import com.medhok.mm.server.models.CodesMaster;
import com.medhok.mm.server.models.EdiTransactions;
import com.medhok.mm.server.models.Member;
import com.medhok.mm.ws.endpoint.medicalum.WsAuthorization;


public class EdiProcess {
	@Autowired
	private AppServiceDAO appServiceDAO;
	@Autowired
	private EdiAuthService ediAuthService;
	private String outgoingFolder;
	private String decisionedCasesSql;
	private JdbcTemplate jdbcTemplate;
	private HashMap<String,String> communicationEnvelope;
	
	public EdiProcess(){
		this.communicationEnvelope= new HashMap<String,String>();
	}
	private static final Logger logger = LoggerFactory.getLogger(EdiProcess.class);

	public enum ProcessType {
		P, // Pending Parsing
		PA, // Pending Auth Creation
		PAG,
		ACK// Pending Auth Create and Report Generation
		;
		public static ProcessType getProcessType(String arg) {
			for (ProcessType pType : ProcessType.values()) {
				if (pType.toString().equalsIgnoreCase(arg)) {
					return pType;
				}
			}
			return null;
		}
	}

	/**
	 * This method is a dispatcher method. It collects all the transaction in
	 * Pending State delegates to transaction to appropriate methods.
	 * 
	 * @param processType
	 * @return 0 if process was successfull value greater than 0 indicating
	 *         number of errors
	 * @throws SQLException 
	 */
	public int process(String processType){
		if (processType != null) {
			HashMap<String, Object> map = getCriteriaToLoad(processType);
			List<EdiTransactions> transanctions = appServiceDAO.findByCriteria(EdiTransactions.class, map);

			if ((transanctions == null || transanctions.isEmpty()) && !processType.equalsIgnoreCase("PAG")) {
				Log.info("No new Files to Process.");
				return 0;
			}
			int errorCounter=0;
			
			switch (ProcessType.getProcessType(processType)) {
			case P:
				for (EdiTransactions edi : transanctions) {
					parseTransaction(edi);
				}
				break;
			case PA:
				for (EdiTransactions edi : transanctions) {
					saveAuth(edi);
				}
				break;
			case ACK:
				for (EdiTransactions edi : transanctions) {
					generateAck(edi);
				}
				break;
			case PAG:
				transanctions= this.getDecisionedCases();
				
				for (EdiTransactions edi : transanctions)
					 errorCounter+=generateReport(edi);
				break;
			default:
				Log.info("Argument Does not match the defined variables");
				break;
			}
			
			return errorCounter;
		}

		return 0;
	}
	
	/**
	 * This method is used to get list of decisioned cases that where loaded through EDI process
	 * @return
	 */
	private List<EdiTransactions> getDecisionedCases() {
		if(this.getDecisionedCasesSql()==null) return new ArrayList<EdiTransactions>();
		final List<EdiTransactions> transactions= new ArrayList<EdiTransactions>();
		this.jdbcTemplate.query(this.getDecisionedCasesSql(), new RowCallbackHandler(){
			
			@Override
			public void processRow(java.sql.ResultSet rs) throws SQLException {
				try {
					EdiTransactions edi = new EdiTransactions();
					edi.setId(rs.getLong("id"));
					edi.setAuthId(rs.getLong("AUTH_ID"));
					edi.setTransactionFilePath(rs.getString("transaction_file_path"));
					edi.setTransactionStatus(rs.getString("transaction_status"));
					transactions.add(edi);
				} catch (SQLException e) {
					Log.error("Error while retreiving decisioned cases");
					Log.error(e.getSQLState());
					Log.error(e.getMessage());
				}
				
			}
		});
		return transactions;
	}
	
	
	/**
	 * This method provides criteria required to fetch data
	 * <p>
	 * from EDI_Transaction table
	 * 
	 * @param processType
	 * @return HashMap
	 */
	private HashMap<String, Object> getCriteriaToLoad(String processType) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("transactionType", "I");
		map.put("transactionStatus", processType);
		return map;
	}
	
	private int generateAck(EdiTransactions edi){
		File f = new File(edi.getTransactionFilePath());
		EdiReader reader= new EdiReader();
		try {
			Authorizations auth= getAuth(edi);
			List<AuthReview> lstAuthReview= this.getAuthReview(edi);
			String ackString= reader.getRecordAckString(new FileInputStream(f), auth); 
			return ackString.length()>0?0:1;
		} catch (Exception e) {
			logger.error("processTransaction: Exception creating the Edi 278 11 files", e);
			return 1;
		}
	}
	

	
	/**
	 * This Method produces report for the valid 278 files
	 * which were loaded into MEDHOK's system and cases
	 * that were reviewed and decisioned by user  
	 * @param EdiTransactions 
	 */
	private int generateReport(EdiTransactions edi) {
		
		File f = new File(edi.getTransactionFilePath());
		try {
			Authorizations auth= getAuth(edi);
			List<AuthReview> lstAuthReview= this.getAuthReview(edi);
			int reportStatus= generateReport(new FileInputStream(f),lstAuthReview,f.getName());
			edi.setTransactionStatus(reportStatus==0?"C":"X");
		} catch (Exception e) {
			logger.error("processTransaction: Exception creating the Edi 278 11 files", e);
			return 1;
		}
		return 0;
	}
	private int generateReport(FileInputStream f, List<AuthReview> lstAuthReview,String fileName) {
		EdiReader reader = new EdiReader();
		String str=reader.getRecordsAsString(f, lstAuthReview);
		this.writeRespose(str, fileName, this.outgoingFolder);
		
		return 1;
	}
	
	/**
	 * This method is used to write responses to files
	 */
	void writeRespose(String data, String fileName, String outputPath){
		if(!isValid(data)) {
			Log.info("Nothing written to response data is empty");
			return;
		}
		if(!isValid(fileName)) {
			Log.error("FileName is empty. Cannot proceed with writing a response file");
			return;
		}
		
		if(!isValid(outputPath)){
			Log.error("Response file path is not defined");
			return;
		}
		
		fileName=fileName.replace("_27813.", "_27811.");
		
		try {
		File file = new File(outputPath+fileName);

		if (!file.exists()) file.createNewFile();

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data);
		bw.close();

		Log.info("Completed writining response file"+ fileName);
		
		} catch (IOException e) {
			Log.error("Problem occurred while trying to write response file::"+ fileName);
			Log.error(e.getMessage());
		}
		
		
	}
	
	private String createFileName(String fileName) {
		
		String[] f= fileName.split("\\.(?=[^\\.]+$)");
		
		return f[0]+"_27811."+f[1];
	}

	boolean isValid(String str){
		return str!=null && str.length()>1;
	}
	/**
	 * Program to find list of Auth
	 * @param edi
	 * @return
	 */
	private List<AuthReview> getAuthReview(EdiTransactions edi){
		if(edi==null) return new ArrayList<AuthReview>();
		HashMap<String, Object> map= new HashMap<String,Object>();
		map.put("authorization_id", edi.getAuthId());
		return this.appServiceDAO.findByCriteria(AuthReview.class, map);
	}
	
	/**
	 * This medthod is used to retreive Authorizations Hibernate objects
	 * @param edi
	 * @return
	 */
	private Authorizations getAuth(EdiTransactions edi) {
		if(edi==null) return new Authorizations();
		HashMap<String, Object> map = new HashMap<String,Object>();
		map.put("ID", edi.getAuthId());
		return this.appServiceDAO.findById(Authorizations.class, edi.getAuthId());
	}

	private int generateReport(Transaction transaction) {
		if (transaction == null) {
			Log.error("The Transcation is empty. Report generation cannot proceed");
			return 1;
		}
		return 0;
	}

	protected void parseTransaction(EdiTransactions ediTransaction) {
		if (ediTransaction != null && ediTransaction.getTransactionFilePath() != null) {
			File file = new File(ediTransaction.getTransactionFilePath());
			EdiReader reader = new EdiReader();

			reader.setDependentValidator(new DependentValidator() {

				@Override
				public EdiValidationResult validate() {
					EdiValidationResult result = new EdiValidationResult();
					String memberId = getDependent().getSubscriberPrimaryIdentifier();
					String firstname = getDependent().getFirstName();
					String lastName = getDependent().getLastOrOrgName();
					Date doB = getDependent().getDateOfBirth();
					List<Member> memberList = null;
					if (memberId != null) {
						memberId = memberId.substring(0, memberId.length() - 2);
						char c = memberId.charAt(0);
						if (!Character.isDigit(c)) {
							memberId = memberId.substring(3);
						}
					} else {
						result.setValid(false);
						result.setReasonCode("64");
						result.setFollowupActionCode("C");
					}

					if (memberId != null && doB != null) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("subscriberId", memberId);
						map.put("dateOfBirth", doB);
						memberList = appServiceDAO.findByCriteria(Member.class, map);
					}
					if ((memberList == null || memberList.size() > 1) && firstname != null && lastName != null
							&& doB != null) {
						HashMap<String, Object> map1 = new HashMap<String, Object>();
						map1.put("firstName", firstname);
						map1.put("lastName", lastName);
						map1.put("dateOfBirth", doB);
						memberList = appServiceDAO.findByCriteria(Member.class, map1);
					}
					if (memberList != null && !memberList.isEmpty() && memberList.size() > 1) {
						result.setValid(false);
						result.setReasonCode("68");
						result.setFollowupActionCode("C");
					} else if (memberList == null || memberList.isEmpty()) {
						result.setValid(false);
						result.setReasonCode("67");
						result.setFollowupActionCode("C");
					}
					return result;

				}

			});
			reader.setSubscriberValidator(new SubscriberValidator() {

				@Override
				public EdiValidationResult validate() {
					EdiValidationResult result = new EdiValidationResult();
					String memberId = getSubscriber().getSubscriberPrimaryIdentifier();
					String firstname = getSubscriber().getFirstName();
					String lastName = getSubscriber().getLastOrOrgName();
					Date doB = getSubscriber().getDateOfBirth();
					List<Member> memberList = null;
					if (memberId != null) {
						memberId = memberId.substring(0, memberId.length() - 2);
						char c = memberId.charAt(0);
						if (!Character.isDigit(c)) {
							memberId = memberId.substring(3);
						}
					} else {
						result.setValid(false);
						result.setReasonCode("64");
						result.setFollowupActionCode("C");
					}

					if (memberId != null && doB != null) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("subscriberId", memberId);
						map.put("dateOfBirth", doB);
						memberList = appServiceDAO.findByCriteria(Member.class, map);
					}
					if ((memberList == null || memberList.size() > 1) && firstname != null && lastName != null
							&& doB != null) {
						HashMap<String, Object> map1 = new HashMap<String, Object>();
						map1.put("firstName", firstname);
						map1.put("lastName", lastName);
						map1.put("dateOfBirth", doB);
						memberList = appServiceDAO.findByCriteria(Member.class, map1);
					}
					if (memberList != null && !memberList.isEmpty() && memberList.size() > 1) {
						result.setValid(false);
						result.setReasonCode("68");
						result.setFollowupActionCode("C");
					} else if (memberList == null || memberList.isEmpty()) {
						result.setValid(false);
						result.setReasonCode("67");
						result.setFollowupActionCode("C");
					}
					return result;

				}
			});

			reader.setServiceValidator(new ServiceValidator() {

				public EdiValidationResult validate() {
					EdiValidationResult result = new EdiValidationResult();
					Service serv = getService();
					if (serv != null && serv.getProfessionalService() != null
							&& serv.getProfessionalService().getProcedureCode() != null
							&& serv.getProfessionalService().getProcedureCode().getProductOrServiceId() != null) {
						String code = serv.getProfessionalService().getProcedureCode().getProductOrServiceId();
						String sql = "select cm from CodesMaster as cm "
								+ "where context = 2 and code = ? and (cm.effectiveDate is null or cm.effectiveDate < ?) "
								+ "and (cm.termDate is null or cm.termDate > ? )";
						List<CodesMaster> cm = appServiceDAO.findByQueryString(CodesMaster.class, sql,
								new Object[] { code, new Date(), new Date() });
						if (cm == null || cm.isEmpty()) {
							result.setValid(false);
							result.setReasonCode("AG");
							result.setFollowupActionCode("C");
						}

					}
					return result;
				}
			});
			reader.setPatientEventValidator(new PatientEventValidator() {

				@Override
				public EdiValidationResult validate() {
					EdiValidationResult result = new EdiValidationResult();
					PatientEvent pe = getPatientEvent();
					if (pe != null && pe.getPatientDiagnosisList() != null) {
						List<HealthcareCodeInformation> dxList = pe.getPatientDiagnosisList();
						for (HealthcareCodeInformation dx : dxList) {
							if (dx.getDiagnosisCode() != null) {
								String sql = "select cm from CodesMaster as cm "
										+ "where context = 1 and code = ? and (cm.effectiveDate is null or cm.effectiveDate < ?) "
										+ "and (cm.termDate is null or cm.termDate > ? )";
								List<CodesMaster> cm = appServiceDAO.findByQueryString(CodesMaster.class, sql,
										new Object[] { dx, new Date(), new Date() });
								if (cm == null || cm.isEmpty()) {
									result.setValid(false);
									result.setReasonCode("AF"); // AF -
																// Invalid/Missing
																// Diagnosis
																// Code(s)
									result.setFollowupActionCode("C");
								}
							}
						}
					}
					return result;
				}
			});

			try {
				Transaction transaction = reader.process(new FileInputStream(file));
				ediTransaction.setMessage("Success");
				ediTransaction.setTransactionStatus("PA");
				appServiceDAO.update(ediTransaction);
				// TODO update the transaction with 999 message if success
			} catch (Exception e) {
				logger.error("Error processing {}: {}", file.getName(), e.getMessage());
				ediTransaction.setTransactionStatus("F");
				// ediTransaction.setTransactionMessage(e.toString());
				appServiceDAO.update(ediTransaction);
			}
		} else {
			logger.error("No File Path");
			ediTransaction.setTransactionStatus("F");
			appServiceDAO.update(ediTransaction);
		}
	}

	protected void saveAuth(EdiTransactions ediTransaction) {
		File f = new File(ediTransaction.getTransactionFilePath());
		logger.info("Processing file ::"+ediTransaction.getTransactionFilePath());
		EdiReader reader = new EdiReader();
		try {
			Transaction transaction = reader.process(new FileInputStream(f));
			WsAuthorization auth=ediAuthService.createAuth(transaction);
			if(auth!=null){
				ediTransaction.setTransactionStatus("PAG");
			ediTransaction.setAuthId(auth.getMedhokID());
			}
			else ediTransaction.setTransactionStatus("X");
			
			appServiceDAO.update(ediTransaction);

		} catch (Exception e) {
			logger.error("processTransaction: Exception creating the Auth", e);
			ediTransaction.setTransactionStatus("X");
			ediTransaction.setTransactionMessage(e.toString());
			appServiceDAO.update(ediTransaction);
		}
	}

	public String getOutgoingFolder() {
		return outgoingFolder;
	}

	public void setOutgoingFolder(String outgoingFolder) {
		this.outgoingFolder = outgoingFolder;
	}

	public String getDecisionedCasesSql() {
		return decisionedCasesSql;
	}

	public void setDecisionedCasesSql(String decisionedCasesSql) {
		this.decisionedCasesSql = decisionedCasesSql;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public HashMap<String, String> getCommunicationEnvelope() {
		return communicationEnvelope;
	}

	public void setCommunicationEnvelope(HashMap<String, String> communicationEnvelope) {
		this.communicationEnvelope = communicationEnvelope;
	}
	
	
	
	
}

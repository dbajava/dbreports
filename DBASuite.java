/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbasuite;

import dbasuite.Instance;
import dbasuite.Report;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 *
 * @author c954080
 */
public class DBASuite {
	private static boolean argC=false;
	private static boolean argS=false;
	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
	private static final byte[] SALT = {
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	};

	/**
	 * @param args the command line arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		//Setting Arguments
		if(args.length>2){
			System.out.println("Invalid number of arguments Max is 2: c s");
			System.exit(0);

		}
		//generate encrypt password
		if(System.getProperty("passwd")!=null){
			try {
				System.out.println("Encrypted Password: "+encrypt(System.getProperty("passwd")));
				System.exit(0);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
		}

		if(args.length!=0){
			for (int i =0;i<args.length;i++){
				if(!args[i].equals("p") &&(!args[i].equals("s"))&&(!args[i].equals("c"))){
					System.out.println("Invalid argument: use s or c");
					System.exit(0);
				}else if(args[i].equals("s")){
					argS=true;
				}
				if(args[i].equals("c")){
					argC=true;
				}
			}
		}
		try {
			//reading directory with xml and building the report instances
			File[] files = new File("xml").listFiles();
			if (!argS){
				System.out.println("checking xml files...");
			}
			//control if a report is scheduled to send on the current day.
			//	boolean toRun=false;
			//If this pathname does not denote a directory, then listFiles() returns null. 
			for (File file : files) {
				//control if there is any report to be send on the current day.
				boolean toSend=false;
				if (file.isFile()) {
					if (!argS){
						System.out.println("Parsing xml file: "+file.getAbsoluteFile().getName());
					}
					if(file.getName().replaceAll("^.*\\.(.*)$", "$1").toLowerCase().equals("xml")){   	
						//reading xml file
						File fXmlFile = new File("xml\\"+file.getAbsoluteFile().getName());
						//parsing xml file into a class
						JAXBContext jaxbContext;
						jaxbContext = JAXBContext.newInstance(Instance.class);
						Unmarshaller jaxbUnmarshaller;
						jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						Instance instance = (Instance)jaxbUnmarshaller.unmarshal( fXmlFile );
						//db connections
						if (!argS){
							System.out.println("instance connection string: "+"jdbc:oracle:thin:@"+instance.getHostName()+":"+instance.getPort()+"/"+instance.getDbName());
						}
						Class.forName("oracle.jdbc.driver.OracleDriver");
						Connection connection = null;
						connection = DriverManager.getConnection("jdbc:oracle:thin:@"+instance.getHostName()+":"+instance.getPort()+"/"+instance.getDbName(),instance.getUserName(),decrypt(instance.getPassw()));
						Statement stmt = connection.createStatement();
						//make html file report
						String content="<html>";
						content=content+"<body><title>Database Report instance: "+instance.getFantasyName()+"</title>";
						//running all reports
						for (int i=0;i<instance.getReports().size();i++){
							Report report = (Report) instance.getReports().get(i);
							if(report.toRun()){
								ArrayList<String> arrLis = report.getColname();
								if (!argS){
									System.out.println("Generating Report: "+report.getTitle());
									System.out.println("Using the query:/n"+report.getQuery());
								}
								if(!report.getTitle().equals("N0N3")){
									content=content+"<hr><font size=\"2\" face=\"arial\" color=\"black\"><br><center><header><h3>"+report.getTitle()+"</h3></header><center>";
								}
								content=content+"<center><table border=\"1\"><tr>";
								for(int n=0;n<arrLis.size();n++){
									content=content+"<th>"+arrLis.get(n).toString()+"</th>";
								}
								content=content+" </tr>";
								ResultSet rs = runQuery(report.getQuery(), stmt);
								content=content+addResults(rs,report.getColname().size());
								content=content+"</table></center></body></font></html>";
							}
							if(!toSend)toSend=report.toRun();
						}
						connection.close();
						if(argC){
							if(toSend){
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss") ;
								Date date = new Date();
								String curDate =dateFormat.format(date);
								String htmlName="report_"+instance.getDbName()+"_"+curDate+".html";
								if (!argS){
									System.out.println("Writing file report.html to path:");
									System.out.println(System.getProperty("user.dir")+File.separator+htmlName);

								}
								//Writing html report
								File htmlFile = new File(System.getProperty("user.dir")+File.separator+htmlName);
								FileWriter fw =new FileWriter(htmlFile);
								fw.write(content);
								fw.close();
							}
						}else{
							if(toSend){
								if (!argS){
									System.out.println("Sending email to:"+instance.getToMail());
								}
								sendMail(content,instance);
							}
						}
					}
				}
			}
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch(JAXBException e){
			e.printStackTrace();
		} catch(SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			System.out.println("Check if your password is crypted on XML files:");
			e.printStackTrace();
		}

	}
	//Get the query, run the query and return the result set
	private static ResultSet runQuery(String sql,Statement stm){
		ResultSet rs=null;
		try {
			rs = stm.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	/*
	 * Used to add the query results into the report with HTML format
	 */
	private static String addResults(ResultSet rs,int numOfEle){
		String content="";
		content=content+"<tr>";
		try {
			while (rs.next()) {
				for(int i=1;i<=numOfEle;i++){
					String tempVal=rs.getString(i);
					content=content+"<td>"+tempVal+"</td>";
				}
				content=content+"</tr>";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return content;
	}
	//send email
	public static void sendMail(String content,Instance inst){
		String from = "AO_Notification_NoReply@boi.com";//change accordingly  
		String host = inst.getHostmail();
		//Get the session object  
		Properties properties = System.getProperties();  
		properties.setProperty("mail.smtp.host", host);  
		Session session = Session.getDefaultInstance(properties);  
		//compose the message  
		try{  
			MimeMessage message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(from));  
			for (int i=0;i<inst.getToMail().size();i++)
				message.addRecipient(Message.RecipientType.TO,new InternetAddress(inst.getToMail().get(i)));  
			message.setSubject("Database Report - Instance: "+inst.getFantasyName());  
			message.setContent(content,"text/html" );  
			// Send message  
			Transport.send(message);  
		}catch (MessagingException mex) {mex.printStackTrace();}  
	}  
	/*
	 * Used to encrypt the password
	 */
	private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}

	private static String base64Encode(byte[] bytes) {
		// NB: This class is internal, and you probably should use another impl
		return new BASE64Encoder().encode(bytes);
	}
	private static byte[] base64Decode(String property) throws IOException {
		// NB: This class is internal, and you probably should use another impl
		return new BASE64Decoder().decodeBuffer(property);
	}
	/*
	 * used to decrypt the password, only used internally
	 */
	private static String decrypt(String property) throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}



}
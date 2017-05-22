package dbasuite;

import dbasuite.Instance;
import dbasuite.Report;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class DBASuite {
	private static boolean argC=false;
	private static boolean argS=false;
	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
	private static final byte[] SALT = {
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
			(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	};

	public static void main(String[] args) {
		if(args.length>2){
			System.out.println("Invalid number of arguments Max is 2: c s");
			System.exit(0);

		}
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
			File[] files = new File("xml").listFiles();
			if (!argS){
				System.out.println("checking xml files...");
			}
			for (File file : files) {
				boolean toSend=false;
				if (file.isFile()) {
					if (!argS){
						System.out.println("Parsing xml file: "+file.getAbsoluteFile().getName());
					}
					if(file.getName().replaceAll("^.*\\.(.*)$", "$1").toLowerCase().equals("xml")){   	
						File fXmlFile = new File("xml\\"+file.getAbsoluteFile().getName());
						JAXBContext jaxbContext;
						jaxbContext = JAXBContext.newInstance(Instance.class);
						Unmarshaller jaxbUnmarshaller;
						jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						Instance instance = (Instance)jaxbUnmarshaller.unmarshal( fXmlFile );
						if (!argS){
							System.out.println("instance connection string: "+"jdbc:oracle:thin:@"+instance.getHostName()+":"+instance.getPort()+"/"+instance.getDbName());
						}
						Class.forName("oracle.jdbc.driver.OracleDriver");
						Connection connection = null;
						connection = DriverManager.getConnection("jdbc:oracle:thin:@"+instance.getHostName()+":"+instance.getPort()+"/"+instance.getDbName(),instance.getUserName(),decrypt(instance.getPassw()));
						Statement stmt = connection.createStatement();
						String content="<html>";
						content=content+"<body><title>Database Report instance: "+instance.getFantasyName()+"</title>";
						for (int i=0;i<instance.getReports().size();i++){
							Report report = (Report) instance.getReports().get(i);
							if(report.toRun()){
								List<String> repQuery=report.getQuery();
								for (int k=0;k<repQuery.size();k++){
									ArrayList<String> arrLis = report.getColname(k);
									if (!argS){
										System.out.println("Generating Report: "+report.getTitle());
										System.out.println("Using the query:\n"+report.getQuery());
										System.out.println("Report:"+k+" out of "+repQuery.size());
									}
									if((!report.getTitle().equals(""))&&(k==0)){
										content=content+"<hr><font size=\"2\" face=\"arial\" color=\"black\"><br><center><header><h3>"+report.getTitle()+"</h3></header><center>";
									}
									content=content+"<center><table border=\"1\"><tr>";
									for(int n=0;n<arrLis.size();n++) content=content+"<th>"+arrLis.get(n).toString()+"</th>";
									content=content+" </tr>";
									ResultSet rs = runQuery(repQuery.get(k), stmt);
									content=content+addResults(rs);
									content=content+"</table></center></body></font></html>";
								}
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
	private static ResultSet runQuery(String sql,Statement stm){
		ResultSet rs=null;
		try {
			rs = stm.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	private static String addResults(ResultSet rs){
		String content="";
		content=content+"<tr>";
		ResultSetMetaData rsmd;
		int columnsNumber=0;
		try {
			rsmd = rs.getMetaData();
			columnsNumber = rsmd.getColumnCount();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			while (rs.next()) {
				for(int i=1;i<=columnsNumber;i++){
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
	public static void sendMail(String content,Instance inst){
		String from = "AO_Notification_NoReply@boi.com";
		String host = inst.getHostmail();
		Properties properties = System.getProperties();  
		properties.setProperty("mail.smtp.host", host);  
		Session session = Session.getDefaultInstance(properties);  
		try{  
			Message message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(from));  
			for (int i=0;i<inst.getToMail().size();i++)
				message.addRecipient(Message.RecipientType.TO,new InternetAddress(inst.getToMail().get(i)));  
	        message.setSubject("Database Report - Instance: "+inst.getFantasyName());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd") ;
			Date date = new Date();
			String curDate =dateFormat.format(date);
			File htmlFile = new File(inst.getDbName()+"_"+curDate+".html");
			FileWriter fw= new FileWriter(htmlFile);;
			fw.write(content);
			fw.close();
			DataSource source = new FileDataSource(htmlFile);
			BodyPart messageBodyPart = new MimeBodyPart();
			dateFormat = new SimpleDateFormat("dd/MM/yyyy") ;
			curDate =dateFormat.format(date);
			messageBodyPart.setText("Please find attached the report for instance: "+inst.getDbName()+"\nDate: "+curDate+"\n\nBEPPAS Integration");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();
			dateFormat = new SimpleDateFormat("ddMMyyyy") ;
			curDate =dateFormat.format(date);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(inst.getDbName()+"_"+curDate+".html");
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);  
			Files.delete(htmlFile.toPath());
		}catch (MessagingException mex) {mex.printStackTrace();}
		catch (IOException e) {
			e.printStackTrace();
		}
	}  
	private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}

	private static String base64Encode(byte[] bytes) {
		return new BASE64Encoder().encode(bytes);
	}
	private static byte[] base64Decode(String property) throws IOException {
		return new BASE64Decoder().decodeBuffer(property);
	}
	private static String decrypt(String property) throws GeneralSecurityException, IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}



}
package dbasuite;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

@XmlRootElement( name = "instance" )
@XmlType( propOrder = { "dbName", "fantasyName","hostName", "port", "userName","passw","reports","mailto","hostmail","fatt" } )
public class Instance {
	private String dbName;
	private String fantasyName;
	private String hostName;
	private String port;
	private String userName;
	private String passw;
	private String mailto;
	private String hostmail;
	private String fatt;
	List<Report> reports;

	
	public String getFatt() {
		return fatt;
	}
	@XmlElement( name = "fattach" )
	public void setFatt(String fatt) {
		this.fatt = fatt;
	}
	public String getDbName() {
		return dbName;
	}
	@XmlElement( name = "iname" )
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getHostName() {
		return hostName;
	}
	@XmlElement( name = "hostname" )
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getPort() {
		return port;
	}
	@XmlElement( name = "port" )
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	@XmlElement( name = "username" )
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassw() {
		return passw;
	}
	@XmlElement( name = "password" )
	public void setPassw(String passw) {
		this.passw = passw;
	}
	public List<Report> getReports() {
		return reports;
	}
	public void add(Report report){
		if (this.reports==null){
			this.reports=new ArrayList<Report>();
		}
		this.reports.add(report);
	}
	@XmlElement(name="report")
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	public String getMailto() {
		return mailto;
	}
	@XmlElement(name="mailto")
	public void setMailto(String mailto) {
		this.mailto = mailto;
	}
	public String getHostmail() {
		return hostmail;
	}
	@XmlElement(name="mailhost")
	public void setHostmail(String hostmail) {
		this.hostmail = hostmail;
	}
	public String getFantasyName() {
		return fantasyName;
	}
	@XmlElement(name="fname")
	public void setFantasyName(String fantasyName) {
		this.fantasyName = fantasyName;
	}
	public ArrayList<String> getToMail(){
		StringTokenizer strTkn = new StringTokenizer(this.mailto, ",");
		ArrayList<String> arrLis = new ArrayList<String>(this.mailto.length());
		while(strTkn.hasMoreTokens())
			arrLis.add(strTkn.nextToken());
		return arrLis;
	}
	public void sendMail(String content){
		String from = "AO_Notification_NoReply@boi.com";
		String host = this.getHostmail();
		Properties properties = System.getProperties();  
		properties.setProperty("mail.smtp.host", host);  
		Session session = Session.getDefaultInstance(properties);  
		try{  
			Message message = new MimeMessage(session);  
			message.setFrom(new InternetAddress(from));  
			for (int i=0;i<this.getToMail().size();i++)
				message.addRecipient(Message.RecipientType.TO,new InternetAddress(this.getToMail().get(i)));  
			message.setSubject(this.getFantasyName());
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd") ;
			Date date = new Date();
			String curDate =dateFormat.format(date);
			File htmlFile = new File(this.getDbName()+"_"+curDate+".html");
			FileWriter fw= new FileWriter(htmlFile);;
			fw.write(content);
			fw.close();
			if(this.port.equals("666")||this.fatt.toLowerCase().equals("false")){
				Scanner scanner = new Scanner( htmlFile, "UTF-8" );
				String text = scanner.useDelimiter("\\A").next();
				if(this.port.equals("666"))
					text="Error found processing the XML file: "+text;
				scanner.close(); 
				message.setContent(text, "text/html");
			}else{
				DataSource source = new FileDataSource(htmlFile);
				BodyPart messageBodyPart = new MimeBodyPart();
				dateFormat = new SimpleDateFormat("dd/MM/yyyy") ;
				curDate =dateFormat.format(date);
				messageBodyPart.setText("Please find attached the report for instance: "+this.getDbName()+"\nDate: "+curDate+"\n\nBEPPAS Integration");
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				messageBodyPart = new MimeBodyPart();
				dateFormat = new SimpleDateFormat("ddMMyyyy") ;
				curDate =dateFormat.format(date);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(this.getDbName()+"_"+curDate+".html");
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
			}
			Transport.send(message);  
			htmlFile.delete();
		}catch (MessagingException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

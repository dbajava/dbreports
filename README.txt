DBREPORTS - README
----------------------
Version Control
----------------------
Version Control
- 1.0 Created 07/04/2017 
- 1.1 Added tag<dayofmonth> and <runhour>
- 1.2 Now is possible to add more than one query per report. Add as many <colnames> and <query> in a report as you want
- 1.3 Removed the N0N3 title, now if a non-titled report is needed, leave the title in blank
- 1.4 Classes more organized. Added the feature to run the report at any time using the "anytime" keyword.
- 1.5 Dealing with bad XML format
- 1.6 Added tag <fattach> on instance level. set it to false to get the report on the e-mail body instead of attached file.

----------------------

----------------------
Command Line Arguments 
----------------------
s silent mode (use this argument for schedules)
c create html file
eg.
java -jar dbreport.jar s
java -jar dbreport.jar c s

----------------------
Installation
----------------------
To install dbreports you will need:
- JRE 1.6+
- Compatible with Oracle 11g and 12c(maybe 10g)
- Copy dbreports.jar to any folder
- Create a folder called xml on the same level as dbreports.jar
- Add your xml files to the xml folder

----------------------
How to run
----------------------
-On dbreport.jar path, using command line, run the following command:
java -jar dbreport.jar -<argument>
eg. 
java -jar dbreport.jar c s
java -jar dbreport.jar s

-You can run standalone xml for tests, just use the parameter xmlsa=xmlfile.xml. Note that the xmlfile.xml has to be on the xml folder
java -Dxmlsa=db01_dbreport.xml -jar dbreport.jar 

Schedule dbreport.jar on crontab or taskmanager to run every hour
----------------------
How to generate an encrypted password
----------------------
-On dbreport.jar
java -Dpasswd=<newPassword> -jar dbreport.jar
If you are running in a Unix/Linux operational system and your password has the character $, you will find a different encrypted password,
comparing with the WINDOWS version. This encrypted password won't work.
$ is a special character on Unix systems.
Put *single* quotes(') around the password on Unix in order to get the correct one.

----------------------
XML configuration
----------------------
Dbreports works in one xml file per database instance. Every file holds only one database instace connection parameters and as many reports as it's needed.
These are the necessary parameters for the database xml file:
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<instance>
		<iname>ptrb.eeee.com</iname> --Database service name, it must be the same name used to connect to the database instance
		<fname>PTRB - Application Report</fname> --Database Report name, the database name that you want to show on the report
		<port>1921</port> --Listener port 
		<hostname>svora03.imagi.nation</hostname> --database hostname
		<username>patmurphy</username> --Database username
		<password>nOpAsSfOrYa</password> --Database password
		<mailto>pat.murphy@eeee.com</mailto> --Email(s) that will recive the report. for more than one e-mail use ","
		<mailhost>54.33.162.22</mailhost> -- SMTP server needed to send e-mail
		<fattach>true</fattach> -- set it to false to get the report on the e-mail body instead of attached file. True to have the report attached
		<report> -- add one tag report for each report for this particular instance
			<title></title> - Report Title
			<query>select instance_name,host_name,version,startup_time,SYSDATE from gv$instance</query> -- Report Query - Please check XML special chars session
			<colnames>instance_name,host_name,version,startup_time,date</colnames> -- Column name that you want to show on report
			<dayofweek>Everyday</dayofweek> --What day of week the report will run. You can write everyday for all days of week(Mon-Sun) or you can write the days that you'd like that the report run separed by ",": Monday,Friday,Sunday
		<dayofmonth>9</dayofmonth> -- From 1 to 31, If you will use this option, change the <dayofweek> tag to "Month" or any word but "Everyday" or a actual week day
		<runhour>12</runhour> -- hour that the report will run. put the keyword "anytime" to run at anytime.
		</report>
</instance>

----------------------
XML Specials
----------------------
-- If your report name is blank the report will have no title and no division(<hr>) with the next report.
-- Special chars
    Symbol 	  					   Escape Sequence
< (less-than)						&#60; or &lt;
> (greater-than)					&#62; or &gt;
& (ampersand)						&#38;
' (apostrophe or single quote)		&#39;
" (double-quote)					&#34;
-- Check the last session XML Example

----------------------
XML Example
----------------------
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<instance>
		<iname>gppmpqes</iname>
		<fname>GPPMPQ - Application Report</fname>
		<port>1921</port>
		<hostname>bbcqgsm28.boigroup.net</hostname>
		<username>b938482</username>
		<password>NoP4$$F4rU%%==</password>
		<mailto>igor.laguardia@tralala.com</mailto>
		<mailhost>20.51.120.1</mailhost>
		<fattach>true</fattach> 
		<report>
			<title>N0N3</title>
			<query>select instance_name,host_name,version,startup_time,SYSDATE from gv$instance</query>
			<colnames>instance_name,host_name,version,startup_time,date</colnames>
			<dayofweek>Everyday</dayofweek>
			<dayofmonth>0</dayofmonth>
			<runhour>12</runhour>
		</report>
		<report>
			<title>EOD job</title>
			<query>select count(*),buyer_name from tableowner1.tablebuyers</query>
			<colnames>Number of buyers, buyer name</colnames>
			<dayofweek>0</dayofweek>
			<dayofmonth>9,15,20</dayofmonth>
			<runhour>10,12,14</runhour>
		</report>	
			<report>
			<title>EOD job</title>
			<query>select count(*),buyer_name from tableowner1.tablebuyers</query>
			<colnames>Number of buyers, buyer name</colnames>
			<dayofweek>Everyday</dayofweek>
			<dayofmonth>0</dayofmonth>
			<runhour>anytime</runhour>
		</report>
</instance>

----------------------
error_xml.conf
----------------------
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<instance>
		<iname>error_xml</iname> -- you can leave it blank or as is.
		<fname>Error on processing XML file</fname> - here goes your error mail subject
		<port>0000</port>-- you can leave it blank or as is.
		<hostname></hostname>-- you can leave it blank or as is.
		<username></username>-- you can leave it blank or as is.
		 <password></password>-- you can leave it blank or as is.
    <mailto>igor.laguardia@tchutchutchu.com</mailto> --Email(s) that will recive the report. for more than one e-mail use ","
    <mailhost>22.22.22.22</mailhost> -- SMTP server needed to send e-mail
</instance>

----------------------
TODO
----------------------
- Remove not necessary files from jdbc and mail libraries
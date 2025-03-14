package com.keyrus.jade.detectincoherence.scheduler;

import com.keyrus.jade.detectincoherence.dto.Action;
import com.keyrus.jade.detectincoherence.dto.Codification;
import com.keyrus.jade.detectincoherence.repository.ActionRepository;
import com.keyrus.jade.detectincoherence.repository.CodificationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import  com.keyrus.jade.detectincoherence.utils.ConstantsHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Configuration
public class DetectIncoherenceJob {


    @Autowired
    private ActionRepository actionRepositoryConfig;
    @Autowired
    private CodificationRepository codificationRepositoryConfig;


    private  static ActionRepository actionRepository ;
    private  static CodificationRepository codificationRepository ;


    private  static String requestCrm ;
    @Value("${request.crm}")
    private  String requestConfigCrm ;

    private  static String requestVue ;
    @Value("${request.vue}")
    private  String requestConfigVue ;

    private  static Boolean vueFromCsv ;
    @Value("${from.csv.vue}")
    private  Boolean vueFromCsvConfig;

    private  static Boolean crmFromCsv ;
    @Value("${from.csv.crm}")
    private  Boolean crmFromCsvConfig;


    @Value("${cron.expression}")
    private  String cron;
    private  static String startDay ;
    @Value("${start.date}")
    private  String startDayConfig;




    private static String inputPathCrm;
    @Value("${job.directory.input.path.crm}")
    private   String inputPathCrmConfig;

    private static String inputPathVue;
    @Value("${job.directory.input.path.confidentialite.vue}")
    private   String inputPathVueConfig;

    private static String outputPath;
    @Value("${job.directory.output.path}")
    private   String outputPathConfig;

    private static String datePattern;
    @Value("${date.pattern}")
    private String datePatternConfig;


    private static String mailTo;
    @Value("${mailTo}")
    private  String mailToConfig;

    private static String mailFrom;
    @Value("${mailFrom}")
    private  String mailFromConfig;

    private static String subject;
    @Value("${subject}")
    private  String subjectConfig;

    private static String body;
    @Value("${body}")
    private  String bodyConfig;

    private static String password;
    @Value("${password}")
    private  String passwordConfig;

    @PostConstruct
    public void init() {
        actionRepository=actionRepositoryConfig;
        codificationRepository=codificationRepositoryConfig;
        requestVue=requestConfigVue;
        requestCrm=requestConfigCrm;
        vueFromCsv = vueFromCsvConfig;
        crmFromCsv = crmFromCsvConfig;
        startDay=startDayConfig;
        inputPathCrm=inputPathCrmConfig;
        inputPathVue=inputPathVueConfig;
        outputPath=outputPathConfig;
        datePattern=datePatternConfig;
        mailTo=mailToConfig;
        mailFrom=mailFromConfig;
        subject=subjectConfig;
        body=bodyConfig;
        password=passwordConfig;
    }

    public  void mainFunc() throws SchedulerException {
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        try {
            Date startDayFormatted = formatter.parse(startDay);
        // Définir le job
        JobDetail job = JobBuilder.newJob(DetectIncoherence.class)
                .withIdentity("databaseJob", "group1")
                .build();

           // Créer un Trigger avec une expression CRON:le job s'exécutera chaque jour, précisément à 01h00 du matin
                  Trigger cronTrigger = TriggerBuilder.newTrigger()
                          .withIdentity("databaseJob", "group1")
                        .startAt(startDayFormatted) // Commence demain à 01h00
                           .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                         .build();

        // Démarrer le scheduler
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        // Planifier le job avec le trigger
        scheduler.scheduleJob(job, cronTrigger);
        } catch (ParseException e) {
            System.err.println("Erreur lors de la conversion de la date : " + e.getMessage());
        }
    }




    public  static   class DetectIncoherence implements Job {

        private static List<Codification> readCSVCrm(String filePath) throws IOException {
            List<Codification> records = new ArrayList<>();
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
                String line;
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    if (values.length >= 3) {
                        Codification codification = new Codification(values[0], values[1], values[2]);
                        if (codification.isValid()) {
                            records.add(codification);
                        }
                    }
                }
            }
            return records;
        }

        private static List<Action> readCSVVue(String filePath) throws IOException {
            List<Action> records = new ArrayList<>();
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
                String line;
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    if (values.length >= 8) {

                        Boolean active = Boolean.parseBoolean(values[6]);
                        Boolean estCreateCaseAuto = Boolean.parseBoolean(values[7]);
                        Action action = new Action(values[0], values[1], values[2],values[3], values[4], values[5], active, estCreateCaseAuto);
                            records.add(action);

                    }
                }
            }
            return records;
        }

        private static List<Codification> fetchDataCRM(String dbUrl, String dbUsername, String dbPassword,String request) throws SQLException {
            List<Codification> entries = new ArrayList<>();
            String query = request;
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {

                    Codification codification = new Codification();

                    codification.setSujet(rs.getString(ConstantsHolder.CRMCASESUBJECT) != null
                            ? rs.getString(ConstantsHolder.CRMCASESUBJECT) : "");
                    codification.setCategorie(rs.getString(ConstantsHolder.CRMCASECATEGORY) != null
                            ? rs.getString(ConstantsHolder.CRMCASECATEGORY) : "");
                    codification.setMotif(rs.getString(ConstantsHolder.CRMCASEREASON) != null
                            ? rs.getString(ConstantsHolder.CRMCASEREASON) : "");

                }

            }
            return entries;
        }
        private static List<Action> fetchDataVue(String dbUrl, String dbUsername, String dbPassword,String request) throws SQLException {
            List<Action> entries = new ArrayList<>();
            String query = request;
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {

                    Action action = new Action();
                    action.setOperateur(rs.getString(ConstantsHolder.TAGOPERATOR) != null
                            ? rs.getString(ConstantsHolder.TAGOPERATOR) : "");
                    action.setTag(
                            rs.getString(ConstantsHolder.TAGACTION) != null ? rs.getString(ConstantsHolder.TAGACTION) : "");
                    action.setLibelle(
                            rs.getString(ConstantsHolder.LABELACTION) != null ? rs.getString(ConstantsHolder.LABELACTION) : "");
                    action.setSujet(rs.getString(ConstantsHolder.CRMCASESUBJECT) != null
                            ? rs.getString(ConstantsHolder.CRMCASESUBJECT) : "");
                    action.setCategorie(rs.getString(ConstantsHolder.CRMCASECATEGORY) != null
                            ? rs.getString(ConstantsHolder.CRMCASECATEGORY) : "");
                    action.setMotif(rs.getString(ConstantsHolder.CRMCASEREASON) != null
                            ? rs.getString(ConstantsHolder.CRMCASEREASON) : "");
                    action.setActive(rs.getBoolean(ConstantsHolder.ACTIVE));
                    action.setEstCreateCaseAuto(rs.getBoolean(ConstantsHolder.EstCreateCaseAuto));
                    entries.add(action);

                    //LIBELLE
                }

            }
            return entries;
        }


        private static List<Action> findIncoherences(List<Action> vueData, List<Codification> crmData) {
            Set<String> crmSet = crmData.stream()
                    .map(Codification::getConcatenatedFields)
                    .collect(Collectors.toSet());

            return vueData.stream()
                    .filter(v -> !crmSet.contains(v.getConcatenatedFields())&& (v.getActive()&&v.getEstCreateCaseAuto()
                                )

                    )
                    .collect(Collectors.toList());
        }


        //return File ou string p   path
        private static void writeCSVAndSend(String filePath, List<Action> data,String date) throws IOException, MessagingException {


            filePath=filePath+"incoherent_vue"+"_"+date+".csv";

            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
                bw.write("libelle;tag;operateur;sujet;categorie;motif\n");

                for (Action record : data) {
                    bw.write(record.getLibelle()+";"+record.getTag()+";"+record.getOperateur()+";"+record.getConcatenatedFields().replace("|", ";"));
                    bw.newLine();
                }
            }


            sendMail( mailTo, mailFrom,filePath, body,subject);

        }




        private static void sendMail(String emailTo, String emailfrom , String csvFilePath,String body,String subject) {
            //  emailList = emailList.replace(";", ",");
            // Configurer le service de messagerie (remplacez par votre configuration)
            String host = "smtp.gmail.com";

            int port = 587; // Port pour TLS
            //  Obtenir une session de messagerie
            Properties properties = System.getProperties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.host", host);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.mime.charset", "UTF-8");
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(emailfrom, password);
                }
            });
            try {

                //Créer le message
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailfrom));
                message.addRecipients(Message.RecipientType.TO, emailTo);
                message.setSubject(subject);
                // Joindre le fichier CSV
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(body);
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(csvFilePath);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(attachmentPart);
                message.setContent(multipart);

                // Envoyer le message

                Transport.send(message);
                System.out.println("E-mail envoyé avec succès à: " + emailTo);
            } catch (Exception e) {
                        e.printStackTrace();
                    }
        }

        @Override
        public void execute(JobExecutionContext context) {
            try {
                List<Codification> crmData = List.of();
                List<Action> vueData= List.of();

                if(crmFromCsv==true) {
                    crmData = readCSVCrm(inputPathCrm);
                }
                else  {
                    System.out.println(
                            "from db"
                    );
                    crmData = codificationRepository.fetchDataCRM(requestCrm);

                    System.out.println("crmdata"+crmData);                }


                if(vueFromCsv==true) {
                    vueData = readCSVVue(inputPathVue);
                }
                else  {
                    vueData = actionRepository.fetchDataVue(requestVue);
                }

                List<Action> incoherentData = findIncoherences(vueData, crmData);

                LocalDateTime currentDateTime =LocalDateTime.now();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
                String date = currentDateTime.format(formatter);

                writeCSVAndSend(outputPath, incoherentData,date);
                System.out.println("Incoherent data exported successfully to " + outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

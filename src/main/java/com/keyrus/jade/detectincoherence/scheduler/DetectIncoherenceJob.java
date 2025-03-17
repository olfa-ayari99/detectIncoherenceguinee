package com.keyrus.jade.detectincoherence.scheduler;

import com.keyrus.jade.detectincoherence.dto.Action;
import com.keyrus.jade.detectincoherence.dto.CaseCrm;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Calendar;
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

    private static String nbJours;
    @Value("${retentionLog}")
    private   String nbJoursConfig;
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

    private  static String intervalle ;
    @Value("${intervall.tolerence.action}")
    private  String intervalleConfig;


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
        nbJours=nbJoursConfig;
        intervalle=intervalleConfig;
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

        private static List<CaseCrm> readCSVCrm(String filePath,String date) throws IOException {
            List<CaseCrm> records = new ArrayList<>();
           String dateFormatted= dateFormatter(date,"yyyy-MM-dd","d/M/yyyy");

            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
                String line;
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    if (values.length >= 4 && (values[0].startsWith(dateFormatted))) {
                        CaseCrm caseCrm = new CaseCrm(values[0], values[1], values[2],values[3]);

                            records.add(caseCrm);

                    }
                }
            }
            return records;
        }

        private static List<Action> readCSVVue(String filePath,String date) throws IOException {
            String dateFormatted= dateFormatter(date,"yyyy-MM-dd","d/M/yyyy");

            List<Action> records = new ArrayList<>();
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
                String line;
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    if (values.length >= 18) {
                        Action action = new Action(values[0],
                                values[1],
                                values[2],
                                values[3],
                                values[4],
                                values[5],
                                values[6],
                                values[7],
                                values[8],
                                values[9],
                                values[10],
                                values[11],
                                values[12],
                                values[13],
                                values[14],
                                values[15],
                                values[16],
                                values[17]
                                ,values[18]
                        );

                        if( values[0].startsWith(dateFormatted) && values[13].equals("SUCCESS") ) {
                            records.add(action);
                        }
                    }
                }
            }
            return records;
        }


       public static String dateFormatter(String dateParam, String inputPattern, String outputPattern) {
           Date date1;
           String date2 = "";
           SimpleDateFormat formatter = new SimpleDateFormat(inputPattern);
           try {
               date1 = formatter.parse(dateParam);
               formatter.applyPattern(outputPattern);
               date2 = formatter.format(date1);
           } catch (Exception e) {
               return "";
           }
           return date2;
       }

        public  List<Action> findIncoherences(List<Action> actions,List<CaseCrm> crmCases ) {

             DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy H:m:s");

            Map<String, LocalDateTime> crmMap = new HashMap<>();

            for (CaseCrm crm : crmCases) {
                crmMap.put(crm.getKey(), LocalDateTime.parse(crm.getDateCreationCase(), FORMATTER));
            }
            List<Action> inconsistencies = new ArrayList<>();
            for (Action action : actions) {
                LocalDateTime actionDate = LocalDateTime.parse(action.getDate(), FORMATTER);
                LocalDateTime crmDate = crmMap.get(action.getKey());
                if (crmDate == null || Math.abs(Duration.between(crmDate, actionDate).getSeconds()) > Integer.valueOf(intervalle)) {
                    inconsistencies.add(action);
                }
            }

            return inconsistencies;
        }



        //return File ou string p   path
        private static void writeCSVAndSend(String filePath, List<Action> data,String date) throws IOException, MessagingException {


            filePath=filePath+"incoherent_vue"+"_"+date+".csv";

            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
                bw.write("Date;ip;server Ip;Login;Module;Service;Code service;Operation;Msisdn;Id Contrat;Id Customer;Source d'interface;Id Crm;Statut;Résultat;Codification;Id Interaction;Type d'interaction;Case Id\n");

                for (Action record : data) {
                    bw.write(record.getDate()+";"+record.getIp()+";"+record.getServerIp()+";"+record.getLogin()+";"+record.getModule()+";"+
                            record.getService()+";"+record.getCodeService()+";"+record.getOperation()+";"+record.getMsisdn()+";"+record.getIdContrat()
                            +";"+record.getIdCustomer()+";"+record.getSourceInterface()+";"+record.getIdCrm()+";"+record.getStatut()+";"+record.getResultat()+";"+record.getCodification()+";"+record.getIdInteraction()+
                            ";"+record.getTypeInteraction()+";"+record.getCaseId());
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
        private static String extractDateFromFileName(String fileName) {
            if (fileName.matches(".*(\\d{1,2}/\\d{1,2}/\\d{4}).*")) {
                return fileName.replaceAll(".*?(\\d{1,2}/\\d{1,2}/\\d{4}).*", "$1");
        /* Remplace tout le texte du nom du fichier par uniquement la date trouvée.
           $1 représente la partie capturée (d/M/yyyy) */
            }
            return null;
        }


        public static List<String> findMissingDates(String directoryPath, int days) {
            File folder = new File(directoryPath);
            if (!folder.exists() || !folder.isDirectory()) {
                System.out.println("Erreur : Le dossier n'existe pas !");
                return Collections.emptyList();
            }
            //  Récupérer les fichiers et extraire les dates valides
            Set<String> fileDates = new HashSet<>();

            /**
             *listFiles() peut retourner null si folder n'existe pas ou n'est pas un dossier.
             * Objects.requireNonNull() déclenche une NullPointerException si listFiles() renvoie null.
             * Cela permet d’éviter d’avoir une boucle sur null, ce qui provoquerait une erreur.
             */
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile()) {
                    String date = extractDateFromFileName(file.getName());
                    if (date != null) {
                        fileDates.add(date);
                    }
                }
            }
            //  Générer les 10 dernières dates
            List<String> missingDates = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            for (int i = 0; i < days; i++) {
                String dateString = dateFormat.format(calendar.getTime());
                if (!fileDates.contains(dateString)) {
                    missingDates.add(dateString);
                }
                calendar.add(Calendar.DAY_OF_YEAR, -1); // Reculer d'un jour
            }

            return missingDates;
        }

        @Override
        public void execute(JobExecutionContext context) {
            try {

                List<CaseCrm> crmData = List.of();
                List<Action> vueData= List.of();

                List<String> missingDates = findMissingDates(outputPath, Integer.valueOf(nbJours));
                System.out.println("missingDates"+missingDates);
                for(String date: missingDates) {
                    if(crmFromCsv==true) {

                        crmData = readCSVCrm(inputPathCrm,date);
                    }
                    else  {
                        System.out.println(
                                "from db"
                        );
                        //crmData = codificationRepository.fetchDataCRM(requestCrm);

                        System.out.println("crmdata"+crmData);                }


                    if(vueFromCsv==true) {
                        vueData = readCSVVue(inputPathVue,date);
                    }
                    else  {
                        //vueData = actionRepository.fetchDataVue(requestVue);
                    }

                    List<Action> incoherentData = findIncoherences(vueData, crmData);

                    writeCSVAndSend(outputPath, incoherentData,date);
                    System.out.println("Incoherent data exported successfully to " + outputPath);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        }


}
}

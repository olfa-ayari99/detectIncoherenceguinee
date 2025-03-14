package com.keyrus.jade.detectincoherence.scheduler;

import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
@Configuration
public class FileCleanupJob {
    @Value("${cron.expression.delete}")
    private String cronExpression;
    private  static String startDay ;
    @Value("${start.date}")
    private  String startDayConfig;



    private static String nbJours;
    @Value("${deleteJob.nbrJour}")
    private   String nbJoursConfig;


   /* private static String agencyPath;
    @Value("${job.directory.path.agency}")
    private   String agencyPathConfig;*/

    private static String outputPath;
    @Value("${job.directory.output.path}")
    private   String outputPathConfig;

    private static String datePattern;
    @Value("${date.pattern}")
    private String datePatternConfig;


    @PostConstruct
    public void init() {
        startDay=startDayConfig;
        nbJours=nbJoursConfig;
   //     agencyPath=agencyPathConfig;
        outputPath=outputPathConfig;
        datePattern=datePatternConfig;

    }
    public  void mainFunc() throws SchedulerException {
        System.out.println("###hello delete file");
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);

        try {
            Date startDayFormatted = formatter.parse(startDay);
            System.out.println("Date convertie : " + startDayFormatted);

            // Définir le job
            JobDetail job = JobBuilder.newJob(FileCleanupJob.DeleteJob.class)
                    .withIdentity("deleteJob", "group2")
                    .build();

            // Créer un Trigger avec une expression CRON:le job s'exécutera chaque jour, précisément à 01h00 du matin
            Trigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("deleteJob", "group2")
                    .startAt(startDayFormatted) // Commence demain à 01h00
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
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


    public  static   class DeleteJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
           // cleanOldFiles(agencyPath, Integer.valueOf(nbJours));
            cleanOldFiles(outputPath, Integer.valueOf(nbJours));
        }
        public static void cleanOldFiles(String directoryPath, int daysToKeep) {
            File folder = new File(directoryPath);
            if (!folder.exists() || !folder.isDirectory()) {
                System.out.println("Erreur : Le dossier n'existe pas !");
                return;
            }

            // Récupérer la date limite (J-10)
            Calendar limitDate = Calendar.getInstance();
            limitDate.add(Calendar.DAY_OF_YEAR, -daysToKeep-1); // Recule de X jours

            //  Parcourir les fichiers du dossier
            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("Aucun fichier à traiter.");
                return;
            }

            int deletedFiles = 0;
            for (File file : files) {
                if (file.isFile()) {
                    Date fileDate = extractDateFromFileName(file.getName());
                    System.out.println(fileDate);
                    if (fileDate != null && fileDate.before(limitDate.getTime())) {
                        if (file.delete()) {
                            System.out.println(" Supprimé : " + file.getName());
                            deletedFiles++;
                        } else {
                            System.out.println("Échec suppression : " + file.getName());
                        }
                    }
                }
            }

            System.out.println(" Nettoyage terminé. " + deletedFiles + " fichiers supprimés.");
        }

        // Extraire la date depuis le nom du fichier (Format: YYYY-MM-DD)
        private static Date extractDateFromFileName(String fileName) {
            try {
                if (fileName.matches(".*(\\d{4}-\\d{2}-\\d{2}).*")) {
                    String dateStr = fileName.replaceAll(".*?(\\d{4}-\\d{2}-\\d{2}).*", "$1");
                    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

                    return dateFormat.parse(dateStr);
                }
            } catch (ParseException e) {
                System.out.println("Format de date invalide pour le fichier : " + fileName);
            }
            return null; // Pas de date valide trouvée
        }
    }


}



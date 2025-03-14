package com.keyrus.jade.detectincoherence;

import com.keyrus.jade.detectincoherence.scheduler.DetectIncoherenceJob;
import com.keyrus.jade.detectincoherence.scheduler.FileCleanupJob;
import jakarta.annotation.PostConstruct;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DetectIncoherenceApplication {

	@Autowired
	DetectIncoherenceJob detectIncoherenceJob;



	@Autowired
	FileCleanupJob fileCleanupJob;

	public static void main(String[] args) {
		SpringApplication.run(DetectIncoherenceApplication.class, args);
	}


	@PostConstruct
	private void init() throws SchedulerException {
		    this.detectIncoherenceJob.mainFunc();
		//	this.fileCleanupJob.mainFunc();

	}

}

package mx.edu.uteq.idgs12.attendance_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class AttendanceMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceMsApplication.class, args);
	}

}

package mx.edu.uteq.idgs12.attendance_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AttendanceMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceMsApplication.class, args);
	}

}

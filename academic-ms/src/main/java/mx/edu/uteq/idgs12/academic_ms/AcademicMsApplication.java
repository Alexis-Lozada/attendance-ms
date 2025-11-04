package mx.edu.uteq.idgs12.academic_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AcademicMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcademicMsApplication.class, args);
	}

}

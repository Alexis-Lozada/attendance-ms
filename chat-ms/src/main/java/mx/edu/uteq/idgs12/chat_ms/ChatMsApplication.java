package mx.edu.uteq.idgs12.chat_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "mx.edu.uteq.idgs12.chat_ms.client")
public class ChatMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatMsApplication.class, args);
	}

}

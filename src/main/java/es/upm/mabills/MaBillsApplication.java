package es.upm.mabills;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MaBillsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaBillsApplication.class, args);
	}

}

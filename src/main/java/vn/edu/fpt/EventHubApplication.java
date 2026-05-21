package vn.edu.fpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EventHubApplication {

	public static void main(String[] args) {

        SpringApplication.run(EventHubApplication.class, args);
        System.out.println("Hello world");
        System.out.println("Nguyễn Phúc Anh Tuấn");
        System.out.println("Khổng Thị Thu Trà");
	}

}

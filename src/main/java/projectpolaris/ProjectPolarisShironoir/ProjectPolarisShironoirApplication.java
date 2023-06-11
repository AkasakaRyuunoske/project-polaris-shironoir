package projectpolaris.ProjectPolarisShironoir;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@Log4j2
public class ProjectPolarisShironoirApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectPolarisShironoirApplication.class, args);
	}

//	@Bean
//	public NewTopic topic() {
//		return TopicBuilder.name("UtsuP")
//				.partitions(1)
//				.replicas(1)
//				.build();
//	}
//
//	@KafkaListener(id = "Sayuri", topics = "UtsuP")
//	public void listen(String in) {
//		log.info(in);
//	}
//
//	@Bean
//	public ApplicationRunner runner(KafkaTemplate<String, String> template) {
//		return args -> {
//			for (int i = 0; i < 10; i++) {
//				Thread.sleep(6000); //six sec
//				template.send("UtsuP", i + ":" +
//						"\"The importance of patience and perseverance in life is like preparing a majestic bird that will soar and cross the vast sky. Through steadfast effort and unwavering determination, we will overcome challenges and reach dreams we never thought possible.\"");
//			}
//		};
//	}
}

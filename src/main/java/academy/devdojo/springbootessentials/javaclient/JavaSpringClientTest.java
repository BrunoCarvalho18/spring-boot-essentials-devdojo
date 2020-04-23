package academy.devdojo.springbootessentials.javaclient;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import academy.devdojo.springbootessentials.model.Student;

public class JavaSpringClientTest {

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplateBuilder()
				.rootUri("http://localhost:8080/v1/protected/students")
				.basicAuthentication("bruno", "devdojo").build();
		restTemplate.getForObject("/{id}", Student.class,3);
	}

}

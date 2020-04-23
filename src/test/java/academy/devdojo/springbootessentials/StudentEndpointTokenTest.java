package academy.devdojo.springbootessentials;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import academy.devdojo.springbootessentials.model.Student;
import academy.devdojo.springbootessentials.repository.StudentRepository;
import org.junit.Before;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEndpointTokenTest {

	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;
	@MockBean
	private StudentRepository studentRepository;
	private HttpEntity<Void> protectedHeader;
	private HttpEntity<Void> adminHeader;
	private HttpEntity<Void> wrongHeader;

	@Before
	public void configProtectedHeaders() {
		String str = "{\"username\":\"matheus\",\"password\":\"devdojo\"}";
		HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
		this.protectedHeader = new HttpEntity<>(headers);
	}

	@Before
	public void configAdminHeaders() {
		String str = "{\"username\":\"bruno\",\"password\":\"devdojo\"}";
		HttpHeaders headers = restTemplate.postForEntity("/login", str, String.class).getHeaders();
		this.adminHeader = new HttpEntity<>(headers);
	}

	@Before
	public void configWrongHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "11111");
		this.wrongHeader = new HttpEntity<>(headers);
	}

	@Test
	public void listStudentsWhenTokenIsIncorrectShouldReturnStatusCode403() {
		ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/v1/protected/students/", GET,
				wrongHeader, String.class);
		Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(401);
	}

	@Test
	public void listStudentsWhenTokenIsCorrectShouldReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/v1/protected/students/2", GET,
				adminHeader, String.class);
		Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
	}

	@Test
	public void listAdminStudentsWhenTokenIsCorrectShouldReturnStatusCode200() {
		ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/v1/protected/students/2", GET,
				protectedHeader, String.class);
		Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
	}

	@Test
	public void getStudentsByIdWhenTokenIsIncorrectShouldReturnStatusCode401() {
		ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/v1/protected/students/2", GET,
				wrongHeader, String.class);
		Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(401);
	}

	@Test
	public void createShouldPersistDataAndReturnStatusCode400EmailExists() throws Exception {
		Student student = new Student(3L, "Sam", "sam@lotr.com");
		BDDMockito.when(studentRepository.save(student)).thenReturn(student);
		ResponseEntity<Student> response = restTemplate.exchange("http://localhost:8080/v1/students/", POST,
				new HttpEntity<>(student, adminHeader.getHeaders()), Student.class);
		Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(400);
	}

}

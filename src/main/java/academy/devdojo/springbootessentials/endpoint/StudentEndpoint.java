package academy.devdojo.springbootessentials.endpoint;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import academy.devdojo.springbootessentials.error.EmailExists;
import academy.devdojo.springbootessentials.error.ResourceNotFoundException;
import academy.devdojo.springbootessentials.model.Student;
import academy.devdojo.springbootessentials.repository.StudentRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("v1")
public class StudentEndpoint {

	@Autowired
	private StudentRepository studentDAO;

	@GetMapping("protected/students")
	@ApiOperation(value="Return a list with all students", response = Student[].class)
	@ApiImplicitParams({
		@ApiImplicitParam(name="Authorization", value="Bearer token", required=true,dataType="string",paramType="header")
	})
	public ResponseEntity<?> listAll(Pageable pageable) {
		return new ResponseEntity<>(studentDAO.findAll(pageable), HttpStatus.OK);
	}

	@GetMapping(path = "protected/students/{id}")
	public ResponseEntity<?> getStudentById(@PathVariable("id") long id, Authentication authentication) {
		Optional<Student> student = studentDAO.findById(id);
		if (!student.isPresent()) {
			throw new ResourceNotFoundException("Student not found for ID: " + id);
		}
		return new ResponseEntity<>(student, HttpStatus.OK);
	}

	@GetMapping(path = "protected/students/findByName/{name}")
	public ResponseEntity<?> findStudentByName(@PathVariable String name) {
		return new ResponseEntity<>(studentDAO.findByNameIgnoreCaseContaining(name), HttpStatus.OK);

	}

	@PostMapping("students")
	@Transactional
	public ResponseEntity<?> save(@Valid @RequestBody Student student) {
	    List<Student> searchStudent = studentDAO.findByEmailIgnoreCaseContaining(student.getEmail());
	    if(!searchStudent.isEmpty()) {
	    	throw new EmailExists("Email exists");
	    }
		return new ResponseEntity<>(studentDAO.save(student), HttpStatus.OK);
	}

	@DeleteMapping(path = "students/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		studentDAO.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("students")
	public ResponseEntity<?> update(@RequestBody Student student) {
		studentDAO.save(student);
		return new ResponseEntity<>(student, HttpStatus.OK);
	}

}

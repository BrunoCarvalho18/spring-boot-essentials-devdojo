package academy.devdojo.springbootessentials.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
public class Student extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String name;

	@NotEmpty
	@Column(unique=true)
	@Email
	private String email;

	public Student() {
	}

	public Student(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public Student(Long id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

}

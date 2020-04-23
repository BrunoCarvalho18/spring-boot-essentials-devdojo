package academy.devdojo.springbootessentials.repository;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import academy.devdojo.springbootessentials.model.Student;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student,Long>{
	
	List<Student> findByNameIgnoreCaseContaining(String name);
	
	List<Student> findByEmailIgnoreCaseContaining(String email);

}

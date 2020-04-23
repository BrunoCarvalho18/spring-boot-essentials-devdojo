package academy.devdojo.springbootessentials.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import academy.devdojo.springbootessentials.model.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User,Long>{
	
	User findByUsername(String username);

}

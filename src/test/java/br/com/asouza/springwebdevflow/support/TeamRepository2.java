package br.com.asouza.springwebdevflow.support;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository2 extends CrudRepository<Team, Long>{

}

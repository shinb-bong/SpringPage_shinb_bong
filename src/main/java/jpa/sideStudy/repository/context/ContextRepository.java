package jpa.sideStudy.repository.context;

import jpa.sideStudy.domain.context.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContextRepository extends JpaRepository<Context,Long> {

}

package jpa.sideStudy.repository.context;

import jpa.sideStudy.domain.context.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContextRepository extends JpaRepository<Context,Long> {

    @Modifying
    @Query("update Context c set c.viewCount = c.viewCount + 1 where c.id = :id")
    void updateView(@Param("id") Long id);

}

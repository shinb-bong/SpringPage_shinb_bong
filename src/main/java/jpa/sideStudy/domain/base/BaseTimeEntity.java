package jpa.sideStudy.domain.base;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    // 생성될때 자동 시간 저장
    @CreatedDate
    private LocalDateTime createdDate;

    // 조회한 Enitity 값이 변경 될때 시간이 자동 저장
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

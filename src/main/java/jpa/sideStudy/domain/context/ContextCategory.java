package jpa.sideStudy.domain.context;

import javax.persistence.Embeddable;

public enum ContextCategory {
    JUST_CHAT("수다"), CODING("코딩"), DAILY("일상"), ETC("기타");

    private String category;
    private ContextCategory(String category) {
        this.category=category;
    }

}

package jpa.sideStudy.domain.member;

public enum MemberRole {
    USER("유저"),ADMIN("관리자"),DEL("삭제");
    private String role;

    MemberRole(String role) {
        this.role = role;
    }
}

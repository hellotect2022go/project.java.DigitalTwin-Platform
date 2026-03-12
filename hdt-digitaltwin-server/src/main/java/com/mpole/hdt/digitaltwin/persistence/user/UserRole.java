package com.mpole.hdt.digitaltwin.persistence.user;

import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="tbl_user_role",
uniqueConstraints = {
        @UniqueConstraint(name="uk_user_role",columnNames = {"user_id","role_id"})
})
public class UserRole extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_role_user"))
    @Comment("users 와 1:N")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id", foreignKey = @ForeignKey(name = "fk_user_role_role"))
    @Comment("role 과 1:N")
    private Role role;
}

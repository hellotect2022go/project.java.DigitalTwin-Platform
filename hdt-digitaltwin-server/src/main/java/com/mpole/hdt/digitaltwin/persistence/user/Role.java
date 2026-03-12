package com.mpole.hdt.digitaltwin.persistence.user;


import com.mpole.hdt.digitaltwin.persistence.common.DateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="tbl_role")
public class Role extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("id")
    private Long roleId;

    @Column(unique = true)
    @Comment("권한이름")
    private String roleName;

    @Comment("권한에 대한 설명")
    private String description;

}

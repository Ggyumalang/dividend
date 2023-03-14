package com.project.dividend.model;

import com.project.dividend.persist.entity.MemberEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private String username;

    private List<String> roles;

    public static MemberDto fromEntity(MemberEntity memberEntity) {
        return MemberDto.builder()
                .username(memberEntity.getUsername())
                .roles(memberEntity.getRoles())
                .build();
    }
}

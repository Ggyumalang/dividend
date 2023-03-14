package com.project.dividend.model;

import com.project.dividend.persist.entity.CompanyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    private String ticker;
    private String name;

    public static Company fromEntity(CompanyEntity companyEntity) {
        return Company.builder()
                .ticker(companyEntity.getTicker())
                .name(companyEntity.getName())
                .build();
    }
}

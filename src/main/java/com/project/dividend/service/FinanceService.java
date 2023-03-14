package com.project.dividend.service;

import com.project.dividend.exception.DividendException;
import com.project.dividend.model.Company;
import com.project.dividend.model.Dividend;
import com.project.dividend.model.ScrapedResult;
import com.project.dividend.model.constants.CacheKey;
import com.project.dividend.persist.entity.CompanyEntity;
import com.project.dividend.persist.entity.DividendEntity;
import com.project.dividend.persist.repository.CompanyRepository;
import com.project.dividend.persist.repository.DividendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.dividend.model.constants.ErrorCode.COMPANY_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;

    //요청의 빈도가 높은가?
    //자주 변경되는 데이터인가?
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        //1. 회사명을 기준으로 회사 정보 조회
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(() -> new DividendException(COMPANY_NOT_FOUND));

        //2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntityList = dividendRepository.findAllByCompanyId(companyEntity.getId());

        //3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntityList.stream()
                .map(e -> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(companyEntity.getTicker())
                .name(companyEntity.getName())
                .build(), dividends);
    }
}

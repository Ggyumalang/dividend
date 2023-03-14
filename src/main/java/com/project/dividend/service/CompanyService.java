package com.project.dividend.service;

import com.project.dividend.exception.DividendException;
import com.project.dividend.model.Company;
import com.project.dividend.model.ScrapedResult;
import com.project.dividend.persist.entity.CompanyEntity;
import com.project.dividend.persist.entity.DividendEntity;
import com.project.dividend.persist.repository.CompanyRepository;
import com.project.dividend.persist.repository.DividendRepository;
import com.project.dividend.scraper.YahooFinanceScraper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.dividend.model.constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final YahooFinanceScraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new DividendException(ALREADY_EXIST_TICKER);
        }

        return storeCompanyAndDividend(ticker);
    }

    public Page<Company> getAllCompany(Pageable pageable) {
        Page<CompanyEntity> companyEntityPage = this.companyRepository.findAll(pageable);
        return companyEntityPage.map(Company::fromEntity);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크래핑
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new DividendException(SCRAPING_FAILED);
        }
        //해당 회사가 존재할 경우, 회사의 배당금 정보 스크래핑
        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        //스크래핑 결과
        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public void addAutocompleteKeyword(String keyword) {
        if (!ObjectUtils.isEmpty(keyword)) {
            trie.put(keyword, null);
        }
    }

    //1. Keyword를 통해서 회사 이름을 가져오는 방법 - Trie를 이용
    public List<String> autocomplete(String keyword) {
        if (ObjectUtils.isEmpty(keyword)) {
            return new ArrayList<>();
        }
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }


    @Transactional
    public String deleteCompany(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new DividendException(COMPANY_NOT_FOUND));

        dividendRepository.deleteAllByCompanyId(companyEntity.getId());
        companyRepository.delete(companyEntity);
        deleteAutocompleteKeyword(companyEntity.getName());
        return companyEntity.getName();
    }


}

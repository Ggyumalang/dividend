package com.project.dividend.scheduler;

import com.project.dividend.model.Company;
import com.project.dividend.model.ScrapedResult;
import com.project.dividend.model.constants.CacheKey;
import com.project.dividend.persist.entity.CompanyEntity;
import com.project.dividend.persist.entity.DividendEntity;
import com.project.dividend.persist.repository.CompanyRepository;
import com.project.dividend.persist.repository.DividendRepository;
import com.project.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableCaching
@RequiredArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    //일정 주기마다 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info(getClass().getSimpleName() + "started " + LocalDateTime.now());
        //저장된 회사 목록을 조회
        List<CompanyEntity> companyEntityList = companyRepository.findAll();

        //회사마다 배당금 정보를 새로 스크래핑
        for (CompanyEntity entity : companyEntityList) {
            log.info("getCompanyEntityStarted >> " + LocalDateTime.now() + " company >> " + entity.getName());
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(Company.builder()
                    .name(entity.getName())
                    .ticker(entity.getTicker())
                    .build());
            //스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장한다.
            scrapedResult.getDividends().stream()
                    //Dividend 모델을 Dividend 엔디티로 매핑
                    .map(e -> new DividendEntity(entity.getId(), e))
                    //엘리먼트를 하나씩 확인 후 존재하지 않으면 레파지토리에 삽입한다.
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });
            //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            //해당 사이트에 부하를 줄 수 있으므로.
            try {
                Thread.sleep(3000); //3Seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


}

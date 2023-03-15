package com.project.dividend.scraper;

import com.project.dividend.exception.DividendException;
import com.project.dividend.model.Company;
import com.project.dividend.model.Dividend;
import com.project.dividend.model.ScrapedResult;
import com.project.dividend.model.constants.Month;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.project.dividend.model.constants.ErrorCode.*;

@Slf4j
@Component
public class YahooFinanceScraper implements Scraper {

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400; // 60 * 60 * 24 1일

    private static final String ATTRIBUTE_KEY = "data-test";
    private static final String ATTRIBUTE_VALUE = "historical-prices";
    private static final String DIVIDEND = "Dividend";

    @Override
    public ScrapedResult scrap(Company company) {
        log.info("scrap is started : " + LocalDateTime.now()
                + " ticker : " + company.getTicker() + " company : " + company.getName());
        ScrapedResult scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);
        try {
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue(ATTRIBUTE_KEY, ATTRIBUTE_VALUE);
            Element tableEle = parsingDivs.get(0); //table 전체를 가져온다.

            Element tbody = tableEle.children().get(1);// 0 : head / 1 : body / 2 : foot
            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith(DIVIDEND)) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new DividendException(INVALID_MONTH);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
            }
            scrapResult.setDividends(dividends);
        } catch (IOException e) {
            log.error("Scrap is failed by IOException", e);
            throw new DividendException(SCRAPING_FAILED);
        }
        log.info("scrap is finished : " + LocalDateTime.now()
                + " ticker : " + company.getTicker() + " company : " + company.getName());
        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        log.info("scrapCompanyByTicker is started : " + LocalDateTime.now() + " ticker : " + ticker);
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);

            //기존 강사님이 진행하실 때와 홈페이지 코드가 달라져 하기와 같이 변경함.
            StringBuilder title = new StringBuilder();
            String[] splitWords = titleEle.text().split(" ");
            for (String word : splitWords) {
                if (word.startsWith("(")) {
                    break;
                }
                title.append(word).append(" ");
            }

            if (title.toString().trim().length() == 0) {
                log.error("유효하지 않은 TICKER라 회사명이 추출되지 않았습니다. " + ticker);
                throw new DividendException(INVALID_TICKER);
            }

            log.info("scrapCompanyByTicker is finished : " + LocalDateTime.now()
                    + " ticker : " + ticker + " companyName : " + title.toString().trim());

            return Company.builder()
                    .ticker(ticker)
                    .name(title.toString().trim())
                    .build();
        } catch (IOException e) {
            log.error("scrapCompanyByTicker is failed by IOException", e);
            throw new DividendException(SCRAPING_FAILED);
        }
    }
}

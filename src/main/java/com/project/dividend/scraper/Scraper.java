package com.project.dividend.scraper;

import com.project.dividend.model.Company;
import com.project.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}

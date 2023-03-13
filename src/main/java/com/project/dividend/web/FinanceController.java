package com.project.dividend.web;

import com.project.dividend.model.ScrapedResult;
import com.project.dividend.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<ScrapedResult> searchFinance(@PathVariable String companyName){
        ScrapedResult scrapedResult = financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(scrapedResult);
    }


}

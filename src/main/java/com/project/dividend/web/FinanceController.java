package com.project.dividend.web;

import com.project.dividend.model.ScrapedResult;
import com.project.dividend.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<ScrapedResult> searchFinance(@PathVariable String companyName) {
        return ResponseEntity.ok(financeService.getDividendByCompanyName(companyName));
    }


}

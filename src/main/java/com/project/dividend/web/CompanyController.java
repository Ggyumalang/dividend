package com.project.dividend.web;

import com.project.dividend.model.Company;
import com.project.dividend.model.constants.CacheKey;
import com.project.dividend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    private final CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocomplete(@RequestParam String keyword) {
        //트라이 자료구조를 이용해서 찾는 방법
//        List<String> autocomplete = companyService.autocomplete(keyword);
        //키워드를 이용해 (StartsWith) 찾는 방법
        List<String> autocomplete = companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(autocomplete);
    }

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany() {
        Pageable pageable = PageRequest.of(0, 5);
        return ResponseEntity.ok(this.companyService.getAllCompany(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Company> addCompany(
            @RequestBody Company request
    ) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }
        Company company = companyService.save(ticker);
        companyService.addAutocompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = companyService.deleteCompany(ticker);
        this.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        Objects.requireNonNull(redisCacheManager.getCache(CacheKey.KEY_FINANCE)).evict(companyName);
    }
}

package com.villysiu.yumtea.service;


import com.villysiu.yumtea.models.purchase.Purchase;
import com.villysiu.yumtea.projection.PurchaseProjection;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PurchaseService {
    Long createPurchase(Map<String, Object> purchaseDto);

//    List<PurchaseProjection> getPurchases();
    List<PurchaseProjection> getUserPurchases(Long userId);
    List<Purchase> getFullPurchases(Long userId);

//    List<PurchaseProjection> getPurchaseProjectionWithChildren(Long userId);

    PurchaseProjection getPurchaseById(Long id);
}

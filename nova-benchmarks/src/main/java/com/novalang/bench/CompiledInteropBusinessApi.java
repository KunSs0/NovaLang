package com.novalang.bench;

/**
 * 用于编译后 Java 互操作基准的确定性业务 API。
 */
public final class CompiledInteropBusinessApi {

    private CompiledInteropBusinessApi() {
    }

    /**
     * 简单场景：根据会员等级和优惠券计算商品报价（单位：分）。
     */
    public static int quoteMemberPrice(int listPriceCents, int memberLevel, int couponCode) {
        int normalizedLevel = memberLevel % 4;
        int levelDiscountBps;
        if (normalizedLevel == 1) {
            levelDiscountBps = 300;
        } else if (normalizedLevel == 2) {
            levelDiscountBps = 650;
        } else if (normalizedLevel == 3) {
            levelDiscountBps = 1000;
        } else {
            levelDiscountBps = 0;
        }

        int couponDiscountBps = (couponCode % 5) * 125;
        int totalDiscountBps = levelDiscountBps + couponDiscountBps;
        int discountedPrice = listPriceCents * (10000 - totalDiscountBps) / 10000;
        int handlingFee = couponCode % 3 == 0 ? 0 : 25 + normalizedLevel * 5;
        return discountedPrice + handlingFee;
    }

    /**
     * 中等场景：结算一条订单行，并把滚动小计混入下一轮状态。
     */
    public static int settleOrderLine(int unitPriceCents, int quantity, int memberLevel,
                                      int promotionCode, int rollingSubtotal) {
        int normalizedQuantity = quantity < 1 ? 1 : quantity;
        int normalizedLevel = memberLevel % 4;
        int normalizedPromotion = promotionCode % 7;
        int lineAmount = unitPriceCents * normalizedQuantity;
        int memberDiscountBps = normalizedLevel * 250;
        int promotionDiscountBps;
        if (normalizedPromotion == 1 || normalizedPromotion == 4) {
            promotionDiscountBps = 450;
        } else if (normalizedPromotion == 2 || normalizedPromotion == 5) {
            promotionDiscountBps = 700;
        } else if (normalizedPromotion == 3 || normalizedPromotion == 6) {
            promotionDiscountBps = 250;
        } else {
            promotionDiscountBps = 0;
        }

        int discountBps = memberDiscountBps + promotionDiscountBps;
        int discountedAmount = lineAmount * (10000 - discountBps) / 10000;
        int taxBps = 500 + normalizedPromotion * 25;
        int taxAmount = discountedAmount * taxBps / 10000;
        int stateMix = (rollingSubtotal * 31 + normalizedPromotion * 17
                + normalizedLevel * 13 + normalizedQuantity) % 1000003;
        return (discountedAmount + taxAmount + stateMix) % 1000003;
    }

    /**
     * 复杂场景：基于支付上下文计算下一轮风险评分。
     */
    public static int evaluatePaymentRisk(int customerId, int amountCents, int channelCode,
                                          int regionCode, int deviceTrustScore, int previousRisk) {
        int normalizedChannel = channelCode % 5;
        int normalizedRegion = regionCode % 8;
        int normalizedTrust = deviceTrustScore % 100;
        int score = previousRisk % 1000003;

        if (amountCents >= 70000) {
            score = score + 340;
        } else if (amountCents >= 25000) {
            score = score + 150;
        } else {
            score = score + 45;
        }

        if (normalizedChannel == 3 || normalizedChannel == 4) {
            score = score + 95;
        } else {
            score = score + normalizedChannel * 21;
        }

        if (normalizedRegion == 2 || normalizedRegion == 6) {
            score = score + 70;
        } else {
            score = score + normalizedRegion * 9;
        }

        if (normalizedTrust < 30) {
            score = score + 180;
        } else if (normalizedTrust < 70) {
            score = score + 55;
        } else {
            score = score - 20;
        }

        int identityMix = (customerId * 31 + amountCents * 7
                + normalizedChannel * 43 + normalizedRegion * 59) % 997;
        score = (score + identityMix) % 1000003;
        if (score < 0) {
            score = score + 1000003;
        }
        return score;
    }
}

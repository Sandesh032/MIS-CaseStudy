package com.example.parknride.Utils;

public class QrCodeGenerator {
    public static String generateQrCodeUrl(Long reservationId) {
        String qrText = "https://example.com/parking/reservation/" + reservationId;
        return "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + qrText;
    }
}
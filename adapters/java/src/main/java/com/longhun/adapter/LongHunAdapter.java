package com.longhun.adapter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LongHunAdapter {
    private final String uid;
    private final String device;
    private static final Pattern DNA_REGEX = Pattern.compile("^#LongHun⚡️([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([A-Z][a-zA-Z]+)·([\\u4DC0-\\u4DFF][A-Za-z]+)-(.+)-([a-f0-9]{8})$");

    public LongHunAdapter(String uid, String device) {
        this.uid = uid != null ? uid : "9622";
        this.device = device != null ? device : "HM-9622-001";
    }

    public String generateDNA(String taskType, String action, String version) {
        String body = "ADAPTER-" + taskType.toUpperCase() + "-" + action.toUpperCase() + "-" + version;
        String raw = "BingWuGuiWeiJiaZiZiShi䷾JiJi" + body + this.device + ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String hash8 = sha256Hex(raw).substring(0, 8);
        return "#LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-" + body + "-" + hash8;
    }

    public boolean validate(String dna) {
        return dna != null && DNA_REGEX.matcher(dna).matches();
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "00000000";
        }
    }
}

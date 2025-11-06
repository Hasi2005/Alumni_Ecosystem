package com.example.fund_allocation2.controller;

import com.example.fund_allocation2.auth.AuthServiceClient;
import com.example.fund_allocation2.auth.MeResponse;
import com.example.fund_allocation2.dto.DonationRequest;
import com.example.fund_allocation2.models.Donation;
import com.example.fund_allocation2.services.DonationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/donations")
public class DonationController {
    private final DonationService donationService;
    private final AuthServiceClient authClient;

    @Value("${stripe.secret.key:}")
    private String stripeSecretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public DonationController(DonationService donationService, AuthServiceClient authClient) {
        this.donationService = donationService;
        this.authClient = authClient;
    }

    @PostMapping
    public ResponseEntity<?> donate(@RequestBody DonationRequest request, HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only alumni can donate");
        }
        Donation saved = donationService.donate(request, me.getUsername());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> myDonations(HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only alumni can view their donations");
        }
        List<Donation> list = donationService.getDonationsForAlumni(me.getUsername());
        return ResponseEntity.ok(list);
    }

    // Create a Stripe Checkout Session using HTTP API and return the session URL
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody DonationRequest request, HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (me.getRoles() == null || !me.getRoles().contains("alumni")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only alumni can donate");
        }
        if (request == null || request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("Invalid amount");
        }
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe not configured on server");
        }

        long amountInCents = Math.round(request.getAmount() * 100);

        // Build form-encoded body
        StringBuilder form = new StringBuilder();
        try {
            // line_items[0][price_data][currency]=usd
            appendForm(form, "line_items[0][price_data][currency]", "usd");
            appendForm(form, "line_items[0][price_data][product_data][name]", "Alumni Donation");
            appendForm(form, "line_items[0][price_data][unit_amount]", String.valueOf(amountInCents));
            appendForm(form, "line_items[0][quantity]", "1");
            appendForm(form, "mode", "payment");
            // Include session_id placeholder in success URL so client can verify
            // after successful payment Stripe will redirect here; include redirect=true so server can send a browser redirect to the list page
            appendForm(form, "success_url", "http://localhost:8080/donations/complete?session_id={CHECKOUT_SESSION_ID}&redirect=true");
            appendForm(form, "cancel_url", "http://localhost:8080/donations/new");
            // record who started the session so the server can attribute the payment even if auth cookie is not present on redirect
            appendForm(form, "client_reference_id", me.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to build request: " + e.getMessage());
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.stripe.com/v1/checkout/sessions"))
                .header("Authorization", "Bearer " + stripeSecretKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form.toString()))
                .build();

        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 400) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe error: " + resp.body());
            }
            JsonNode json = objectMapper.readTree(resp.body());
            String url = json.has("url") ? json.get("url").asText() : null;
            Map<String, String> out = new HashMap<>();
            out.put("url", url != null ? url : "");
            return ResponseEntity.ok(out);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe request failed: " + e.getMessage());
        }
    }

    // Called by browser after successful Checkout; verify session and record donation server-side
    @GetMapping("/complete")
    public ResponseEntity<?> complete(@RequestParam("session_id") String sessionId,
                                      @RequestParam(value = "redirect", defaultValue = "true") boolean redirect,
                                      HttpServletRequest httpRequest) {
        MeResponse me = authClient.me(httpRequest);
        if (me == null || !me.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe not configured on server");
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.stripe.com/v1/checkout/sessions/" + encode(sessionId)))
                .header("Authorization", "Bearer " + stripeSecretKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .GET()
                .build();

        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 400) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe error: " + resp.body());
            }
            JsonNode json = objectMapper.readTree(resp.body());
            String paymentStatus = json.has("payment_status") ? json.get("payment_status").asText() : null;
            if (paymentStatus == null || (!"paid".equalsIgnoreCase(paymentStatus) && !"complete".equalsIgnoreCase(paymentStatus))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment is not completed yet");
            }
            long amountTotal = json.has("amount_total") ? json.get("amount_total").asLong() : 0L;
            double amount = amountTotal / 100.0;
            // Determine alumni username: prefer authenticated user, otherwise use client_reference_id from the session
            String clientRef = json.has("client_reference_id") && !json.get("client_reference_id").isNull() ? json.get("client_reference_id").asText() : null;
            String alumniUsername = null;
            if (me != null && me.isAuthenticated()) {
                alumniUsername = me.getUsername();
            } else if (clientRef != null && !clientRef.isBlank()) {
                alumniUsername = clientRef;
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated and no client reference available");
            }
            DonationRequest dr = new DonationRequest();
            dr.setAmount(amount);
            Donation saved = donationService.donate(dr, alumniUsername);
            if (redirect) {
                // Redirect browser to donations list after successful recording
                return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/donations/list").build();
            }
            return ResponseEntity.ok(saved);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe request failed: " + e.getMessage());
        }
    }

    private static void appendForm(StringBuilder b, String key, String value) {
        if (b.length() > 0) b.append('&');
        b.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
        b.append('=');
        b.append(URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8));
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

package com.example.mentorship_service.controller;

import com.example.mentorship_service.model.Appointment;
import com.example.mentorship_service.service.AppointmentService;
import com.example.mentorship_service.auth.AuthServiceClient;
import com.example.mentorship_service.auth.MeResponse;
import com.example.mentorship_service.auth.UserLookupResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;
    private final AuthServiceClient authClient;

    @Value("${auth.public.base-url:http://34.66.236.172:8081}")
    private String authPublicBaseUrl;

    public AppointmentController(AppointmentService service, AuthServiceClient authClient) {
        this.service = service;
        this.authClient = authClient;
    }

    @GetMapping("/new")
    public String showForm(Model model, HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can create appointments");
        }
        Appointment appt = Appointment.builder().status("SCHEDULED").build();
        model.addAttribute("appointment", appt);
        model.addAttribute("currentUser", me.getUsername());
        return "appointment_form";
    }

    @GetMapping("/api/check-alumni")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAlumni(@RequestParam("username") String username, HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated() || me.getRoles() == null || !me.getRoles().contains("student")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> out = new HashMap<>();
        out.put("query", username);
        if (username == null || username.trim().isEmpty()) {
            out.put("exists", false);
            return ResponseEntity.ok(out);
        }
        UserLookupResponse res = authClient.findUserByUsername(username.trim());
        out.put("exists", res != null && res.isExists());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/save")
    public String saveAppointment(@ModelAttribute Appointment appointment, Model model, HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        if (me.getRoles() == null || !me.getRoles().contains("student")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can create appointments");
        }
        // Validate alumni exists in auth-service
        String alumni = appointment.getAlumniName() != null ? appointment.getAlumniName().trim() : "";
        if (alumni.isEmpty()) {
            model.addAttribute("error", "Alumni name is required.");
            model.addAttribute("appointment", appointment);
            model.addAttribute("currentUser", me.getUsername());
            return "appointment_form";
        }
        UserLookupResponse user = authClient.findUserByUsername(alumni);
        if (user == null || !user.isExists()) {
            model.addAttribute("error", "Alumni '" + alumni + "' was not found.");
            model.addAttribute("appointment", appointment);
            model.addAttribute("currentUser", me.getUsername());
            return "appointment_form";
        }

        // Enforce server-side fields
        appointment.setAlumniName(alumni);
        appointment.setStudentName(me.getUsername());
        appointment.setStatus("SCHEDULED");
        service.createAppointment(appointment);
        model.addAttribute("message", "Appointment saved successfully");
        return "appointment_success";
    }

    @GetMapping("/all")
    public String listAppointments(Model model, HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        String username = me.getUsername();
        boolean isStudent = me.getRoles() != null && me.getRoles().contains("student");
        boolean isAlumni = me.getRoles() != null && me.getRoles().contains("alumni");

        List<Appointment> list;
        if (isStudent) {
            list = service.getAppointmentsByStudent(username);
        } else if (isAlumni) {
            list = service.getAppointmentsByAlumni(username);
        } else {
            list = service.getAllAppointments();
        }
        model.addAttribute("appointments", list);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isAlumni", isAlumni);
        model.addAttribute("currentUser", username);
        return "appointments_list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Integer id, HttpServletRequest request) {
        MeResponse me = authClient.me(request);
        if (me == null || !me.isAuthenticated()) {
            return "redirect:" + authPublicBaseUrl + "/login";
        }
        boolean isAlumni = me.getRoles() != null && me.getRoles().contains("alumni");
        if (!isAlumni) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only alumni can approve appointments");
        }
        Appointment appt = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!me.getUsername().equals(appt.getAlumniName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only approve your own appointments");
        }
        service.approveAppointment(id);
        return "redirect:/appointments/all";
    }
}

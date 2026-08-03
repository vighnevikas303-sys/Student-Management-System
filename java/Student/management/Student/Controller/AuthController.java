package Student.management.Student.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import Student.management.Student.model.User;
import Student.management.Student.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081")
public class AuthController {


	

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    //Get current logged-in user info 
	    @GetMapping("/me")
	    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication auth) {
	        if (auth == null || !auth.isAuthenticated()) {
	            Map<String, Object> res = new HashMap<>();
	            res.put("success", false);
	            res.put("message", "Not logged in");
	            return ResponseEntity.status(401).body(res);
	        }
	        User user = userRepository.findByUsername(auth.getName())
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        Map<String, Object> res = new HashMap<>();
	        res.put("success",   true);
	        res.put("username",  user.getUsername());
	        res.put("fullName",  user.getFullName());
	        res.put("role",      user.getRole().replace("ROLE_", ""));
	        res.put("email",     user.getEmail());
	        res.put("studentId", user.getStudentId());
	        return ResponseEntity.ok(res);
	    }

	    //Get all users (ADMIN only) 
	    @GetMapping("/users")
	    public ResponseEntity<List<User>> getAllUsers() {
	        return ResponseEntity.ok(userRepository.findAll());
	    }

	    // ── Create new user (ADMIN only) ───────────────────────────────────────
	    @PostMapping("/users")
	    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
	        Map<String, Object> res = new HashMap<>();
	        if (userRepository.existsByUsername(user.getUsername())) {
	            res.put("success", false);
	            res.put("message", "Username already exists");
	            return ResponseEntity.badRequest().body(res);
	        }
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        // Ensure role prefix
	        if (!user.getRole().startsWith("ROLE_")) {
	            user.setRole("ROLE_" + user.getRole().toUpperCase());
	        }
	        userRepository.save(user);
	        res.put("success", true);
	        res.put("message", "User created successfully");
	        return ResponseEntity.ok(res);
	    }

	    // ── Update user (ADMIN only) ───────────────────────────────────────────
	    @PutMapping("/users/{id}")
	    public ResponseEntity<Map<String, Object>> updateUser(
	            @PathVariable Long id, @RequestBody User updated) {
	        Map<String, Object> res = new HashMap<>();
	        return userRepository.findById(id).map(user -> {
	            user.setFullName(updated.getFullName());
	            user.setEmail(updated.getEmail());
	            user.setRole(!updated.getRole().startsWith("ROLE_")
	                    ? "ROLE_" + updated.getRole().toUpperCase()
	                    : updated.getRole());
	            user.setActive(updated.isActive());
	            user.setStudentId(updated.getStudentId());
	            if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
	                user.setPassword(passwordEncoder.encode(updated.getPassword()));
	            }
	            userRepository.save(user);
	            res.put("success", true);
	            res.put("message", "User updated");
	            return ResponseEntity.ok(res);
	        }).orElseGet(() -> {
	            res.put("success", false);
	            res.put("message", "User not found");
	            return ResponseEntity.notFound().<Map<String,Object>>build();
	        });
	    }

	    // ── Delete user (ADMIN only) ───────────────────────────────────────────
	    @DeleteMapping("/users/{id}")
	    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
	        userRepository.deleteById(id);
	        Map<String, Object> res = new HashMap<>();
	        res.put("success", true);
	        res.put("message", "User deleted");
	        return ResponseEntity.ok(res);
	    }

	    // ── Change own password ────────────────────────────────────────────────
	    @PostMapping("/change-password")
	    public ResponseEntity<Map<String, Object>> changePassword(
	            @RequestBody Map<String, String> body,
	            Authentication auth) {
	        Map<String, Object> res = new HashMap<>();
	        User user = userRepository.findByUsername(auth.getName())
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        if (!passwordEncoder.matches(body.get("oldPassword"), user.getPassword())) {
	            res.put("success", false);
	            res.put("message", "Old password is incorrect");
	            return ResponseEntity.badRequest().body(res);
	        }
	        user.setPassword(passwordEncoder.encode(body.get("newPassword")));
	        userRepository.save(user);
	        res.put("success", true);
	        res.put("message", "Password changed successfully");
	        return ResponseEntity.ok(res);
	    }
	}




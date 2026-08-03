package Student.management.Student.Security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import Student.management.Student.model.User;
import Student.management.Student.repository.UserRepository;
import Student.management.Student.service.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Lazy
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ── Public pages and auth endpoints ──────────────────────
                .requestMatchers(
                    "/", "/index.html", "/login.html",
                    "/*.html", "/*.js", "/*.css",
                    "/student-dashboard.html",
                    "/student-attendance.html",
                    "/student-marks.html",
                    "/student-fee.html"
                ).permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()

                // ── Auth - current user (authenticated only) ─────────────
                .requestMatchers("/api/auth/me").authenticated()

                // ── User management (ADMIN only) ─────────────────────────
                .requestMatchers("/api/auth/users/**").hasRole("ADMIN")

                // ── Fee summary (ADMIN only - specific before general) ────
                .requestMatchers(HttpMethod.GET, "/api/fee/summary").hasRole("ADMIN")

                // ── Student CRUD ─────────────────────────────────────────
                .requestMatchers(HttpMethod.POST,   "/api/students").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/students/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/students").hasAnyRole("ADMIN","TEACHER")
                .requestMatchers(HttpMethod.GET,    "/api/students/**").hasAnyRole("ADMIN","TEACHER","STUDENT")

                // ── Attendance ───────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/attendance/**").hasAnyRole("ADMIN","TEACHER")
                .requestMatchers(HttpMethod.PUT,  "/api/attendance/**").hasAnyRole("ADMIN","TEACHER")
                .requestMatchers(HttpMethod.GET,  "/api/attendance/**").hasAnyRole("ADMIN","TEACHER","STUDENT")

                // ── Marks ────────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/marks").hasAnyRole("ADMIN","TEACHER")
                .requestMatchers(HttpMethod.PUT,  "/api/marks/**").hasAnyRole("ADMIN","TEACHER")
                .requestMatchers(HttpMethod.GET,  "/api/marks/**").hasAnyRole("ADMIN","TEACHER","STUDENT")

                // ── Fee ──────────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST,   "/api/fee").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/fee/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/fee/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,    "/api/fee/**").hasAnyRole("ADMIN","STUDENT")

                // ── Everything else needs login ───────────────────────────
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler((req, res, auth) -> {
                    res.setContentType("application/json");
                    String role = auth.getAuthorities()
                            .iterator().next().getAuthority()
                            .replace("ROLE_", "");
                    res.getWriter().write(
                        "{\"success\":true,\"role\":\"" + role +
                        "\",\"username\":\"" + auth.getName() + "\"}"
                    );
                })
                .failureHandler((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write(
                        "{\"success\":false," +
                        "\"message\":\"Invalid username or password\"}"
                    );
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setContentType("application/json");
                    res.getWriter().write(
                        "{\"success\":true,\"message\":\"Logged out\"}"
                    );
                })
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write(
                        "{\"success\":false," +
                        "\"message\":\"Please login first\"}"
                    );
                })
                .accessDeniedHandler((req, res, accessEx) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write(
                        "{\"success\":false," +
                        "\"message\":\"Access denied for your role\"}"
                    );
                })
            );

        return http.build();
    }

    @Bean
    public CommandLineRunner createDefaultUsers() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder().encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setFullName("System Admin");
                admin.setEmail("admin@sms.com");
                userRepository.save(admin);
                System.out.println("✅ Admin created: admin / admin123");
            }
            if (!userRepository.existsByUsername("teacher1")) {
                User teacher = new User();
                teacher.setUsername("teacher1");
                teacher.setPassword(passwordEncoder().encode("teacher123"));
                teacher.setRole("ROLE_TEACHER");
                teacher.setFullName("Teacher One");
                teacher.setEmail("teacher@sms.com");
                userRepository.save(teacher);
                System.out.println("✅ Teacher created: teacher1 / teacher123");
            }
            if (!userRepository.existsByUsername("student1")) {
                User student = new User();
                student.setUsername("student1");
                student.setPassword(passwordEncoder().encode("student123"));
                student.setRole("ROLE_STUDENT");
                student.setFullName("Student One");
                student.setEmail("student@sms.com");
                userRepository.save(student);
                System.out.println("✅ Student created: student1 / student123");
            }
        };
    }
}
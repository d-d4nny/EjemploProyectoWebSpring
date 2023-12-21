package com.biblioteca.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración donde se detallan los filtro de seguridad para Spring Security
 * en la que se delega la autenticación y la autorización.
 */
@Configuration
@EnableMethodSecurity
public class SeguridadConfig {

	@Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Configura el codificador de contraseñas utilizado para encriptar y comparar contraseñas.
     * @return Instancia del codificador de contraseñas BCrypt.
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    /**
     * Configura el proveedor de autenticación de spring que utiliza el servicio
     * de detalles de usuario y el codificador de contraseñas.
     * @return El proveedor de autenticación configurado.
     */
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService);
	    authProvider.setPasswordEncoder(passwordEncoder());
	   
	    return authProvider;
	}

    /**
     * Configura el administrador de autenticación de spring para manejar la autenticación.
     * @param authConfig La configuración de autenticación.
     * @return El administrador de autenticación configurado.
     * @throws Exception Si surge algun error durante la autenticación.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	    return authConfig.getAuthenticationManager();
	}

    /**
     * Configura la cadena de filtros de seguridad de spring security para solicitudes HTTP, entre otras configuraciones.
     * @param http La configuración de seguridad HTTP.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si hay un error durante la configuración.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Configura las reglas de autorización para las solicitudes HTTP.
            .authorizeHttpRequests(auth -> 
                auth
                	// Permite el acceso público a ciertos recursos y direcciones de URL que no requieren autenticación.
                    .requestMatchers("/", "/webjars/**", "/css/**", "/script/**", "/auth/**").permitAll()
                    .anyRequest().authenticated()// Exige autenticación para cualquier otra solicitud.
            )
            // Configura el proceso de inicio de sesión y la página de inicio de sesión.
            .formLogin(login ->
                login
                    .loginPage("/auth/login") // Establece la página de inicio de sesión personalizada.
                    .defaultSuccessUrl("/privada/home", true) // Establece la URL de redirección después de un inicio de sesión exitoso.
                    .loginProcessingUrl("/auth/login-post") // Establece la URL de procesamiento del formulario de inicio de sesión.
            )
            // Configura el proceso de cierre de sesión.
            .logout(logout ->
                logout
                    .logoutUrl("/auth/logout") // Establece la URL de cierre de sesión personalizada.
                    .logoutSuccessUrl("/") // Establece la URL de redirección después de un cierre de sesión exitoso.
            );
        
        // Configura un proveedor de autenticación personalizado.
        http.authenticationProvider(authenticationProvider());

        // Construye y devuelve la cadena de filtros de seguridad configurada.
        return http.build();
    }
}

package com.biblioteca.seguridad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.biblioteca.repositorios.UsuarioRepositorio;
import com.biblioteca.dao.Usuario;

/**
 * Para que spring gestione la autentiación y la autorización de la aplicación,
 * es necesario una clase en el proyecto que implemente la interfaz UserDetailsService y
 * sobre escribir el método loadUserByUsername de la misma.
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UsuarioRepositorio usuarioRepository;
	
	/**
	 * Se debe sobrescribir este método de la interface {@link UserDetailsService}
	 * para que spring se encargue de procesar las solicitudes de autenticación del usuario.
	 * Buscando un usuario por su nombre de usuario y después devolviendo
	 * un objeto de tipo UserDetails para que spring pueda completar la autenticación
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

	    System.out.printf("\nIntento de inicio de sesión para el usuario: %s\n", username);

		//El nombre de usuario en la aplicación es el email
		Usuario user = usuarioRepository.findFirstByEmailUsuario(username);
		
		//Construir la instancia de UserDetails con los datos del usuario
		UserBuilder builder = null;
		if (user != null) {
	    	System.out.printf("\nUsuario encontrado en la base de datos: %s\n", user.getEmailUsuario());

			builder = User.withUsername(username);
			builder.disabled(false);
			builder.password(user.getClaveUsuario());
			builder.authorities(new SimpleGrantedAuthority("ROLE_USER"));
		} else {
	    	System.out.println("Usuario no encontrado en la base de datos");
			throw new UsernameNotFoundException("Usuario no encontrado");
		}
		return builder.build();
	}

}

package com.biblioteca.servicios;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.biblioteca.dao.Usuario;
import com.biblioteca.dto.UsuarioDTO;
import com.biblioteca.repositorios.UsuarioRepositorio;

import jakarta.transaction.Transactional;

/**
 * Servicio que implementa los metodos de la interface {@link IUsuarioServicio} 
 * y en esta clase es donde se entra al detalle de la logica de dichos métodos
 * para la gestión de usuarios.
 */
@Service
@Transactional
public class UsuarioServicioImpl implements IUsuarioServicio {

	@Autowired
	private UsuarioRepositorio repositorio;

	@Autowired
	private IUsuarioToDao toDao;
	
	@Autowired
	private IUsuarioToDto toDto;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private IEmailServicio emailServicio;

	
	@Override
	public UsuarioDTO registrar(UsuarioDTO userDto) {

		try {
			// Comprueba si ya existe un usuario con el email que quiere registrar
			Usuario usuarioDaoByEmail = repositorio.findFirstByEmailUsuario(userDto.getEmailUsuario());

			if (usuarioDaoByEmail != null) { 
				return null; // Si no es null es que ya está registrado
			}

			// Ahora se comprueba si hay un usuario por el DNI que quiere registrar
			boolean yaExisteElDNI = repositorio.existsByDniUsuario(userDto.getDniUsuario());

			if (yaExisteElDNI) {
				// Si es que ya hay un usuario con ese dni se setea a null para controlar el error en controlador
				userDto.setDniUsuario(null); 
				return userDto;
			}

			// Si llega a esta línea es que no existe el usuario con el email y el dni a registrar
			userDto.setClaveUsuario(passwordEncoder.encode(userDto.getClaveUsuario()));
			Usuario usuarioDao = toDao.usuarioToDao(userDto);
			usuarioDao.setFchAltaUsuario(Calendar.getInstance());
			repositorio.save(usuarioDao);

			return userDto;
		} catch (IllegalArgumentException iae) {
			System.out.println("[Error UsuarioServicioImpl - registrar() ]" + iae.getMessage());	
		} catch (Exception e) {
			System.out.println("[Error UsuarioServicioImpl - registrar() ]" + e.getMessage());
		}
		return null;
	}
	
	@Override
	public boolean iniciarResetPassConEmail(String emailUsuario) {
		try {
			Usuario usuarioExistente = repositorio.findFirstByEmailUsuario(emailUsuario);
	
			if (usuarioExistente != null) {
				// Generar el token y establece la fecha de expiración
				String token = passwordEncoder.encode(RandomStringUtils.random(30));
				Calendar fechaExpiracion = Calendar.getInstance();
				fechaExpiracion.add(Calendar.MINUTE, 10);
				// Actualizar el usuario con el nuevo token y la fecha de expiración
				usuarioExistente.setToken(token);
				usuarioExistente.setExpiracionToken(fechaExpiracion);

				//Actualizar el usuario en la base de datos
				repositorio.save(usuarioExistente);

				//Enviar el correo de recuperación
				String nombreUsuario = usuarioExistente.getNombreUsuario()+" "+usuarioExistente.getApellidosUsuario();
				emailServicio.enviarEmailRecuperacion(emailUsuario, nombreUsuario, token);
				
				return true;

			} else {
				System.out.println("[Error iniciarRecuperacionConEmail() ]El usuario con email "+ emailUsuario +" no existe");
				return false;		}
		} catch (IllegalArgumentException iae) {
			System.out.println("[Error UsuarioServicioImpl - registrar() ]" + iae.getMessage());
			return false;
		}  catch (Exception e) {	
			System.out.println("[Error UsuarioServicioImpl - iniciarRecuperacionConEmail()]"+ e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean modificarContraseñaConToken(UsuarioDTO usuario) {
		
		Usuario usuarioExistente = repositorio.findByToken(usuario.getToken());
		
		if(usuarioExistente != null) {
			String nuevaContraseña = passwordEncoder.encode(usuario.getPassword());
			usuarioExistente.setClaveUsuario(nuevaContraseña);
			usuarioExistente.setToken(null); //Se setea a null para invalidar el token ya consumido al cambiar de password
			repositorio.save(usuarioExistente);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public UsuarioDTO obtenerUsuarioPorToken(String token) {
		Usuario usuarioExistente = repositorio.findByToken(token);
		
		if(usuarioExistente != null) {
			UsuarioDTO usuario = toDto.usuarioToDto(usuarioExistente);
			return usuario;
		} else {
			System.out.println("No existe el usuario con el token "+token);
			return null;
		}		
		
	}
	
	@Override
	public Usuario buscarPorEmail(String email) {
		return repositorio.findFirstByEmailUsuario(email);
	}

	
	
	//ESTOS METODO NO SE USAN DE MOMENTO
	@Override
	public boolean buscarPorDni(String dni) {
		return repositorio.existsByDniUsuario(dni);
	}
	
	@Override
	public Usuario buscarPorId(long id) {
		return repositorio.findById(id).orElse(null);
	}

	@Override
	public List<UsuarioDTO> buscarTodos() {	
		return toDto.listaUsuarioToDto(repositorio.findAll());
	}
}

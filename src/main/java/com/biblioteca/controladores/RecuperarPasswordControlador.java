package com.biblioteca.controladores;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.dto.UsuarioDTO;
import com.biblioteca.servicios.IUsuarioServicio;

/**
 * Clase que ejerce de controlador de la vista de iniciarRecuperacion/recuperar 
 * y que gestiona las solicitudes relacionadas con dichas vistas.
 */
@Controller
@RequestMapping("/auth")
public class RecuperarPasswordControlador {

	@Autowired
	private IUsuarioServicio usuarioServicio;
	

	/**
	 * Gestiona la solicitud HTTP GET para la url /auth/iniciarRecuperacion 
	 * y muestra la vista de inicio de recuperación.
	 * @param model El modelo en el que se añade como atributo un objeto usuarioDTO vacío para enviarlo a la vista.
	 * @return La vista de inicioRecuperación.html
	 */
	@GetMapping("/iniciarRecuperacion")
	public String mostrarVistainiciarRecuperacion(Model model) {
		model.addAttribute("usuarioDTO", new UsuarioDTO());
		return "iniciarRecuperacion";
	}

	/**
	 * Procesa la solicitud HTTP POST para la url /auth/iniciarRecuperacion 
	 * Se utiliza el email del usuario para intentar iniciar el proceso de recuperación.
	 * @param usuarioDTO El objeto UsuarioDTO que recibe del modelo y contiene el email del usuario.
	 * @param model El modelo que se utiliza para enviar mensajes a la vista.
	 * @return La vista de login.html si el envío del email fue exitoso; 
	 * 		   en caso contrario, la vista de inicio de recuperación.html
	 */
	@PostMapping("/iniciarRecuperacion")
	public String procesarInicioRecuperacion(@ModelAttribute UsuarioDTO usuarioDTO, Model model) {
		
		boolean envioConExito = usuarioServicio.iniciarResetPassConEmail(usuarioDTO.getEmailUsuario());
		
		if(envioConExito) {
	        model.addAttribute("mensajeExitoMail", "Proceso de recuperacion OK");
	        return "login";
		} else {
	        model.addAttribute("mensajeErrorMail", "Error en el proceso de recuperacion.");
		}
		return "iniciarRecuperacion";
	}
	
	/**
	 * Gestiona la solicitud HTTP GET para la url /auth/recuperar.
	 * Muestra la vista de recuperación de contraseña y enviando a esta en el modelo el dto con el token
	 * asociado al usuario para recuperar la contraseña, o en caso de no encontrarlo, mostrar un mensaje de error.
	 * @param token El token necesario para recuperar la contraseña obtenido de la url de la solicitud.
	 * @param model El modelo que se utiliza para enviar mensajes y datos en el modelo a la vista.
	 * @return La vista de recuperación de contraseña (recuperar.html) si el token es válido;
	 * 		   de lo contrario, la vista de inicioRecuperacion.html
	 */
	@GetMapping("/recuperar")
	public String mostrarVistaRecuperar(@RequestParam(name = "token") String token, Model model) {
		UsuarioDTO usuario = usuarioServicio.obtenerUsuarioPorToken(token);
		if(usuario != null) {
			model.addAttribute("usuarioDTO", usuario);
		} else {
	        model.addAttribute("usuarioDTO", new UsuarioDTO()); 
	        model.addAttribute("mensajeErrorTokenValidez", "Token no válido o usuario no encontrado");
	        return "iniciarRecuperacion";
		}
        return "recuperar";
	}
	
	/**
	 * Procesa la solicitud HTTP POST para la url /auth/recuperar.
	 * Se utiliza el token del usuario para comprobar la validez e intentar recuperar la contraseña.
	 * @param usuarioDTO El objeto UsuarioDTO que recibe del modelo y contiene los nuevos datos de la contraseña.
	 * @param model El modelo que se utiliza para enviar mensajes a la vista.
	 * @return La vista de login.html si la modificación fue exitosa; de lo contrario, la vista de iniciarRecuperación.html
	 */
	@PostMapping("/recuperar")
	public String procesarRecuperacionContraseña(@ModelAttribute UsuarioDTO usuarioDTO, Model model) {
		
	    UsuarioDTO usuarioExistente = usuarioServicio.obtenerUsuarioPorToken(usuarioDTO.getToken());
	    
	    if (usuarioExistente == null) {
	    	model.addAttribute("mensajeErrorTokenValidez", "Token no válido");
	        return "iniciarRecuperacion";
	    }
	    if (usuarioExistente.getExpiracionToken().before(Calendar.getInstance())) {
	        model.addAttribute("mensajeErrorTokenExpirado", "El token ha expirado");
	        return "iniciarRecuperacion";
	    }
	    
		boolean modificadaPassword = usuarioServicio.modificarContraseñaConToken(usuarioDTO);
		
		if(modificadaPassword) {
			model.addAttribute("contraseñaModificadaExito", "Contraseña modificada OK");
	        return "login";
		} else {
			model.addAttribute("contraseñaModificadaError", "Error al cambiar de contraseña");
			return "iniciarRecuperacion";
		}	
	}




}

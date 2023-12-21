package com.biblioteca.servicios;

import java.util.List;

import com.biblioteca.dao.Usuario;
import com.biblioteca.dto.UsuarioDTO;

/**
 * Interface donde se declaran los metodos que son necesarios para el paso de una entidad
 * usuario (DAO) a usuarioDTO
 */
public interface IUsuarioToDto {
	
	/**
	 * Método que convierte campo a campo un objeto entidad Usuario a usuarioDTO
	 * @param u El usuario a transformar
	 * @return El DTO del usuario
	 */
	public UsuarioDTO usuarioToDto(Usuario u);
	
	/**
	 * Metodo que convierte todos los objetos entidad usuario DAO a una lista UsuarioDTO 
	 * @param listaUsuario
	 * @return
	 */
	public List<UsuarioDTO> listaUsuarioToDto(List<Usuario> listaUsuario);
}

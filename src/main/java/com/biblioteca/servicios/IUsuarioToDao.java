package com.biblioteca.servicios;

import java.util.List;

import com.biblioteca.dao.Usuario;
import com.biblioteca.dto.UsuarioDTO;

/**
 * Interface donde se declaran los metodos necesarios para el paso de un usuarioDTO a DAO
 */
public interface IUsuarioToDao {
	
	/**
	 * Metodo que convierte campo a campo un objeto UsuarioDTO a DAO
	 * @param ausuarioDTO el objeto usuarioDTO
	 * @return Usuario convertido a DAO
	 */
	public Usuario usuarioToDao(UsuarioDTO usuarioDTO);
	
	/**
	 * Metodo que convierte toda una lista de objetos UsuarioDTO a lista de DAOs
	 * @param listaUsuarioDTO lista cargadas de objetos usuarioDTO
	 * @return Lista de usuarios DAO
	 */
	public List<Usuario> listUsuarioToDao(List<UsuarioDTO>listaUsuarioDTO);
	


}

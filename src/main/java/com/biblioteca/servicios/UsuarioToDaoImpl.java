package com.biblioteca.servicios;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.biblioteca.dao.Usuario;
import com.biblioteca.dto.UsuarioDTO;

/**
 * Servicio que implementa los metodos de la interface {@link IUsuarioToDao} 
 * y en esta clase es donde se entra al detalle de la logica de dichos métodos
 * para el paso de usuarioDTO a DAO
 */
@Service
public class UsuarioToDaoImpl implements IUsuarioToDao {

	@Override
	public Usuario usuarioToDao(UsuarioDTO usuarioDTO) {

		Usuario usuarioDao = new Usuario();

		try {
			usuarioDao.setNombreUsuario(usuarioDTO.getNombreUsuario());
			usuarioDao.setApellidosUsuario(usuarioDTO.getApellidosUsuario());
			usuarioDao.setEmailUsuario(usuarioDTO.getEmailUsuario());
			usuarioDao.setClaveUsuario(usuarioDTO.getClaveUsuario());
			usuarioDao.setTlfUsuario(usuarioDTO.getTlfUsuario());
			usuarioDao.setDniUsuario(usuarioDTO.getDniUsuario());
			
			return usuarioDao;

		} catch (Exception e) {
			System.out.println(
					"\n[ERROR UsuarioToDaoImpl - toUsuarioDao()] - Al convertir usuarioDTO a usuarioDAO (return null): "
							+ e);
			return null;
		}

	}

	@Override
	public List<Usuario> listUsuarioToDao(List<UsuarioDTO> listaUsuarioDTO) {

		List<Usuario> listaUsuarioDao = new ArrayList<>();

		try {
			for (UsuarioDTO usuarioDTO : listaUsuarioDTO) {
				listaUsuarioDao.add(usuarioToDao(usuarioDTO));
			}

			return listaUsuarioDao;

		} catch (Exception e) {
			System.out.println(
					"\n[ERROR UsuarioToDaoImpl - toListUsuarioDao()] - Al convertir lista de usuarioDTO a lista de usuarioDAO (return null): "
							+ e);
		}
		return null;
	}


}

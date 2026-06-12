package com.mesalista.service;

import com.mesalista.dto.UsuarioRegistroDTO;
import com.mesalista.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsuarioService extends UserDetailsService {
    Usuario registrarUsuario(UsuarioRegistroDTO registroDTO);
    Usuario buscarPorEmail(String email);
    java.util.List<Usuario> listarTodos();
    void toggleEstadoUsuario(Long id);
}

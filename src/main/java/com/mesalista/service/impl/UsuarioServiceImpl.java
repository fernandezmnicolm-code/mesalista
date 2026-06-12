package com.mesalista.service.impl;

import com.mesalista.dto.UsuarioRegistroDTO;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.Rol;
import com.mesalista.repository.UsuarioRepository;
import com.mesalista.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrarUsuario(UsuarioRegistroDTO registroDTO) {
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        if (usuarioRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        // Evitar que se registren como administradores desde la web
        if (registroDTO.getRol() == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException("Rol no permitido para registro público");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRol(registroDTO.getRol());
        usuario.setEstado(true);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public void toggleEstadoUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        // No se puede suspender a un administrador (para no bloquearnos)
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException("No se puede suspender a un administrador");
        }
        usuario.setEstado(!usuario.getEstado());
        usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String emailToSearch = username != null ? username.trim() : "";
        System.out.println("====== LOGIN INTENT ======");
        System.out.println("Buscando usuario por email: [" + emailToSearch + "]");
        
        Usuario usuario = usuarioRepository.findByEmail(emailToSearch)
                .orElseThrow(() -> {
                    System.out.println("-> FALLO: Usuario no encontrado en la BD con email: [" + emailToSearch + "]");
                    return new UsernameNotFoundException("Usuario o password inválidos");
                });

        System.out.println("-> ÉXITO: Usuario encontrado. ID: " + usuario.getId() + ", Email: " + usuario.getEmail());
        System.out.println("-> ROL: " + usuario.getRol());
        System.out.println("-> ESTADO (Activo?): " + usuario.getEstado());

        if (!usuario.getEstado()) {
            System.out.println("-> FALLO: La cuenta está desactivada.");
            throw new UsernameNotFoundException("La cuenta está desactivada");
        }

        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}

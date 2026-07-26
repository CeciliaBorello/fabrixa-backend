package com.fabrixa.backend.usuarios;

import com.fabrixa.backend.usuarios.model.Permiso;
import com.fabrixa.backend.usuarios.model.Rol;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.PermisoRepository;
import com.fabrixa.backend.usuarios.repository.RolRepository;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RolRepository rolRepository,
                      PermisoRepository permisoRepository,
                      UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return; // ya hay datos, no volver a sembrar
        }

        // Permisos base (algunos de ejemplo, se van sumando a medida que armamos módulos)
        List<Permiso> permisos = List.of(
                crearPermiso("usuarios.administrar"),
                crearPermiso("comercial.ver"),
                crearPermiso("comercial.editar"),
                crearPermiso("facturacion.crear"),
                crearPermiso("facturacion.ver"),
                crearPermiso("stock.ver"),
                crearPermiso("contabilidad.ver"),
                crearPermiso("contabilidad.editar")
        );

        // Rol Administrador: todos los permisos
        Rol admin = new Rol();
        admin.setNombre("ADMINISTRADOR");
        admin.setPermisos(Set.copyOf(permisos));
        rolRepository.save(admin);

        // Rol Oficina: sin administrar usuarios ni tocar contabilidad
        Rol oficina = new Rol();
        oficina.setNombre("OFICINA");
        oficina.setPermisos(Set.of(permisos.get(1), permisos.get(2), permisos.get(3), permisos.get(4), permisos.get(5)));
        rolRepository.save(oficina);

        // Rol Fábrica: solo lectura de stock
        Rol fabrica = new Rol();
        fabrica.setNombre("FABRICA");
        fabrica.setPermisos(Set.of(permisos.get(5)));
        rolRepository.save(fabrica);

        // Rol Cliente (portal): sin permisos internos, se maneja con endpoints propios
        Rol cliente = new Rol();
        cliente.setNombre("CLIENTE");
        rolRepository.save(cliente);

        // Usuario admin de prueba
        Usuario admin1 = new Usuario();
        admin1.setNombre("Administrador");
        admin1.setEmail("admin@fabrixa.com");
        admin1.setPasswordHash(passwordEncoder.encode("admin123"));
        admin1.setRol(admin);
        admin1.setActivo(true);
        usuarioRepository.save(admin1);

        System.out.println(">>> Datos iniciales creados. Login de prueba: admin@fabrixa.com / admin123");
    }

    private Permiso crearPermiso(String nombre) {
        Permiso p = new Permiso();
        p.setNombre(nombre);
        return permisoRepository.save(p);
    }
}
package com.fabrixa.backend.pedidos.service;

import com.fabrixa.backend.pedidos.dto.PedidoDTO.ItemRequest;
import com.fabrixa.backend.pedidos.dto.PedidoDTO.Request;
import com.fabrixa.backend.pedidos.dto.PedidoDTO.Response;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.model.TipoClienteProveedor;
import com.fabrixa.backend.comercial.repository.ClienteProveedorRepository;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import com.fabrixa.backend.pedidos.model.EstadoPedido;
import com.fabrixa.backend.pedidos.model.Pedido;
import com.fabrixa.backend.pedidos.repository.PedidoRepository;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test unitario de PedidoService: alta (validaciones) y máquina de estados.
 * No levanta Spring ni toca la base: los repositorios son mocks.
 * Correr con:  .\mvnw.cmd test -Dtest=PedidoServiceTest
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    private static final String EMAIL = "test@fabrixa.com";

    @Mock PedidoRepository pedidoRepository;
    @Mock ClienteProveedorRepository clienteRepository;
    @Mock ProductoRepository productoRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock Authentication auth;

    @InjectMocks PedidoService pedidoService;

    // ------------------------------------------------------------------ helpers

    private ClienteProveedor cliente(long id, TipoClienteProveedor tipo, boolean activo) {
        ClienteProveedor c = new ClienteProveedor();
        c.setId(id);
        c.setRazonSocial("Distribuidora Norte");
        c.setTipo(tipo);
        c.setActivo(activo);
        return c;
    }

    private Producto producto(long id, String nombre, boolean activo) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setActivo(activo);
        return p;
    }

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Usuario Test");
        u.setEmail(EMAIL);
        return u;
    }

    private Pedido pedidoEnEstado(long id, EstadoPedido estado) {
        Pedido p = new Pedido();
        p.setId(id);
        p.setCliente(cliente(10L, TipoClienteProveedor.CLIENTE, true));
        p.setUsuario(usuario());
        p.setEstado(estado);
        return p;
    }

    /** Stubs comunes para llegar a la validación de ítems. */
    private void prepararClienteYUsuario(ClienteProveedor c) {
        when(clienteRepository.findById(c.getId())).thenReturn(Optional.of(c));
        when(auth.getName()).thenReturn(EMAIL);
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario()));
    }

    private void saveDevuelveLoMismo() {
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void assertError(Request request, String mensaje) {
        assertThatThrownBy(() -> pedidoService.crear(request, auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(mensaje);
        verify(pedidoRepository, never()).save(any());
    }

    // ------------------------------------------------------------------ A. Alta

    @Nested
    @DisplayName("A. Alta de pedido")
    class Alta {

        @Test
        @DisplayName("Sin cliente → error")
        void sinCliente() {
            assertError(new Request(null, List.of(new ItemRequest(1L, new BigDecimal("5")))), "Seleccioná un cliente");
        }

        @Test
        @DisplayName("Cliente inexistente → error")
        void clienteInexistente() {
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
            assertError(new Request(99L, List.of(new ItemRequest(1L, new BigDecimal("5")))), "Cliente no encontrado");
        }

        @Test
        @DisplayName("Cliente inactivo → error")
        void clienteInactivo() {
            ClienteProveedor c = cliente(10L, TipoClienteProveedor.CLIENTE, false);
            when(clienteRepository.findById(10L)).thenReturn(Optional.of(c));
            assertError(new Request(10L, List.of(new ItemRequest(1L, new BigDecimal("5")))),
                    "El cliente Distribuidora Norte está inactivo");
        }

        @Test
        @DisplayName("Cliente cargado solo como PROVEEDOR → error")
        void soloProveedor() {
            ClienteProveedor c = cliente(10L, TipoClienteProveedor.PROVEEDOR, true);
            when(clienteRepository.findById(10L)).thenReturn(Optional.of(c));
            assertError(new Request(10L, List.of(new ItemRequest(1L, new BigDecimal("5")))),
                    "Distribuidora Norte está cargado solo como proveedor");
        }

        @ParameterizedTest(name = "items = {0}")
        @NullAndEmptySource
        @DisplayName("Pedido sin ítems → error")
        void sinItems(List<ItemRequest> items) {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            assertError(new Request(10L, items), "El pedido necesita al menos un ítem");
        }

        @Test
        @DisplayName("Ítem sin producto → error")
        void itemSinProducto() {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            assertError(new Request(10L, List.of(new ItemRequest(null, new BigDecimal("5")))), "Hay un ítem sin producto");
        }

        @ParameterizedTest(name = "cantidad = {0}")
        @NullSource
        @ValueSource(strings = {"0", "-3", "-0.5"})
        @DisplayName("Cantidad nula, cero o negativa → error")
        void cantidadInvalida(BigDecimal cantidad) {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            assertError(new Request(10L, List.of(new ItemRequest(1L, cantidad))),
                    "La cantidad de cada ítem debe ser mayor a cero");
        }

        @ParameterizedTest(name = "cantidad = {0}")
        @ValueSource(strings = {"1.2345", "0.0001"})
        @DisplayName("Cantidad con más de 3 decimales → error")
        void cantidadConDemasiadosDecimales(BigDecimal cantidad) {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            assertError(new Request(10L, List.of(new ItemRequest(1L, cantidad))),
                    "La cantidad admite hasta 3 decimales");
        }

        @ParameterizedTest(name = "cantidad = {0}")
        @ValueSource(strings = {"2.5", "0.125", "1.500", "0.001"})
        @DisplayName("Cantidad decimal de hasta 3 decimales → se acepta tal cual")
        void cantidadDecimalValida(BigDecimal cantidad) {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1L, "Queso cremoso", true)));
            saveDevuelveLoMismo();

            Response r = pedidoService.crear(new Request(10L, List.of(new ItemRequest(1L, cantidad))), auth);

            assertThat(r.items()).singleElement()
                    .satisfies(i -> assertThat(i.cantidad()).isEqualByComparingTo(cantidad));
        }

        @Test
        @DisplayName("Producto inexistente → error")
        void productoInexistente() {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            when(productoRepository.findById(99L)).thenReturn(Optional.empty());
            assertError(new Request(10L, List.of(new ItemRequest(99L, new BigDecimal("5")))), "Producto no encontrado: 99");
        }

        @Test
        @DisplayName("Producto inactivo → error")
        void productoInactivo() {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1L, "Dulce de leche 1kg", false)));
            assertError(new Request(10L, List.of(new ItemRequest(1L, new BigDecimal("5")))),
                    "El producto Dulce de leche 1kg está inactivo");
        }

        @Test
        @DisplayName("Si el segundo ítem es inválido, no se guarda nada")
        void segundoItemInvalidoNoGuarda() {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1L, "Dulce de leche 1kg", true)));
            assertError(new Request(10L, List.of(new ItemRequest(1L, new BigDecimal("5")), new ItemRequest(2L, new BigDecimal("0")))),
                    "La cantidad de cada ítem debe ser mayor a cero");
        }

        @Test
        @DisplayName("Alta válida → estado NUEVO con todos los ítems")
        void altaValida() {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.CLIENTE, true));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1L, "Dulce de leche 1kg", true)));
            when(productoRepository.findById(2L)).thenReturn(Optional.of(producto(2L, "Dulce de leche 400g", true)));
            saveDevuelveLoMismo();

            Response r = pedidoService.crear(
                    new Request(10L, List.of(new ItemRequest(1L, new BigDecimal("5")), new ItemRequest(2L, new BigDecimal("12")))), auth);

            assertThat(r.estado()).isEqualTo(EstadoPedido.NUEVO);
            assertThat(r.clienteNombre()).isEqualTo("Distribuidora Norte");
            assertThat(r.usuarioNombre()).isEqualTo("Usuario Test");
            assertThat(r.fechaPedido()).isNotNull();
            assertThat(r.fechaEntrega()).isNull();
            assertThat(r.items()).hasSize(2);
            assertThat(r.items()).extracting(i -> i.cantidad()).containsExactly(new BigDecimal("5"), new BigDecimal("12"));

            // Los ítems quedan vinculados al pedido (necesario para el cascade)
            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
            verify(pedidoRepository).save(captor.capture());
            Pedido guardado = captor.getValue();
            assertThat(guardado.getItems()).allSatisfy(i -> assertThat(i.getPedido()).isSameAs(guardado));
            // Nota: PedidoService no tiene dependencia de stock, así que el alta no puede descontarlo.
        }

        @Test
        @DisplayName("Cliente tipo AMBOS también puede hacer pedidos")
        void clienteAmbos() {
            prepararClienteYUsuario(cliente(10L, TipoClienteProveedor.AMBOS, true));
            when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1L, "Dulce de leche 1kg", true)));
            saveDevuelveLoMismo();

            Response r = pedidoService.crear(new Request(10L, List.of(new ItemRequest(1L, new BigDecimal("1")))), auth);

            assertThat(r.estado()).isEqualTo(EstadoPedido.NUEVO);
        }
    }

    // ------------------------------------------------------------------ B. Transiciones

    @Nested
    @DisplayName("B. Transiciones de estado")
    class Transiciones {

        private Response ejecutar(EstadoPedido destino, Long id) {
            return switch (destino) {
                case PENDIENTE_ENTREGA -> pedidoService.marcarPendienteEntrega(id);
                case ENTREGADO -> pedidoService.marcarEntregado(id);
                case CANCELADO -> pedidoService.cancelar(id);
                case NUEVO -> throw new IllegalStateException("No hay método para volver a NUEVO");
            };
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "NUEVO, PENDIENTE_ENTREGA",
                "PENDIENTE_ENTREGA, ENTREGADO",
                "NUEVO, CANCELADO",
                "PENDIENTE_ENTREGA, CANCELADO"
        })
        @DisplayName("Transiciones válidas")
        void validas(EstadoPedido actual, EstadoPedido destino) {
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEnEstado(1L, actual)));
            saveDevuelveLoMismo();

            Response r = ejecutar(destino, 1L);

            assertThat(r.estado()).isEqualTo(destino);
            if (destino == EstadoPedido.ENTREGADO) {
                assertThat(r.fechaEntrega()).isNotNull();
            } else {
                assertThat(r.fechaEntrega()).isNull();
            }
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "NUEVO, ENTREGADO",
                "PENDIENTE_ENTREGA, PENDIENTE_ENTREGA",
                "ENTREGADO, PENDIENTE_ENTREGA",
                "ENTREGADO, ENTREGADO",
                "ENTREGADO, CANCELADO",
                "CANCELADO, PENDIENTE_ENTREGA",
                "CANCELADO, ENTREGADO",
                "CANCELADO, CANCELADO"
        })
        @DisplayName("Transiciones inválidas → error y el estado no cambia")
        void invalidas(EstadoPedido actual, EstadoPedido destino) {
            Pedido pedido = pedidoEnEstado(1L, actual);
            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

            assertThatThrownBy(() -> ejecutar(destino, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("No se puede pasar de " + actual + " a " + destino);

            assertThat(pedido.getEstado()).isEqualTo(actual);
            verify(pedidoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Pedido inexistente → error")
        void pedidoInexistente() {
            when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pedidoService.cancelar(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pedido no encontrado");
        }
    }
}

package ni.edu.uam.papeleria.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ni.edu.uam.papeleria.dao.ProductoDAO;
import ni.edu.uam.papeleria.model.Producto;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ProductoController implements Initializable {

    // Constantes para validaciones de categoría y rango de precios
    private static final List<String> CATEGORIAS_PERMITIDAS = List.of("Papelería", "Tecnología", "Accesorios");
    private static final Set<String> CONJUNTO_CATEGORIAS = Set.copyOf(CATEGORIAS_PERMITIDAS);
    private static final BigDecimal PRECIO_MINIMO = new BigDecimal("0.01");
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    // Componentes del formulario
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;

    // Botones del CRUD
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    // Componentes de búsqueda y filtros
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroCategoria;
    @FXML private CheckBox chkSoloExistencias;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiarFiltros;
    @FXML private Button btnRecargar;
    @FXML private Label lblContador;

    // TableView y sus 6 columnas
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    // Variables de control interno
    private ProductoDAO productoDAO;
    private ObservableList<Producto> listaProductos;
    private Integer idProductoSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productoDAO = new ProductoDAO();
        listaProductos = FXCollections.observableArrayList();

        // Cargar las opciones de categoría en el formulario y en el filtro
        cmbCategoria.setItems(FXCollections.observableArrayList(CATEGORIAS_PERMITIDAS));
        cmbFiltroCategoria.setItems(FXCollections.observableArrayList("Todas", "Papelería", "Tecnología", "Accesorios"));
        cmbFiltroCategoria.setValue("Todas");

        configurarTableView();
        configurarSeleccionTableView();
        cargarProductos();
    }


    private void configurarTableView() {
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdProducto()).asObject());
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria()));
        colPrecio.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrecio()));
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        tblProductos.setItems(listaProductos);
    }

    /**
     * Detecta la selección de una fila en la tabla para cargar sus datos en el formulario.
     */
    private void configurarSeleccionTableView() {
        tblProductos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarProductoEnFormulario(newValue);
            }
        });
    }

    /**
     * Llena los controles del formulario con los datos del producto seleccionado y guarda su ID real.
     */
    private void cargarProductoEnFormulario(Producto p) {
        idProductoSeleccionado = p.getIdProducto();
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        cmbCategoria.setValue(p.getCategoria());
        txtPrecio.setText(p.getPrecio().toPlainString());
        txtStock.setText(String.valueOf(p.getStock()));
    }


    private void cargarProductos() {
        try {
            List<Producto> productos = productoDAO.obtenerTodos();
            listaProductos.setAll(productos);
            actualizarContador();
        } catch (SQLException e) {
            listaProductos.clear();
            actualizarContador();
            manejarExcepcionSQL("cargar los productos iniciales", e);
        }
    }

    /**
     * Limpia únicamente los campos del formulario y la selección de la tabla, sin tocar los filtros.
     */
    @FXML
    public void nuevoProducto(ActionEvent event) {
        limpiarFormulario();
    }

    /**
     * Valida el formulario y registra un nuevo producto en PostgreSQL.
     */
    @FXML
    public void guardarProducto(ActionEvent event) {
        Producto nuevo = validarYConstruirProducto(null);
        if (nuevo == null) {
            return;
        }

        try {
            int filas = productoDAO.guardar(nuevo);
            if (filas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El producto '" + nuevo.getCodigo() + "' fue guardado correctamente.");
                limpiarFormulario();
                consultarConFiltrosVigentes(false);
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                        "No se pudo guardar el producto en la base de datos.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("guardar el producto '" + nuevo.getCodigo() + "'", e);
        }
    }

    /**
     * Valida el formulario y actualiza el producto seleccionado por su ID en PostgreSQL.
     */
    @FXML
    public void actualizarProducto(ActionEvent event) {
        // V10: No permitir actualizar sin seleccionar un producto del TableView
        if (idProductoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Debe seleccionar un producto de la tabla antes de actualizar.");
            return;
        }

        Producto modificado = validarYConstruirProducto(idProductoSeleccionado);
        if (modificado == null) {
            return;
        }
        modificado.setIdProducto(idProductoSeleccionado);

        try {
            int filas = productoDAO.actualizarPorId(modificado);
            // E3: Comprobar cuando executeUpdate() devuelve 0 filas afectadas
            if (filas == 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Registro no encontrado",
                        "El producto seleccionado ya no existe en la base de datos (0 filas afectadas). " +
                                "Se recargará el listado.");
                limpiarFormulario();
                consultarConFiltrosVigentes(false);
                return;
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "El producto '" + modificado.getCodigo() + "' fue actualizado correctamente.");
            limpiarFormulario();
            consultarConFiltrosVigentes(false);
        } catch (SQLException e) {
            manejarExcepcionSQL("actualizar el producto con ID " + idProductoSeleccionado, e);
        }
    }

    /**
     * Solicita confirmación y elimina físicamente (DELETE) el producto seleccionado por su ID.
     */
    @FXML
    public void eliminarProducto(ActionEvent event) {
        // V10: No permitir eliminar sin seleccionar un producto del TableView
        if (idProductoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida",
                    "Debe seleccionar un producto de la tabla antes de eliminar.");
            return;
        }

        String codigoActual = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar el producto " + codigoActual +
                " (ID: " + idProductoSeleccionado + ")?");

        ButtonType btnConfirmar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnConfirmar, btnCancelar);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == btnConfirmar) {
                try {
                    int filas = productoDAO.eliminarPorId(idProductoSeleccionado);
                    // E3: Comprobar cuando executeUpdate() devuelve 0 filas afectadas
                    if (filas == 0) {
                        mostrarAlerta(Alert.AlertType.WARNING, "Registro no encontrado",
                                "El producto ya no existía en la base de datos (0 filas afectadas). " +
                                        "Se recargará el listado.");
                    } else {
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                                "El producto fue eliminado correctamente.");
                    }
                    limpiarFormulario();
                    consultarConFiltrosVigentes(false);
                } catch (SQLException e) {
                    manejarExcepcionSQL("eliminar el producto con ID " + idProductoSeleccionado, e);
                }
            }
        });
    }

    /**
     * Ejecuta la búsqueda en PostgreSQL aplicando los filtros activos.
     */
    @FXML
    public void buscarProductos(ActionEvent event) {
        consultarConFiltrosVigentes(true);
    }

    /**
     * Restaura los controles de filtro a su estado inicial y recarga los 12 productos desde PostgreSQL.
     */
    @FXML
    public void limpiarFiltros(ActionEvent event) {
        txtBuscar.clear();
        cmbFiltroCategoria.setValue("Todas");
        chkSoloExistencias.setSelected(false);
        cargarProductos();
    }

    /**
     * Vuelve a consultar PostgreSQL manteniendo los filtros vigentes en la interfaz.
     */
    @FXML
    public void recargarProductos(ActionEvent event) {
        consultarConFiltrosVigentes(false);
    }

    // ==========================================
    // MÉTODOS AUXILIARES Y VALIDACIONES V1 - V9
    // ==========================================

    /**
     * Ejecuta las validaciones V1 a V9 y construye el objeto Producto.
     * Retorna null si algún dato es inválido.
     */
    private Producto validarYConstruirProducto(Integer idEnEdicion) {
        // V1. Código: aplicar trim y convertir a mayúsculas antes de validar; rechazar vacío y > 10 caracteres
        String codigoOriginal = txtCodigo.getText() != null ? txtCodigo.getText() : "";
        String codigo = codigoOriginal.trim().toUpperCase();
        txtCodigo.setText(codigo);

        if (codigo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código inválido",
                    "El código es obligatorio y no puede estar vacío ni contener solo espacios.");
            return null;
        }
        if (codigo.length() > 10) {
            mostrarAlerta(Alert.AlertType.WARNING, "Código inválido",
                    "El código no puede tener más de 10 caracteres.");
            return null;
        }

        // V2. Nombre: aplicar trim; rechazar solo espacios, menos de 3 caracteres y más de 80 caracteres
        String nombreOriginal = txtNombre.getText() != null ? txtNombre.getText() : "";
        String nombre = nombreOriginal.trim();
        txtNombre.setText(nombre);

        if (nombre.length() < 3 || nombre.length() > 80) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nombre inválido",
                    "El nombre debe tener entre 3 y 80 caracteres después de quitar espacios laterales.");
            return null;
        }

        // V3. Categoría: obligatoria y únicamente Papelería, Tecnología o Accesorios
        String categoria = cmbCategoria.getValue();
        if (categoria == null || !CONJUNTO_CATEGORIAS.contains(categoria)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Categoría inválida",
                    "Debe seleccionar una categoría válida: Papelería, Tecnología o Accesorios.");
            return null;
        }

        // V4, V5 y V6. Precio: numérico, > 0, máximo 2 decimales sin redondear, rango 0.01 a 99999999.99
        String textoPrecio = txtPrecio.getText() != null ? txtPrecio.getText().trim() : "";
        if (textoPrecio.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido",
                    "El precio es obligatorio.");
            return null;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(textoPrecio);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio no numérico",
                    "El precio debe ser un valor numérico válido (por ejemplo: 65.00).");
            return null;
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido",
                    "El precio debe ser mayor que cero.");
            return null;
        }

        if (precio.scale() > 2) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precisión inválida",
                    "El precio admite como máximo dos cifras decimales (no se redondea automáticamente).");
            return null;
        }

        if (precio.compareTo(PRECIO_MINIMO) < 0 || precio.compareTo(PRECIO_MAXIMO) > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio fuera de rango",
                    "El precio debe estar entre 0.01 y 99999999.99.");
            return null;
        }

        // V7 y V8. Stock: número entero exacto, >= 0 y dentro del límite de Integer (hasta 2147483647)
        String textoStock = txtStock.getText() != null ? txtStock.getText().trim() : "";
        if (textoStock.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock inválido",
                    "El stock es obligatorio.");
            return null;
        }

        if (!textoStock.matches("^-?\\d+$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock no entero",
                    "El stock debe ser un número entero exacto sin decimales ni letras.");
            return null;
        }

        int stock;
        try {
            long stockLong = Long.parseLong(textoStock);
            if (stockLong < 0 || stockLong > Integer.MAX_VALUE) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock fuera de rango",
                        "El stock no puede ser negativo ni superar el límite permitido (0 a 2147483647).");
                return null;
            }
            stock = (int) stockLong;
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock fuera de rango",
                    "El valor ingresado para el stock está fuera del rango entero permitido.");
            return null;
        }

        // V9. Código único en la base de datos (permite conservar el propio código al actualizar)
        try {
            if (productoDAO.existeCodigoDuplicado(codigo, idEnEdicion)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Código duplicado",
                        "Ya existe otro producto registrado con el código '" + codigo + "'.");
                return null;
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("verificar duplicidad del código '" + codigo + "'", e);
            return null;
        }

        return new Producto(codigo, nombre, categoria, precio, stock);
    }

    /**
     * Consulta la base de datos aplicando los filtros activos y actualiza el TableView y el contador.
     */
    private void consultarConFiltrosVigentes(boolean avisarSiVacio) {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim() : "";
        String categoria = cmbFiltroCategoria.getValue();
        boolean soloExistencias = chkSoloExistencias.isSelected();

        try {
            List<Producto> resultados = productoDAO.buscarConFiltros(texto, categoria, soloExistencias);
            listaProductos.setAll(resultados);
            actualizarContador();

            if (avisarSiVacio && resultados.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sin coincidencias",
                        "Sin coincidencias: no se encontraron productos con los filtros especificados.");
            }
        } catch (SQLException e) {
            manejarExcepcionSQL("buscar productos con filtros", e);
        }
    }

    /**
     * Limpia las cajas de texto del formulario y deselecciona la fila activa de la tabla.
     */
    private void limpiarFormulario() {
        idProductoSeleccionado = null;
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtPrecio.clear();
        txtStock.clear();
        tblProductos.getSelectionModel().clearSelection();
    }

    /**
     * Actualiza la etiqueta del contador visible con la cantidad de productos en la tabla.
     */
    private void actualizarContador() {
        lblContador.setText("Total de productos: " + listaProductos.size());
    }

    /**
     * Registra en consola la operación fallida junto con su SQLState y muestra una alerta clara al usuario.
     */
    private void manejarExcepcionSQL(String operacion, SQLException e) {
        String sqlState = e.getSQLState() != null ? e.getSQLState() : "DESCONOCIDO";
        System.err.printf("[DIAGNÓSTICO SQL] Operación fallida: %s | SQLState: %s | Detalle: %s%n",
                operacion, sqlState, e.getMessage());

        String mensajeUsuario;
        if (sqlState.startsWith("08")) {
            mensajeUsuario = "No se pudo conectar con PostgreSQL al intentar " + operacion + ".\n" +
                    "Verifique que el servidor PostgreSQL esté activo y el puerto sea correcto.";
        } else if ("28P01".equals(sqlState)) {
            mensajeUsuario = "Error de autenticación al conectar con PostgreSQL.";
        } else if ("23505".equals(sqlState)) {
            mensajeUsuario = "La base de datos rechazó el registro porque el código ya existe (UNIQUE).";
        } else if ("23514".equals(sqlState)) {
            mensajeUsuario = "La base de datos rechazó el registro porque incumple una restricción CHECK.";
        } else {
            mensajeUsuario = "Ocurrió un error en la base de datos al intentar " + operacion + " (SQLState: " + sqlState + ").";
        }

        mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", mensajeUsuario);
    }

    /**
     * Muestra una ventana de alerta de JavaFX.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
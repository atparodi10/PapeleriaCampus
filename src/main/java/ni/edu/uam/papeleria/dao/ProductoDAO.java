package ni.edu.uam.papeleria.dao;

import ni.edu.uam.papeleria.connection.DataBaseConnection;
import ni.edu.uam.papeleria.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProductoDAO {

    /** Obtiene todos los productos de la base de datos ordenados por código ascendente. */
    public List<Producto> obtenerTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, codigo, nombre, categoria, precio, stock " +
                "FROM public.producto " +
                "ORDER BY codigo ASC";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        }
        return lista;
    }

    /** Busca productos en PostgreSQL combinando filtros mediante condiciones AND.*/
    public List<Producto> buscarConFiltros(String texto, String categoria, boolean soloConExistencias) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id_producto, codigo, nombre, categoria, precio, stock " +
                        "FROM public.producto WHERE 1=1"
        );

        boolean filtrarTexto = texto != null && !texto.trim().isEmpty();
        boolean filtrarCategoria = categoria != null && !categoria.trim().isEmpty() && !"Todas".equalsIgnoreCase(categoria.trim());

        if (filtrarTexto) {
            sql.append(" AND (codigo ILIKE ? OR nombre ILIKE ?)");
        }
        if (filtrarCategoria) {
            sql.append(" AND categoria = ?");
        }
        if (soloConExistencias) {
            sql.append(" AND stock > 0");
        }

        sql.append(" ORDER BY codigo ASC");

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int indice = 1;
            if (filtrarTexto) {
                String patron = "%" + texto.trim() + "%";
                ps.setString(indice++, patron);
                ps.setString(indice++, patron);
            }
            if (filtrarCategoria) {
                ps.setString(indice++, categoria.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(crearProductoDesdeResultSet(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Inserta un nuevo producto en la tabla public.producto.
     * Devuelve el número de filas insertadas. */
    public int guardar(Producto producto) throws SQLException {
        String sql = "INSERT INTO public.producto (codigo, nombre, categoria, precio, stock) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());

            return ps.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un producto existente mediante su id_producto.
     * Devuelve el número de filas afectadas (0 si el ID ya no existe). */
    public int actualizarPorId(Producto producto) throws SQLException {
        String sql = "UPDATE public.producto " +
                "SET codigo = ?, nombre = ?, categoria = ?, precio = ?, stock = ? " +
                "WHERE id_producto = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setBigDecimal(4, producto.getPrecio());
            ps.setInt(5, producto.getStock());
            ps.setInt(6, producto.getIdProducto());

            return ps.executeUpdate();
        }
    }

    /**
     * Elimina físicamente un producto de la tabla mediante su id_producto.
     * Devuelve el número de filas eliminadas (0 si el ID no existía). */
    public int eliminarPorId(int idProducto) throws SQLException {
        String sql = "DELETE FROM public.producto WHERE id_producto = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate();
        }
    }

    /**
     * Comprueba si un código normalizado ya existe en la tabla public.producto.
     * Cuando se está actualizando (idExcluir != null), excluye el propio ID del producto en edición. */
    public boolean existeCodigoDuplicado(String codigo, Integer idExcluir) throws SQLException {
        String sql = (idExcluir == null)
                ? "SELECT COUNT(*) FROM public.producto WHERE UPPER(BTRIM(codigo)) = UPPER(BTRIM(?))"
                : "SELECT COUNT(*) FROM public.producto WHERE UPPER(BTRIM(codigo)) = UPPER(BTRIM(?)) AND id_producto <> ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            if (idExcluir != null) {
                ps.setInt(2, idExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /** Construye un objeto Producto a partir de la fila actual del ResultSet. */
    private Producto crearProductoDesdeResultSet(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock")
        );
    }
}
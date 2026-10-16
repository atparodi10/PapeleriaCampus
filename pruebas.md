# Registro y Matriz de Pruebas — Catálogo de Productos Papelería Campus


## 1. Pruebas de Búsqueda y Filtros 

| Código | Entrada / Filtro aplicado | Resultado esperado | Resultado obtenido | Estado       | Referencia de evidencia |
|---|---|---|-------------------|--------------|---|
| **B1** | Texto: `CUADERNO`, Categoría: `Todas`, Solo con existencias: `No` | Muestra `P001` y `P002`; contador en `2` | `Exitoso`         | `Completada` | `evidencias/b1_cuaderno.png` |
| **B2** | Texto: `T00`, Categoría: `Todas`, Solo con existencias: `No` | Muestra `T001` a `T004`; contador en `4` | `Exitoso`         | `Completada` | `evidencias/b2_t00.png` |
| **B3** | Sin texto, Categoría: `Papelería`, Solo con existencias: `No` | Muestra `P001` a `P004`; contador en `4` | `Exitoso`         | `Completada` | `evidencias/b3_papeleria.png` |
| **B4** | Sin texto, Categoría: `Todas`, Solo con existencias: `Sí` | Muestra `9` productos con stock > 0; contador en `9` | `Exitoso`         | `Completada` | `evidencias/b4_existencias.png` |

---

## 2. Evidencias Obligatorias

| Operación                                          | Entrada / Acción realizada                                                   | Resultado esperado                                                              | Resultado obtenido | Estado       | Referencia de evidencia              |
|----------------------------------------------------|------------------------------------------------------------------------------|---------------------------------------------------------------------------------|-------------------|--------------|--------------------------------------|
| **Todos los datos en pantalla**                    | Datos al iniciar el programa                                                 | Se muestan todos los datos presentes en la base de datos                        | `Exitoso`         | `Completado` | `evidencias/todosLosDatos.png`       |
| **Mostrar Constrainst creados en la base de datos** | Se agrega un precio igual a 0 al intentar agregar un nuevo producto          | Alerta por intentar agregar un precio igual 0, vale tambien para un monto menor | `Exitoso`         | `Completada` | `evidencias/prueba_constraint.png`   |
| **Busqueda por filtro**                            | Resultados al aplicar filtros en busqueda de producto (Prueba con Papelería) | Todos los productos de Categoria `Papelería`                                    | `Completada`      | `Completada` | `evidencias/busqueda_por_filtro.png` |


---
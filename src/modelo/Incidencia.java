package modelo;

import java.time.LocalDateTime;

public class Incidencia {
    private int id;
    private String titulo;
    private String descripcion;
    private String estado;
    private int clienteId;
    private Integer tecnicoId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private String resolucion;

    public Incidencia() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public Integer getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Integer tecnicoId) { this.tecnicoId = tecnicoId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    
    @Override
    public String toString() {
        return this.titulo;
    }
}

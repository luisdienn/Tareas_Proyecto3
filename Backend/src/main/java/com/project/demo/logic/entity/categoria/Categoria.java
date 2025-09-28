package com.project.demo.logic.entity.categoria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.demo.logic.entity.producto.Producto;
import jakarta.persistence.*;

import java.util.List;

@Table(name ="categoria")
@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Producto> productos;


    //Constructor
    public Categoria() {}

    //Set y Get

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}

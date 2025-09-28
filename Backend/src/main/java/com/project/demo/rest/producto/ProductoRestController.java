package com.project.demo.rest.producto;

import com.project.demo.logic.entity.categoria.Categoria;
import com.project.demo.logic.entity.categoria.CategoriaRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.producto.Producto;
import com.project.demo.logic.entity.producto.ProductoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;


    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Producto> ordersPage = productoRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Productos retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping("/categoria/{categoriaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> addProductoToCategoria(@PathVariable Long categoriaId, @RequestBody Producto producto, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(categoriaId);
        if(foundCategoria.isPresent()) {
            producto.setCategoria(foundCategoria.get());
            Producto savedProducto = productoRepository.save(producto);
            return new GlobalResponseHandler().handleResponse("Productos created successfully",
                    savedProducto, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria id " + categoriaId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{productoId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> updateProducto(@PathVariable Long productoId, @RequestBody Producto producto, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(productoId);
        if(foundProducto.isPresent()) {
            producto.setId(foundProducto.get().getId());
            producto.setCategoria(foundProducto.get().getCategoria());
            productoRepository.save(producto);
            return new GlobalResponseHandler().handleResponse("Producto updated successfully",
                    producto , HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Producto id " + productoId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{productoId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> deleteProducto(@PathVariable Long productoId, HttpServletRequest request) {
        Optional<Producto> foundProducto = productoRepository.findById(productoId);
        if(foundProducto.isPresent()) {
            Optional<Categoria> categoria = categoriaRepository.findById(foundProducto.get().getCategoria().getId());
            categoria.get().getProductos().remove(foundProducto.get());
            productoRepository.deleteById(foundProducto.get().getId());
            return new GlobalResponseHandler().handleResponse("Producto deleted successfully",
                    foundProducto.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Producto id " + productoId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }
}

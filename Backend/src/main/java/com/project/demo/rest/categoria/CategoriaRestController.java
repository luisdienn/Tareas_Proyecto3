package com.project.demo.rest.categoria;

import com.project.demo.logic.entity.categoria.Categoria;
import com.project.demo.logic.entity.categoria.CategoriaRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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
@RequestMapping("/categorias")
public class CategoriaRestController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Categoria> ordersPage = categoriaRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Categorias retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> addCategoria( @RequestBody Categoria categoria, HttpServletRequest request) {
        if(categoria != null) {
            Categoria savedCategoria = categoriaRepository.save(categoria);
            return new GlobalResponseHandler().handleResponse("Categoria created successfully",
                    savedCategoria, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Error creando Categoria"  ,
                    HttpStatus.BAD_REQUEST, request);
        }
    }

    @PutMapping("/{categoriaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> updateCategoria(@PathVariable Long categoriaId, @RequestBody Categoria categoria, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(categoriaId);
        if(foundCategoria.isPresent()) {
            foundCategoria.get().setId(categoriaId);
            foundCategoria.get().setNombre(categoria.getNombre());
            foundCategoria.get().setDescripcion(categoria.getDescripcion());

            categoriaRepository.save(foundCategoria.get());
            return new GlobalResponseHandler().handleResponse("Categoria updated successfully",
                    categoria , HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria id " + categoriaId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{categoriaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') and isAuthenticated()")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long categoriaId, HttpServletRequest request) {
        Optional<Categoria> foundCategoria = categoriaRepository.findById(categoriaId);
        if(foundCategoria.isPresent()) {
            categoriaRepository.deleteById(foundCategoria.get().getId());
            return new GlobalResponseHandler().handleResponse("Categoria deleted successfully",
                    foundCategoria.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Categoria id " + categoriaId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }
}

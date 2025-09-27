package com.project.demo.rest.car;

import com.project.demo.logic.entity.car.Car;
import com.project.demo.logic.entity.car.CarRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.order.Order;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarRestController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Car> ordersPage = carRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Cars retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }



    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addCarToUser(@PathVariable Long userId, @RequestBody Car car, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(userId);
        if(foundUser.isPresent()) {
            car.setUser(foundUser.get());
            Car savedCar = carRepository.save(car);
            return new GlobalResponseHandler().handleResponse("Car created successfully",
                    savedCar, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("User id " + userId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{carId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long carId, @RequestBody Car car, HttpServletRequest request) {
        Optional<Car> foundCar = carRepository.findById(carId);
        if(foundCar.isPresent()) {
            car.setId(foundCar.get().getId());
            car.setUser(foundCar.get().getUser());
            carRepository.save(car);
            return new GlobalResponseHandler().handleResponse("Car updated successfully",
                    car, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Car id " + carId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{carId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteCar(@PathVariable Long carId, HttpServletRequest request) {
        Optional<Car> foundCar = carRepository.findById(carId);
        if(foundCar.isPresent()) {
            Optional<User> user = userRepository.findById(foundCar.get().getUser().getId());
            user.get().getOrders().remove(foundCar.get());
            carRepository.deleteById(foundCar.get().getId());
            return new GlobalResponseHandler().handleResponse("Car deleted successfully",
                    foundCar.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Car id " + carId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

}
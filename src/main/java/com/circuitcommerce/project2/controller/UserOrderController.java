package com.circuitcommerce.project2.controller;

import java.util.List;

import com.circuitcommerce.project2.dto.UserOrderDto;
import com.circuitcommerce.project2.dto.UserOrderUpdateDto;
import com.circuitcommerce.project2.dto.UserDto;
import com.circuitcommerce.project2.model.UserOrder;
import com.circuitcommerce.project2.model.User;
import com.circuitcommerce.project2.service.UserOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@RestController
@RequestMapping(value="/orders")
@AllArgsConstructor(onConstructor=@__(@Autowired))
@NoArgsConstructor
public class UserOrderController {
  public UserOrderService uoServ;

  @PostMapping("/all")
  public ResponseEntity<List<UserOrder>> getUserOrderByUsername(@RequestBody UserDto uDto){
    return new ResponseEntity<>(uoServ.getUserOrderHistory(uDto.getUsername()),HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity<String> addOrder(@RequestBody UserOrderDto uOrder){
    uoServ.addOrder(uOrder);
    return new ResponseEntity<>("Order added to history", HttpStatus.ACCEPTED);
  }

  @PostMapping("/update")
  public ResponseEntity<String> changeStatus(@RequestBody UserOrderUpdateDto updateDto){
    uoServ.updateOrder(updateDto);
    return new ResponseEntity<>("Order Status has been changed", HttpStatus.ACCEPTED);
  }
}

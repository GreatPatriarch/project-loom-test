package com.dnu.controller;

import com.dnu.controller.payload.UserPayload;
import com.dnu.entity.User;
import com.dnu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor()
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/get-user/{userId:\\d+}")
    public User getUser(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }


   @PostMapping("/user-create")
   public ResponseEntity<User> createUser(@RequestBody UserPayload payload) {
       return new ResponseEntity<>(userService.createUser(payload.firstName(), payload.lastName(), payload.email()),
               HttpStatus.OK);
   }
   @PatchMapping("/user-edit/{userId:\\d+}")
   public ResponseEntity<?> editUser(@PathVariable Long userId, @RequestBody UserPayload payload) {
        userService.editUser(userId, payload.firstName(), payload.lastName(), payload.email());
        return ResponseEntity.noContent()
                .build();
   }

    @DeleteMapping("/user-delete/{userId:\\d+}")
   public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
   }
}

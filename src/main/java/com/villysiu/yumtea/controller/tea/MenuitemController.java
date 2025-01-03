package com.villysiu.yumtea.controller.tea;

import com.villysiu.yumtea.dto.tea.MenuitemDto;
import com.villysiu.yumtea.models.tea.Menuitem;
import com.villysiu.yumtea.repo.tea.MenuitemRepo;
import com.villysiu.yumtea.service.MenuitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MenuitemController {
    @Autowired
    private MenuitemRepo menuitemRepo;

    @Autowired
    private MenuitemService menuitemService;
    @GetMapping("/menuitems")
    public List<Menuitem> getMenuitems() {
        return menuitemRepo.findAll();
    }

    @PostMapping("/menuitem")
    public ResponseEntity<Menuitem> createMenuitem(@RequestBody MenuitemDto menuitemDto) {
        Menuitem menuitem = menuitemService.createMenuitem(menuitemDto);
        return new ResponseEntity<>(menuitem, HttpStatus.CREATED);
    }

    @PatchMapping("/menuitem/{id}")
    public ResponseEntity<Menuitem> updateMenuitem(@PathVariable Long id, @RequestBody Map<String, Object> menuitemDto) {
        Menuitem menuitem = menuitemService.updateMenuitem(id, menuitemDto);
        return new ResponseEntity<>(menuitem, HttpStatus.CREATED);
    }

    @DeleteMapping("/menuitem/{id}")
    public ResponseEntity<String> deleteMenuitem(@PathVariable Long id) {
        Menuitem menuitem = menuitemRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Menuitem not found."));
        menuitemRepo.delete(menuitem);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);



    }
}

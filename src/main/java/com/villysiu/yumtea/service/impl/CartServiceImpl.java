package com.villysiu.yumtea.service.impl;

import com.villysiu.yumtea.dto.request.CartInputDto;
import com.villysiu.yumtea.models.cart.Cart;
import com.villysiu.yumtea.models.tea.*;
import com.villysiu.yumtea.models.user.User;
import com.villysiu.yumtea.projection.CartProjection;
import com.villysiu.yumtea.repo.cart.CartRepo;


import com.villysiu.yumtea.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepo cartRepo;
    private final MenuitemService menuitemService;
    private final MilkService milkService;

    private final SizeService sizeService;
    private final UserService userService;

    @Override
    public Long createCart(CartInputDto cartInputDto) throws RuntimeException {
        System.out.println(cartInputDto);
        User user = userService.getCurrentUser();

        Optional<Cart> cart = cartRepo.findByUserIdAndMenuitemIdAndMilkIdAndSizeIdAndSugarAndTemperature(
                user.getId(),
                cartInputDto.getMenuitemId(),
                cartInputDto.getMilkId(),
                cartInputDto.getSizeId(),
                cartInputDto.getSugar(),
                cartInputDto.getTemperature()
        );
        //cart already existed, update quantity
        if(cart.isPresent()){
            Cart dupCart = cart.get();
            dupCart.setQuantity(dupCart.getQuantity() + cartInputDto.getQuantity());
//
            cartRepo.save(dupCart);
            return dupCart.getId();
        }
        else{
            System.out.println("creating a new cart");
            Cart newCart = new Cart();
            newCart.setUser(user);

            Menuitem menuitem = menuitemService.getMenuitemById(cartInputDto.getMenuitemId());
            newCart.setMenuitem(menuitem);

            Milk milk = (menuitem.getMilk().getTitle().equals("NA")) ?
                    menuitem.getMilk() : milkService.getMilkById(cartInputDto.getMilkId());
            newCart.setMilk(milk);

            Size size = sizeService.getSizeById(cartInputDto.getSizeId());
            newCart.setSize(size);

            newCart.setPrice(menuitem.getPrice() + milk.getPrice() + size.getPrice());

            newCart.setQuantity(cartInputDto.getQuantity());

            Temperature NATemperature = Temperature.valueOf("NA");
            newCart.setTemperature(
                    menuitem.getTemperature().equals(NATemperature)  ? NATemperature : cartInputDto.getTemperature()
            );
            Sugar NASugar = Sugar.valueOf("NA");
            newCart.setSugar(
                    menuitem.getSugar().equals(NASugar) ? NASugar : cartInputDto.getSugar()
            );
            System.out.println(newCart);

            cartRepo.save(newCart);
            return newCart.getId();


        }
    }

    @Override
    public Long updateCart(Long id, CartInputDto cartInputDto) throws RuntimeException {
        User user = userService.getCurrentUser();
        Cart cart = cartRepo.findById(id).orElseThrow(()-> new NoSuchElementException("Cart not found"));

        Optional<Cart> duplicatedCart = cartRepo.findByUserIdAndMenuitemIdAndMilkIdAndSizeIdAndSugarAndTemperature(
                user.getId(),
                cartInputDto.getMenuitemId(),
                cartInputDto.getMilkId(),
                cartInputDto.getSizeId(),
                cartInputDto.getSugar(),
                cartInputDto.getTemperature()
        );
        if(duplicatedCart.isPresent()){
            duplicatedCart.get().setQuantity(cartInputDto.getQuantity() + duplicatedCart.get().getQuantity());
            cartRepo.save(duplicatedCart.get());
            cartRepo.delete(cart);
            return duplicatedCart.get().getId();
        }
//        during update, only properties are allowed to update, not the menuitem
//        Menuitem menuitem = menuitemService.getMenuitemById(cartInputDto.getMenuitemId());
//        cart.setMenuitem(menuitem);
        Menuitem menuitem = cart.getMenuitem();

        Milk milk = (menuitem.getMilk().getTitle().equals("NA")) ?
                menuitem.getMilk() : milkService.getMilkById(cartInputDto.getMilkId());
        cart.setMilk(milk);

        Size size = sizeService.getSizeById(cartInputDto.getSizeId());
        cart.setSize(size);

        cart.setPrice(menuitem.getPrice() + milk.getPrice() + size.getPrice());
        cart.setQuantity(cartInputDto.getQuantity());

        Temperature NATemperature = Temperature.valueOf("NA");
        cart.setTemperature(
                menuitem.getTemperature().equals(NATemperature)  ? NATemperature : cartInputDto.getTemperature()
        );
        Sugar NASugar = Sugar.valueOf("NA");
        cart.setSugar(
                menuitem.getSugar().equals(NASugar) ? NASugar : cartInputDto.getSugar()
        );

        cartRepo.save(cart);

        return cart.getId();
    }

    @Override
    public List<Cart> getCartsByUserId(Long id) {
        return cartRepo.findByUserId(id, Cart.class);
    }

    @Override
    public List<CartProjection> getCartProjectionsByUserId(Long id){
        return cartRepo.findByUserId(id, CartProjection.class);
    }

    @Override
    public Cart getCartById(Long id) throws NoSuchElementException {
            Optional<Cart> cart = cartRepo.findById(id, Cart.class);
            if(cart.isPresent())
                return cart.get();
            else
                throw new NoSuchElementException("Cart not found");

    }
    @Override
    public CartProjection getCartProjectionById(Long id) throws NoSuchElementException {
        Optional<CartProjection> cartProjection = cartRepo.findById(id, CartProjection.class);
        if(cartProjection.isPresent())
            return cartProjection.get();
        else
            throw new NoSuchElementException("Cart not found");

    }


    @Override
    public ResponseEntity<String> removeUserCart(List<Cart> userCarts){
//        cartRepo.deleteById(id);
        cartRepo.deleteAll(userCarts);
        return ResponseEntity.ok("Cart removed");
    }

}

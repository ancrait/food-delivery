package com.sorokaandriy.restaurant_service.service;

import com.sorokaandriy.restaurant_service.dto.MenuItemRequest;
import com.sorokaandriy.restaurant_service.dto.MenuItemResponse;
import com.sorokaandriy.restaurant_service.dto.RestaurantRequest;
import com.sorokaandriy.restaurant_service.dto.RestaurantResponse;
import com.sorokaandriy.restaurant_service.entity.Category;
import com.sorokaandriy.restaurant_service.entity.MenuItem;
import com.sorokaandriy.restaurant_service.entity.Restaurant;
import com.sorokaandriy.restaurant_service.exception.MenuItemNotFoundException;
import com.sorokaandriy.restaurant_service.exception.RestaurantNotFoundException;
import com.sorokaandriy.restaurant_service.repository.CategoryRepository;
import com.sorokaandriy.restaurant_service.repository.MenuItemRepository;
import com.sorokaandriy.restaurant_service.service.mapper.RestaurantMapper;
import com.sorokaandriy.restaurant_service.repository.RestaurantRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_KEY = "restaurant:";
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;


    public Page<RestaurantResponse> findAll(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return restaurantRepository.findAll(pageable)
                .map(restaurant -> restaurantMapper.fromRestaurnatToRestaurantResponse(restaurant));
    }


    public RestaurantResponse findRestaurantById(UUID id) {

        String key = REDIS_KEY + id;

        RestaurantResponse restaurantFromCache = (RestaurantResponse) redisTemplate.
                opsForValue().get(key);

        if (restaurantFromCache != null){
            return restaurantFromCache;
        }

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found"));

        RestaurantResponse response = restaurantMapper.fromRestaurnatToRestaurantResponse(restaurant);

        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(3));

        return response;

    }


    public RestaurantResponse createRestaurant(RestaurantRequest request) {


        Restaurant restaurant = restaurantRepository
                .save(restaurantMapper.fromRequestToResponseRestaurant(request));

        RestaurantResponse response = restaurantMapper.fromRestaurnatToRestaurantResponse(restaurant);
        redisTemplate.opsForValue()
                .set(REDIS_KEY + restaurant.getId(), response, Duration.ofMinutes(3));

        return response;
    }


    public RestaurantResponse updateRestaurant(RestaurantRequest request, UUID id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found"));

        restaurantMapper.updateEntityRestaurant(restaurant, request);

        restaurantRepository.save(restaurant);
        redisTemplate.delete(REDIS_KEY + id);

        return restaurantMapper.fromRestaurnatToRestaurantResponse(restaurant);

    }


    public void deleteRestaurant(UUID id) {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found"));

        restaurantRepository.delete(restaurant);
        redisTemplate.delete(REDIS_KEY + id);
    }


    public List<MenuItemResponse> findAllMenuItemFromRestaurant(UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found"));

        return restaurantMapper.fromMenuItemToMenuItemResponse(restaurant);
    }


    public MenuItemResponse createMenuItem(UUID id, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id " + id + " not found"));

        Category category = categoryRepository.findByName(request.category())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(request.category()).build()));

        MenuItem menuItem = restaurantMapper.fromMenuItemRequestToMenuItem(restaurant, request, category);
        menuItemRepository.save(menuItem);

        MenuItemResponse response = restaurantMapper.fromMenuItemToMenuItemResponse(menuItem);
        redisTemplate.delete(REDIS_KEY + id);

        return response;
    }


    public MenuItemResponse updateMenuItem(UUID id, MenuItemRequest request, UUID idItem) {
        Category category = categoryRepository.findByName(request.category())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(request.category()).build()));

        MenuItem menuItem = menuItemRepository.findById(idItem)
                .orElseThrow(() -> new MenuItemNotFoundException("MenuItem with id " + idItem + " not found"));

        if (!menuItem.getRestaurant().getId().equals(id)) {
            throw new IllegalArgumentException("MenuItem does not belong to this restaurant");
        }

        restaurantMapper.updateMenuItem(menuItem, request, category);

        menuItemRepository.save(menuItem);

        MenuItemResponse response = restaurantMapper.fromMenuItemToMenuItemResponse(menuItem);

        redisTemplate.delete(REDIS_KEY + id);

        return response;


    }

    public void deleteMenuItem(UUID id, UUID idItem) {

        MenuItem menuItem = menuItemRepository.findById(idItem)
                .orElseThrow(() -> new MenuItemNotFoundException("MenuItem with id " + idItem + " not found"));

        if (!menuItem.getRestaurant().getId().equals(id)) {
            throw new IllegalArgumentException("MenuItem does not belong to this restaurant");
        }

        menuItemRepository.delete(menuItem);

        redisTemplate.delete(REDIS_KEY + id);

    }
}


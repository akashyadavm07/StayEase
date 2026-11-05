
package com.stayease.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import com.stayease.model.FoodItem;
import com.stayease.model.FoodOrder;
import com.stayease.model.FoodOrderItem;
import com.stayease.repository.FoodItemRepository;
import com.stayease.repository.FoodOrderRepository;

@Controller
public class FoodOrderController {

    private final FoodItemRepository foodItemRepository;
    private final FoodOrderRepository foodOrderRepository;

    public FoodOrderController(
            FoodItemRepository foodItemRepository,
            FoodOrderRepository foodOrderRepository) {
        this.foodItemRepository = foodItemRepository;
        this.foodOrderRepository = foodOrderRepository;
    }

    @PostMapping("/food/order")
    @Transactional
    public String placeOrder(
            @RequestParam String guestName,
            @RequestParam String roomNumber,
            @RequestParam Map<String, String> formData,
            RedirectAttributes redirectAttributes) {

        if (guestName.isBlank() || roomNumber.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "foodError", "Please enter your name and room number.");
            return "redirect:/";
        }

        FoodOrder order = new FoodOrder();
        order.setGuestName(guestName.trim());
        order.setRoomNumber(roomNumber.trim());

        BigDecimal total = BigDecimal.ZERO;
        int itemCount = 0;

        for (Map.Entry<String, String> entry : formData.entrySet()) {

            String key = entry.getKey();

            if (!key.startsWith("quantity_")) {
                continue;
            }

            int quantity;

            try {
                quantity = Integer.parseInt(entry.getValue());
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute(
                        "foodError", "Invalid food quantity.");
                return "redirect:/";
            }

            if (quantity < 0 || quantity > 20) {
                redirectAttributes.addFlashAttribute(
                        "foodError", "Please select a valid quantity.");
                return "redirect:/";
            }

            if (quantity == 0) {
                continue;
            }

            Long foodId;

            try {
                foodId = Long.parseLong(key.substring("quantity_".length()));
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute(
                        "foodError", "Invalid food item.");
                return "redirect:/";
            }

            FoodItem food = foodItemRepository.findById(foodId).orElse(null);

            if (food == null || !food.isAvailable()) {
                redirectAttributes.addFlashAttribute(
                        "foodError",
                        "One or more selected items are no longer available.");
                return "redirect:/";
            }

            BigDecimal lineTotal = food.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));

            FoodOrderItem orderItem = new FoodOrderItem();
            orderItem.setItemName(food.getName());
            orderItem.setUnitPrice(food.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setLineTotal(lineTotal);

            order.addItem(orderItem);

            total = total.add(lineTotal);
            itemCount++;
        }

        if (itemCount == 0) {
            redirectAttributes.addFlashAttribute(
                    "foodError", "Please select at least one food item.");
            return "redirect:/";
        }

        order.setTotalAmount(total);

        FoodOrder savedOrder = foodOrderRepository.save(order);

        redirectAttributes.addFlashAttribute(
                "foodOrderId", savedOrder.getId());

        return "redirect:/?foodSuccess";
    }

    @GetMapping("/admin/food")
    public String manageFood(Model model) {

        model.addAttribute(
                "foodItems",
                foodItemRepository.findAll());

        return "admin-food";
    }

    @PostMapping("/admin/food/add")
    public String addFood(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal price,
            RedirectAttributes redirectAttributes) {

        if (name.isBlank() || price.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute(
                    "foodMessage", "Enter a valid name and price.");
            return "redirect:/admin/food";
        }

        FoodItem food = new FoodItem();
        food.setName(name.trim());
        food.setDescription(description.trim());
        food.setPrice(price);
        food.setAvailable(true);

        foodItemRepository.save(food);

        return "redirect:/admin/food";
    }

    @PostMapping("/admin/food/toggle")
    public String toggleAvailability(@RequestParam Long id) {

        FoodItem food = foodItemRepository.findById(id).orElse(null);

        if (food != null) {
            food.setAvailable(!food.isAvailable());
            foodItemRepository.save(food);
        }

        return "redirect:/admin/food";
    }

    @PostMapping("/admin/food/delete")
    public String deleteFood(@RequestParam Long id) {

        if (foodItemRepository.existsById(id)) {
            foodItemRepository.deleteById(id);
        }

        return "redirect:/admin/food";
    }

    @GetMapping("/admin/food-orders")
    public String manageOrders(Model model) {

        model.addAttribute(
                "foodOrders",
                foodOrderRepository.findAllByOrderByCreatedAtDesc());

        return "admin-food-orders";
    }

    @PostMapping("/admin/food-orders/update")
    public String updateOrderStatus(
            @RequestParam Long id,
            @RequestParam String status) {

        List<String> validStatuses = List.of(
                "PENDING", "PREPARING", "DELIVERED", "CANCELLED");

        if (validStatuses.contains(status)) {

            FoodOrder order = foodOrderRepository.findById(id).orElse(null);

            if (order != null) {
                order.setOrderStatus(status);
                foodOrderRepository.save(order);
            }
        }

        return "redirect:/admin/food-orders";
    }
}

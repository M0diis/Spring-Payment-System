package me.modkzl.controllers.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.modkzl.mapper.payment.PaymentMapper;
import me.modkzl.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @Operation(summary = "Create a new payment", description = "Creates a payment of type TYPE1, TYPE2, or TYPE3.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentIdResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public @NotNull PaymentIdResponse createPayment(@RequestBody @Valid PaymentRequest request) {
        Long id = this.paymentService.create(this.paymentMapper.mapRequestToDto(request));
        return new PaymentIdResponse(id);
    }

    @Operation(summary = "Get payment by ID", description = "Retrieves a payment by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    @GetMapping("/{id}")
    public @NotNull PaymentResponse getPayment(@PathVariable Long id) {
        return this.paymentMapper.mapDomainToResponse(this.paymentService.get(id));
    }

    @Operation(summary = "Cancel a payment", description = "Cancels a payment if it was created on the same day.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment canceled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cancellation request", content = @Content)
    })
    @PutMapping("/cancel/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelPayment(@PathVariable Long id) {
        this.paymentService.cancel(id);
    }

    @Operation(summary = "Get all payments", description = "Retrieves all non-canceled payments, optionally filtered by amount.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentResponse.class)))
    })
    @PostMapping
    public @NotNull List<@NotNull PaymentResponse> getPayments(@RequestBody PaymentFilterCriteria criteria) {
        return this.paymentService.get(criteria)
                .stream()
                .map(this.paymentMapper::mapDomainToResponse)
                .toList();
    }
}

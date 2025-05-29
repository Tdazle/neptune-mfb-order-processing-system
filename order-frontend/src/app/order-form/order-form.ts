import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {OrderService} from '../services/order.service';
import {ProductModel} from '../models/product.model';
import {OrderModel} from '../models/order.model';
import {ProductService} from '../services/product.service';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-order-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-form.html',
  styleUrls: ['./order-form.css']
})

export class OrderForm implements OnInit {
  orderForm: FormGroup;
  products: ProductModel[] = [];
  loading = false;
  error: string | null = null;

  @Output() orderCreated = new EventEmitter<void>();

  /**
   * Initializes the component with a reactive form.
   *
   * @param fb The form builder service.
   * @param orderService The order service.
   * @param productService The product service.
   */
  constructor(
    private fb: FormBuilder,
    private orderService: OrderService,
    private productService: ProductService
  ) {
    this.orderForm = this.fb.group({
      product: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]]
    });
  }

  /**
   * Lifecycle hook called after data-bound properties of a directive is initialized.
   * This method is used to initiate the fetching of products when the component is initialized.
   */
  ngOnInit(): void {
    this.fetchProducts();
  }

  /**
   * Fetches the list of products and updates the component's `products` property.
   *
   * If the request fails, the component's `error` property is set to the error message.
   *
   * @returns A subscription to the observable returned from `ProductService.getProducts()`.
   */
  fetchProducts() {
    this.productService.getProducts().subscribe({
      next: (products) => this.products = products,
      error: () => this.error = 'Failed to load products'
    });
  }

  /**
   * Submits the order form and creates a new order.
   *
   * This method is called when the form is submitted. It first checks if the form is invalid,
   * and if it is, it does nothing. Then, it sets the loading state of the component and
   * resets the error message. It creates a new `OrderModel` with the form values, and
   * calls the `createOrder` method of the `OrderService` with the new order. If the
   * request is successful, it resets the form, sets the loading state to false, and
   * emits the `orderCreated` event. If the request fails, it sets the error message
   * to the error response body's message, or to a default message if the error response
   * body does not contain a message. Finally, it sets the loading state to false.
   */
  submitOrder() {
    if (this.orderForm.invalid) return;
    this.loading = true;
    this.error = null;
    const order: OrderModel = {
      product: this.orderForm.value.product,
      quantity: this.orderForm.value.quantity
    };
    this.orderService.createOrder(order).subscribe({
      next: () => {
        this.orderForm.reset({ product: '', quantity: 1 });
        this.loading = false;
        this.orderCreated.emit();
      },
      error: (err) => {
        this.error = err?.error?.message || 'Order creation failed';
        this.loading = false;
      }
    });
  }
}

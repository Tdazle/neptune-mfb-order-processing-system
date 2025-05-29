import {Component, OnInit} from '@angular/core';
import {OrderService} from '../services/order.service';
import {OrderModel} from '../models/order.model';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-order-list',
  standalone: true, // Add this
  imports: [CommonModule], // Add CommonModule if you use *ngIf, *ngFor, etc.
  templateUrl: './order-list.html',
  styleUrls: ['./order-list.css']
})

export class OrderList implements OnInit {
  orders: OrderModel[] = [];
  loading = false;
  error: string | null = null;

  /**
   * Initializes the component by injecting the OrderService.
   * @param orderService the injected OrderService
   */
  constructor(private orderService: OrderService) {
  }

  /**
   * Initializes the component by calling the `fetchOrders` method.
   * This hook is called once after the component is initialized.
   */
  ngOnInit(): void {
    this.fetchOrders();
  }

  /**
   * Fetches the list of orders from the server and updates the component's state.
   * This method is called in the `ngOnInit` lifecycle hook and when the user clicks
   * the "Refresh" button.
   *
   * If the request is successful, the `orders` array is updated and the `loading`
   * flag is set to false.
   *
   * If the request fails, the `error` string is set to an error message and the
   * `loading` flag is set to false.
   */
  fetchOrders() {
    this.loading = true;
    this.error = null;
    this.orderService.getOrders().subscribe({
      /**
       * Handles the successful retrieval of orders.
       * Updates the component's `orders` array with the fetched data
       * and sets the `loading` flag to false.
       *
       * @param orders - The array of orders retrieved from the server.
       */
      next: (orders) => {
        this.orders = orders;
        this.loading = false;
      },
      /**
       * Handles errors that occur during the retrieval of orders.
       * Sets the component's `error` string to an error message and
       * sets the `loading` flag to false.
       */
      error: () => {
        this.error = 'Failed to load orders';
        this.loading = false;
      }
    });
  }
}

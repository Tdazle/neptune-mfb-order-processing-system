import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {OrderModel} from '../models/order.model';


@Injectable({
  providedIn: 'root'
})

export class OrderService {
  private apiUrl = 'http://localhost:8080'; // Adjust if backend runs elsewhere

  /**
   * @constructor
   * @param http The Angular HTTP client
   * @description Initializes the service with the injected HTTP client.
   */
  constructor(private http: HttpClient) {
  }

  /**
   * Fetches all orders from the backend.
   * @returns An observable stream of {@link OrderModel} objects.
   */
  getOrders(): Observable<OrderModel[]> {
    return this.http.get<OrderModel[]>(`${this.apiUrl}/orders`);
  }

  /**
   * Submits a new order to the backend.
   * @param order The new order as an {@link OrderModel}
   * @returns An observable stream of the created {@link OrderModel}.
   */
  createOrder(order: OrderModel): Observable<OrderModel> {
    return this.http.post<OrderModel>(`${this.apiUrl}/orders`, order);
  }
}
